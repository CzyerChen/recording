## 一、并发可能出现的问题
死锁、内存可见性、竞态条件
为了解决并发中可能出现的问题，我们可以通过加锁或者使用一些内存可见技术来避免这些问题的产生

## 二、解决并发问题的几个措施
锁机制（显示锁、悲观锁）、原子变量CAS、volatile、并发包(Concurrent*,例如ConcurrentHashMap,CopyOnWriteArrayList)、ThreadLocal

## 三、java中15种锁机制
#### 1.公平锁/非公平锁
- 公平锁是指多个线程按照申请锁的顺序来获取锁
- 非公平锁是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁。有可能，会造成优先级反转或者饥饿现象

公平锁涉及到的比较少，或者是本人没有怎么接触到，在并发编程使用过程中都是非公平锁占主导，非公平锁有较高的吞吐量，当然也会有一些问题

- 对于悲观锁Synchronized ,是一种非公平锁，并不能通过某些操作让其实现公平锁的功能
- 对于乐观锁ReetrantLock 是通过构造方法指定是不是公平锁，默认是非公平的。指定为公平锁，是通过AQS实现线程调度的过程中来保证其公平

非公平锁这里用一段ReetrantLock中的源码来展示
```java
public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
             }
         }
         //就是这里
         else if (current == getExclusiveOwnerThread()) {
             int nextc = c + acquires;
             if (nextc < 0) {// overflow
                throw new Error("Maximum lock count exceeded");
                }
             setState(nextc);
            return true;
        }
        return false;
    }
}
```
在AQS中维护了一个private volatile int state来计数重入次数，避免了频繁的持有释放操作，这样既提升了效率，又避免了死锁。

这里简单的描述一下AQS的含义：
**AbstractQueuedSynchronizer（AQS）** 抽象的队列式的同步器
- AQS定义了一套多线程访问共享资源的同步器框架，它维护了一个volatile int state（代表共享资源）和一个FIFO线程等待队列（多线程争用资源被阻塞时会进入此队列），许多同步类实现都依赖于它，如常用的ReentrantLock/Semaphore/CountDownLatch
- 以ReentrantLock为例，state初始化为0，表示未锁定状态。A线程lock()时，会调用tryAcquire()独占该锁并将state+1。此后，其他线程再tryAcquire()时就会失败，直到A线程unlock()到state=0（即释放锁）为止，其它线程才有机会获取该锁。当然，释放锁之前，A线程自己是可以重复获取此锁的（state会累加），这就是可重入的概念。但要注意，获取多少次就要释放多么次，这样才能保证state是能回到零态的。
- 再以CountDownLatch以例，任务分为N个子线程去执行，state也初始化为N（注意N要与线程个数一致）。这N个子线程是并行执行的，每个子线程执行完后countDown()一次，state会CAS减1。等到所有子线程都执行完后(即state=0)，会unpark()主调用线程，然后主调用线程就会从await()函数返回，继续后余动作。
- 资源分为独占和分享两种模式

#### 2.可重入锁/不可重入锁
- 可重入锁指的是可重复可递归调用的锁，在外层使用锁之后，在内层仍然可以使用，并且不发生死锁（前提得是同一个对象或者class），这样的锁就叫做可重入锁
  上述的悲观锁Synchronized和乐观锁ReetrantLock ，都是可重入的，不保证公平
- 因为锁机制加载对象上，如果锁不可重入，容易发生两个对象调用同一个对象的方法造成死锁的问题
- 不可重入锁，不可递归调用，递归调用容易产生死锁，可是使用自旋锁来模拟一个不可重入锁，以下自旋锁部分介绍书写的方式


#### 3.独享锁/共享锁
- 独享锁：顾名思义就是独享对象资源，只有当当前对象的锁释放之后，其他线程才能够获取
- 共享锁：共享锁典型的例子就是ReentrantReadWriteLock，读操作可支持并发，无需加锁，能够高效的实现并发读，写操作需要加锁，资源独占，读写和写写，写读都是互斥的。
- 独享锁与共享锁也是通过AQS来实现的，实现独享或者共享。Synchronized是独享锁

#### 4.互斥锁/读写锁
- 互斥锁，又称为排他锁，主要描述当一个线程对某个资源加锁后，只有当该线程释放锁，不然其他线程均无法访问该资源
- 读写锁，既是互斥锁，又是共享锁，它分为读锁、写锁、不加锁的三种状态

当其处于读锁时，允许其余读锁一起操作，但是写锁不能操作
当其处于写锁时，不允许任何线程对其操作
为了避免写锁在与读锁并存时，无法获取锁，因而在写锁之后的读锁会被写锁阻塞

#### 5.乐观锁/悲观锁
- 悲观锁，指对资源操作之前，先把资源上锁，使其他线程无法访问，然后在对资源进行操作，操作完后释放锁资源，例子：数据库的表锁、行锁以及读锁、写锁等
- 乐观锁，通过版本号机制与CAS的方法实现，适用于多读的应用类型，这样可以提高吞吐量，原子变量的底层就是使用了乐观锁的CAS机制

#### 6.分段锁
- 分段锁也不陌生，就是ConcurrentHashMap应对并发操作的技术，这边单独描述一下
- 分段锁的实现就是默认初始对象有16个锁，分别加载16个散列分散桶上，就是一段一段的数据，因而数据访问的加锁概率被分散到16个数据段上（第N个散列桶由第（N mod 16）个锁来保护），大大降低了加锁的概率，也就大大提高了并发

#### 7.偏向锁/轻量级锁/重量级锁
见JavaMemoryModel-JMM5-lock

#### 8.自旋锁
- 自旋锁使用原子引用，提供了引用变量的读写原子性操作，当前demo即对当前线程加锁
- 自旋锁是指当一个线程尝试获取某个锁时，如果该锁已被其他线程占用，就一直循环检测锁是否被释放，而不是进入线程挂起或睡眠状态。
- 模拟一个不可重入锁，不可重入即每一个线程只能持有对象，只有当释放才能再次使用，同一个线程对该对象的操作不可叠加

代码见UnReetrantLock + TestMain

- 上面简单CAS操作无法保证公平性，不能保证等待线程按照FIFO顺序获得锁。可以通过类似排队叫号方案实现公平锁：锁对象拥有一个服务号，表示正在服务的线程，还有一个排队号，每个线程获取锁前先拿一个排队号，然后不断轮询当前的服务号是否是自己的排队号，若是就拥有锁，否则继续轮询。释放锁时将服务号加1，从而使下一个线程退出自旋。但这个方法多个线程都在读写同一个变量服务号，每次操作都会导致多个处理器缓存之间同步，增加系统总线和内存流量，降低系统整体性能。

- 因此、产生了MCS锁和CLH锁。MCS锁是基于链表的可扩展、高性能、公平自旋锁，申请线程只在本地变量上自旋，直接前驱负责通知其自旋结束，从而减少了处理器缓存同步次数，降低了系统总开销。

- CLH锁是基于隐形链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，它不断轮询前驱的状态，发现前驱释放了锁就结束自旋。CLH锁代码实现上比MCS要简单的多，它在释放锁时只改变自己的属性，而MCS锁释放锁时需要改变后继节点的属性。

- 这里实现的锁都是独占的，且不能重入的

## 四、volatile的原子性和内存可见性
volatile关键字，解决了内存不可见的问题，但是并不具有原子性
- 它的内存可见性，主要取决于，每一次操作都要求获取内存中最新的值，因而不会出现脏读的问题
- 它的非原子性，主要取决于，它对于内存的读和写的操作是原子的，能保证读是当前唯一的，写是当前唯一的，但是它读取变量后是拷贝到本地变量中，如果再读写之间对于读取的变量有修改操作，那么这个修改作用在本地，并不能实时操作内存中的数组，在修改结束和写入内存之间，其他进程读取变量值的时候就丧失了原子性
- 写的原子性主要依赖内存屏障，阻塞等待，保证在当前操作之前的操作都执行完毕，才执行写入内存的操作

## 五、降低锁竞争的方法
- 1、减少锁的持有时间 
- 2、降低锁的请求频率 
- 3、使用带有协调机制的独占锁，这些机制允许更高的并发性。

## 锁的状态
- 1.无锁状态
CAS是英文单词Compare and Swap（比较并交换），是一种有名的无锁算法
- 2.偏向锁状态
偏向锁是指一段同步代码一直被一个线程所访问，那么该线程会自动获取锁。降低获取锁的代价
- 3.轻量级锁状态
轻量级锁是指当锁是偏向锁的时候，被另一个线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，提高性能
- 4.重量级锁状态
锁的状态是通过对象监视器在对象头中的字段来表明的，以上状态锁的重量级由低到高，不可降级
重量级锁是指当锁为轻量级锁的时候，另一个线程虽然是自旋，但自旋不会一直持续下去，当自旋一定次数的时候，还没有获取到锁，就会进入阻塞，该锁膨胀为重量级锁。重量级锁会让其他申请的线程进入阻塞，性能降低。

### sychronized的执行指令
#### 修饰对象
- 类：
```text
public class SynchronizedDemo {
    public void method() {
        synchronized (this) {
            System.out.println("synchronized ...");
        }
    }
}
```
- javac SynchronizedDemo.java
- java -c -s -v -l SynchronizedDemo.class
```text
$ javap -c -s -v -l SynchronizedDemo.class
Classfile /D:/SynchronizedDemo.class
  Last modified 2019-5-5; size 531 bytes
  MD5 checksum ecfc3d628f7ba427f6416c5b639498dd
  Compiled from "SynchronizedDemo.java"
public class SynchronizedDemo
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#18         // java/lang/Object."<init>":()V
   #2 = Fieldref           #19.#20        // java/lang/System.out:Ljava/io/Print                                                                                                                Stream;
   #3 = String             #21            // synchronized ...
   #4 = Methodref          #22.#23        // java/io/PrintStream.println:(Ljava/                                                                                                                lang/String;)V
   #5 = Class              #24            // SynchronizedDemo
   #6 = Class              #25            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               method
  #12 = Utf8               StackMapTable
  #13 = Class              #24            // SynchronizedDemo
  #14 = Class              #25            // java/lang/Object
  #15 = Class              #26            // java/lang/Throwable
  #16 = Utf8               SourceFile
  #17 = Utf8               SynchronizedDemo.java
  #18 = NameAndType        #7:#8          // "<init>":()V
  #19 = Class              #27            // java/lang/System
  #20 = NameAndType        #28:#29        // out:Ljava/io/PrintStream;
  #21 = Utf8               synchronized ...
  #22 = Class              #30            // java/io/PrintStream
  #23 = NameAndType        #31:#32        // println:(Ljava/lang/String;)V
  #24 = Utf8               SynchronizedDemo
  #25 = Utf8               java/lang/Object
  #26 = Utf8               java/lang/Throwable
  #27 = Utf8               java/lang/System
  #28 = Utf8               out
  #29 = Utf8               Ljava/io/PrintStream;
  #30 = Utf8               java/io/PrintStream
  #31 = Utf8               println
  #32 = Utf8               (Ljava/lang/String;)V
{
  public SynchronizedDemo();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>                                                                                                                ":()V
         4: return
      LineNumberTable:
        line 1: 0

  public void method();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=3, args_size=1
         0: aload_0 // 无引用
         1: dup
         2: astore_1
         //获取对象
         3: monitorenter
         4: getstatic     #2                  // Field java/lang/System.out:Ljav                                                                                                                a/io/PrintStream;
         7: ldc           #3                  // String synchronized ...
         9: invokevirtual #4                  // Method java/io/PrintStream.prin                                                                                                                tln:(Ljava/lang/String;)V
        12: aload_1  // 有一个引用
        //释放对象
        13: monitorexit
        14: goto          22
        17: astore_2
        18: aload_1
        19: monitorexit
        20: aload_2
        21: athrow
        22: return
      Exception table:
         from    to  target type
             4    14    17   any
            17    20    17   any
      LineNumberTable:
        line 3: 0
        line 4: 4
        line 5: 12
        line 6: 22
      StackMapTable: number_of_entries = 2
        frame_type = 255 /* full_frame */
          offset_delta = 17
          locals = [ class SynchronizedDemo, class java/lang/Object ]
          stack = [ class java/lang/Throwable ]
        frame_type = 250 /* chop */
          offset_delta = 4
}
SourceFile: "SynchronizedDemo.java"

```
### 锁住方法
- 类：
```text
public class SynchronizedDemo2 {
    public synchronized void method() {
        System.out.println("synchronized2!!!");
    }
}
```
- javap -c -s -v -l SynchronizedDemo2.class
```text
$ javap -c -s -v -l SynchronizedDemo2.class
Classfile /D:/SynchronizedDemo2.class
  Last modified 2019-5-5; size 421 bytes
  MD5 checksum 330170b77a797cf07bdbc688574e3e00
  Compiled from "SynchronizedDemo2.java"
public class SynchronizedDemo2
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#14         // java/lang/Object."<init>":()V
   #2 = Fieldref           #15.#16        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #17            // synchronized2!!!
   #4 = Methodref          #18.#19        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #20            // SynchronizedDemo2
   #6 = Class              #21            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               method
  #12 = Utf8               SourceFile
  #13 = Utf8               SynchronizedDemo2.java
  #14 = NameAndType        #7:#8          // "<init>":()V
  #15 = Class              #22            // java/lang/System
  #16 = NameAndType        #23:#24        // out:Ljava/io/PrintStream;
  #17 = Utf8               synchronized2!!!
  #18 = Class              #25            // java/io/PrintStream
  #19 = NameAndType        #26:#27        // println:(Ljava/lang/String;)V
  #20 = Utf8               SynchronizedDemo2
  #21 = Utf8               java/lang/Object
  #22 = Utf8               java/lang/System
  #23 = Utf8               out
  #24 = Utf8               Ljava/io/PrintStream;
  #25 = Utf8               java/io/PrintStream
  #26 = Utf8               println
  #27 = Utf8               (Ljava/lang/String;)V
{
  public SynchronizedDemo2();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 1: 0

  public synchronized void method();
    descriptor: ()V
    //没有monitor对象
    //JVM 通过该 ACC_SYNCHRONIZED 访问标志来
     //辨别一个方法是否声明为同步方法，从而执行相应的同步调用。
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String synchronized2!!!
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 3: 0
        line 4: 8
}
SourceFile: "SynchronizedDemo2.java"

```

### jdk1.6之后，synchronized做了什么优化
- JDK1.6 对锁的实现引入了大量的优化，如偏向锁、轻量级锁、自旋锁、适应性自旋锁、锁消除、锁粗化等技术来减少锁操作的开销


  