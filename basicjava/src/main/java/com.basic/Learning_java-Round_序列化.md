> 序列化场景：持久化到磁盘或内存，进行网络传输、远程对象传输

> 序列化对象以字节数组保持-静态成员不保存,对象序列化保存的是对象的”状态”，即它的成员变量。由此可知，对象序列化不会关注类中的静态变量

### 什么是序列化
- 先使对象实现Serializable接口，然后把对象（实际上只是对象的一个拷贝）写到一个流里，再从流里读出来，便可以重建对象

- 在Java中，只要一个类实现了java.io.Serializable接口，那么它就可以被序列化

- ObjectOutputStream和ObjectInputStream对对象进行序列化及反序列化 通过ObjectOutputStream和ObjectInputStream对对象进行序列化及反序列化。 writeObject 和 readObject自定义序列化策略 在类中增加writeObject 和 readObject 方法可以实现自定义序列化策略


- 虚拟机是否允许反序列化，不仅取决于类路径和功能代码是否一致，一个非常重要的一点是两个类的序列化 ID 是否一致（就是 private static final long serialVersionUID）

- Transient 关键字阻止该变量被序列化到文件中，在被反序列化后，transient 变量的值被设为初始值，如 int 型的是 0，对象型的是 null

#### 直接赋值复制 -- 两个指针指向同一块内存
- A a = b

#### 浅复制（复制引用但不复制引用的对象）-- 值不同，对象是同一个
- 创建一个新对象，然后将当前对象的非静态字段复制到该新对象，如果字段是值类型的，那么对该字段执行复制；如果该字段是引用类型的话，则复制引用但不复制引用的对象。因此，原始对象及其副本引用同一个对象
- implements Cloneable return (Resume)super.clone();

#### 深复制（复制对象和其应用对象） -- 值和对象都不同
- implements Cloneable  o = (Student) super.clone(); o.p = (Professor) p.clone();


