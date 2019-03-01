- final 是一个java关键字，修饰的变量不能修改，修饰的引用引用不能改变，内容可以，修饰的类不能被继承，它里面用得到的是怎样的内存语义呢？

### 1. final的重排序规则
- 编译器和处理器会遵循两个重排序规则：
1. 在构造函数内对一个 final 域的写入，与随后把这个被构造对象的引用赋值给一个引用变量，这两个操作之间不能重排序
2. 初次读一个包含 final 域的对象的引用，与随后初次读这个 final 域，这两个操作之间不能重排序
- 例子介绍
```text
public class FinalExample {
    int i;                            // 普通变量 
    final int j;                      //final 变量 
    static FinalExample obj;

    public void FinalExample () {     // 构造函数 
        i = 1;                        // 写普通域 
        j = 2;                        // 写 final 域 
    }

    public static void writer () {    // 写线程 A 执行 
        obj = new FinalExample ();
    }

    public static void reader () {       // 读线程 B 执行 
        FinalExample object = obj;       // 读对象引用 
        int a = object.i;                // 读普通域 
        int b = object.j;                // 读 final 域 
    }
}
```

### 2. 写 final 域的重排序规则
- 其实final比如修饰一个变量，就是初次赋值之后就不能进行值的修改了，我们首先来看写final
- 写 final 域的重排序规则禁止把 final 域的写重排序到构造函数之外，有两个规则实现：
1. JMM 禁止编译器把 final 域的写重排序到构造函数之外
2. 编译器会在 final 域的写之后，构造函数 return 之前，插入一个 StoreStore 屏障。这个屏障禁止处理器把 final 域的写重排序到构造函数之外

### 3.读 final 域的重排序规则
- 读 final 域的重排序规则如下
1. 在一个线程中，初次读对象引用与初次读该对象包含的 final 域，JMM 禁止处理器重排序这两个操作（注意，这个规则仅仅针对处理器）。编译器会在读 final 域操作的前面插入一个 LoadLoad 屏障
2. 初次读对象引用与初次读该对象包含的 final 域，这两个操作之间存在间接依赖关系。由于编译器遵守间接依赖关系，因此编译器不会重排序这两个操作

### 4.

