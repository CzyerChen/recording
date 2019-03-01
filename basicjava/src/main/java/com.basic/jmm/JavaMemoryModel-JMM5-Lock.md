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
