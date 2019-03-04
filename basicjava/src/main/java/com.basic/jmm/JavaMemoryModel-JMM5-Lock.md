### 1.锁的释放 - 获取建立的 happens before 关系
- 锁除了让临界区互斥执行外，还可以让释放锁的线程向获取同一个锁的线程发送消息
```text
class MonitorExample {
    int a = 0;

    public synchronized void writer() {  //1
        a++;                             //2
    }                                    //3

    public synchronized void reader() {  //4
        int i = a;                       //5
        ……
    }                                    //6
}
```
假设线程 A 执行 writer() 方法，随后线程 B 执行 reader() 方法。根据 happens before 规则，这个过程包含的 happens before 关系可以分为两类：
1. 根据程序次序规则，1 happens before 2, 2 happens before 3; 4 happens before 5, 5 happens before 6。
2. 根据监视器锁规则，3 happens before 4。
3. 根据 happens before 的传递性，2 happens before 5。

### 2. 锁释放和获取的内存语义
- 锁释放与 volatile 写有相同的内存语义；锁获取与 volatile 读有相同的内存语义
- 锁的释放与获取：
    - 线程 A 释放一个锁，实质上是线程 A 向接下来将要获取这个锁的某个线程发出了（线程 A 对共享变量所做修改的）消息。
    - 线程 B 获取一个锁，实质上是线程 B 接收了之前某个线程发出的（在释放这个锁之前对共享变量所做修改的）消息。
    - 线程 A 释放锁，随后线程 B 获取这个锁，这个过程实质上是线程 A 通过主内存向线程 B 发送消息

### 3.锁内存语义的实现
- 接下来通过ReentrantLock 的源代码，来学习以下内存语义的具体实现
```text
class ReentrantLockExample {
int a = 0;
ReentrantLock lock = new ReentrantLock();

public void writer() {
    lock.lock();         // 获取锁 
    try {
        a++;
    } finally {
        lock.unlock();  // 释放锁 
    }
}

public void reader () {
    lock.lock();        // 获取锁 
    try {
        int i = a;
        ……
    } finally {
        lock.unlock();  // 释放锁 
    }
}
}
```
- volatile对于并发中用处很大，ReentrantLock 的实现依赖于 java 同步器框架 AbstractQueuedSynchronizer（本文简称之为 AQS）。AQS 使用一个整型的 volatile 变量（命名为 state）来维护同步状态
```text
                 |----------------------------------|
                 |     AbstractQueuedSynchronizer   |
                 |   private volatile int state     |
                 |          acquire(int i )         |
                 |          release(int i )         |
                 |----------------------------------|
                                  /|\
                                   |
                 |----------------------------------|
                 |                Sync              |
                 |          tryRelease( int i)      |
                 |      nonfairTryAcquire(int i )   |
                                  /|\
                                   |
             |--------------------------------------------|
  |---------------------|                        |-------------------|
  |     FairSync        |                        |     NonfairSync   |
  |      lock()         |                        |      lock()       |
  | tryAcquire(int i )  |                        | tryAcquire(int i) |
  |---------------------|                        |-------------------|
            /|\                                           /|\
             |---------------------------------------------|
                                    |
                      |----------------------------|
                      |       ReetrantLock         |
                      |  private final Sync sync   |
                      |          lock()            |
                      |         unlock()           |
                      |----------------------------|
```
#### 3.1.解析公平锁
1.RetrantLock:lock()
2.FairSync:lock()
3.AbstractQueuedSynchronizer : acquire(int arg)
4.ReentrantLock : tryAcquire(int acquires)
5.第四步开始执行锁：下面的C就是volatile变量
```text
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();   // 获取锁的开始，首先读 volatile 变量 state
    if (c == 0) {
        if (isFirst(current) &&
            compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)  
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```
6.解锁时：
7.ReentrantLock : unlock()
8.AbstractQueuedSynchronizer : release(int arg)
9.Sync : tryRelease(int releases)
10.第9步释放锁,会将state更新
```text
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    if (c == 0) {
        free = true;
        setExclusiveOwnerThread(null);
    }
    setState(c);           // 释放锁的最后，写 volatile 变量 state
    return free;
}
```
- 根据 volatile 的 happens-before 规则，释放锁的线程在写 volatile 变量之前可见的共享变量，在获取锁的线程读取同一个 volatile 变量后将立即变的对获取锁的线程可见
#### 3.2.非公平锁中的内存语义
- 非公平锁的释放和公平锁是一致的，接下来我们看一下加锁部分
1.ReentrantLock : lock()
2.NonfairSync : lock()
3.AbstractQueuedSynchronizer : compareAndSetState(int expect, int update)
4.第三步和公平锁不同：
```text
protected final boolean compareAndSetState(int expect, int update) {
    return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
}
```
5.这边的unsafe就是由JVM去执行操作系统的相关指令，CAS


#### 3.3.CAS的内存语义，如何同时具有volatile 读和volatile 写的双重语义
- 提要:编译器不会对 volatile 读与 volatile 读后面的任意内存操作重排序；编译器不会对 volatile 写与 volatile 写前面的任意内存操作重排序
- CAS同时实现 volatile 读和 volatile 写的内存语义，也就是说，编译器不能对 CAS 与 CAS 前面和后面的任意内存操作重排序
- 下面分析在常见的 intel x86 处理器中，CAS 是如何同时具有 volatile 读和 volatile 写的内存语义的
1.sun.misc.Unsafe 类的 compareAndSwapInt() 方法的源代码
````text
public final native boolean compareAndSwapInt(Object o, long offset,
                                              int expected,
                                              int x);
````
2.这个本地方法在 openjdk 中依次调用的 c++ 代码为：unsafe.cpp，atomic.cpp 和 atomicwindowsx86.inline.hpp。这个本地方法的最终实现在 openjdk 的如下位置：openjdk-7-fcs-src-b147-27jun2011\openjdk\hotspot\src\oscpu\windowsx86\vm\ atomicwindowsx86.inline.hpp（对应于 windows 操作系统，X86 处理器）
3.对应于 intel x86 处理器的源代码的片段
```text
// Adding a lock prefix to an instruction on MP machine
// VC++ doesn't like the lock prefix to be on a single line
// so we can't insert a label after the lock prefix.
// By emitting a lock prefix, we can define a label after it.
#define LOCK_IF_MP(mp) __asm cmp mp, 0  \
                       __asm je L0      \
                       __asm _emit 0xF0 \
                       __asm L0:

inline jint     Atomic::cmpxchg    (jint     exchange_value, volatile jint*     dest, jint     compare_value) {
  // alternative for InterlockedCompareExchange
  int mp = os::is_MP();
  __asm {
    mov edx, dest
    mov ecx, exchange_value
    mov eax, compare_value
    LOCK_IF_MP(mp)
    cmpxchg dword ptr [edx], ecx
  }
}
```
4.程序会根据当前处理器的类型来决定是否为 cmpxchg 指令添加 lock 前缀。如果程序是在多处理器上运行，就为 cmpxchg 指令加上 lock 前缀（lock cmpxchg）。反之，如果程序是在单处理器上运行，就省略 lock 前缀
5.intel 的手册对 lock 前缀的说明如下
  - 确保对内存的读 - 改 - 写操作原子执行
  - 禁止该指令与之前和之后的读和写指令重排序
  - 把写缓冲区中的所有数据刷新到内存中
  - 以上下面两点就是内存屏障的效果，和volatile 读 和volatile 写的内存一致
  - 以上就说明了CAS 同时具有volatile 读 和volatile 写的内存语义

#### 3.4.公平锁和非公平锁 加锁/释放锁
1. 关于锁的释放，都是最后更新volatile变量
2. 公平锁加锁的时候，首先读取volatile变量
3. 非公平锁加锁，最后通过CAS更新volatile变量，利用了双重 volatile 读和 volatile 写的内存语义

#### 3.5.从JAVA内存语义看concurrent并发包
- 以上分析了Java 中CAS 同时具有双重 volatile 读和 volatile 写的内存语义，那Java线程中的通信就有了以下4中简化方式
1. A 线程写 volatile 变量，随后 B 线程读这个 volatile 变量
2. A 线程写 volatile 变量，随后 B 线程用 CAS 更新这个 volatile 变量
3. A 线程用 CAS 更新一个 volatile 变量，随后 B 线程用 CAS 更新这个 volatile 变量
4. A 线程用 CAS 更新一个 volatile 变量，随后 B 线程读这个 volatile 变量

- 因为总结下来，并发包里面能发现很多以下操作
1. 首先，声明共享变量为 volatile
2. 然后，使用 CAS 的原子条件更新来实现线程之间的同步
3. 同时，配合以 volatile 的读 / 写和 CAS 所具有的 volatile 读和写的内存语义来实现线程之间的通信
```text
|---------------------------------------------------------------------------|
|                                                                           |
|   (Lock)      (同步器)     （阻塞队列）     （执行器）      （并发容器）      |
|                                                                           |
|---------------------------------------------------------------------------|
                 /|\               /|\                /|\
                  |                 |                  |
        |----------------------------------------------------|
        |                                                    |       
        |     (AQS)     (非阻塞数据结构)   （原子变量类）       |
        |                                                    |
        |----------------------------------------------------|
                         /|\             /|\
                          |               |
                |-----------------------------------|
                |                                   |
                |  （volatile 变量的读写） （CAS）    |
                |                                   |
                |-----------------------------------|

```

### 4.不同类别的锁
- synchronized锁：synchronized用的锁是存在Java对象头里的
    - JVM基于进入和退出Monitor对象来实现方法同步和代码块同步。代码块同步是使用monitorenter和monitorexit指令实现的，monitorenter指令是在编译后插入到同步代码块的开始位置，而monitorexit是插入到方法结束处和异常处。任何对象都有一个monitor与之关联，当且一个monitor被持有后，它将处于锁定状态
    - 根据虚拟机规范的要求，在执行monitorenter指令时，首先要去尝试获取对象的锁，如果这个对象没被锁定，或者当前线程已经拥有了那个对象的锁，把锁的计数器加1；相应地，在执行monitorexit指令时会将锁计数器减1，当计数器被减到0时，锁就释放了。如果获取对象锁失败了，那当前线程就要阻塞等待，直到对象锁被另一个线程释放为止
    - synchronized同步快对同一条线程来说是可重入的，不会出现自己把自己锁死的问题
    - 同步块在已进入的线程执行完之前，会阻塞后面其他线程的进入
    - synchronized是Java语言中的一个重量级操作
    

- Mutex Lock
    - 监视器锁（Monitor）本质是依赖于底层的操作系统的Mutex Lock（互斥锁）来实现的
    - 每个对象都对应于一个可称为" 互斥锁" 的标记，这个标记用来保证在任一时刻，只能有一个线程访问该对象。
    - 互斥锁：用于保护临界区，确保同一时间只有一个线程访问数据。对共享资源的访问，先对互斥量进行加锁，如果互斥量已经上锁，调用线程会阻塞，直到互斥量被解锁。在完成了对共享资源的访问后，要对互斥量进行解锁
    - 由于Java的线程是映射到操作系统的原生线程之上的，如果要阻塞或唤醒一条线程，都需要操作系统来帮忙完成，这就需要从用户态转换到核心态中，因此状态转换需要耗费很多的处理器时间
    - ReentrantLock只是提供了synchronized更丰富的功能，而不一定有更优的性能，所以在synchronized能实现需求的情况下，优先考虑使用synchronized来进行同步

### 5. 锁优化
#### 5.1 synchronized
- synchronized是通过对象内部的一个叫做监视器锁（monitor）来实现的，监视器锁本质又是依赖于底层的操作系统的Mutex Lock（互斥锁）来实现的
- 需要阻塞和通知，因而需要用户态和内核态的切换，消耗较大，效率较低的
- 依赖于操作系统Mutex Lock(互斥锁)所实现的锁我们称之为“重量级锁”

#### 5.2 锁的级别
- 锁一共有4种状态，级别从低到高依次是：无锁状态、偏向锁状态、轻量级锁状态和重量级锁状态。锁可以升级但不能降级

#### 5.3 偏向锁
- 偏向锁是为了在只有一个线程执行同步块时提高性能，明明只有一个线程占有着同步代码块，但是每一次还是要使用CAS去获取对象锁，就增加了判断的消耗
- 当一个线程访问同步块并获取锁时，会在对象头和栈帧中的锁记录里存储锁偏向的线程ID，以后该线程在进入和退出同步块时不需要进行CAS操作来加锁和解锁，只需简单地测试一下对象头的Mark Word里是否存储着指向当前线程的偏向锁
- 引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径，因为轻量级锁的获取及释放依赖多次CAS原子指令，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令
- 锁的获取过程：
（1）访问Mark Word中偏向锁的标识是否设置成1，锁标志位是否为01——确认为可偏向状态。

（2）如果为可偏向状态，则测试线程ID是否指向当前线程，如果是，进入步骤（5），否则进入步骤（3）。

（3）如果线程ID并未指向当前线程，则通过CAS操作竞争锁。如果竞争成功，则将Mark Word中线程ID设置为当前线程ID，然后执行（5）；如果竞争失败，执行（4）。

（4）如果CAS获取偏向锁失败，则表示有竞争（CAS获取偏向锁失败说明至少有过其他线程曾经获得过偏向锁，因为线程不会主动去释放偏向锁）。当到达全局安全点（safepoint）时，会首先暂停拥有偏向锁的线程，然后检查持有偏向锁的线程是否活着（因为可能持有偏向锁的线程已经执行完毕，但是该线程并不会主动去释放偏向锁），如果线程不处于活动状态，则将对象头设置成无锁状态（标志位为“01”），然后重新偏向新的线程；如果线程仍然活着，撤销偏向锁后升级到轻量级锁状态（标志位为“00”），此时轻量级锁由原持有偏向锁的线程持有，继续执行其同步代码，而正在竞争的线程会进入自旋等待获得该轻量级锁。

（5）执行同步代码。

- 锁的释放过程：

    - 偏向锁使用了一种等到竞争出现才释放偏向锁的机制：偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动去释放偏向锁。偏向锁的撤销，需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，判断锁对象是否处于被锁定状态，撤销偏向锁后恢复到未锁定（标志位为“01”）或轻量级锁（标志位为“00”）的状态。

- 关闭偏向锁:
    - 偏向锁在Java 6和Java 7里是默认启用的。由于偏向锁是为了在只有一个线程执行同步块时提高性能，如果你确定应用程序里所有的锁通常情况下处于竞争状态，可以通过JVM参数关闭偏向锁：-XX:-UseBiasedLocking=false，那么程序默认会进入轻量级锁状态

#### 5.4 轻量级锁
- 轻量级锁是为了在线程近乎交替执行同步块时提高性能
- 加锁过程：
    - 1.在代码进入同步块的时候，如果同步对象锁状态为无锁状态（锁标志位为“01”状态，是否为偏向锁为“0”），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储锁对象目前的Mark Word的拷贝，官方称之为 Displaced Mark Word
    - 2.拷贝对象头中的Mark Word复制到锁记录中
    - 3.拷贝成功后，虚拟机将使用CAS操作尝试将对象的Mark Word更新为指向Lock Record的指针，并将Lock record里的owner指针指向object mark word。如果更新成功，则执行步骤（4），否则执行步骤（5）
    - 4.如果这个更新动作成功了，那么这个线程就拥有了该对象的锁，并且对象Mark Word的锁标志位设置为“00”，即表示此对象处于轻量级锁定状态
    - 5.如果这个更新操作失败了，虚拟机首先会检查对象的Mark Word是否指向当前线程的栈帧，如果是就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行。否则说明多个线程竞争锁，若当前只有一个等待线程，则可通过自旋稍微等待一下，可能另一个线程很快就会释放锁。 但是当自旋超过一定的次数，或者一个线程在持有锁，一个在自旋，又有第三个来访时，轻量级锁膨胀为重量级锁，重量级锁使除了拥有锁的线程以外的线程都阻塞，防止CPU空转，锁标志的状态值变为“10”，Mark Word中存储的就是指向重量级锁（互斥量）的指针，后面等待锁的线程也要进入阻塞状态
    
- 解锁过程：
    - 1.通过CAS操作尝试把线程中复制的Displaced Mark Word对象替换当前的Mark Word
    - 2.如果替换成功，整个同步过程就完成了
    - 3.如果替换失败，说明有其他线程尝试过获取该锁（此时锁已膨胀），那就要在释放锁的同时，唤醒被挂起的线程

#### 5.5 偏向锁、轻量级锁、重量级锁的转换
- 偏向所锁，轻量级锁都是乐观锁，重量级锁是悲观锁
- 1.一个对象刚开始实例化的时候，没有任何线程来访问它的时候。它是可偏向的，意味着，它现在认为只可能有一个线程来访问它，所以当第一个线程来访问它的时候，它会偏向这个线程，此时，对象持有偏向锁。偏向第一个线程，这个线程在修改对象头成为偏向锁的时候使用CAS操作，并将对象头中的ThreadID改成自己的ID，之后再次访问这个对象时，只需要对比ID，不需要再使用CAS在进行操作
- 2.一旦有第二个线程访问这个对象，因为偏向锁不会主动释放，所以第二个线程可以看到对象时偏向状态，这时表明在这个对象上已经存在竞争了。检查原来持有该对象锁的线程是否依然存活，如果挂了，则可以将对象变为无锁状态，然后重新偏向新的线程。如果原来的线程依然存活，则马上执行那个线程的操作栈，检查该对象的使用情况，如果仍然需要持有偏向锁，则偏向锁升级为轻量级锁，（偏向锁就是这个时候升级为轻量级锁的），此时轻量级锁由原持有偏向锁的线程持有，继续执行其同步代码，而正在竞争的线程会进入自旋等待获得该轻量级锁；如果不存在使用了，则可以将对象回复成无锁状态，然后重新偏向
- 3.轻量级锁认为竞争存在，但是竞争的程度很轻，一般两个线程对于同一个锁的操作都会错开，或者说稍微等待一下（自旋），另一个线程就会释放锁。 但是当自旋超过一定的次数，或者一个线程在持有锁，一个在自旋，又有第三个来访时，轻量级锁膨胀为重量级锁，重量级锁使除了拥有锁的线程以外的线程都阻塞，防止CPU空转
```text
                                     (分配对象)
                                         |
                -----------------------------------------------------
                |                                                   |
        如果偏向锁可用                                         如果偏向锁不可用
      |----------------------|                             |-------------------|
      | 0 |epoch |age | 1| 01|                             |hashcode | age|0|01|<---------------|
      |----------------------|            |----------->    |-------------------|                |
     未锁定、未偏向但是可偏向的对象          |                 未锁定、不可偏向的对象                 |
          |            /|\                |                          |                          |
       初始锁定         重偏向             |(如果对象未锁定)         轻量级锁定                     |
         \|/            |                 |(撤销偏向)                \|/     --->递归锁定         |
      |-----------------------|           |                |--------------------------|         |
      |threadID|epoch|age|0|01|-----------|------------>   | pointer to lock Record|00|---------|  解锁
      |-----------------------|             对象已锁定      |--------------------------|         |
       已偏向、锁定或未锁定的对象                                  被轻量级锁定的对象                |
           ----->锁定/解锁                                              |                        |
                                                                      膨胀                      |
                                                                      \|/                       |   
                                                       |-----------------------------------|    |
                                                       | pointer to heavyweight monitor |10| ---|
                                                       |-----------------------------------|
                                                             被重量级锁定的对象          
```

#### 5.6 锁消除
- 锁消除即删除不必要的加锁操作。虚拟机即时编辑器在运行时，对一些“代码上要求同步，但是被检测到不可能存在共享数据竞争”的锁进行消除
- 根据代码逃逸技术，如果判断到一段代码中，堆上的数据不会逃逸出当前线程，那么可以认为这段代码是线程安全的，不必要加锁

#### 5.7 锁粗化
- 如果一系列的连续操作都对同一个对象反复加锁和解锁，甚至加锁操作是出现在循环体中的，那即使没有出现线程竞争，频繁地进行互斥同步操作也会导致不必要的性能损耗
- 如果虚拟机检测到有一串零碎的操作都是对同一对象的加锁，将会把加锁同步的范围扩展（粗化）到整个操作序列的外部

#### 5.8 自旋锁和自适应锁
- 引入自旋锁的原因：因为普通锁互斥同步会造成阻塞，挂起和恢复线程都需要内核态和用户态之间的切换，并且共享数据的锁定一般只持续一小段时间，加锁的操作消耗太大
- 自旋锁：让该线程执行一段无意义的忙循环（自旋）等待一段时间，不会被立即挂起（自旋不放弃处理器额执行时间），看持有锁的线程是否会很快释放锁。自旋锁在JDK 1.4.2中引入，默认关闭，但是可以使用-XX:+UseSpinning开开启；在JDK1.6中默认开启
- 自旋锁的缺点：
    - 自旋等待不能替代阻塞，虽然它可以避免线程切换带来的开销，但是它占用了处理器的时间
    - 如果持有锁的线程很快就释放了锁，那么自旋的效率就非常好
    - 反之，自旋的线程就会白白消耗掉处理器的资源，它不会做任何有意义的工作，这样反而会带来性能上的浪费
    - 所以说，自旋等待的时间（自旋的次数）必须要有一个限度，例如让其循环10次，如果自旋超过了定义的时间仍然没有获取到锁，则应该被挂起（进入阻塞状态）
    - 通过参数-XX:PreBlockSpin可以调整自旋次数，默认的自旋次数为10

- 自适应的自旋锁：
    - JDK1.6引入自适应的自旋锁，自适应就意味着自旋的次数不再是固定的，它是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定
    - 如果在同一个锁的对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也很有可能再次成功，进而它将允许自旋等待持续相对更长的时间
    - 如果对于某个锁，自旋很少成功获得过，那在以后要获取这个锁时将可能省略掉自旋过程，以避免浪费处理器资源
    - 简单来说，就是线程如果自旋成功了，则下次自旋的次数会更多，如果自旋失败了，则自旋的次数就会减少 
    
- 自旋锁使用场景：从轻量级锁获取的流程中我们知道，当线程在获取轻量级锁的过程中执行CAS操作失败时，是要通过自旋来获取重量级锁的


