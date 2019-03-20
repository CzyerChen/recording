- redis中数据结构有哪些，有哪些用途都有了一定的认识
- 但是每一个数据结构的组成，不谈redis的全部源码，我们应该对我们常用的数据结构的底层实现由基本了解，就像认识jdk源码一样，从源头知道为什么String是不可变长的
- 以下内容借鉴来自java知音

### 一、redis
Redis是一个由ANSI C语言编写，性能优秀、支持网络、可持久化的K-K内存数据库

### 二、数据类型
- String：key-value, 缓存、限流、计数器、分布式锁、分布式Session
- Hash：field-value, 存储用户信息、用户主页访问量、组合查询
- List：queue,微博关注人时间轴列表、简单队列
- Set：无序集合，赞、踩、标签、好友关系
- Zset：有序集合，排行榜，也称SortedSet

### 三、给出例子
执行`Set hello world`：
```text
            |
           \|/
 |----------------------|
 |     DictEntry        |
 | void*            key |--------->  sds "hello"
 | void*            val |---------> |-----------------------|
 | Struct dicEntry% next|           |    redisObject        |
 |----------------------|           | unsigned  type(String)|
            |                       | void*     ptr         |-----> sds "world"
           \|/                      |-----------------------|
 |-----------------------|
 |       DictEntry       |
 |                       |
 |-----------------------|
```
- dictEntry：Redis给每个key-value键值对分配一个dictEntry，里面有着key和val的指针，next指向下一个dictEntry形成链表，这个指针可以将多个哈希值相同的键值对链接在一起，通过链地址法解决哈希冲突
- sds：键key“hello”是以SDS（简单动态字符串，Simple Dynamic String）存储
- redisObject：值val“world”存储在redisObject中。redis常用5种类型都是以redisObject来存储的；而redisObject中的type字段指明了Value对象的类型，ptr字段则指向对象所在的地址
- redisObject对象很重要，Redis对象的类型、内部编码、内存回收、共享对象等功能，都需要redisObject支持
    - 这样设计的好处是，可以针对不同的使用场景，对5种常用类型设置多种不同的数据结构实现，从而优化对象在不同场景下的使用效率
- dictEntry对象、redisObject、SDS对象，都需要内存分配器（如jemalloc）分配内存进行存储
- jemalloc是Redis的默认内存分配器，很好地减小内存碎片。比如jemalloc在64位系统中，将内存空间划分为小、大、巨大三个范围；每个范围内又划分了许多小的内存块单位；当Redis存储数据时，会选择大小最合适的内存块进行存储。

redisObject的ptr指针指向底层实现的数据结构，而数据结构由encoding属性决定。不同编码与数据结构的对应关系如下：
```text
|------------------------------------------------------------------------------|
|    底层数据结构      |     编码常量              |    object encoding 命令输出  |
|------------------------------------------------------------------------------|
|        整数         | REDIS_ENCODING_INT       |      int                    |
|------------------------------------------------------------------------------|
|  embstr编码的SDS    | REDIS_ENCODING_ENBSTR     |     embstr                  |
|------------------------------------------------------------------------------| 
|        SDS         | REDIS_ENCODING_RAW        |     raw                     |
|------------------------------------------------------------------------------|
|        字典         | REDIS_ENCODING_HT         |   hashtable                |
|------------------------------------------------------------------------------|
|       双端链表      | REDIS_ENCODING_LINKEDLIST  |    linkedlist              |
|-------------------------------------------------------------------------------|
|       压缩列表       | REDIS_ENCODING_ZIPLIST    |    ziplist                 |
|-------------------------------------------------------------------------------|
|       整数集合       | REDIS_ENCOSING_INTSET     |     intset                  |
|-------------------------------------------------------------------------------|
|    跳跃表和字典       | REDIS_ENCODIN_SKIPLIST    |    skiplist                |
|-------------------------------------------------------------------------------|
```

### 四、String

- 字符串对象的底层实现可以是int、raw、embstr
- embstr编码是通过调用一次内存分配函数来分配一块连续的空间，而raw需要调用两次
- int编码字符串对象和embstr编码字符串对象在一定条件下会转化为raw编码字符串对象
- embstr：<=39字节的字符串
- int：8个字节的长整型
- raw：大于39个字节的字符串
- 简单动态字符串（SDS），这种结构像C++的String或者Java的ArrayList，内部有一个可变长数组，有容量，有负载因子
```text
struct sdshdr {
    // buf 中已占用空间的长度
    int len;
    // buf 中剩余可用空间的长度
    int free;
    // 数据空间
    char buf[]; // ’\0’空字符结尾
};

get：sdsrange---O(n)

set：sdscpy—O(n)

create：sdsnew---O(1)

len：sdslen---O(1)

```

#### 1.预空间分配
- 当SDS长度（len的值）小于1MB，程序将分配和len属性同样大小的未使用空间，这时free和len属性值相同
   - 如果len的空间未15字节，那么free空间也是15字节，再添加一个字节的空字符，那么buff数组实际长度就是15+15+1 = 31 字节
  
- 当SDS长度（len的值）大于等于1MB，程序会分配1MB的未使用空间
   -  如果len是30M，加上未使用空间为1M ，一个字节的空白字符，那么buff数组实际长度 30M +1M +1字节

#### 2.惰性释放空间
- 惰性释放空间主要避免了某些情况下操作字符串内存的重新分配
- 例如在执行字符串截取操作的时候，被截取剩下的空间不会立刻被回收，当发生拼接字符串，并且新增加的字符长度小于被截取剩下的空间大小，就很好的使用了未使用的空间

#### 3.杜绝了缓冲区溢出
- 因为String内部数组有长度的记录，每次操作会判断数组长度能否满足预期，不能进行动态扩容，因而很好地避免了因为拼接等操作而造成可能的内存溢出情况

### 五、List
- redis种的list底层是通过快速列表quicklist实现的，快速列表是压缩列表ziplist和双端链表linkedlist
- 支持两端的插入和删除，也能够获取指定位置的元素（效率相对不高），可以当作数组、队列和栈使用，通常的使用场景是队列
```text
typedef struct listNode {
     // 前置节点
    struct listNode *prev;
    // 后置节点
    struct listNode *next;
    // 节点的值
    void *value;
 } listNode;

 typedef struct list {
     // 表头节点
    listNode *head;
    // 表尾节点
    listNode *tail;
    // 节点值复制函数
    void *(*dup)(void *ptr);
    // 节点值释放函数
    void (*free)(void *ptr);
     // 节点值对比函数
    int (*match)(void *ptr, void *key);
     // 链表所包含的节点数量
    unsigned long len;
 } list;
 
 rpush: listAddNodeHead ---O(1)
 
 lpush: listAddNodeTail ---O(1)
 
 push:listInsertNode ---O(1)
 
 index : listIndex ---O(N)
 
 pop:ListFirst/listLast ---O(1)
 
 llen:listLength ---O(N)
```

#### 1.双端链表
- 双端链表和我们以前接触的双向链表很相似，有向前和向后的指针，有当前节点的value,还有头指针和尾指针
- 但是链表的内存数据其实是不连续的，依靠多余的向前向后指针来维系，增加了内存的消耗和内存碎片化，降低了内存管理的效率
- 因而这种数据结构会考虑在对象相对较大的情况下转换

#### 2.压缩列表
- 当一个列表键只包含少量列表项，且是小整数值或长度比较短的字符串时，那么redis就使用ziplist（压缩列表）来做列表键的底层实现
- ziplist是Redis为了节约内存而开发的，是由一系列特殊编码的连续内存块
- 新版本中list链表使用 quicklist 代替了 ziplist和 linkedlist

#### 3.快速列表
- quickList 是 zipList 和 linkedList 的混合体
- 将 linkedList 按段切分，每一段使用 zipList 来紧凑存储，多个 zipList 之间使用双向指针串接起来
- quicklist 默认的压缩深度是 0，也就是不压缩。为了支持快速的 push/pop 操作，quicklist 的首尾两个 ziplist 不压缩，此时深度就是 1
- 为了进一步节约空间，Redis 还会对 ziplist 进行压缩存储，使用 LZF 算法压缩
```text
   |----------------------------quicklist----------------------------------|
   |head                                                               tail|
   |---->  quicklist node <----> quicklist node <---> quicklist node <-----|
                 |                    |                    |
              ziplist              ziplist              ziplist
```


### 六、HASH
- Hash对象的底层实现可以是压缩列表ziplist或者字典或者也叫哈希表hashtable
- 使用ziplist的场景不多：哈希中元素数量小于512个 以及 哈希中所有键值对的键和值字符串长度都小于64字节
- hashtable哈希表可以实现O(1)复杂度的读写操作，因此效率很高
```text
typedef struct dict {
    // 类型特定函数
    dictType *type;
     // 私有数据
    void *privdata;
     // 哈希表
    dictht ht[2];
    // rehash 索引
    // 当 rehash 不在进行时，值为 -1
    int rehashidx; /* rehashing not in progress if rehashidx == -1 */
     // 目前正在运行的安全迭代器的数量
    int iterators; /* number of iterators currently running */
 } dict;
 typedef struct dictht {
    // 哈希表数组
    dictEntry **table;
     // 哈希表大小
    unsigned long size;
    // 哈希表大小掩码，用于计算索引值
    // 总是等于 size - 1
    unsigned long sizemask;
    // 该哈希表已有节点的数量
    unsigned long used;
} dictht;
typedef struct dictEntry {
    void *key;
    union {void *val;uint64_t u64;int64_t s64;} v;
    // 指向下个哈希表节点，形成链表
    struct dictEntry *next;
 } dictEntry;
 typedef struct dictType {
     // 计算哈希值的函数
    unsigned int (*hashFunction)(const void *key);
     // 复制键的函数
    void *(*keyDup)(void *privdata, const void *key);
     // 复制值的函数
    void *(*valDup)(void *privdata, const void *obj);
     // 对比键的函数
    int (*keyCompare)(void *privdata, const void *key1, const void *key2);
    // 销毁键的函数
    void (*keyDestructor)(void *privdata, void *key);
    // 销毁值的函数
    void (*valDestructor)(void *privdata, void *obj);
} dictType;
```
- hash表的源码可能有点长，但是它的大体实现我们应该都很熟悉了，和HashMap类似，也是采用链地址法来处理哈希冲突
 - 一个字典数据结构下，维系这一个hash数组，，有标识是否在重哈希阶段
 - 每一个哈希表的元素里面都有一个哈希表数组，表的大小，已有节点数
 - 最后每一个哈希表数组元素就是一个dictEntry对象，在前面也说明了它就是存储的基本单位了，包含向前向后指针，和指向具体底层存储数据结构的指针
 
- 还有一个值得说的就是，redis中每个字典有两个hash表，一个是正常时候使用，一个是重哈希的时候使用
```text
   |------------------|         |--------------|             |-------------|
   |    dict          |         |   dict       |     |------>| dictEntry   |---->null
   |    type          |         |   table      |-----|       | k0    v0    |
   |    private       |         |   size 4     |     |       |-------------|
   |    ht            |-------->|   sizemark 3 |     |
   |    rehashidx = -1|     |   |   used 2     |     |------>|-------------|
   |------------------|     |   |--------------|             |  dictEntry  |-----> null
                            |                                |  k1   v1    |
                            |   |--------------|             |-------------|
                            |   |   dict       |
                            |-->|   table      |----->null
                                |   size 0     |
                                |   sizemark 0 |
                                |   used 0     |
                                |--------------|
```
 
### 七、Set
- set的底层实现是intset或者哈希表/字典
- intset是只有集合中只有整数，并且数量不多的情况下，会使用intset做底层存储
```
typedef struct intset {
    // 编码方式
    uint32_t encoding;
    // 集合包含的元素数量
    uint32_t length;
    // 保存元素的数组
    int8_t contents[];
} intset

sadd:intsetAdd---O(1)

smembers:intsetGetO(1)---O(N)

srem:intsetRemove---O(N)

slen:intsetlen ---O(1)

```
- intset底层实现为有序，无重复数组保存集合元素
- 内部可以支持整数的类型有16位、32位、64位，它能够通过适应内部整数的类型向上升级，但是不能够向下降级，比如本来是一个16位类型的数组，只要加入了32位的整数，数组就编程了32位类型的数组，即使删除了原来添加到元素也不能变回16位类型的数组了
- 因而intset还是比较灵活的，在最大程度上适应存储的数据类型而节省存储

### 八、ZSet 又称SortedSet
- Zset的底层实现是ziplist压缩列表或者skiplist跳跃表
- ziplist存在的理由，只要记住，内容字节数相对较小的时候采用ziplist的压缩算法能够节省存储，但是对象的大的时候都不可用，因而zset也不例外，当对象相对小的时候，存储使用ziplist,而对象相对大的时候就采用skiplist
```text
typedef struct zskiplist {
     // 表头节点和表尾节点
    struct zskiplistNode *header, *tail;
    // 表中节点的数量
    unsigned long length;
    // 表中层数最大的节点的层数
    int level;
 } zskiplist;
typedef struct zskiplistNode {
    // 成员对象
    robj *obj;
    // 分值
    double score;
     // 后退指针
    struct zskiplistNode *backward;
    // 层
    struct zskiplistLevel {
        // 前进指针
        struct zskiplistNode *forward;
         // 跨度---前进指针所指向节点与当前节点的距离
        unsigned int span;
    } level[];
} zskiplistNode;

zadd---zslinsert---平均O(logN), 最坏O(N)

zrem---zsldelete---平均O(logN), 最坏O(N)

zrank--zslGetRank---平均O(logN), 最坏O(N)

```
- 跳跃表也类似于java数据结构的跳跃表
- 跳跃表(skiplist)是一种有序数据结构，它通过在某个节点中维持多个指向其他节点的指针，从而达到快速访问节点的目的
- 跳跃表是通过一个随机数来判断对应的数字在二维数组中是否需要继续在指定的位置插入，最高层位maxlevel,元素插入的概率为1/2^maxLevel
- 先随机生成一个范围为0~2^maxLevel-1的一个整数r，那么元素r小于2^(maxLevel-1)的概率为1/2，r小于2^(maxLevel-2)的概率为1/4，……，r小于2的概率为1/2^(maxLevel-1)，r小于1的概率为1/2^maxLevel
- 例子：，假设maxLevel为4，那么r的范围为0~15，则r小于8的概率为1/2，r小于4的概率为1/4，r小于2的概率为1/8，r小于1的概率为1/16。1/16正好是maxLevel层插入元素的概率，1/8正好是maxLevel层插入的概率
- 通过这样的分析，我们可以先比较r和1，如果r<1，那么元素就要插入到maxLevel层以下；否则再比较r和2，如果r<2，那么元素就要插入到maxLevel-1层以下；再比较r和4，如果r<4，那么元素就要插入到maxLevel-2层以下……如果r>2^(maxLevel - 1)，那么元素就只要插入在底层即可
- java 版本的跳跃表
```text

/***************************  SkipList.java  *********************/

import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {
    private int maxLevel;
    private SkipListNode<T>[] root;
    private int[] powers;
    private Random rd = new Random();
    SkipList() {
        this(4);
    }
    SkipList(int i) {
        maxLevel = i;
        root = new SkipListNode[maxLevel];
        powers = new int[maxLevel];
        for (int j = 0; j < maxLevel; j++)
            root[j] = null;
        choosePowers();
    }
    public boolean isEmpty() {
        return root[0] == null;
    }
    public void choosePowers() {
        powers[maxLevel-1] = (2 << (maxLevel-1)) - 1;    // 2^maxLevel - 1
        for (int i = maxLevel - 2, j = 0; i >= 0; i--, j++)
           powers[i] = powers[i+1] - (2 << j);           // 2^(j+1)
    }
    public int chooseLevel() {
        int i, r = Math.abs(rd.nextInt()) % powers[maxLevel-1] + 1;
        for (i = 1; i < maxLevel; i++)
            if (r < powers[i])
                return i-1; // return a level < the highest level;
        return i-1;         // return the highest level;
    }
    // make sure (with isEmpty()) that search() is called for a nonempty list;
    public T search(T key) { 
        int lvl;
        SkipListNode<T> prev, curr;            // find the highest nonnull
        for (lvl = maxLevel-1; lvl >= 0 && root[lvl] == null; lvl--); // level;
        prev = curr = root[lvl];
        while (true) {
            if (key.equals(curr.key))          // success if equal;
                 return curr.key;
            else if (key.compareTo(curr.key) < 0) { // if smaller, go down,
                 if (lvl == 0)                 // if possible
                      return null;      
                 else if (curr == root[lvl])   // by one level
                      curr = root[--lvl];      // starting from the
                 else curr = prev.next[--lvl]; // predecessor which
            }                                  // can be the root;
            else {                             // if greater,
                 prev = curr;                  // go to the next
                 if (curr.next[lvl] != null)   // non-null node
                      curr = curr.next[lvl];   // on the same level
                 else {                        // or to a list on a lower level;
                      for (lvl--; lvl >= 0 && curr.next[lvl] == null; lvl--);
                      if (lvl >= 0)
                           curr = curr.next[lvl];
                      else return null;
                 }
            }
        }
    }
    public void insert(T key) {
        SkipListNode<T>[] curr = new SkipListNode[maxLevel];
        SkipListNode<T>[] prev = new SkipListNode[maxLevel];
        SkipListNode<T> newNode;
        int lvl, i;
        curr[maxLevel-1] = root[maxLevel-1];
        prev[maxLevel-1] = null;
        for (lvl = maxLevel - 1; lvl >= 0; lvl--) {
            while (curr[lvl] != null && curr[lvl].key.compareTo(key) < 0) { 
                prev[lvl] = curr[lvl];           // go to the next
                curr[lvl] = curr[lvl].next[lvl]; // if smaller;
            }
            if (curr[lvl] != null && key.equals(curr[lvl].key)) // don't 
                return;                          // include duplicates;
            if (lvl > 0)                         // go one level down
                if (prev[lvl] == null) {         // if not the lowest
                      curr[lvl-1] = root[lvl-1]; // level, using a link
                      prev[lvl-1] = null;        // either from the root
                }
                else {                           // or from the predecessor;
                     curr[lvl-1] = prev[lvl].next[lvl-1];
                     prev[lvl-1] = prev[lvl];
                }
        }
        lvl = chooseLevel();                // generate randomly level 
        newNode = new SkipListNode<T>(key,lvl+1); // for newNode;
        for (i = 0; i <= lvl; i++) {        // initialize next fields of
            newNode.next[i] = curr[i];      // newNode and reset to newNode
            if (prev[i] == null)            // either fields of the root
                 root[i] = newNode;         // or next fields of newNode's
            else prev[i].next[i] = newNode; // predecessors;
        }
    }
}
```