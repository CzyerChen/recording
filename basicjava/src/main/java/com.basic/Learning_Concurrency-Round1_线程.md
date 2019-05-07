> 实现多线程有两个方法，一个继承Thread类，一个实现Runnable接口，这是我们都知道的
### Java线程的创建和实现
- 继承Thread类
```text
public class Thread implements Runnable {//这个Thread类自己实现了Runnable接口，去执行run方法，所以，本质上就是一个实现Runnable接口
    /* Make sure registerNatives is the first thing <clinit> does. */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    private volatile String name;
    private int            priority;
    private Thread         threadQ;
    private long           eetop;
......
做了一系列初始化，从init方法到最后,会需要传入一个Runnable的方法，就是这个调用这个target的run方法
}

```
```text
MyThread myThread = new MyThread(); 
Thread thread = new Thread(myThread); 
thread.start();
```
- 实现Runnable接口：这就很直接了，直接做run方法
- 如果需要返回值就需要实现Callable接口，获取一个Future对象，future.get()就可以获取返回的object
```text
//创建一个线程池 
ExecutorService pool = Executors.newFixedThreadPool(taskSize); 
// 创建多个有返回值的任务 
List<Future> list = new ArrayList<Future>(); 
for (int i = 0; i < taskSize; i++) { 
Callable c = new MyCallable(i + " "); 
// 执行任务并获取Future对象 
Future f = pool.submit(c); 
list.add(f); 
} 
// 关闭线程池 pool.shutdown(); 
// 获取所有并发任务的运行结果 
for (Future f : list) { 
// 从Future对象上获取任务的返回值，并输出到控制台 
System.out.println("res：" + f.get().toString()); 
}
```

### 线程的五种状态：新建New 就绪Runnable 运行Running 阻塞Blocked 死亡Dead
- New -- JVM为其分配内存
- Runnable -- 调用start方法之后，虚拟机会为其创建方法调用栈和程序技术器
- Running --- 就绪状态如果获得了CPU资源，就可执行run方法体中的内容了
- Blocked -- 线程让出CPU执行权，暂停运行，想要重新运行就需要重新进入Runnbale状态等待CPU资源
```text
等待阻塞 wait -> 等待队列 ：线程中主动调用wait()方法，JVM会将其放入等待队列

同步阻塞 lock -> 锁池: 线程中需要访问同步对象，或者进行加锁操作，并且没有获得锁资源，就会将其放入锁池中

其他阻塞 sleep/join:主动调用sleep()\join()\进行阻塞的IO请求，JVM会将其设置为阻塞状态，sleep超时，join线程终止，或者IO处理完毕就可重新进入Runnable状态等待CPU资源

```
- Dead : 正常结束，出现异常，调用stop结束线程（容易死锁）

### 如何判断线程终止
- 自然结束
- 使用volatile关键字标识，通过while 获取并判断
- 使用intercept（） 阻塞过程用捕获异常退出，非阻塞情况使用isInterrupt（）获取中断情况
- stop方式，线程不安全，可能导致死锁

### sleep和wait的区别
- sleep是Thread类的方法，wait是Object类的方法
- sleep让出CPU，但是保留了监控状态，到指定时间就会恢复，不会释放对象锁
- wait方法让出CPU，释放对象锁，自己进入等待锁定池，只有当调用notify方法才能进入对象锁定池准备获取对象锁进入运行状态

### start和run的区别
- start是启动一个线程的方法，run是线程中执行业务逻辑的核心方法
- start之后线程进入就绪状态，当线程获取CPU资源，就能够执行run方法中的逻辑


### 守护进程/后台进程/服务进程
- 可以通过setDeamon来设置成守护进程
- 守护进程就和JVM是一个级别的了，JVM自身有几个守护进程：虚拟机进程，周期性计划进程，GC进程，信号分发进程，编译器进程

