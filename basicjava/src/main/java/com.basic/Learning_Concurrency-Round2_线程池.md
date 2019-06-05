> 线程池能够更好地管理创建线程后的资源管理

### 线程池
```text
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("test-pool-%d").daemon(false).build();
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        //创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程
        //使用LinkedBlockingQueue作为队列，接受无限长度的任务
        ExecutorService executorService1 = Executors.newFixedThreadPool(10);
        
        //终止并从缓存中移除那些已有 60 秒钟未被使用的线程
        //使用SynchronousQueue
        ExecutorService executorService2 = Executors.newCachedThreadPool();
        
        //使用WorkDelayQueue存放各种任务
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        
        ExecutorService executorService3 = Executors.newWorkStealingPool(10);//并行度
```

### 线程池的种类，每个种类的用途，对应的队列

