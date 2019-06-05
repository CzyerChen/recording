### 线程池的运行流程
```text
开始
提交任务
有效线程小于核心线程    
---是---直接创建线程，执行任务
---否---
核心线程已满，进入队列
判断队列是否已满       
---是--- 有效线程是否大于最大线程数 
---是--- 拒绝策略
        ---否--- 直接创建线程，执行任务
---否---加入队列
```
### 线程池操作主要涉及几大问题
#### 问题一：如何维护全局的有效线程数
- Ctl AutomicInteger维护，通过CAS进行数据的更新
````text
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
private static final int COUNT_BITS = Integer.SIZE - 3;
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

// runState is stored in the high-order bits
private static final int RUNNING    = -1 << COUNT_BITS;
private static final int SHUTDOWN   =  0 << COUNT_BITS;
private static final int STOP       =  1 << COUNT_BITS;
private static final int TIDYING    =  2 << COUNT_BITS;
private static final int TERMINATED =  3 << COUNT_BITS;
````


#### 问题二：如何维护线程池状态和有效线程数
- 线程池的设计十分巧妙，运用整型的不同位数来表示线程池状态和数量，数量最大5亿。一个数值表示两种含义，能够保证数据和状态的同步。
- ctl是一个 Integer 值，它是对线程池运行状态和线程池中有效线程数量进行控制的字段，Integer值一共有32位，其中高3位表示”线程池状态”，低29位表示”线程池中的任务数量”

#### 问题三：线程池有哪些状态？
- Running: 线程池初始化时默认的状态，表示线程正处于运行状态，能够接受新提交的任务，同时也能够处理阻塞队列中的任务；
- ShutDown: 调用 shutdown() 方法会使线程池进入到该状态，该状态下不再继续接受新提交的任务，但是还会处理阻塞队列中的任务；
- Stop: 调用 shutdownNow() 方法会使线程池进入到该状态，该状态下不再继续接受新提交的任务，同时不再处理阻塞队列中的任务；
- Tidying: 如果线程池中workerCount=0，即有效线程数量为0时，会进入该状态
- Terminated: 在terminated()方法执行完后进入该状态，只不过terminated()方法需要我们自行实现

- 刚才高三位表示线程池状态：
```text
•RUNNING：高三位值111
•SHUTDOWN：高三位值000
•STOP：高三位值001
•TIDYING：高三位值010
•TERMINATED：高三位值011
```


#### 问题四：如何维护corePoolSize数量的存活线程数用于线程复用？
- 通过timeOut标识和timed标识为主要判断，是否大于最大核心线程数，线程池状态是否合法
- 整体来判断线程是否存活。timeOut主要用于判断是否能从阻塞队列中获取元素，timed主要判断当前是否允许线程超时，并且有效线程数是否大于核心线程数，
```text
if ((wc > maximumPoolSize || (timed && timedOut))
    && (wc > 1 || workQueue.isEmpty())) {
    if (compareAndDecrementWorkerCount(c))
        return null;
    continue;
}

```



### 问题五：线程池有哪些核心操作?
1）添加任务addWorker：将一个可执行的Runnable 封装成Worker , 继承AQS实现队列同步,根据是否大于核心线程数，是否队列已满，是否有效线程大于最大线程数，进行判断，做出不同的处理，是直接执行还是放入队列，并对ctl进行递增
```text
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        // Check if queue empty only if necessary.
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;

        for (;;) {
            int wc = workerCountOf(c);
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            if (compareAndIncrementWorkerCount(c))
                break retry;
            c = ctl.get();  // Re-read ctl
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
    }

    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        w = new Worker(firstTask);
        final Thread t = w.thread;
        if (t != null) {
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                // Recheck while holding lock.
                // Back out on ThreadFactory failure or if
                // shut down before lock acquired.
                int rs = runStateOf(ctl.get());

                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    if (t.isAlive()) // precheck that t is startable
                        throw new IllegalThreadStateException();
//HashSet线程不安全，所以需要加锁
                    workers.add(w);
                    int s = workers.size();
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        if (! workerStarted)
            addWorkerFailed(w);
    }
    return workerStarted;
}

```

2）获取任务(getTask)主要是从队列中获取，中间需要考虑线程池shutdown的状态，核心线程数存活问题，还有ctl的递减
```text
private Runnable getTask() {
    boolean timedOut = false; // Did the last poll() time out?

    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        // Check if queue empty only if necessary.
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }

        int wc = workerCountOf(c);

        // Are workers subject to culling?
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

        if ((wc > maximumPoolSize || (timed && timedOut))
            && (wc > 1 || workQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c))
                return null;
            continue;
        }

        try {
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();
            if (r != null)
                return r;
            timedOut = true;
        } catch (InterruptedException retry) {
            timedOut = false;
        }
    }
}

```

3）execute执行，我们最外层就是调用线程池的一个execute，将一个runnable或者callable的线程进行执行，执行核心逻辑，选择一个Worker运行就是执行它的run方法
```text
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
      int c = ctl.get();
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (! isRunning(recheck) && remove(command))
            reject(command);
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    else if (!addWorker(command, false))
        reject(command);
}

final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock(); // allow interrupts
    boolean completedAbruptly = true;
    try {
        while (task != null || (task = getTask()) != null) {
            w.lock();
            // If pool is stopping, ensure thread is interrupted;
            // if not, ensure thread is not interrupted.  This
            // requires a recheck in second case to deal with
            // shutdownNow race while clearing interrupt
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                wt.interrupt();
            try {
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    afterExecute(task, thrown);
                }
            } finally {
                task = null;
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        processWorkerExit(w, completedAbruptly);
    }
}
```



