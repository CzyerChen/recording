### 线程安全的几种模型，主要基于jdk8来分析
- **COW:copyOnWrite（copyOnWriteArrayList）** ---> **写时拷贝**

在写操作时，会将内部的动态数组按照预期长度（原长度加上将要写入的长度，还要判断是否需要动态扩展）
拷贝出一份，申请一个新数组容纳新数据，然后将原来数组的指针指向它，完成写是拷贝，读在指针没有转换之前，还是读原有数据到内存进行处理，因而可能出现读脏数据的情况

- **CAS：compare  and swap (ConcurrentHashMap)** ---> **即比较与替换，CAS造作将比较和替换封装为一组原子操作(原子操作指要么全做要么全部不做)，不会被外部打断。这种原子操作的保证往往由处理器层面提供支持**

在Java中有一个非常神奇的Unsafe类来对CAS提供语言层面的接口。Unsafe类可以执行操作系统层面的指令，因而很重要
ConcurrentHashMap与HashMap对数据的存储有着相似的地方，都采用数组+链表+红黑树的方式。基本逻辑是内部使用Node来保存map中的一项key， value结构，对于hash不冲突的key，使用数组来保存Node数据，
而每一项Node都是一个链表，用来保存hash冲突的Node，当链表的大小达到一定程度会转为红黑树，这样会使在冲突数据较多时也会有比较好的查询效率。

了解了ConcurrentHashMap的存储结构后，我们来看下在这种结构下，ConcurrentHashMap是如何实现高效的并发操作，这得益于ConcurrentHashMap中的如下三个函数。


```$xslt
static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
    return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
}
static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                    Node<K,V> c, Node<K,V> v) {
    return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
}
static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {
    U.putOrderedObject(tab, ((long)i << ASHIFT) + ABASE, v);
}
```
其中的U就是我们前文提到的Unsafe的一个实例，这三个函数都通过Unsafe的几个方法保证了是原子性：

- tabAt作用是返回tab数组第i项
- casTabAt函数是对比tab第i项是否与c相等，相等的话将其设置为v。
- setTabAt将tab的第i项设置为v

有了这三个函数就可以保证ConcurrentHashMap的线程安全吗？并不是的，ConcurrentHashMap内部也使用比较多的synchronized，不过与HashTable这种对所有操作都使用synchronized不同，ConcurrentHashMap只在特定的情况下使用synchronized，来较少锁的定的区域。来看下putVal方法（精简版）:
```$xslt
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to embin
        }
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            synchronized (f) {
                    ....
            }
        }
    }
    addCount(1L, binCount);
    return null;
}
```
整个put流程大致如下：
- 判断key与value是否为空，为空抛异常
- 计算kek的hash值，然后进入死循环，一般来讲，caw算法与死循环是搭档。
- 判断table是否初始化，未初始化进行初始化操作
- Node在table中的目标位置是否为空，为空的话使用caw操作进行赋值，当然，这种赋值是有可能失败的，所以前面的死循环发挥了重试的作用。
- 如果当前正在扩容，则尝试协助其扩容，死循环再次发挥了重试的作用，有趣的是ConcurrentHashMap是可以多线程同时扩容的。
**这里说协助的原因在于，对于数组扩容，一般分为两步**：
1.新建一个更大的数组；
2.将原数组数据copy到新数组中。
- 对于第一步，ConcurrentHashMap通过CAW来控制一个int变量保证新建数组这一步只会执行一次。对于第二步，ConcurrentHashMap采用CAW + synchronized + 移动后标记 的方式来达到多线程扩容的目的。感兴趣可以查看transfer函数。
- 最后的一个else分支，黑科技的流程已尝试无效，目标Node已经存在值，只能锁住当前Node来进行put操作，当然，这里省略了很多代码，包括链表转红黑树的操作等等。

相比于put，get的代码更好理解一下：
```$xslt
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode());
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```
- 检查表是否为空
- 获取key的hash h，获取key在table中对应的Node e
- 判断Node e的第一项是否与预期的Node相等，相等话， 则返回e.val
- 如果e.hash < 0, 说明e为红黑树，调用e的find接口来进行查找。
- 走到这一步，e为链表无疑，且第一项不是需要查询的数据，一直调用next来进行查找即可。





