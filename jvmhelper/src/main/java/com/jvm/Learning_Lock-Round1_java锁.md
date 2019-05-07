> 锁的内容有很多：悲观锁 乐观锁 自旋锁 可重入锁 公平非公平锁 锁的升级。。。。

### 乐观锁 -- 读多写少，并发写可能性低 --CAS
- 直接通过CAS去进行内容的设置，调用unsafe类调用操作系统底层的CAS指令来实现
- 这个set是一个原子性操作，但是由于拿到值并操作的过程中不能确定是否有别的写入操作，如果有set操作就会失败

### 悲观锁 -- 写多 并发写可能性高 -- Synchronized
- 通过对操作对象进行加锁，保证一次只有一个线程在操作该对象，如果有多方需要获取该对象并操作就需要在队列中等待
- AQS框架下的锁则是先尝试cas乐观锁去获取锁，获取不到，才会转换为悲观锁，如RetreenLock

### 自旋锁 -- 锁竞争不激烈的情况
- 如果持有锁的线程能在很短时间内释放锁资源，那么那些等待竞争锁的线程就不需要做内核态和用户态之间的切换进入阻塞挂起状态，避免用户线程和内核的切换的消耗
- 锁的自选大量消耗了CPU，不断循环，自旋锁需要设置一个最长时间，时间到了自旋锁会进入阻塞阶段
- 当锁资源竞争不激烈，可以通过锁的自选减少两次上下文的切换，如果竞争激烈，显然自旋锁不是一个好方案
- jdk1.6中，jdk1.7开始又废除了，由JVM统一管理。
- 引入适应性自旋锁，适应性自旋锁意味着自旋的时间不在是固定的了，而是由前一次在同一个锁上的自旋时间以及锁的拥有者的状态来决定，基本认为一个线程上下文切换的时间是最佳的一个时间
- 同时JVM还针对当前CPU的负荷情况做了较多的优化，如果平均负载小于CPUs则一直自旋，如果有超过(CPUs/2)个线程正在自旋，则后来线程直接阻塞，如果正在自旋的线程发现Owner发生了变化则延迟自旋时间（自旋计数）或进入阻塞，如果CPU处于节电模式则停止自旋，自旋时间的最坏情况是CPU的存储延迟（CPU A存储了一个数据，到CPU B得知这个数据直接的时间差），自旋时会适当放弃线程优先级之间的差异
- 

### Synchronized同步锁  ---属于独占式的悲观锁，同时属于可重入锁
- 作用范围
```text
1. 作用于方法时，锁住的是对象的实例(this)； 

2. 当作用于静态方法时，锁住的是Class实例，又因为Class的相关数据存储在永久带PermGen（jdk1.8则是metaspace），永久带是全局共享的，因此静态方法锁相当于类的一个全局锁，会锁所有调用该方法的线程； 

3. synchronized作用于一个对象实例时，锁住的是所有以该对象为锁的代码块。它有多个队列，当多个线程一起访问某个对象监视器的时候，对象监视器会将这些线程存储在不同的容器中。
```
- 核心组件
```text
1) Wait Set：哪些调用wait方法被阻塞的线程被放置在这里； 

2) Contention List：竞争队列，所有请求锁的线程首先被放在这个竞争队列中； 

3) Entry List：Contention List中那些有资格成为候选资源的线程被移动到Entry List中； 

4) OnDeck：任意时刻，最多只有一个线程正在竞争锁资源，该线程被成为OnDeck； 

5) Owner：当前已经获取到所资源的线程被称为Owner； 

6) !Owner：当前释放锁的线程


    |------------WaitingQueue---------------|                Ready Thread
    |                                       |
（1）| ContentionList --（2）--> EntryList   |---（3）--->       OnDeck 一个线程竞争锁资源
    |  竞争队列                   候选线程    |                     |
    |---------------------------------------|                     |（4） 
                    /|\                                          \|/
                     | (6)                                    Running Thead
         |----Blocking Queue -----|                            Owner  已经获取资源的线程
         |       Wait Set         |               <------(5)------|
         |------------------------|
```  
- 锁资源竞争的流程
```text
1. JVM每次从队列的尾部取出一个数据用于锁竞争候选者（OnDeck），
但是并发情况下，ContentionList会被大量的并发线程进行CAS访问，
为了降低对尾部元素的竞争，JVM会将一部分线程移动到EntryList中作为候选竞争线程。 

2. Owner线程会在unlock时，将ContentionList中的部分线程迁移到EntryList中，
并指定EntryList中的某个线程为OnDeck线程（一般是最先进去的那个线程）。 

3. Owner线程并不直接把锁传递给OnDeck线程，而是把锁竞争的权利交给OnDeck，OnDeck需要重新竞争锁。
这样虽然牺牲了一些公平性，但是能极大的提升系统的吞吐量，在JVM中，也把这种选择行为称之为“竞争切换”。 

4. OnDeck线程获取到锁资源后会变为Owner线程，而没有得到锁资源的仍然停留在EntryList中。
如果Owner线程被wait方法阻塞，则转移到WaitSet队列中，直到某个时刻通过notify或者notifyAll唤醒，会重新进去EntryList中。 

5. 处于ContentionList、EntryList、WaitSet中的线程都处于阻塞状态，该阻塞是由操作系统来完成的（Linux内核下采用pthread_mutex_lock内核函数实现的）

6. Synchronized是非公平锁。 Synchronized在线程进入ContentionList时，等待的线程会先尝试自旋获取锁，
如果获取不到就进入ContentionList，这明显对于已经进入队列的线程是不公平的，
还有一个不公平的事情就是自旋获取锁的线程还可能直接抢占OnDeck线程的锁资源。 

7. 每个对象都有个monitor对象，加锁就是在竞争monitor对象，代码块加锁是在前后分别加上monitorenter和monitorexit指令来实现的，方法加锁是通过一个标记位来判断的 

8. synchronized是一个重量级操作，需要调用操作系统相关接口，性能是低效的，有可能给线程加锁消耗的时间比有用操作消耗的时间更多。 

9. Java1.6，synchronized进行了很多的优化，有适应自旋、锁消除、锁粗化、轻量级锁及偏向锁等，效率有了本质上的提高。
在之后推出的Java1.7与1.8中，均对该关键字的实现机理做了优化。引入了偏向锁和轻量级锁。都是在对象头中有标记位，不需要经过操作系统加锁。 

10. 锁可以从偏向锁升级到轻量级锁，再升级到重量级锁。这种升级过程叫做锁膨胀； 

11. JDK 1.6中默认是开启偏向锁和轻量级锁，可以通过-XX:-UseBiasedLocking来禁用偏向锁
```

#### 锁膨胀:偏向锁、轻量级锁、重量级锁的转换
- 偏向所锁，轻量级锁都是乐观锁，重量级锁是悲观锁
1.一个对象刚开始实例化的时候，没有任何线程来访问它的时候,它是可偏向的
- 意味着，它现在认为只可能有一个线程来访问它，所以当第一个线程来访问它的时候，它会偏向这个线程，此时，对象持有偏向锁
- 偏向第一个线程，这个线程在修改对象头成为偏向锁的时候使用CAS操作，并将对象头中的ThreadID改成自己的ID
- 之后再次访问这个对象时，只需要对比ID，不需要再使用CAS在进行操作

2.一旦有第二个线程访问这个对象
- 因为偏向锁不会主动释放，所以第二个线程可以看到对象时偏向状态，这时表明在这个对象上已经存在竞争了
- 检查原来持有该对象锁的线程是否依然存活，如果挂了，则可以将对象变为无锁状态，然后重新偏向新的线程
- 如果原来的线程依然存活，则马上执行那个线程的操作栈，检查该对象的使用情况
- 如果仍然需要持有偏向锁，则偏向锁升级为轻量级锁，（偏向锁就是这个时候升级为轻量级锁的），此时轻量级锁由原持有偏向锁的线程持有，继续执行其同步代码，而正在竞争的线程会进入自旋等待获得该轻量级锁
- 如果不存在使用了，则可以将对象回复成无锁状态，然后重新偏向

3.轻量级锁认为竞争存在，但是竞争的程度很轻，一般两个线程对于同一个锁的操作都会错开，或者说稍微等待一下（自旋），另一个线程就会释放锁
 - 但是当自旋超过一定的次数，或者一个线程在持有锁，一个在自旋，又有第三个来访时，轻量级锁膨胀为重量级锁
 - 重量级锁使除了拥有锁的线程以外的线程都阻塞，防止CPU空转


### ReentrantLock -- 继承接口Lock并实现了接口中定义的方法，是一种可重入锁
- 除了能完成synchronized所能完成的所有工作外，还提供了诸如可响应中断锁、可轮询锁请求、定时锁等避免多线程死锁的方法
- Lock接口的方法有：
```text
1. void lock(): 执行此方法时, 如果锁处于空闲状态, 当前线程将获取到锁. 
相反, 如果锁已经被其他线程持有, 将禁用当前线程, 直到当前线程获取到锁. 

2. boolean tryLock()：如果锁可用, 则获取锁, 并立即返回true, 否则返回false. 
该方法和lock()的区别在于, tryLock()只是"试图"获取锁, 如果锁不可用, 不会导致当前线程被禁用, 当前线程仍然继续往下执行代码. 
而lock()方法则是一定要获取到锁, 如果锁不可用, 就一直等待, 在未获得锁之前,当前线程并不继续向下执行. 

3. void unlock()：执行此方法时, 当前线程将释放持有的锁. 锁只能由持有者释放, 如果线程并不持有锁, 却执行该方法, 可能导致异常的发生. 

4. Condition newCondition()：条件对象，获取等待通知组件。该组件和当前的锁绑定，当前线程只有获取了锁，
才能调用该组件的await()方法，而调用后，当前线程将缩放锁。 

5. getHoldCount() ：查询当前线程保持此锁的次数，也就是执行此线程执行lock方法的次数。

6. getQueueLength（）：返回正等待获取此锁的线程估计数，比如启动10个线程，1个线程获得锁，此时返回的是9 

7. getWaitQueueLength：（Condition condition）返回等待与此锁相关的给定条件的线程估计数。
比如10个线程，用同一个condition对象，并且此时这10个线程都执行了condition对象的await方法，那么此时执行此方法返回10 

8. hasWaiters(Condition condition)：查询是否有线程等待与此锁有关的给定条件(condition)，
对于指定contidion对象，有多少线程执行了condition.await方法 

9. hasQueuedThread(Thread thread)：查询给定线程是否等待获取此锁 

10. hasQueuedThreads()：是否有线程等待此锁 

11. isFair()：该锁是否公平锁

12. isHeldByCurrentThread()： 当前线程是否保持锁锁定，线程的执行lock方法的前后分别是false和true 

13. isLock()：此锁是否有任意线程占用 

14. lockInterruptibly（）：如果当前线程未被中断，获取锁 

15. tryLock（）：尝试获得锁，仅在调用时锁未被线程占用，获得锁 

16. tryLock(long timeout TimeUnit unit)：如果锁在给定等待时间内没有被另一个线程保持，则获取该锁。

```

### 公平锁/非公平锁
- JVM会按照随机、就近原则分配锁的机制称为不公平锁，非公平锁是默认的ReentrantLock锁方式，效率比公平锁高很多，完全依赖进程做锁资源的竞争
- 非公平锁会比公平锁的效率提高十几倍
- Java中的synchronized是非公平锁，ReentrantLock 默认的lock()方法采用的是非公平锁

### ReentrantLock 与synchronized 
1. ReentrantLock通过方法lock()与unlock()来进行加锁与解锁操作，与synchronized会被JVM自动解锁机制不同，ReentrantLock加锁后需要手动进行解锁。为了避免程序出现异常而无法正常解锁的情况，使用ReentrantLock必须在finally控制块中进行解锁操作。
2. ReentrantLock相比synchronized的优势是可中断、公平锁、多个锁。这种情况下需要使用ReentrantLock。
```text
public class ReentrantLockTest {
    private Lock lock = new ReentrantLock();//默认参数false,是非公平锁
    private Condition condition = lock.newCondition();//创建Condition

    /**
     * await signal 
     * wait notify
     */
    public  void test(){
        try{
            lock.lock();
            //1 wait
            System.out.print("尝试wait");
            condition.await();
            //2. 使用signal唤醒
            condition.signal();
            for (int i = 0; i < 5; i++) { 
                System.out.println("ThreadName=" + Thread.currentThread().getName()+ (" " + (i + 1))); 
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
```

### 信号量 Semaphore
- Semaphore是一种基于计数的信号量。它可以设定一个阈值
- 多个线程竞争获取许可信号，做完自己的申请后归还，超过阈值后，线程申请许可信号将会被阻塞。Semaphore可以用来构建一些对象池，资源池之类的，比如数据库连接池
```text
public class SemaphoreTest {
    
    public void test(){
        Semaphore semephore = new Semaphore(10);
        try {
            semephore.acquire();
            try {
                // do something
            }catch (Exception e){
                
            }finally {
                semephore.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```

### Semaphore 与ReentrantLock
1. Semaphore基本能完成ReentrantLock的所有工作，使用方法也与之类似，通过acquire()与release()方法来获得和释放临界资源。

2. 经实测，Semaphone.acquire()方法默认为可响应中断锁，与ReentrantLock.lockInterruptibly()作用效果一致，也就是说在等待临界资源的过程中可以被Thread.interrupt()方法中断。 

3. 此外，Semaphore也实现了可轮询的锁请求与定时锁的功能，除了方法名tryAcquire与tryLock不同，其使用方法与ReentrantLock几乎一致。

4. Semaphore也提供了公平与非公平锁的机制，也可在构造函数中进行设定。 Semaphore的锁释放操作也由手动进行，因此与ReentrantLock一样，为避免线程因抛出异常而无法正常释放锁的情况发生，释放锁的操作也必须在finally代码块中完成。

### 原子变量 AtomicInteger、AtomicBoolean、AtomicInteger、AtomicLong、AtomicReference
- AtomicReference<V>将一个对象的所有操作转化成原子操作
- JVM为此类操作特意提供了一些同步类，使得使用更方便，且使程序运行效率变得更高。通过相关资料显示，通常AtomicInteger的性能是ReentantLock的好几倍


### 可重入锁
- 可重入锁，也叫做递归锁，指的是同一线程 外层函数获得锁之后 ，内层递归函数仍然有获取该锁的代码，但不受影响
- 在JAVA环境下 ReentrantLock 和synchronized 都是 可重入锁


### 读写锁ReadWriteLock
- 为了提高性能，Java提供了读写锁，在读的地方使用读锁，在写的地方使用写锁，灵活控制
- 读写锁分为读锁和写锁，多个读锁不互斥，读锁与写锁互斥，这是由jvm自己控制的，你只要上好相应的锁即可
- 在我看来，这个锁的存在是更好的区分功能，而不是一味都用重量级锁来做控制

### 共享锁和独占锁
#### 独占锁EXCLUSIVE
- 写锁就是独占锁
- 独占锁模式下，每次只能有一个线程能持有锁，ReentrantLock就是以独占方式实现的互斥锁
- 独占锁是一种悲观保守的加锁策略，它避免了读/读冲突，如果某个只读线程获取锁，则其他读线程都只能等待

#### 共享锁SHARED
- 读锁就是共享锁
- 共享锁则是一种乐观锁，它放宽了加锁策略，允许多个执行读操作的线程同时访问共享资源


//------------------------锁的状态总共有四种：无锁状态、偏向锁、轻量级锁和重量级锁------------------------------//
### 重量级锁（Mutex Lock）
- Synchronized是通过对象内部的一个叫做监视器锁（monitor）来实现的。但是监视器锁本质又是依赖于底层的操作系统的Mutex Lock来实现的
- 由于操作系统底层的线程切换成本高，因而Synchronized效率低
- 依赖于操作系统Mutex Lock所实现的锁我们称之为“重量级锁”。

### 轻量级锁
- 锁的状态总共有四种：无锁状态、偏向锁、轻量级锁和重量级锁
- 锁的升级： 偏向锁 ---> 轻量级锁 --> 重量级锁 ，锁的升级是单向的，只增不减
- 轻量级锁是会减少一些损耗，但是轻量级锁和重量级锁本来使用场景就不同，轻量级锁所适应的场景是线程交替执行同步块的情况，如果存在同一时间访问同一锁的情况，就会导致轻量级锁膨胀为重量级锁

### 偏向锁
- 偏向锁的目的是在某个线程获得锁之后，消除这个线程锁重入（CAS）的开销，看起来让这个线程得到了偏护
- 引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径，因为轻量级锁的获取及释放依赖多次CAS原子指令，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令
- 轻量级锁是为了在线程交替执行同步块时提高性能，而偏向锁则是在只有一个线程执行同步块时进一步提高性能

### 分段锁
- ConcurrentHashMap是学习分段锁的最好实践

### 锁的优化
- 减少锁持有的时间：只有在线程安全的情况下再加锁
- 减小锁粒度：降低了锁的竞争，偏向锁，轻量级锁成功率才会提高。最最典型的减小锁粒度的案例就是ConcurrentHashMap。
- 锁分离：最常见的锁分离就是读写锁ReadWriteLock，根据功能进行分离成读锁和写锁，LinkedBlockingQueue 从头部取出，从尾部放数据
- 锁粗化：如果对同一个锁不停的进行请求、同步和释放，其本身也会消耗系统宝贵的资源，反而不利于性能的优化
- 锁消除：锁消除是在编译器级别的事情。在即时编译器时，如果发现不可能被共享的对象，则可以消除这些对象的锁操作，多数是因为程序员编码不规范引起。


### 同步锁和死锁
#### 什么是同步锁？
当多个线程同时访问同一个数据时，很容易出现问题。为了避免这种情况出现，我们要保证线程同步互斥，就是指并发执行的多个线程，在同一时间内只允许一个线程访问共享数据。 Java中可以使用synchronized关键字来取得一个对象的同步锁

#### 什么是死锁？
何为死锁，就是多个线程同时被阻塞，它们中的一个或者全部都在等待某个资源被释放

绝策略均实现了RejectedExecutionHandler接口，若以上策略仍无法满足实际需要，完全可以自己扩展RejectedExecutionHandler接口


