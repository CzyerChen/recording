- volatile这个关键字并不陌生，我初次接触它是在学习java基础的时候，讨论到并发，数据的内存不可见，怎么办呢，就是用volatile修饰，使它能内存可见
- 在接下来的学习中，我又了解到volatile是依靠内存屏障，保证在它操作之前的操作必须全部执行，然后它再原子地进行内存数据交换
- 接下来我们就来继续学习volatile

#### 1.volatile的特性
- 我们如果不能理解volatile，其实我们可以通过把它当作通过锁的操作
- 可见性。对一个volatile变量的读，总是能看到（任意线程）对这个volatile变量最后的写入
- 原子性：对任意单个volatile变量的读/写具有原子性，但类似于volatile++这种复合操作不具有原子性
- 对一个volatile变量的单个读/写操作，与对一个普通变量的读/写操作使用同一个锁来同步，它们之间的执行效果相同
- 锁的语义决定了临界区代码的执行具有原子性,锁的happens-before规则保证释放锁和获取锁的两个线程之间的内存可见性

#### 2.volatile的写-读建立的happens before关系
-从内存语义的角度来说，volatile与锁有相同的效果：volatile写和锁的释放有相同的内存语义；volatile读与锁的获取有相同的内存语义
```text
public class VolatileExample {
    int a = 0;
    volatile boolean flag = false;

    public void writer() {
        a = 1;                   //1
        flag = true;               //2
    }

    public void reader() {
        if (flag) {                //3
            int i =  a;           //4
        }
    }

}
```
假设线程 A 执行 writer() 方法之后，线程 B 执行 reader() 方法。根据 happens before 规则，这个过程建立的 happens before 关系可以分为两类
1. 根据程序次序规则，1 happens before 2; 3 happens before 4。
2. 根据 volatile 规则，2 happens before 3。
3. 根据 happens before 的传递性规则，1 happens before 4。
4. 1->2，3->4 是顺序的保障，其中2->3的操作是volatile保障的

#### 3.volatile 写 - 读的内存语义
- volatile的操作，每次原子地将内存中的数据读取到本地内存，它只能保证原子地将数据从主存读入本地，或者原子地将本地写入主存
- 线程 A 写一个 volatile 变量，实质上是线程 A 向接下来将要读这个 volatile 变量的某个线程发出了（其对共享变量所在修改的）消息
- 线程 B 读一个 volatile 变量，实质上是线程 B 接收了之前某个线程发出的（在写这个 volatile 变量之前对共享变量所做修改的）消息
- 线程 A 写一个 volatile 变量，随后线程 B 读这个 volatile 变量，这个过程实质上是线程 A 通过主内存向线程 B 发送消息

#### 4.volatile 内存语义的实现
- 对于volatile修饰的变量，不支持编译器和处理器的重排序,JMM对它制定了编译器重排序的规则
- 加入有两个操作，普通读写，volatile读，volatile写
    - 当第二个操作是 volatile 写时，不管第一个操作是什么，都不能重排序。这个规则确保 volatile 写之前的操作不会被编译器重排序到 volatile 写之后
    - 当第一个操作是 volatile 读时，不管第二个操作是什么，都不能重排序。这个规则确保 volatile 读之后的操作不会被编译器重排序到 volatile 读之前
    - 当第一个操作是 volatile 写，第二个操作是 volatile 读时，不能重排序
- 为了实现 volatile 的内存语义，编译器在生成字节码时，会在指令序列中插入内存屏障来禁止特定类型的处理器重排序
- 之前也有说明，插入内存屏障是需要消耗的，而且不同的内存屏障保障的操作也不同。
JMM为volatile内存屏障插入保守策略为：
    - 在每个 volatile 写操作的前面插入一个 StoreStore 屏障。
    - 在每个 volatile 写操作的后面插入一个 StoreLoad 屏障。
    - 在每个 volatile 读操作的后面插入一个 LoadLoad 屏障。
    - 在每个 volatile 读操作的后面插入一个 LoadStore 屏障
- 一个volatile写操作
```text
    普通读
      |
    普通写
      |
  StoreStore屏障 ： 禁止普通写和volatile写重排序
      |
  volatile 写
      |
  StoreLoad屏障 ： 防止下面有volatile写/读 重排序
  
```
- 一个volatile读操作
```text
    volatile 读
      |
  LoadLoad屏障 ： 禁止下面普通读和volatile读重排序
      |
  LoadStore屏障 ： 禁止下面的普通读写与volatile读 重排序
      |
    普通读
      |
    普通写  
```
- 一个综合volatile读volatile写的例子
```text
class VolatileBarrierExample {
    int a;
    volatile int v1 = 1;
    volatile int v2 = 2;

    void readAndWrite() {
        int i = v1;           // 第一个 volatile 读 
        int j = v2;           // 第二个 volatile 读 
        a = i + j;            // 普通写 
        v1 = i + 1;          // 第一个 volatile 写 
        v2 = j * 2;          // 第二个 volatile 写 
    }

    …                    // 其他方法 
}
```
```text
    volatile 读1
        |
    LoadLoad屏障 ： 禁止下面的读2与读1重排序，下面读2会拦截写操作，这边不用考虑
        |
    volatile 读2
        |
    LoadStore屏障： 禁止普通写和读2重排序。在下一个屏障之前，没有普通读操作省略LoadLoad
        |
      普通写
        |
    StoreStore屏障：禁止普通写和写1 重排序，下一个屏障之前没有普通读，不需要StoreLoad
        |
    volatile 写1
        |
    StoreStore屏障：禁止写1与写2的重排序
        |
    volatile 写2
        |
    StoreLoad屏障 ： 禁止下面volatile 读写与写2 重排序
        
```
- 在我们之前介绍的操作系统重排序的介绍中，X86只会对写读的操作重排序，JMM 仅需在 volatile 写后面插入一个 StoreLoad 屏障即可正确实现 volatile 写 - 读的内存语义

- 由于 volatile 仅仅保证对单个 volatile 变量的读 / 写具有原子性，而监视器锁的互斥执行的特性可以确保对整个临界区代码的执行具有原子性。在功能上，监视器锁比 volatile 更强大；在可伸缩性和执行性能上，volatile 更有优势
- volatile需要注意的是，它旨在读取和写入内存的操作上能够保障原子性，关于volatile 操作，指对本地副本的算术操作再将数据刷新到主存，可能出现ABA的问题
- volatile的额外操作没有原子性，需要操作的原子性还是需要依赖锁