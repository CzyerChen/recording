### HashMap（数组+链表+红黑树）
- HashMap最多只允许一条记录的键为null，允许多条记录的值为null。HashMap非线程安全，即任一时刻可以有多个线程同时写HashMap，可能会导致数据的不一致
- 如果需要满足线程安全，可以用 Collections的synchronizedMap方法使HashMap具有线程安全的能力，或者使用ConcurrentHashMap
#### jdk7中的HashMap
```text
static class Entry<K,V> implements Map.Entry<K,V>{
   final K key;
   V value;
   Entry<K,V> next;
   int hash;
}
```
- Entry 包含四个属性：key, value, hash 值和用于单向链表的 next
- capacity：当前数组容量，始终保持 2^n，可以扩容，扩容后数组大小为当前的 2 倍
- loadFactor：负载因子，默认为 0.75
- threshold：扩容的阈值，等于 capacity * loadFactor

### jdk8中的HashMap
- 处于对HashMap中同Hash值后单链表的遍历的优化，在链表长度超过8的时候，会自动转化为红黑树进行存储，原本O（n）的查询复杂度，降为了O（logN）
- 组成：数组+链表+红黑树

### ConcurrentHashMap --线程安全， 一定程度承受并发的hashmap
- 内部是分成多个Segment进行存储，Segment继承 ReentrantLock ，也就有了分段锁的概念
- 通过分段的方式，增加了每一段进行并发操作的可能，也通过加锁的方式控制并发对象访问，这样就保证了每一个段是线程安全的
- 并行度默认16，concurrencyLevel：并行级别、并发数、Segment 数，ConcurrentHashMap 有 16 个 Segments

### HashTable -- 线程安全
- 很多映射的常用功能与HashMap类似，不同的是它承自Dictionary类，并且是线程安全的，任一时间只有一个线程能写Hashtable
- 并发性不如ConcurrentHashMap，因为ConcurrentHashMap引入了分段锁
- Hashtable不建议在新代码中使用，不需要线程安全的场合可以用HashMap替换，需要线程安全的场合可以用ConcurrentHashMap替换

### TreeMap ---可排序
- TreeMap实现SortedMap接口，能够把它保存的记录根据键排序，默认是按键值的升序排序，也可以指定排序的比较器，当用Iterator遍历TreeMap时，得到的记录是排过序的。 如果使用排序的映射，建议使用TreeMap

### LinkHashMap --- 记录插入顺序
- LinkedHashMap是HashMap的一个子类，保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先得到的记录肯定是先插入的，也可以在构造时带参数，按照访问次序排序


