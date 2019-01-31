### ThreadLocal概念
- ThreadLocal 主要解决多线程本地变量的问题，能够满足每个线程都可以持有仅与当前进程相关的对象，比如线程计数器等
- 主要用于将私有线程和该线程存放的副本对象做一个映射，可以实现无状态的调用
- 解决变量线程安全问题
- 往ThreadLocal里面存数据，就是想ThrealLocal的Map中存对象，然后将这个Map的副本放入当前线程

### 结构图


### 数据结构
- key是弱引用类型，无法生存到下一次GC，但是值是正常强引用类型，需要没有对象引用它才会被标记被清除
- ThreadLocal 内部有一个静态内部类 ThreadLocalMap ，依靠这个Map 实现K-V操作，内部Map主要依靠自定义的Entry数组、数组大小、数组初始容量、容量阈值等参数来构建Map,这几个基本要素和构建HashMap的要素也是类似的
- ThreadLocal的几个常规操作：get\set\remove ，基本都是依靠map的操作实现，其中需要做容量的判断和map初始化等工作

### hash冲突如何解决
我们通过查看ThreadLocal中map解决重哈希采用开放定址法中的线性探测，通过固定的步长解决哈希冲突问题
```
/**
 * Increment i modulo len.
 */
private static int nextIndex(int i, int len) {
    return ((i + 1 < len) ? i + 1 : 0);
}

/**
 * Decrement i modulo len.
 */
private static int prevIndex(int i, int len) {
    return ((i - 1 >= 0) ? i - 1 : len - 1);
}
```
此处简单的线性探测方法对大并发的冲突解决效率并不是很好
此处操作的建议：单个线程内建立尽量少的ThreadLocal对象，以减少哈希冲突的可能

### ThreadLocalMap内存泄露的原因
- 前面提到ThreadLocal的ThreadLocalMap的KEY是一个指向ThreadLocal的弱引用，在下一次GC来之前就会被回收，那么key的值就会变为null,value是强引用不被回收，因为Map中大量存在key为null，value不为null的情况，如果ThreadLocal对应的线程一直运行，value一直得不到回收，就可能会造成内存溢出
- get set remove方法中也有调用expungeStaleEntry进行资源回收，可是并不及时
- ThreadPool和Tomcat中使用ThreadLocal变量需要谨慎，使用不当容易造成内存泄漏

- 如何避免
  在每一次get/set调用确保使用完value后都显示的调用remove ,手动解除本地Map与Entry之间的强引用，使内存及时回收
  
### 应用场景
- Hiberante的Session 工具类HibernateUtil
- 通过不同的线程对象设置Bean属性，保证各个线程Bean对象的独立性
- 主要就是用来保存线程隔离、线程安全的对象

### 与synchronized的对比
- synchronized通过锁的方式，保证一个时刻一个对象/代码块只被一个线程访问，实现数据共享
- ThreadLocal通过数据副本的方式，保证一个时刻每个线程之操作自己持有变量的副本，实现数据隔离

