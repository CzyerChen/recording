### java中的四种引用类型
- 为了更好的分类进行垃圾回收，为了很好的管理对象在JVM里面的生命周期
- Java中的引用分为： 强（StrongRerence）> 软(SoftReference) > 弱（WeakReference） > 虚(PlantomReference)四种
- StrongRerence为JVM内部实现。其他三类引用类型全部继承自Reference父类

### 强引用（StrongReference）
StrongRerence这个类并不存在，而是在JVM底层实现,默认的对象都是强引用类型
```
String a = "haha";
```
强引用类型：在GC 可达性分析中认为可达，那即使OOM，这类对象也不会被回收，如果为不可达，则会在GC的时候被回收

### 软引用（SoftReference）
- 软引用是比强引用生命周期弱一些的引用，它会根据JVM内存是否充足来判断对象是否会被GC回收
- 由于这种特性，弱引用可作用于网页缓存或者图片缓存
```
SoftReference<String> softReference = new SoftReference<String>(new String("haha"));
System.out.println(softReference.get());
```
### 弱引用（WeakReference)
弱引用比软引用弱一些，生命周期很短，不论JVM是否充足，都会在下一次GC到达之前被回收
```
WeakReference<String> weakReference = new WeakReference<String>(new String("haha"));
System.gc();
if(weakReference.get() == null) {
    System.out.println("weakReference已经被GC回收");
}
```

### 虚引用（PhantomReference）
- 虚引用的引用是虚拟的，这种引用类型不影响对象的生命周期，随时可被GC回收
- 对象垃圾回收过程中，如果发现对象存在虚引用，就会将虚引用加入与之关联的引用队列，程序就通过判断虚引用是否被加入引用队列，来对需要回收的对象采取措施
- 对于业务场景中极少使用，JVM团队用于跟踪对象被垃圾回收的活动
```
PhantomReference<String> phantomReference = new PhantomReference<>(new String("haha"),new ReferenceQueue<String>());
        System.out.println("phantomReference "+phantomReference.get());
```