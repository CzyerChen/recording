> CAS是一种乐观并发策略，虚拟机通过UNSAFE这个类，得以在硬件级别操控操作系统级别的线程，进行并发控制

> 了解CAS 就会要了解Unsafe这个类

> JUC包下的原子处理都是通过CAS来实现的


### 原子操作的概念
- 一个原子方法，最后都是调用unsafe最终实现CompareAndSwapInt CompareAndSwapLong  CompareAndSwapObject
```text
 public final int addAndGet(int delta) {
	        return unsafe.getAndAddInt(this, valueOffset, delta) + delta;
	    }
	
	    public final int getAndAddInt(Object var1, long var2, int var4) {
	        int var5;
	        do {
	            var5 = this.getIntVolatile(var1, var2);
	        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
	
	        return var5;
	    }

```
- CAS可以保证一次的读-改-写操作是原子操作
- CompareAndSwapInt CompareAndSwapLong  CompareAndSwapObject 包含四个参数：对象引用(Object)，对象地址(long valueOffset)，预期值(Object expected)，修改值(Object value)
- 整个“比较+更新”操作封装在compareAndSwapInt()中，在JNI里是借助于一个CPU指令完成的，属于原子操作，可以保证多个线程都能够看到同一个变量的修改值。
- 后续JDK通过CPU的cmpxchg指令，去比较寄存器中的 A 和 内存中的值 V。如果相等，就把要写入的新值 B 存入内存中。如果不相等，就将内存值 V 赋值给寄存器中的值 A。然后通过Java代码中的while循环再次调用cmpxchg指令进行重试，直到设置成功为止。


### 总线加锁或者缓存加锁
- CPU提供了两种方法来实现多处理器的原子操作：总线加锁或者缓存加锁。

#### 总线加锁
- 总线加锁就是就是使用处理器提供的一个LOCK#信号，当一个处理器在总线上输出此信号时，其他处理器的请求将被阻塞住,那么该处理器可以独占使用共享内存。
- 但是这种处理方式有点霸道，把CPU和内存之间的通信锁住了，在锁定期间，其他处理器都不能其他内存地址的数据，开销大。所以就有了缓存加锁。

#### 缓存加锁
- 其实针对于上面那种情况我们只需要保证在同一时刻对某个内存地址的操作是原子性的即可。
- 缓存加锁就是缓存在内存区域的数据如果在加锁期间，当它执行锁操作写回内存时，处理器不在输出LOCK#信号，而是修改内部的内存地址，利用缓存一致性协议来保证原子性。
- 缓存一致性机制可以保证同一个内存区域的数据仅能被一个处理器修改，也就是说当CPU1修改缓存行中的i时使用缓存锁定，那么CPU2就不能同时缓存了i的缓存行

### 原子操作的问题
#### ABA问题
- 什么是ABA？
    - 线程A通过volatile读取了最新的变量A的值到线程1本地的缓存
    - 线程B通过volatile都去了最新的变量A的值到线程2本地的缓存
    - 线程B通过volatile修改变量A的值为B 写入主存，又将主存中变量A的值从B改回原来的值
    - 线程A在进行CAS的时候，发现值并没有改变，那就进行了修改，并返回成功，其实内存中的值已经进行了一定情况的修改，从最初的语义上来讲，这个值并不是当初内存中的情况了（已被修改）
    - 因而，ABA的问题就在实际应用中出现着，但是为什么没有发生什么影响呢？因为如果不考虑什么状态单纯用来计数，自然不会造成什么恶劣影响，但是对于版本敏感的业务逻辑，这边就不允许发生ABA的问题了

- 如何避免ABA的问题？
    - ABA的问题是因为没有版本次数控制的原因，在原子变量中有带版本控制的原子类型AtomicStampedReference
    - AtomicStampedReference的compareAndSet()方法定义如下：
    - compareAndSet有四个参数，分别表示：预期引用、更新后的引用、预期标志、更新后的标志。源码部分很好理解预期的引用 == 当前引用，预期的标识 == 当前标识，如果更新后的引用和标志和当前的引用和标志相等则直接返回true，否则通过Pair生成一个新的pair对象与当前pair CAS替换。Pair为AtomicStampedReference的内部类，主要用于记录引用和版本戳信息（标识）
    - Pair记录着对象的引用和版本戳，版本戳为int型，保持自增。同时Pair是一个不可变对象，其所有属性全部定义为final，对外提供一个of方法，该方法返回一个新建的Pari对象。pair对象定义为volatile，保证多线程环境下的可见性。在AtomicStampedReference中，大多方法都是通过调用Pair的of方法来产生一个新的Pair对象，然后赋值给变量pair
```text
public class Test {
	    private static AtomicInteger atomicInteger = new AtomicInteger(100);
	    private static AtomicStampedReference atomicStampedReference = new AtomicStampedReference(100,1);
	
	    public static void main(String[] args) throws InterruptedException {
	
	        //AtomicInteger
	        Thread at1 = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                atomicInteger.compareAndSet(100,110);
	                atomicInteger.compareAndSet(110,100);
	            }
	        });
	
	        Thread at2 = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    TimeUnit.SECONDS.sleep(2);      // at1,执行完
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                System.out.println("AtomicInteger:" + atomicInteger.compareAndSet(100,120));
	            }
	        });
	
	        at1.start();
	        at2.start();
	
	        at1.join();
	        at2.join();
	
	        //AtomicStampedReference
	
	        Thread tsf1 = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    //让 tsf2先获取stamp，导致预期时间戳不一致
	                    TimeUnit.SECONDS.sleep(2);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                // 预期引用：100，更新后的引用：110，预期标识getStamp() 更新后的标识getStamp() + 1
	                atomicStampedReference.compareAndSet(100,110,atomicStampedReference.getStamp(),atomicStampedReference.getStamp() + 1);
	                atomicStampedReference.compareAndSet(110,100,atomicStampedReference.getStamp(),atomicStampedReference.getStamp() + 1);
	            }
	        });
	
	        Thread tsf2 = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                int stamp = atomicStampedReference.getStamp();
	
	                try {
	                    TimeUnit.SECONDS.sleep(2);      //线程tsf1执行完
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                System.out.println("AtomicStampedReference:" +atomicStampedReference.compareAndSet(100,120,stamp,stamp + 1));
	            }
	        });
	
	        tsf1.start();
	        tsf2.start();
	    }
	
	}
```
- 其中100 110 100 和100  120的操作本来就是互斥的，在并发操作的理论下，就不应该能操作成功，AtomicInterger 却可以，不符合逻辑，使用带版本号的AtomicStampedReference就不可以
- AtomicStampedReference 封装了一个pair就包含了引用和版本号，当比较的时候引用和版本号都要判断
```text
public boolean compareAndSet(V   expectedReference,
                             V   newReference,
                             int expectedStamp,
                             int newStamp) {
    Pair<V> current = pair;
    return
        expectedReference == current.reference &&
        expectedStamp == current.stamp &&
        ((newReference == current.reference &&
          newStamp == current.stamp) ||
         casPair(current, Pair.of(newReference, newStamp)));
}
```


    



