### CyclicBarrier、CountDownLatch、Semaphore的用法
- CountDownLatch 等待子线程全部完成后，主线程才执行
- CyclicBarrier 当子线程都变成某一状态后同时执行，叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用
- Semaphore信号量：Semaphore可以控制同时访问的线程个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可


### volatile实现内存可见，禁止指令重排
- volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，因此在读取volatile类型的变量时总会返回最新写入的值
- 在读或者写之前，通过插入内存屏障，要求向前的指令必须执行完毕，向后指令不允许和当前指令重排序，实现了变量可见性和禁止重排序
- 是一个轻量级的读取最新值的方式，但是写入方面，它只能保证读取的是内存中的最新值，而不能够保证在读取操作过程中内存中值的变化，因而，要求读取写入过程的数据一致性，必须使用重量级锁

### 两个线程如何共享数据
- 将需要共享的数据存放在类的方法中，方法表示为sychronized，从而实现并发的修改和获取
```text

```

### 线程本地变量ThreadLocal
- 这种变量在线程的生命周期内起作用，减少同一个线程内多个函数或者组件之间一些公共变量的传递的复杂度
- 每个线程中都有一个自己的ThreadLocalMap类对象
- 将一个共用的ThreadLocal静态实例作为key，将不同对象的引用保存到不同线程的ThreadLocalMap中，然后在线程执行的各处通过这个静态ThreadLocal实例的get()方法取得自己线程保存的那个对象，避免了将这个对象作为参数传递的麻烦
- 最常见的ThreadLocal使用场景为 用来解决 数据库连接、Session管理等

### synchronized和ReentrantLock的区别
- 共同点
1. 都是用来协调多线程对共享对象、变量的访问 
2. 都是可重入锁，同一线程可以多次获得同一个锁 
3. 都保证了可见性和互斥性

- 区别：
```text
1. ReentrantLock显示的获得、释放锁，synchronized隐式获得释放锁 
2. ReentrantLock可响应中断、可轮回，synchronized是不可以响应中断的，为处理锁的不可用性提供了更高的灵活性 
3. ReentrantLock是API级别的，synchronized是JVM级别的 
4. ReentrantLock可以实现公平锁 
5. ReentrantLock通过Condition可以绑定多个条件 
6. 底层实现不一样， synchronized是同步阻塞，使用的是悲观并发策略，lock是同步非阻塞，采用的是乐观并发策略 
7. Lock是一个接口，而synchronized是Java中的关键字，synchronized是内置的语言实现。 
8. synchronized在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生；而Lock在发生异常时，如果没有主动通过unLock()去释放锁，则很可能造成死锁现象，因此使用Lock时需要在finally块中释放锁。 
9. Lock可以让等待锁的线程响应中断，而synchronized却不行，使用synchronized时，等待的线程会一直等待下去，不能够响应中断。 
10. 通过Lock可以知道有没有成功获取锁，而synchronized却无法办到。
11. Lock可以提高多个线程进行读操作的效率，既就是实现读写锁等。
```
### ConcurrentHashMap并发 -- 减小锁粒度最好的尝试
- 减小锁粒度是指缩小锁定对象的范围，从而减小锁冲突的可能性，从而提高系统的并发能力。减小锁粒度是一种削弱多线程锁竞争的有效手段
- 分段锁Segment:默认情况下一个ConcurrentHashMap被进一步细分为16个段，既就是锁的并发度
- ConcurrentHashMap是由Segment数组结构和HashEntry数组结构组成。Segment是一种可重入锁ReentrantLock，在ConcurrentHashMap里扮演锁的角色，HashEntry则用于存储键值对数据

### java线程调度：抢占式调度和协同式调度
- JVM线程调度的实现
- 线程让出CPU的情况
- 进程调度算法--- 优先调度算法
1. FCFS 先来先服务调度算法FCFS：
2. 短作业优先调度算法：运行时间左端
- 进程调度算法 --- 高优先权有限调度算法
1. 非抢占式优先权算法 ：就按照优先级的先后处理，非抢占式，不会停止当前正在执行的县城
2. 抢占式优先权调度算法：按照优先级，会暂停当前正在执行的线程
3. 高响应比优先调度算法：优先权会有变化：响应时间/要求服务时间,不断变化，对长作业会友好一些
- 进程调度算法： 基于时间片的轮转调度算法
1. 时间片轮转
2. 多级反馈队列调度算法



### 什么是CAS
- CAS（Compare And Swap/Set）比较并交换，CAS算法的过程是这样：它包含3个参数CAS(V,E,N)。V表示要更新的变量(内存值)，E表示预期值(旧的)，N表示新值。当且仅当V值等于E值时，才会将V的值设为N，如果V值和E值不同，则说明已经有其他线程做了更新，则当前线程什么都不做。最后，CAS返回当前V的真实值。
- CAS操作是抱着乐观的态度进行的(乐观锁)，它总是认为自己可以成功完成操作。当多个线程同时使用CAS操作一个变量时，只有一个会胜出，并成功更新，其余均会失败。失败的线程不会被挂起，仅是被告知失败，并且允许再次尝试，当然也允许失败的线程放弃操作。基于这样的原理，CAS操作即使没有锁，也可以发现其他线程对当前线程的干扰，并进行恰当的处理

### 什么是Atomic 锁自旋
- 当某个线程进入方法，执行其中的指令时，不会被其他线程打断，而别的线程就像自旋锁一样，一直等到该方法执行完成，才由JVM从等待队列中选择一个另一个线程进入，这只是一种逻辑上的理解。 相对于对于synchronized这种阻塞算法，CAS是非阻塞算法的一种常见实现。由于一般CPU切换时间比CPU指令集操作更加长， 所以J.U.C在性能上有了很大的提升。

### ABA问题
```text
CAS会导致“ABA问题”。
CAS算法实现一个重要前提需要取出内存中某时刻的数据，而在下时刻比较并替换，
那么在这个时间差类会导致数据的变化。 比如说一个线程one从内存位置V中取出A，
这时候另一个线程two也从内存中取出A，并且two进行了一些操作变成了B，
然后two又将V位置的数据变成A，这时候线程one进行CAS操作发现内存中仍然是A，
然后one操作成功。尽管线程one的CAS操作成功，但是不代表这个过程就是没有问题
的。毕竟值相同，但是中间经历了操作，从数据版本上来说是不一致的，不能够替换 

部分乐观锁的实现是通过版本号（version）的方式来解决ABA问题，乐观锁
每次在执行数据的修改操作时，都会带上一个版本号，一旦版本号和数据的版本号一致就可以执行修改操作并对版本号执行+1操作，否则就执行失败。
因为每次操作的版本号都会随之增加，所以不会出现ABA问题，因为版本号只会增加不会减少。
```

### 什么是AQS？抽象队列同步器
- AbstractQueuedSynchronizer类如其名，抽象的队列式的同步器，
- AQS定义了一套多线程访问共享资源的同步器框架，许多同步类实现都依赖于它，如常用的ReentrantLock/Semaphore/CountDownLatch
- 它维护了一个volatile int state（代表共享资源）和一个FIFO线程等待队列（多线程争用资源被阻塞时会进入此队列）
- 两种资源模式：Exclusive独占资源-ReentrantLock Exclusive（独占，只有一个线程能执行，如ReentrantLock） Share共享资源-Semaphore/CountDownLatch Share（共享，多个线程可同时执行，如Semaphore/CountDownLatch）。
- 同步器的实现是AQS核心（state资源状态计数）:
```text
以ReentrantLock为例，state初始化为0，表示未锁定状态。

A线程lock()时，会调用tryAcquire()独占该锁并将state+1

此后，其他线程再tryAcquire()时就会失败，直到A线程unlock()到state=0（即释放锁）为止,其它线程才有机会获取该锁

释放锁之前，A线程自己是可以重复获取此锁的（state会累加），这就是可重入的概念


以CountDownLatch以例，任务分为N个子线程去执行，state也初始化为N（注意N要与线程个数一致）。

这N个子线程是并行执行的，每个子线程执行完后countDown()一次，state会CAS减1。

等到所有子线程都执行完后(即state=0)，会unpark()主调用线程，然后主调用线程就会从await()函数返回，继续后余动作


```
- ReentrantReadWriteLock实现独占和共享两种方式:只需实现tryAcquire-tryRelease、tryAcquireShared-tryReleaseShared中的一种即可。但AQS也支持自定义同步器同时实现独占和共享两种方式，如ReentrantReadWriteLock。