### 1.什么是Java内存模型？
- Java内存模型描述了在多线程代码中哪些行为是合法的，以及线程如何通过内存进行交互。
- 它描述了“程序中的变量“ 和 ”从内存或者寄存器获取或存储它们的底层细节”之间的关系。
- Java内存模型通过使用各种各样的硬件和编译器的优化来正确实现以上事情。


### 2.多线程同步和互斥有哪几种实现方法？
- Java包含了几个语言级别的关键字，包括：volatile, final以及synchronized，目的是为了帮助程序员向编译器描述一个程序的并发需求。
- Java内存模型定义了volatile和synchronized的行为，更重要的是保证了同步的java程序在所有的处理器架构下面都能正确的运行。


### 3.悲观锁和乐观锁有什么区别？
- 悲观并发，通过进入阻塞队列进行排队竞争获取资源，独占模式
- 乐观并发，通过重试次数，不断进入进出队列，资源竞争，公平或者非公平方式获取资源，共享模式

### 4.Java的锁实现机制，使用场景分析
- 悲观锁，sychronized 悲观并发，资源独占模式，阻塞队列排队，通过竞争争夺对象使用权
- 悲观锁，ReentrantLock ，可以独占 可以共享，队列中公平唤醒或者非公平竞争，比sychronized多了可中断，可轮回，condition条件出发等高级功能
- 乐观锁，CAS 乐观并发，通过锁的自旋（jdk1.6之后，锁的自旋次数由JVM控制），进行资源的竞争

### 5.ReentranLock源码，设计原理，整体过程
- 实现Lock接口，内部类Sync重写AQS的关于独占和共享的4个接口，实现锁的等待获取和唤醒

### 6.volatile的实现原理
通过依靠JVM的unsafe类实现硬件级别的线程控制，依靠内存屏障语义保证了内存的可见性，避免了操作系统底层的指令重排

### 7.AQS的实现过程
- 一个核心status,进行共享和独占思路的实现，通过CAS进行status计数的修改
- 实现加锁的核心方法
```text
Accquire
Release
tryAccquire
tryRelease
AcqueireShared
ReleaseShared
tryAcqueireShared
tryReleaseShared

```
### 8.volatile、synchronized、Lock的区别
- 并发有几个问题：内存可见性，原子性和有序性
- Volatile sychronized是JVM级别的，通过一些标识和控制，告诉JVM需要做同步的控制
- Lock通过手动调用unsafe类实现硬件级别的线程控制
- Volatile能够解决内存可见性，通过写之前storestore指令、写之后storeload指令强制结果刷入主存，读之后加loadload指令、loadstore指令，强制从主存获取变量值，不能保证复合操作的原子性，能够通过内存屏障保证指令操作不被重排
- Synchronized 能保证内存可见性，原子性和有序性。加在对象上，通过添加monitorenter monitorexit的指令进行同步控制，加在方法上，通过告诉JVM一个ACC_SYCHRONIZED的标识，告诉JVM需要用同步控制的方式进行资源控制
- Lock接口和AQS组合，能够做到可中断，可轮回，按照不同condition做灵活控制，这是它相比灵活高级的地方

- 具体的区别：
```text
1、volatile和synchronized是Java的关键字，而Lock是jdk5之后juc包下的一个接口；

2、volatile关键字修饰的变量可以保证可见性、有序性，但是不能保证线程的原子性，而synchronized对可见性、原子性与有序性都能保证；

3、volatile仅能用于修饰变量，而synchronized可用于修饰变量、方法、代码块等；

4、volatile不会造成线程阻塞，synchronized可能会造成线程阻塞；

5、synchronized和Lock都能通过加锁来实现线程同步；

6、synchronized锁在获取锁的线程执行完了该代码块或者线程执行出现异常后释放锁，而Lock可以主动去释放锁；

7、对于不同场景使用不同的锁，Lock实现的锁种类丰富；

8、Lock的性能比synchronized强。
```




