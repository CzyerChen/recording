- 原子操作在Java并发中有大量用到，它不用加锁，以一定量的冲突减少了加锁的性能损耗，调用操作系统底层的compareAndSet很好的应对并发
- 原子的操作有四种：原子更新数据类型、原子更新数组、原子更新引用、原子更新属性
- 都属于java.util.concurrent.atomic下,共13个类


### 一、原子更新数据类型
- 原子数据类型有以下几种
```
AtomicBoolean：原子更新布尔类型
AtomicInteger : 原子更新整型
AtomicLong：原子更新整型
```
- 以上三个类型底层实现都是相似的，以下通过原子整型来讨论
- 操作方法有：
```text
int addAndGet(int delta):以原子方式将输入的数值与实例中的数值(value)相加，并返回结果

boolean compareAndSet(int expect,int update):如果输入的数值等于预期值，则以原子方式将该值设置为输入的值

int getAndIncrement()：以原子方式将当前值加1，返回自增前的值

void lazySet(int newValue)：最终会设置成newValue，使用lazySet设置值后，可能导致其线程在之后的一小段时间内还是可以读到旧的值

int getAndSet(int newValue):以原子方式设置为newValue的值，并返回旧值
```
- 以上底层进行数据交换和变更的时候，都是通过unsafe类调用底层操作系统指令来实现的，以下是getAndIncrement的部分源码
```text
public final int getAndIncrement() {
        for (;;) {//通过自旋锁，不用加锁阻塞，也可以根据内部条件不断重试，最终满足需求，将数据修改
            int current = get();//AtomicInteger存储的数组
            int next = current + 1;//对当前值加1
            /**
            *   CAS操作更新，先检查当前值与current是否等于
            *   如果相等，将AtomicInteger的当前数组更新成next
            *   如果不相等，则返回false，重新循环更新
            */
            if (compareAndSet(current, next))
                return current;//返回更新前的值
        }
    }

public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);//底层调用unsafe类
    }
    
public native boolean compareAndSwapInt(Object obj, long offset,int expect, int update);
```
- 以上unsafe就提供了三种比较compareAndSwapObject/compareAndSwapInt/compareAndSwapLong，没有看到boolean ,float char ,double的转换
- 其中，AtomicBoolean先把boolean类型的参数转换成int类型，然后再调用Unsafe的compareAndSwapInt来进行CAS操作，其他的类型自己可以查看一下源码



### 二、原子更新数组
- 三种原子数组
```text
AtomicIntegerArray:原子更新整型数组里的元素
AtomicLongArray：原子更新长整型数组里的元素
AtomicReferenceArray：原子更新引用类型数组里的元素
```
- AtomicIntegerArray的常用方法有:
```text
addAndGet(int i,int delta):以原子方式将输入值与数组中索引i的元素相加。

boolean compareAndSet(int i,int expect,int update)：如果当前值等于预期值，则以原子方式将数组位置i的元素设置成update值
```
### 三、原子更新引用
- 有三个类：
```text
AtomicReference：原子更新引用类型
AtomicReferenceFieldUpdater：原子更新引用类型里的字段
AtomicMarkableReference：原子更新带有标记为的引用类型
可以原子更新一个布尔类型的标记位和引用类型
构造方法是AtomicMarkableReference(V initialRef,boolean initialMark)
```
- 例子见AtomicTest

### 四、原子更新字段
- 有三个类：
```text
AtomicIntegerFieldUpdater：原子更新整型的字段的更新器
AtomicLongFieldUpdater：原子更新长整型的字段的更新器
AtomicStampedReference：原子更新带有版本号的引用类型
该类型将整数值与引用关联起来，可用于原子的更新数据和数据的版本号，可以解决使用CAS进行原子更新时可能出现的ABA问题

```
- 需要更新的字段需要申明为“public volatile”,不然会提示private 外部无法获取
- 例子见AtomicTest