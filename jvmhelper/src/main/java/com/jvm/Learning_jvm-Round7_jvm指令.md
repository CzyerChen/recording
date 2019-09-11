> 之前所了解到的jvm指令主要有jstat jstack jmap等，但是实际应用中暂时并没有接触到，今天总结一下如何来排查程序的问题

### 1．Jstack 
#### 1.1   jstack能得到运行java程序的java stack和native stack的信息。可以轻松得知当前线程的运行情况。和thread dump是同样的结果，但是thread dump是用kill -3 pid命令，安全性上来说并不算很妥当。

```text
2019-09-06 09:47:40
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.181-b13 mixed mode):

"Attach Listener" #61 daemon prio=9 os_prio=31 tid=0x00007f97c8b37000 nid=0x9007 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"DestroyJavaVM" #60 prio=5 os_prio=31 tid=0x00007f97cab7e800 nid=0x2403 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"pool-1-thread-1" #16 prio=5 os_prio=31 tid=0x00007f97c48d1800 nid=0x7c03 waiting on condition [0x0000700013b13000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at io.netty.util.HashedWheelTimer$Worker.waitForNextTick(HashedWheelTimer.java:567)
	at io.netty.util.HashedWheelTimer$Worker.run(HashedWheelTimer.java:466)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-AsyncTimeout" #58 daemon prio=5 os_prio=31 tid=0x00007f97d0902800 nid=0x7b03 waiting on condition [0x0000700013a10000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at org.apache.coyote.AbstractProtocol$AsyncTimeout.run(AbstractProtocol.java:1143)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-Acceptor-0" #57 daemon prio=5 os_prio=31 tid=0x00007f97c4e2a000 nid=0x7f03 runnable [0x000070001390d000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.ServerSocketChannelImpl.accept0(Native Method)
	at sun.nio.ch.ServerSocketChannelImpl.accept(ServerSocketChannelImpl.java:422)
	at sun.nio.ch.ServerSocketChannelImpl.accept(ServerSocketChannelImpl.java:250)
	- locked <0x000000079a86e788> (a java.lang.Object)
	at org.apache.tomcat.util.net.NioEndpoint$Acceptor.run(NioEndpoint.java:455)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-ClientPoller-1" #56 daemon prio=5 os_prio=31 tid=0x00007f97c4a64000 nid=0x8003 runnable [0x000070001380a000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x000000079b0b4f80> (a sun.nio.ch.Util$3)
	- locked <0x000000079b0b4f70> (a java.util.Collections$UnmodifiableSet)
	- locked <0x000000079b0b4e50> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioEndpoint$Poller.run(NioEndpoint.java:798)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-ClientPoller-0" #55 daemon prio=5 os_prio=31 tid=0x00007f97d0026000 nid=0x7803 runnable [0x0000700013707000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x000000079b0b4748> (a sun.nio.ch.Util$3)
	- locked <0x000000079b0b4738> (a java.util.Collections$UnmodifiableSet)
	- locked <0x000000079b0b4618> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioEndpoint$Poller.run(NioEndpoint.java:798)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-10" #54 daemon prio=5 os_prio=31 tid=0x00007f97d07f6800 nid=0x7603 waiting on condition [0x0000700013604000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-9" #53 daemon prio=5 os_prio=31 tid=0x00007f97d0269800 nid=0x8203 waiting on condition [0x0000700013501000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-8" #52 daemon prio=5 os_prio=31 tid=0x00007f97c5a1f800 nid=0x7503 waiting on condition [0x00007000133fe000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-7" #51 daemon prio=5 os_prio=31 tid=0x00007f97c596a000 nid=0x8403 waiting on condition [0x00007000132fb000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-6" #50 daemon prio=5 os_prio=31 tid=0x00007f97c5bb4800 nid=0x7303 waiting on condition [0x00007000131f8000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-5" #49 daemon prio=5 os_prio=31 tid=0x00007f97c4b38000 nid=0x7203 waiting on condition [0x00007000130f5000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-4" #48 daemon prio=5 os_prio=31 tid=0x00007f97c4a7f000 nid=0x7103 waiting on condition [0x0000700012ff2000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-3" #47 daemon prio=5 os_prio=31 tid=0x00007f97c4de8000 nid=0x8803 waiting on condition [0x0000700012eef000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-2" #46 daemon prio=5 os_prio=31 tid=0x00007f97d0e46800 nid=0x6f03 waiting on condition [0x0000700012dec000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8080-exec-1" #45 daemon prio=5 os_prio=31 tid=0x00007f97d09fa000 nid=0x8a03 waiting on condition [0x0000700012ce9000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x000000079a92d4a8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:103)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:31)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)

"NioBlockingSelector.BlockPoller-1" #44 daemon prio=5 os_prio=31 tid=0x00007f97c4ec5800 nid=0x6c03 runnable [0x0000700012be6000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x000000079a86ef88> (a sun.nio.ch.Util$3)
	- locked <0x000000079a86ef78> (a java.util.Collections$UnmodifiableSet)
	- locked <0x000000079a86ee58> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller.run(NioBlockingSelector.java:298)

"spring.cloud.inetutils" #43 daemon prio=5 os_prio=31 tid=0x00007f97c64dc800 nid=0x6b03 waiting on condition [0x0000700012ae3000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000007beeae070> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"container-0" #41 prio=5 os_prio=31 tid=0x00007f97d1401800 nid=0x6903 waiting on condition [0x00007000129e0000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at org.apache.catalina.core.StandardServer.await(StandardServer.java:427)
	at org.springframework.boot.web.embedded.tomcat.TomcatWebServer$1.run(TomcatWebServer.java:182)

"ContainerBackgroundProcessor[StandardEngine[Tomcat]]" #40 daemon prio=5 os_prio=31 tid=0x00007f97d0e47800 nid=0x6803 waiting on condition [0x00007000128dd000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at org.apache.catalina.core.ContainerBase$ContainerBackgroundProcessor.run(ContainerBase.java:1357)
	at java.lang.Thread.run(Thread.java:748)

"RxIoScheduler-1 (Evictor)" #39 daemon prio=5 os_prio=31 tid=0x00007f97d0310000 nid=0x8e03 waiting on condition [0x00007000127da000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000007bf112b08> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.awaitNanos(AbstractQueuedSynchronizer.java:2078)
	at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1093)
	at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:809)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"FeignApacheHttpClientConfiguration.connectionManagerTimer" #36 daemon prio=5 os_prio=31 tid=0x00007f97c4e5c000 nid=0x9403 in Object.wait() [0x00007000124d1000]
   java.lang.Thread.State: TIMED_WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x000000074273cb60> (a java.util.TaskQueue)
	at java.util.TimerThread.mainLoop(Timer.java:552)
	- locked <0x000000074273cb60> (a java.util.TaskQueue)
	at java.util.TimerThread.run(Timer.java:505)

"Druid-ConnectionPool-Destroy-1003693033" #35 daemon prio=5 os_prio=31 tid=0x00007f97c85ec800 nid=0x6703 waiting on condition [0x00007000123ce000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at com.alibaba.druid.pool.DruidDataSource$DestroyConnectionThread.run(DruidDataSource.java:2540)

"Druid-ConnectionPool-Create-1003693033" #34 daemon prio=5 os_prio=31 tid=0x00007f97d026a800 nid=0x6603 waiting on condition [0x00007000122cb000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000007415b8858> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at com.alibaba.druid.pool.DruidDataSource$CreateConnectionThread.run(DruidDataSource.java:2443)

"mysql-cj-abandoned-connection-cleanup" #33 daemon prio=5 os_prio=31 tid=0x00007f97ccf6b000 nid=0x9803 in Object.wait() [0x00007000121c8000]
   java.lang.Thread.State: TIMED_WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x0000000741685900> (a java.lang.ref.ReferenceQueue$Lock)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)
	- locked <0x0000000741685900> (a java.lang.ref.ReferenceQueue$Lock)
	at com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.run(AbandonedConnectionCleanupThread.java:85)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-16" #32 prio=5 os_prio=31 tid=0x00007f97c6c47000 nid=0x6403 runnable [0x00007000120c5000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007411d3ec8> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007411d3ee0> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007411d3e78> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-15" #31 prio=5 os_prio=31 tid=0x00007f97c4cfc800 nid=0x9903 runnable [0x0000700011fc2000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007411d43b0> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412ab968> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007411d4300> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-14" #30 prio=5 os_prio=31 tid=0x00007f97c690a800 nid=0x9a03 runnable [0x0000700011ebf000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412aba48> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412aba60> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412ab9f8> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-13" #29 prio=5 os_prio=31 tid=0x00007f97c9ff7000 nid=0x9b03 runnable [0x0000700011dbc000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412a9ac0> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412a9ad8> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412a9a70> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-12" #28 prio=5 os_prio=31 tid=0x00007f97c9cc1000 nid=0x5f03 runnable [0x0000700011cb9000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412d7f28> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412d8818> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412d7e88> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-11" #27 prio=5 os_prio=31 tid=0x00007f97c635a800 nid=0x5d03 runnable [0x0000700011bb6000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412aa258> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412aa270> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412aa208> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-10" #26 prio=5 os_prio=31 tid=0x00007f97c49d4800 nid=0x9d03 runnable [0x0000700011ab3000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x000000074133c058> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x000000074133c0b0> (a java.util.Collections$UnmodifiableSet)
	- locked <0x000000074133bfb8> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-9" #25 prio=5 os_prio=31 tid=0x00007f97c4fd7800 nid=0x5c03 runnable [0x00007000119b0000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412f6b50> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412f6ba8> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412f6ab0> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-8" #24 prio=5 os_prio=31 tid=0x00007f97c6412000 nid=0x5a03 runnable [0x00007000118ad000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412ac980> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412ac998> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412ac930> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-7" #23 prio=5 os_prio=31 tid=0x00007f97c58f5800 nid=0x5903 runnable [0x00007000117aa000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412f63b8> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412f6410> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412f6318> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-6" #22 prio=5 os_prio=31 tid=0x00007f97c4dee800 nid=0x5803 runnable [0x00007000116a7000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412aaa08> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412aaa20> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412aa9b8> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-5" #21 prio=5 os_prio=31 tid=0x00007f97cc87a000 nid=0xa203 runnable [0x00007000115a4000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412e9360> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412e93b8> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412e92c0> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-4" #20 prio=5 os_prio=31 tid=0x00007f97c6ded800 nid=0xa303 runnable [0x00007000114a1000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000007412e8bb8> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000007412e8c10> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000007412e8b18> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-3" #19 prio=5 os_prio=31 tid=0x00007f97cf300800 nid=0xa503 runnable [0x000070001139e000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x0000000741354d88> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x0000000741354da0> (a java.util.Collections$UnmodifiableSet)
	- locked <0x0000000741354d38> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-2" #18 prio=5 os_prio=31 tid=0x00007f97ccfc1800 nid=0x5607 runnable [0x000070001129b000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x0000000741350e10> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x0000000741350e28> (a java.util.Collections$UnmodifiableSet)
	- locked <0x0000000741350dc0> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"redisson-netty-1-1" #17 prio=5 os_prio=31 tid=0x00007f97cf8c0000 nid=0x4d0b runnable [0x0000700011198000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x000000074133c508> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x000000074133c560> (a java.util.Collections$UnmodifiableSet)
	- locked <0x000000074133c458> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.SelectedSelectionKeySetSelector.select(SelectedSelectionKeySetSelector.java:62)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:755)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:410)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)

"net.sf.ehcache.CacheManager@15f47664" #15 daemon prio=5 os_prio=31 tid=0x00007f97cbd26800 nid=0xa90f in Object.wait() [0x0000700011095000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x000000074118de20> (a java.util.TaskQueue)
	at java.lang.Object.wait(Object.java:502)
	at java.util.TimerThread.mainLoop(Timer.java:526)
	- locked <0x000000074118de20> (a java.util.TaskQueue)
	at java.util.TimerThread.run(Timer.java:505)

"Service Thread" #9 daemon prio=9 os_prio=31 tid=0x00007f97c6844800 nid=0x4003 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C1 CompilerThread3" #8 daemon prio=9 os_prio=31 tid=0x00007f97c5822800 nid=0x3f03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread2" #7 daemon prio=9 os_prio=31 tid=0x00007f97c5822000 nid=0x3d03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread1" #6 daemon prio=9 os_prio=31 tid=0x00007f97c684b800 nid=0x4503 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" #5 daemon prio=9 os_prio=31 tid=0x00007f97c684a800 nid=0x4703 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Signal Dispatcher" #4 daemon prio=9 os_prio=31 tid=0x00007f97c6830800 nid=0x4903 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Finalizer" #3 daemon prio=8 os_prio=31 tid=0x00007f97c6812000 nid=0x5003 in Object.wait() [0x000070001087d000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x00000007400063d0> (a java.lang.ref.ReferenceQueue$Lock)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)
	- locked <0x00000007400063d0> (a java.lang.ref.ReferenceQueue$Lock)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:165)
	at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:216)

"Reference Handler" #2 daemon prio=10 os_prio=31 tid=0x00007f97c6811800 nid=0x5103 in Object.wait() [0x000070001077a000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x00000007400080b0> (a java.lang.ref.Reference$Lock)
	at java.lang.Object.wait(Object.java:502)
	at java.lang.ref.Reference.tryHandlePending(Reference.java:191)
	- locked <0x00000007400080b0> (a java.lang.ref.Reference$Lock)
	at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)

"VM Thread" os_prio=31 tid=0x00007f97c4848800 nid=0x5203 runnable 

"GC task thread#0 (ParallelGC)" os_prio=31 tid=0x00007f97c6808000 nid=0x1e07 runnable 

"GC task thread#1 (ParallelGC)" os_prio=31 tid=0x00007f97c6809000 nid=0x2003 runnable 

"GC task thread#2 (ParallelGC)" os_prio=31 tid=0x00007f97c4807000 nid=0x2b03 runnable 

"GC task thread#3 (ParallelGC)" os_prio=31 tid=0x00007f97c4808000 nid=0x2d03 runnable 

"GC task thread#4 (ParallelGC)" os_prio=31 tid=0x00007f97c4804800 nid=0x2f03 runnable 

"GC task thread#5 (ParallelGC)" os_prio=31 tid=0x00007f97c6809800 nid=0x3103 runnable 

"GC task thread#6 (ParallelGC)" os_prio=31 tid=0x00007f97c680a000 nid=0x3203 runnable 

"GC task thread#7 (ParallelGC)" os_prio=31 tid=0x00007f97c680a800 nid=0x3303 runnable 

"VM Periodic Task Thread" os_prio=31 tid=0x00007f97c6838000 nid=0x4103 waiting on condition 

JNI global references: 1610

```

#### 1.2   命名行格式 
- jstack [ option ] pid ，这个最常用
- jstack [ option ] executable core 
- jstack [ option ] [server-id@]remote-hostname-or-IP 


#### 1.3   在thread dump中，要留意下面几种状态 
- 死锁，Deadlock
- 等待资源，Waiting on condition 
    -  等待获取监视器，Waiting on monitor entry 
- 阻塞，Blocked 
    -  执行中，Runnable 
    -  暂停，Suspended 
    -  对象等待中，Object.wait() 或 TIMED_WAITING 
    -  停止，Parked 

 

#### 1.4   在thread dump中，有几种线程的定义如下 
线程名称 所属 解释说明 
Attach Listener JVM Attach Listener 线程是负责接收到外部的命令，而对该命令进行执行的并且吧结果返回给发送者。通常我们会用一些命令去要求jvm给我们一些反馈信息，如：java -version、jmap、jstack等等。 如果该线程在jvm启动的时候没有初始化，那么，则会在用户第一次执行jvm命令时，得到启动。 
Signal Dispatcher JVM 前面我们提到第一个Attach Listener线程的职责是接收外部jvm命令，当命令接收成功后，会交给signal dispather 线程去进行分发到各个不同的模块处理命令，并且返回处理结果。 signal dispather线程也是在第一次接收外部jvm命令时，进行初始化工作。 
CompilerThread0 JVM 用来调用JITing，实时编译装卸class 。 通常，jvm会启动多个线程来处理这部分工作，线程名称后面的数字也会累加，例如：CompilerThread1 
Concurrent Mark-Sweep GC Thread JVM 并发标记清除垃圾回收器（就是通常所说的CMS GC）线程， 该线程主要针对于老年代垃圾回收。ps：启用该垃圾回收器，需要在jvm启动参数中加上： -XX:+UseConcMarkSweepGC 
DestroyJavaVM JVM 执行main()的线程在main执行完后调用JNI中的 jni_DestroyJavaVM() 方法唤起DestroyJavaVM 线程。   JVM在 Jboss 服务器启动之后，就会唤起DestroyJavaVM线程，处于等待状态，等待其它线程（java线程和native线程）退出时通知它卸载JVM。线程退出时，都会判断自己当前是否是整个JVM中最后一个非deamon线程，如果是，则通知DestroyJavaVM 线程卸载JVM。 
ps： 
扩展一下： 
1.如果线程退出时判断自己不为最后一个非deamon线程，那么调用thread->exit(false) ，并在其中抛出thread_end事件，jvm不退出。 
2.如果线程退出时判断自己为最后一个非deamon线程，那么调用before_exit() 方法，抛出两个事件：  事件1：thread_end 线程结束事件、事件2：VM的death事件。 
    然后调用thread->exit(true) 方法，接下来把线程从active list卸下，删除线程等等一系列工作执行完成后，则通知正在等待的DestroyJavaVM 线程执行卸载JVM操作。 
ContainerBackgroundProcessor 线程 JBOSS 它是一个守护线程, 在jboss服务器在启动的时候就初始化了,主要工作是定期去检查有没有Session过期.过期则清除. 
参考：http://liudeh-009.iteye.com/blog/1584876 

Dispatcher-Thread-3  线程 Log4j       Log4j具有异步打印日志的功能，需要异步打印日志的Appender都需要注册到 AsyncAppender对象里面去，由AsyncAppender进行监听，决定何时触发日志打印操作。 AsyncAppender如果监听到它管辖范围内的Appender有打印日志的操作，则给这个Appender生成一个相应的event，并将该event保存在一个buffuer区域内。  Dispatcher-Thread-3线程负责判断这个event缓存区是否已经满了，如果已经满了，则将缓存区内的所有event分发到Appender容器里面去，那些注册上来的Appender收到自己的event后，则开始处理自己的日志打印工作。 Dispatcher-Thread-3线程是一个守护线程。 
Finalizer线程 JVM 这个线程也是在main线程之后创建的，其优先级为10，主要用于在垃圾收集前，调用对象的finalize()方法；关于Finalizer线程的几点： 
1) 只有当开始一轮垃圾收集时，才会开始调用finalize()方法；因此并不是所有对象的finalize()方法都会被执行； 
2) 该线程也是daemon线程，因此如果虚拟机中没有其他非daemon线程，不管该线程有没有执行完finalize()方法，JVM也会退出； 
3) JVM在垃圾收集时会将失去引用的对象包装成Finalizer对象（Reference的实现），并放入ReferenceQueue，由Finalizer线程来处理；最后将该Finalizer对象的引用置为null，由垃圾收集器来回收； 
4) JVM为什么要单独用一个线程来执行finalize()方法呢？如果JVM的垃圾收集线程自己来做，很有可能由于在finalize()方法中误操作导致GC线程停止或不可控，这对GC线程来说是一种灾难； 
Gang worker#0 JVM JVM 用于做新生代垃圾回收（monir gc）的一个线程。#号后面是线程编号，例如：Gang worker#1 
GC Daemon JVM GC Daemon 线程是JVM为RMI提供远程分布式GC使用的，GC Daemon线程里面会主动调用System.gc()方法，对服务器进行Full GC。 其初衷是当 RMI 服务器返回一个对象到其客户机（远程方法的调用方）时，其跟踪远程对象在客户机中的使用。当再没有更多的对客户机上远程对象的引用时，或者如果引用的“租借”过期并且没有更新，服务器将垃圾回收远程对象。 
不过，我们现在jvm启动参数都加上了-XX:+DisableExplicitGC配置，所以，这个线程只有打酱油的份了。 
IdleRemover JBOSS Jboss连接池有一个最小值， 该线程每过一段时间都会被Jboss唤起，用于检查和销毁连接池中空闲和无效的连接，直到剩余的连接数小于等于它的最小值。 
Java2D Disposer JVM           这个线程主要服务于awt的各个组件。 说起该线程的主要工作职责前，需要先介绍一下Disposer类是干嘛的。 Disposer提供一个addRecord方法。 如果你想在一个对象被销毁前再做一些善后工作，那么，你可以调用Disposer#addRecord方法，将这个对象和一个自定义的DisposerRecord接口实现类，一起传入进去，进行注册。  
          Disposer类会唤起“Java2D Disposer”线程，该线程会扫描已注册的这些对象是否要被回收了，如果是，则调用该对象对应的DisposerRecord实现类里面的dispose方法。 
          Disposer实际上不限于在awt应用场景，只是awt里面的很多组件需要访问很多操作系统资源，所以，这些组件在被回收时，需要先释放这些资源。 
InsttoolCacheScheduler_ 
QuartzSchedulerThread Quartz         InsttoolCacheScheduler_QuartzSchedulerThread是Quartz的主线程，它主要负责实时的获取下一个时间点要触发的触发器，然后执行触发器相关联的作业 。 
         原理大致如下： 
         Spring和Quartz结合使用的场景下，Spring IOC容器初始化时会创建并初始化Quartz线程池（TreadPool），并启动它。刚启动时线程池中每个线程都处于等待状态，等待外界给他分配Runnable（持有作业对象的线程）。 
         继而接着初始化并启动Quartz的主线程（InsttoolCacheScheduler_QuartzSchedulerThread），该线程自启动后就会处于等待状态。等待外界给出工作信号之后，该主线程的run方法才实质上开始工作。run中会获取JobStore中下一次要触发的作业，拿到之后会一直等待到该作业的真正触发时间，然后将该作业包装成一个JobRunShell对象（该对象实现了Runnable接口，其实看是上面TreadPool中等待外界分配给他的Runnable），然后将刚创建的JobRunShell交给线程池，由线程池负责执行作业。 
线程池收到Runnable后，从线程池一个线程启动Runnable，反射调用JobRunShell中的run方法，run方法执行完成之后， TreadPool将该线程回收至空闲线程中。 
InsttoolCacheScheduler_Worker-2 Quartz InsttoolCacheScheduler_Worker-2线程就是ThreadPool线程的一个简单实现，它主要负责分配线程资源去执行 
InsttoolCacheScheduler_QuartzSchedulerThread线程交给它的调度任务（也就是JobRunShell）。 
JBossLifeThread Jboss         Jboss主线程启动成功，应用程序部署完毕之后将JBossLifeThread线程实例化并且start，JBossLifeThread线程启动成功之后就处于等待状态，以保持Jboss Java进程处于存活中。  所得比较通俗一点，就是Jboss启动流程执行完毕之后，为什么没有结束？ 就是因为有这个线程hold主了它。 牛b吧～～ 
JBoss System Threads(1)-1 Jboss   该线程是一个socket服务，默认端口号为： 1099。 主要用于接收外部naming service（Jboss  JNDI）请求。 
JCA PoolFiller Jboss     该线程主要为JBoss内部提供连接池的托管。  简单介绍一下工作原理 ： 
    Jboss内部凡是有远程连接需求的类，都需要实现ManagedConnectionFactory接口，例如需要做JDBC连接的 
XAManagedConnectionFactory对象，就实现了该接口。然后将XAManagedConnectionFactory对象，还有其它信息一起包装到InternalManagedConnectionPool对象里面，接着将InternalManagedConnectionPool交给PoolFiller对象里面的列队进行管理。   JCA PoolFiller线程会定期判断列队内是否有需要创建和管理的InternalManagedConnectionPool对象，如果有的话，则调用该对象的fillToMin方法， 触发它去创建相应的远程连接，并且将这个连接维护到它相应的连接池里面去。 
JDWP Event Helper Thread JVM            
JDWP是通讯交互协议，它定义了调试器和被调试程序之间传递信息的格式。它详细完整地定义了请求命令、回应数据和错误代码，保证了前端和后端的JVMTI和JDI的通信通畅。  该线程主要负责将JDI事件映射成JVMTI信号，以达到调试过程中操作JVM的目的。   


JDWP Transport Listener: dt_socket JVM 该线程是一个Java Debugger的监听器线程，负责受理客户端的debug请求。 通常我们习惯将它的监听端口设置为8787。 
Low Memory Detector JVM 这个线程是负责对可使用内存进行检测，如果发现可用内存低，分配新的内存空间。 
process reaper JVM     该线程负责去执行一个 OS 命令行的操作。 
Reference Handler JVM         JVM在创建main线程后就创建Reference Handler线程，其优先级最高，为10，它主要用于处理引用对象本身（软引用、弱引用、虚引用）的垃圾回收问题 。 
Surrogate Locker Thread (CMS) JVM           这个线程主要用于配合CMS垃圾回收器使用，它是一个守护线程，其主要负责处理GC过程中，Java层的Reference（指软引用、弱引用等等）与jvm 内部层面的对象状态同步。 这里对它们的实现稍微做一下介绍：这里拿 WeakHashMap做例子，将一些关键点先列出来（我们后面会将这些关键点全部串起来）： 
1.  我们知道HashMap用Entry[]数组来存储数据的，WeakHashMap也不例外, 内部有一个Entry[]数组。 
2.   WeakHashMap的Entry比较特殊，它的继承体系结构为Entry->WeakReference->Reference 。 
3.  Reference 里面有一个全局锁对象：Lock，它也被称为pending_lock.    注意：它是静态对象。 
4.       Reference  里面有一个静态变量：pending。 
5.  Reference  里面有一个静态内部类：ReferenceHandler的线程，它在static块里面被初始化并且启动，启动完成后处于wait状态，它在一个Lock同步锁模块中等待。 
6.  另外，WeakHashMap里面还实例化了一个ReferenceQueue列队，这个列队的作用，后面会提到。 
7.  上面关键点就介绍完毕了，下面我们把他们串起来。 
     假设，WeakHashMap对象里面已经保存了很多对象的引用。 JVM 在进行CMS GC的时候，会创建一个ConcurrentMarkSweepThread（简称CMST）线程去进行GC，ConcurrentMarkSweepThread线程被创建的同时会创建一个SurrogateLockerThread（简称SLT）线程并且启动它，SLT启动之后，处于等待阶段。CMST开始GC时，会发一个消息给SLT让它去获取Java层Reference对象的全局锁：Lock。 直到CMS GC完毕之后，JVM 会将WeakHashMap中所有被回收的对象所属的WeakReference容器对象放入到Reference 的pending属性当中（每次GC完毕之后，pending属性基本上都不会为null了），然后通知SLT释放并且notify全局锁:Lock。此时激活了ReferenceHandler线程的run方法，使其脱离wait状态，开始工作了。ReferenceHandler这个线程会将pending中的所有WeakReference对象都移动到它们各自的列队当中，比如当前这个WeakReference属于某个WeakHashMap对象，那么它就会被放入相应的ReferenceQueue列队里面（该列队是链表结构）。 当我们下次从WeakHashMap对象里面get、put数据或者调用size方法的时候，WeakHashMap就会将ReferenceQueue列队中的WeakReference依依poll出来去和Entry[]数据做比较，如果发现相同的，则说明这个Entry所保存的对象已经被GC掉了，那么将Entry[]内的Entry对象剔除掉。 
taskObjectTimerFactory JVM           顾名思义，该线程就是用来执行任务的。 当我们把一个认为交给Timer对象，并且告诉它执行时间，周期时间后，Timer就会将该任务放入任务列队，并且通知taskObjectTimerFactory线程去处理任务，taskObjectTimerFactory线程会将状态为取消的任务从任务列队中移除，如果任务是非重复执行类型的，则在执行完该任务后，将它从任务列队中移除，如果该任务是需要重复执行的，则计算出它下一次执行的时间点。 
VM Periodic Task Thread JVM         该线程是JVM周期性任务调度的线程，它由WatcherThread创建，是一个单例对象。 该线程在JVM内使用得比较频繁，比如：定期的内存监控、JVM运行状况监控，还有我们经常需要去执行一些jstat 这类命令查看gc的情况，如下： 
jstat -gcutil 23483 250 7   这个命令告诉jvm在控制台打印PID为：23483的gc情况，间隔250毫秒打印一次，一共打印7次。 
VM Thread JVM          这个线程就比较牛b了，是jvm里面的线程母体，根据hotspot源码（vmThread.hpp）里面的注释，它是一个单例的对象（最原始的线程）会产生或触发所有其他的线程，这个单个的VM线程是会被其他线程所使用来做一些VM操作（如，清扫垃圾等）。 
         在 VMThread 的结构体里有一个VMOperationQueue列队，所有的VM线程操作(vm_operation)都会被保存到这个列队当中，VMThread 本身就是一个线程，它的线程负责执行一个自轮询的loop函数(具体可以参考：VMThread.cpp里面的void VMThread::loop()) ，该loop函数从VMOperationQueue列队中按照优先级取出当前需要执行的操作对象(VM_Operation)，并且调用VM_Operation->evaluate函数去执行该操作类型本身的业务逻辑。 
       ps：VM操作类型被定义在vm_operations.hpp文件内，列举几个：ThreadStop、ThreadDump、PrintThreads、GenCollectFull、GenCollectFullConcurrent、CMS_Initial_Mark、CMS_Final_Remark….. 有兴趣的同学，可以自己去查看源文件。 
(搬运自 http://blog.csdn.net/a43350860/article/details/8134234 感谢原著作者) 

2．Jmap 
2.1   得到运行java程序的内存分配的详细情况。例如实例个数，大小等 

2.2   命名行格式 
jmap [ option ] pid 
jmap [ option ] executable core 
jmap [ option ] [server-id@]remote-hostname-or-IP 

-dump:[live,]format=b,file=<filename> 使用hprof二进制形式,输出jvm的heap内容到文件=. live子选项是可选的，假如指定live选项,那么只输出活的对象到文件. 
-finalizerinfo 打印正等候回收的对象的信息. 
-heap 打印heap的概要信息，GC使用的算法，heap的配置及wise heap的使用情况. 
-histo[:live] 打印每个class的实例数目,内存占用,类全名信息. VM的内部类名字开头会加上前缀”*”. 如果live子参数加上后,只统计活的对象数量. 
-permstat 打印classload和jvm heap长久层的信息. 包含每个classloader的名字,活泼性,地址,父classloader和加载的class数量. 另外,内部String的数量和占用内存数也会打印出来. 
-F 强迫.在pid没有相应的时候使用-dump或者-histo参数. 在这个模式下,live子参数无效. 
-h | -help 打印辅助信息 
-J 传递参数给jmap启动的jvm. 

2.3   使用例子 
jmap -histo pid(查看实例) 
 

jmap -dump:format=b,file=heap.bin pid(导出内存，据说对性能有影响，小心使用) 
(format=b是通过二进制的意思，但是能不能导出文本文件我没找到，知道的告诉我) 
把内存结构全部dump到二进制文件中，通过IBM的HeapAnalyzer和eclipse的MemoryAnalyzer都可以分析内存结构。 
这个是我用HeapAnalyzer查看出的我们daily的内存结构，已经列出了可能存在的问题。(这个工具我不熟悉，只供大家参考) 
 

下面是我用eclipse 的MemoryAnalyzer查看内存结构图 
 

 
 

上面的是eclipse分析内存泄漏分析出的。这个功能点非常多。可以慢慢学习 


3．Jstat 
3.1   这是一个比较实用的一个命令，可以观察到classloader，compiler，gc相关信息。可以时时监控资源和性能 

3.2      命令格式 
-class：统计class loader行为信息 
-compile：统计编译行为信息 
-gc：统计jdk gc时heap信息 
-gccapacity：统计不同的generations（不知道怎么翻译好，包括新生区，老年区，permanent区）相应的heap容量情况 
-gccause：统计gc的情况，（同-gcutil）和引起gc的事件 
-gcnew：统计gc时，新生代的情况 
-gcnewcapacity：统计gc时，新生代heap容量 
-gcold：统计gc时，老年区的情况 
-gcoldcapacity：统计gc时，老年区heap容量 
-gcpermcapacity：统计gc时，permanent区heap容量 
-gcutil：统计gc时，heap情况 

3.3   输出参数内容 
S0  — Heap上的 Survivor space 0 区已使用空间的百分比 
S0C：S0当前容量的大小 
S0U：S0已经使用的大小 
S1  — Heap上的 Survivor space 1 区已使用空间的百分比 
S1C：S1当前容量的大小 
S1U：S1已经使用的大小 
E   — Heap上的 Eden space 区已使用空间的百分比 
EC：Eden space当前容量的大小 
EU：Eden space已经使用的大小 
O   — Heap上的 Old space 区已使用空间的百分比 
OC：Old space当前容量的大小 
OU：Old space已经使用的大小 
P   — Perm space 区已使用空间的百分比 
OC：Perm space当前容量的大小 
OU：Perm space已经使用的大小 
YGC — 从应用程序启动到采样时发生 Young GC 的次数 
YGCT– 从应用程序启动到采样时 Young GC 所用的时间(单位秒) 
FGC — 从应用程序启动到采样时发生 Full GC 的次数 
FGCT– 从应用程序启动到采样时 Full GC 所用的时间(单位秒) 
GCT — 从应用程序启动到采样时用于垃圾回收的总时间(单位秒)，它的值等于YGC+FGC 

例子1 
 

例子2(连续5次) 
 

例子3(PGCMN显示的是最小perm的内存使用量，PGCMX显示的是perm的内存最大使用量，PGC是当前新生成的perm内存占用量，PC是但前perm内存占用量) 
 
这个工具的参数非常多，据说基本能覆盖jprofile等收费工具的所有功能了。多用用对于系统调优还是很有帮助的 


注1：我们在daily用这样命令时，都要用-F参数的。因为我们的用户都不是启动命令的用户 

注2：daily的这些命令好像都没有配置到环境变量里面，这个是我在自己应用机器里面看到的。需要去jdk目录底下执行。Sudo当然是必须的了