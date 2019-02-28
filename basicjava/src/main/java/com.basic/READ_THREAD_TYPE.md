> 平时我们使用线程池，今天从源码角度看看他们的参数
> 因为建议使用ThreadPoolService 去显示创建线程池，按需设置参数，所以我们先研究一下原有的配置

`Executors.newCachedThreadPool、newFixedThreadPool 、newScheduledThreadPool 和newSingleThreadExecutor  这4类线程池`

### newCachedThreadPool：可缓存的线程池

```java
  /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to {@code execute} will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool. Threads that have
     * not been used for sixty seconds are terminated and removed from
     * the cache. Thus, a pool that remains idle for long enough will
     * not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor} constructors.
     *
     * @return the newly created thread pool
     */
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }

```
1、该线程池的核心线程数量是0，线程的数量最高可以达到Integer 类型最大值；

2、创建ThreadPoolExecutor实例时传过去的参数是一个SynchronousQueue实例，说明在创建任务时，若存在空闲线程就复用它，没有的话再新建线程。

3、线程处于闲置状态超过60s的话，就会被销毁。

``使用：``
```java
public static void main(String[] args){
      ExecutorService executorService = Executors.newCachedThreadPool();
      executorService.submit(new Runnable() {
         @Override
         public void run() {
            System.out.println("haha");
         }
      });
      
   } 
```

### newFixedThreadPool: 定长线程池
```$xslt
 /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue.  At any point, at most
     * {@code nThreads} threads will be active processing tasks.
     * If additional tasks are submitted when all threads are active,
     * they will wait in the queue until a thread is available.
     * If any thread terminates due to a failure during execution
     * prior to shutdown, a new one will take its place if needed to
     * execute subsequent tasks.  The threads in the pool will exist
     * until it is explicitly {@link ExecutorService#shutdown shutdown}.
     *
     * @param nThreads the number of threads in the pool
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
```
1、线程池的最大线程数等于核心线程数，并且线程池的线程不会因为闲置超时被销毁。

2、使用的列队是LinkedBlockingQueue，表示如果当前线程数小于核心线程数，那么即使有空闲线程也不会复用线程去执行任务，
而是创建新的线程去执行任务。如果当前执行任务数量大于核心线程数，此时再提交任务就在队列中等待，直到有可用线程。

``使用：``
```java
 public static void main(String[] args){
      ExecutorService executorService = Executors.newFixedThreadPool(10);
      executorService.submit(new Runnable() {
         @Override
         public void run() {
            System.out.println("haha");
         }
      });

   }
```

### newSingleThreadExecutor： 单线程线程池
```java
  /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue. (Note however that if this single
     * thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time. Unlike the otherwise equivalent
     * {@code newFixedThreadPool(1)} the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     *
     * @return the newly created single-threaded Executor
     */
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
```
``使用：``
```java
public static void main(String[] args){
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(new Runnable() {
         @Override
         public void run() {
            System.out.println("haha");
         }
      });

   }
```

### newScheduledThreadPool：支持定时的定长线程池
```java
  /**
     * Creates a thread pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     * @param corePoolSize the number of threads to keep in the pool,
     * even if they are idle
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

 /**
     * Creates a new {@code ScheduledThreadPoolExecutor} with the
     * given core pool size.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
    public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
              new DelayedWorkQueue());
    }
```
1、该线程池可以设置核心线程数量，最大线程数与newCachedThreadPool一样，都是Integer.MAX_VALUE。

2、该线程池采用的队列是DelayedWorkQueue，具有延迟和定时的作用。
``使用：``
```java
 public static void main(String[] args){
      ExecutorService executorService = Executors.newScheduledThreadPool(10);
      //延迟1分执行，只执行一次
     ((ScheduledExecutorService) executorService).schedule(new Runnable() {
        @Override
        public void run() {

        }
     },1, TimeUnit.MINUTES);

     //延迟1分，每隔3分执行一次
      // 多少秒之后再执行， 是从上一个任务开始时开始计算
     ((ScheduledExecutorService) executorService).scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {

        }
     },1,3,TimeUnit.MINUTES);

      //延迟1分，每隔3分执行一次
      // 多少秒之后再执行， 是从上一个任务结束时开始计算
     ((ScheduledExecutorService) executorService).scheduleWithFixedDelay(new Runnable() {
        @Override
        public void run() {
           
        }
     },1,3,TimeUnit.MINUTES);
     
   }
```


### 自定义ThreadFactory
四种线程池的使用就说到这里了，值得说明的是，除了上面的参数外，Executors类中还给这四种线程池提供了可传ThreadFactory的重载方法，
每一种线程池都可以添加ThreadFactory用来使用自定义的线程池

ThreadFactory是一个接口类，也就是我们经常说的线程工厂，只有一个方法，可以用于创建线程：
默认情况下，ThreadPoolExecutor构造器传入的ThreadFactory 参数是Executors类中的defaultThreadFactory()，相当于一个线程工厂，帮我们创建了线程池中所需的线程。
自定义线程池就是自定义实现thread的一个接口
```java
 public static void main(String[] args){
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            Thread t = new Thread();
            System.out.println("==self designed==");
            return t;
         }
      });
      threadPoolExecutor.submit(new Runnable() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      });
   }
```

