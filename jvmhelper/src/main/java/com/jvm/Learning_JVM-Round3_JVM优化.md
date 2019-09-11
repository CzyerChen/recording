> 很普遍的JVM优化就是调参数，今天就[参考文章](https://mp.weixin.qq.com/s?__biz=MzUxOTc4NjEyMw==&mid=2247484129&idx=1&sn=f09ced376ccb5ffa7f2bcf4c4171bd2e&key=3394008d3c3493cb4639cd1d8e89c2ecf6fc5146ef835eca975f826787b627f8f47981246b02f2622ebc885e708c6b934ce1b574ea4e3ec2cd6652e247e376b0d4264fbb131153dab2d1c60d9a96f8bc&ascene=1&uin=MTA4NjE2NTIyNA%3D%3D&devicetype=Windows+10&version=62060739&lang=zh_CN&pass_ticket=KFYhA4IsoAmmNZ30lWG2b%2Bj3xJa7rkXfI6Qn2rgostn04tzq4cf6feFY0zSiJXPI),来学习一下JVM调参

### 常用配置
#### 堆参数
|参数|描述|
|:------:|:------:|
|-Xms |设置JVM启动的初始堆大小 initial|
|-Xmx |设置堆的最大值 max|
|-Xmn |设置新生代的大小 new|
|-XX:PermGen | 设置永久代的初始大小，JDK8之后永久代就没有了|
|-XX:mAXPermGen|设置永久代的最大值|
|-XX:SurvivorRatio| 设置Eden区和Survivor区的空间比例;Eden/S0 = Eden/S1 |
|-XX:NewRatio|设置老年代和新生代的比例，默认是2|

#### 回收器参数
|参数|描述|
|:-----:|:----:|
|-XX:+UseSerialGC|串行GC，新生代和老年代都采用复制算法执行串行回收|
|-XX:UseParallelGC|并行GC，老年代单线程串行回收，新生代采用Parallel scavenge回收算法回收，多线程并行回收，可采用-XX:ParallelThreads=n指定并行线程数|
|-XX:UseParallelOldGC|并行GC，老年代和新生代一样，使用多线程并行Parallel scavenge回收算法回收|
|-XX:UseConcMarkSweepGC|并发较少停顿，面向老年代的算法，新生代一般使用并行回收算法，CMS算法，老年代使用典型的标记整理|
|-XX:G1GC|采用G1算法，并行，并发，增量式，无内存碎片，可控的停顿，会将堆分为不同的区域，根据每块区域内的垃圾数量，优先回收垃圾多的区域|
- 这个垃圾回收算法的指定可以依据内存情况或者需求的不同进行调整

#### 项目中常用的配置
|参数|描述|
|:-----:|:----:|
|-Xms4800m|初始堆大小|
|-Xmx4800m|最大堆大小|
|-Xmn1800m|新生代大小，剩下就是老年代大小|
|-Xss512k|线程栈空间大小stack space|
|-XX:PermGen=256m|永久代初始大小，jdk8后不存在了，归为本地元数据了|
|-XX:MaxPermGen=256m|最大永久区空间大小|
|-XX:+UseStringCache|默认开启启用常用的字符串缓存|
|-XX:+UseConcMarkSweepGC|老年代采用CMS作为垃圾回收算法|
|-XX:+UseParNewGC|新生代使用并行收集器|
|-XX:ParallelGCThreads=4|并行回收使用的线程数为4|
|-XX:+CMSClassUnloadingEnabled|允许对累的元数据进行清理|
|-XX:+DisableExplicitGC|禁止显示GC|
|-XX:+UseCMSInitiatingOccupancyOnly|只达到阈值之后才进行CMS回收|
|-XX:CMSInitiaatingOccupancyFaction=68|设置CMS在老年代回收的阈值为68%|
|-verbose:gc|打印输出GC详情|
|-XX:+PrintGCDetails|打印GC详情日志|
|-XX:+PrintGCDateStamps|打印GC的耗时|
|-XX:+PrintTenuringDistribution|打印Tenuring年龄信息|
|-XX:+HeapDumpOnOutOfMemoryError|当OOM的时候进行HeapDump|
|-XX:+HeapDumpPath=/tmp/headpData|指定dump的文件路径|

#### 常用的组合
- 很常见的垃圾回收就是新生代并行回收，老年代CMS的标记整理回收，看看还有什么组合

|新生代|老年代|jvm参数|
|:--------:|:--------:|:--------:|
|Serial串行|Serial串行|-XX:+UseSerialGC|
|Parallel scanvenge|Parallel Old/Serial|-XX:+UseParallelGC -XX:+UseParallelOldGC|
|Serial/Parallel scanvenge|CMS|-XX:+UseParNewGC -XX:+UseConcMarkSweepGC|
|G1|G1|-XX:+UseG1GC|


### 常用GC调优策略
#### GC调优原则
- 多数导致 GC 问题的 Java 应用，都不是因为我们参数设置错误，而是代码问题
-在实际使用中，分析 GC 情况优化代码比优化 GC 参数要多

#### GC调优目的
- 将转移到老年代的对象数量降低到最小
- 减少 GC 的执行时间

#### 方法
- 调整-Xmn调整新生代的大小，避免新生代直接进入老年代
- 调整-XX:PreetenureSizeThreshold,决定可以直接进入老年代的对象大小，大对象首先进入新生代可能会直接影响内存，因而可以考虑大对象直接进入老年代，避免了新生代的内存问题，也能避免full gc
- 调整-XX:MaxTenuringThreshold,设置进入老年代的年龄大小，减少老年代的内存占用，降低fullgc发生的频率
- 调整-Xms -Xmx设置初始堆和最大堆的大小
- 以下情况不需要进行GC优化：
```text
MinorGC 执行时间不到50ms；

Minor GC 执行不频繁，约10秒一次；

Full GC 执行时间不到1s；

Full GC 执行频率不算频繁，不低于10分钟1次。
```

### 64位计算机中的JVM内存管理的技巧-- 压缩指针
#### JVM运行时表示对象
- JVM用Ordinary Object Pointers（OOP）的数据结构表示对象
- 不同于C中的指针
- instanceOops是一种特殊的oop，表示Java中的对象实例
- instanceOop的内存布局，只是对象头后面紧跟零个或多个对实例字段的引用
- 对象头包括：标记词（偏置锁定，身份哈希，GC），KClass词（指向元数据），32位长度字，表示数组长度，32位的间隔来强制对象对齐，单字就是本机机器字，因此在传统的32位机器上为32位，在更现代的系统上为64位

#### 64位机器上JVM性能不一定高于32位
```text
64位引用比32位引用的多占用两倍空间，

因此这会导致更多的内存消耗和更频繁的GC周期。

专用于GC周期的时间越多，应用程序线程的CPU执行切片就越少
```
#### 压缩指针的优化
- JVM为对象进行填充，使其大小变为8个字节的倍数。使用这些填充后，oops中的最后三位始终为零。这是因为在二进制中8的倍数的数字总是以000结尾
- 由于JVM已经知道最后三位始终为零，因此在堆中存储那些零是没有意义的。相反，它假设它们存在并存储3个其他更重要的位
- 启用oop压缩：-XX:+UseCompressedOops进行调整,jdk7以上最大堆小于32G默认打开指针压缩，大于32G将关闭OOP
- 超过32G也可以使用指针压缩，-XX：ObjectAlignmentInBytes配置字节值，需要是2的倍数即可，最大可使用堆空间 = 4GB *ObjectAlignmentInBytes，因为对其值的增加会增加内存的空间，因而这样的设置并不能带来什么好处

### ZGC -- jdk11的实验性低延迟垃圾回收器
- 由于需要64为彩色指针，因而并不能支持指针压缩，因而这种低延迟的回收技术必然是以内存做的交换

### 内存优化的建议
1. 合理配置jvm参数，并使用合适的GC回收策略
2. 对象优先分配在Eden上
3. 大对象可以直接进入老年代，可以通过-XX:PretenureSizeThreshold配置，大于多少的对象会直接分配在老年代上
4. 合理设置进入老年代的年龄阈值：-XX:MaxTenuringThreshold 用来定义年龄的阈值

### 动态对象年龄判定
```text
虚拟机并不是永远地要求对象的年龄必须达到 MaxTenuringThreshold 才能晋升老年代，
如果在 Survivor 中相同年龄所有对象大小的总和大于 Survivor 空间的一半，
则年龄大于或等于该年龄的对象可以直接进入老年代，无需等到 MaxTenuringThreshold 中要求的年龄
```
### 空间分配担保
```text
在发生 Minor GC 之前，虚拟机先检查老年代最大可用的连续空间是否大于新生代所有对象总空间，
如果条件成立的话，那么 Minor GC 可以确认是安全的。

如果不成立的话虚拟机会查看 HandlePromotionFailure 设置值是否允许担保失败，
如果允许那么就会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小，
如果大于，将尝试着进行一次 Minor GC；如果小于，或者 HandlePromotionFailure 设置不允许冒险，
那么就要进行一次 Full GC
```
