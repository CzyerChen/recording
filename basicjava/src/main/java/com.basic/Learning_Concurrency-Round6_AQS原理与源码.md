### 什么是AQS
- AQS的全称为（AbstractQueuedSynchronizer），这个类在java.util.concurrent.locks包下面
- AQS是一个用来构建锁和同步器的框架
- 使用AQS能简单且高效地构造出应用广泛的大量的同步器，比如我们提到的ReentrantLock，Semaphore，其他的诸如ReentrantReadWriteLock，SynchronousQueue，FutureTask等等皆是基于AQS的
- 通过state对资源进行标记，资源空闲，请求线程就成为工作线程，如果资源非空闲，就会执行一套线程等待和唤醒的机制
- 可重入就是同一个线程对state进行累加，并递减的情况
- 将state变量“按位切割”切分成了两个部分，高16位表示读锁状态（读锁个数），低16位表示写锁状态（写锁个数）



### AQS的独占和共享
- 独占就是公平锁非公平锁通过队列排队或者是抢占的方式，占用一个资源，例如ReentrantLock
- 独占情况下， 只有一个线程能拥有资源，其他资源只能等到当前线程使用完毕之后才能够非公平的话，抢占获取（通过进入队列，获取超时放弃，再获取进去队列头部，这样循环往复），公平的话遵循队列顺序，牺牲了并发度

- 共享，例如如Semaphore、CountDownLatCh、 CyclicBarrier、ReadWriteLock
- 共享的话，主要是共同获取资源，严格按照队列去拿到资源，不足的进行等待，存在阻塞的可能，获取资源，使用完之后需要释放，并唤醒队列中的队友，这是一点不同之处


### 同步器
- 同步器的设计基于模板方法，通过抽象类定义方法，子类重写的方式
- 模板方法模式是基于”继承“的，主要是为了在不改变模板结构的前提下,在子类中重新定义模板中的内容,以实现复用代码
```text
isHeldExclusively()//该线程是否正在独占资源。只有用到condition才需要去实现它。
tryAcquire(int)//独占方式。尝试获取资源，成功则返回true，失败则返回false。
tryRelease(int)//独占方式。尝试释放资源，成功则返回true，失败则返回false。
tryAcquireShared(int)//共享方式。尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
tryReleaseShared(int)//共享方式。尝试释放资源，成功则返回true，失败则返回false。
```
- 信号量的实现可以是公平或者非公平的
```text
public Semaphore(int permits) {
        sync = new NonfairSync(permits);
    }

    public Semaphore(int permits, boolean fair) {
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
}
```
  
### 同步器的实现流程
- 流程：资源未占用，获取资源，标识工作线程，资源占用，获取资源失败，放入等待队列，准备资源排队或者抢占


###  AQS源码：acquire，作用在同步器的获取资源上
```text
public final void acquire(int arg) {
```
- 调用自定义同步器的tryAcquire()尝试直接去获取资源，如果成功则直接返回，不成功的话执行后面的acquireQueued
- tryAcquire(arg) 没成功，则addWaiter(),将该线程加入等待队列的尾部，并标记为独占模式,acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
- 如果线程在等待过程中被中断过，它是不响应的。只是获取资源后才再进行自我中断selfInterrupt()，将中断补上,selfInterrupt();
- 找一个ReentrantLock的tryAcquire看看实现，tryAcquire看一个非公平锁的实现nonfairTryAcquire
```text
final boolean nonfairTryAcquire(int acquires) {
//当前线程
    final Thread current = Thread.currentThread();
//获取当前资源的状态，可以说Lock所绑定的当前对象
    int c = getState();
//状态为0，说明资源空闲
    if (c == 0) {
//原子性修改资源的状态，这边就是底层调用unsafe进行原子性的修改
        if (compareAndSetState(0, acquires)) {
//把当前线程标志为工作线程，就是独占线程
            setExclusiveOwnerThread(current);
            return true;
        }
    }
//资源不空闲，被其他线程使用，并且是当前线程，可重入的概念，状态要递增，
//如果不是就不处理，也就是会被放入队列，队列中每个元素自己自旋来抢占资源
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}


//看到需要获取对应模式下的Node
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // Try the fast path of enq; backup to full enq on failure
    Node pred = tail;
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
//此方法用于将node加入队尾
    enq(node);
    return node;
}

//acquireQueued()使线程在等待队列中休息，有机会时（轮到自己，会被unpark()）会去尝试获取资源。
//获取到资源后才返回。如果在整个等待过程中被中断过，则返回true，否则返回false
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
//大家都在自选等待时机，抢占资源（非公平锁的情况下）
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
- acquire就是比较核心的资源抢占的实现，对于使用AQS作为同步器基础实现的类，都采取此种资源获取的形式来获取资源，例如ReentrantLock.lock()的流程，整个函数就是一条acquire(1)
  

### AQS源码：release
- 此方法是独占模式下线程释放共享资源的顶层入口，它会释放指定量的资源，如果彻底释放了（即state=0）,它会唤醒等待队列里的其他线程来获取资源，正是unlock()的语义
```text
public final boolean release(int arg) {
//tryRelase就是将status-1或者归零，如果归零的情况就把占用的线程也清空，接下来的事情就交给队列里面的线程进行抢占啦
    if (tryRelease(arg)) {
        Node h = head; // 找到头节点
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);//唤醒等待队列里的下一个线程
        return true;
    }
    return false;
}

/**
 * Wakes up node's successor, if one exists.
 *
 * @param node the node
 */
private void unparkSuccessor(Node node) {
    /*
     * If status is negative (i.e., possibly needing signal) try
     * to clear in anticipation of signalling.  It is OK if this
     * fails or if status is changed by waiting thread.
     */
    int ws = node.waitStatus;
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);

    /*
     * Thread to unpark is held in successor, which is normally
     * just the next node.  But if cancelled or apparently null,
     * traverse backwards from tail to find the actual
     * non-cancelled successor.
     */
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)
        LockSupport.unpark(s.thread);
}
```
- 用unpark()唤醒等待队列中最前边的那个未放弃线程s，再和acquireQueued()联系起来，s被唤醒后，进入if (p == head && tryAcquire(arg))的判断
- 即使p!=head也没关系，它会再进入shouldParkAfterFailedAcquire()寻找一个安全点。s已经是等待队列中最前边的那个未放弃线程，通过shouldParkAfterFailedAcquire()的调整，s也必然会跑到head的next结点，下一次自旋p==head就成立啦
- 然后s把自己设置成head标杆结点，表示自己已经获取到资源了，acquire()也返回了
```text
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    if (ws == Node.SIGNAL)
        /*
         * This node has already set status asking a release
         * to signal it, so it can safely park.
         */
        return true;
    if (ws > 0) {
        /*
         * Predecessor was cancelled. Skip over predecessors and
         * indicate retry.
         */
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
        /*
         * waitStatus must be 0 or PROPAGATE.  Indicate that we
         * need a signal, but don't park yet.  Caller will need to
         * retry to make sure it cannot acquire before parking.
         */
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
}
```

### AQS源码 AcquireShared
```text
public final void acquireShared(int arg) {
//tryAccquiredShared主要用于获取共享锁，如果当前没有线程获取资源，那么让当前线程获取并且将其表示为第一个线程
//如果不是第一个，是同一个线程firstReaderHoldCount数量累加，如果都不是，cachedHoldCounter累加
    if (tryAcquireShared(arg) < 0)
//负值代表获取失败；0代表获取成功，但没有剩余资源；正数表示获取成功，还有剩余资源，其他线程还可以去获取
//失败则通过doAcquireShared()进入等待队列，直到获取到资源为止才返回
        doAcquireShared(arg);
}


//reyAccquireShared 取自ReentrantReadWriteLock
protected final int tryAcquireShared(int unused) {

    Thread current = Thread.currentThread();
    int c = getState();
    if (exclusiveCount(c) != 0 &&
        getExclusiveOwnerThread() != current)
        return -1;
    int r = sharedCount(c);
    if (!readerShouldBlock() &&
        r < MAX_COUNT &&
        compareAndSetState(c, c + SHARED_UNIT)) {
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;
        } else if (firstReader == current) {
            firstReaderHoldCount++;
        } else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0)
                readHolds.set(rh);
            rh.count++;
        }
        return 1;
    }
    return fullTryAcquireShared(current);
}

//如果获取资源失败
private void doAcquireShared(int arg) {
    final Node node = addWaiter(Node.SHARED); //加入队列尾部
    boolean failed = true; //是否成功标志
    try {
        boolean interrupted = false; //等待过程中是否被中断过的标志

        for (;;) {
            final Node p = node.predecessor();//前驱
            if (p == head) { //如果到head的下一个，因为head是拿到资源的线程，此时node被唤醒，很可能是head用完资源来唤醒自己的

                int r = tryAcquireShared(arg);//尝试获取资源
                if (r >= 0) {//成功
                    setHeadAndPropagate(node, r);// //将head指向自己，还有剩余资源可以再唤醒之后的线程
                    p.next = null; // help GC
                    if (interrupted)
                        selfInterrupt();//如果等待过程中被打断过，此时将中断补上
                    return;
                }
            }

//判断状态，寻找安全点，进入waiting状态，等着被unpark()或interrupt()
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
- 共享模式下，需要说明，AQS严格按照入队的顺序进行唤醒，如果需要唤醒的资源不够，即使后续请求需要比较少的资源也不会被唤醒，需要等到资源充足，将下一个请求唤醒才会继续下去


### AQS源码 ReleaseShared
```text
public final boolean releaseShared(int arg) {
    if (tryReleaseShared(arg)) {//尝试释放资源成功
        doReleaseShared();//唤醒后续线程
        return true;
    }
    return false;
}


//tryReleaseShared
protected final boolean tryReleaseShared(int unused) {
    Thread current = Thread.currentThread();
    if (firstReader == current) {//是当前线程，是firstReader
        // assert firstReaderHoldCount > 0;
        if (firstReaderHoldCount == 1)
            firstReader = null;
        else
            firstReaderHoldCount--;
    } else {//不是firstReader,从缓存中删除
        HoldCounter rh = cachedHoldCounter;
        if (rh == null || rh.tid != getThreadId(current))
            rh = readHolds.get();
        int count = rh.count;
        if (count <= 1) {
            readHolds.remove();
            if (count <= 0)
                throw unmatchedUnlockException();
        }
        --rh.count;
    }
//自旋，保证status的修改
    for (;;) {
        int c = getState();
        int nextc = c - SHARED_UNIT;
        if (compareAndSetState(c, nextc))
            // Releasing the read lock has no effect on readers,
            // but it may allow waiting writers to proceed if
            // both read and write locks are now free.
            return nextc == 0;
    }
}

private void doReleaseShared() {
       for (;;) {//自旋
        Node h = head;//获取队列中的头节点，按照队列顺序严格回收
        if (h != null && h != tail) {
            int ws = h.waitStatus;
            if (ws == Node.SIGNAL) {//节点需要被唤醒
                if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))//改status
                    continue;            // loop to recheck cases
//没有失败，就唤醒节点
                unparkSuccessor(h);//唤醒head节点
            }
            else if (ws == 0 &&
                     !compareAndSetWaitStatus(h, 0, Node.PROPAGATE ))//传播
                continue;                // loop on failed CAS
        }
        if (h == head)                   // loop if head changed
            break;
    }
}
```


