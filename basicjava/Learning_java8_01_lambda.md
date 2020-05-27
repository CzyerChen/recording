## 01_lambda
> java8一个非常大的特点就是lambda的出现，使开发流程更为简洁流畅，当然也会存在遍历带来的一些麻烦

#### 1.1 为什么要使用lambda
1.lambda表达式是一段可以传递的代码，可以被执行一次或多次
### 1.2 lambda表达式语法
- 数学字符 ƛ
- 方法使用-> {}形式
- 可传递参数 @NonNull或final修饰
### 1.3 函数式接口
- 任意函数式接口标注@FunctionalInterface注解，编译器回家查该注解的实体，检查它是否只包含一个抽象方法的接口，在javedoc页面会包含一条声明，说明这个接口是一个函数式接口
#### about javadoc
- IDEA开启鼠标悬浮查看java doc
```$xslt
 1.查找setting (preference)
 2.Editor -> General 
 3.Show quick documentation on mouse move 勾选，并且可以自定义鼠标悬浮的时间
```
#### about @FuntionalInterface
实现一个lambda的执行流程：先定义后实现，适配不同的入参方法的展示形式，适用于一个灵活的抽象方法
```text
  /**
   * 功能简述 <br/> 
   * 〈功能性文档类接口〉
   *  @FunctionalInterface：必须有且仅有一个抽象方法，允许定义静态方法，允许定义默认方法，允许Object中的public方法，此注解不是必须的，只是便于编译器检查
   *
   *
   * @author claire
   * @date 2020-05-25 - 19:33
   * @since 1.0.0
   */
  @FunctionalInterface
  public interface FunctionalDocInterface {
      //抽象方法,有且仅有一个
      public abstract void show(String param);
  
      @Override
      public boolean equals(Object param);
  
      public default void methodDefault(){
  
      }
  
      public static void methodStatistic(){
  
      }
  }

public class FunctionLambdaClass {
    public static void printContent(String content, FunctionalDocInterface functional) {
        functional.show(content);
    }
}

public class LambdaMain {


    public static void main(String[] args) throws UnknownHostException {
        //InetAddress address = InetAddress.getByName("127.0.0.1");
        String content ="content";
        FunctionLambdaClass.printContent(content, param -> System.out.println("结果："+param));
    }
}

```
- lambda接口只是为了简便现有书写方式而出现的，因而lambda的书写方式都可转换为传统java API形式
### 1.4 方法引用
- ::方法，取代 -> 
可以使用
```text
对象::实例方法
类::静态方法
类::实例方法
this::实例方法
super::实例方法
```
### 1.5 构造器引用
- TestClass::new 标识TestClass的构造器引用，关于引用具体哪一个构造函数，编译器会从上而下挑选一个最符合的构造器
- 数组构造器可绕过java中new一个泛型类型T的数组，因为new T[n]会被类型擦除变成 new Object[n],但是流式处理可以，stream.toArray(TestClass[]::new),toArray方法会调用该构造器的一个正确类型的数组，然后会填充并返回该数组

### 1.6 变量作用域
- 我们可能都会遇到，想要在lambda表达式中运用到外部的参数或者变量，但是有时候一定要被定义为final，为什么呢？
- lambda包含：一段实现的代码，一些参数，自有变量-lambda逻辑中引用的变量（并非外部定义的）
a.stream().map((x,y)->x+y) 其中x，y是自有变量
- labmda中定义，被引用的变量值是不可修改的，对于常量值在lambda表达式计算的过程中，值不能被修改。因为更改lambda值不是线程安全的
并发操作内变量的修改并不是原子性的，因而线程不安全
- lambda表达式的方法与嵌套的代码块有相同的作用域，因而内部的参数不允许与局部变量同名

### 1.7 默认方法
- 如果一个接口中定义了一个默认方法，而另外一个接口也提供了一个具有相同名称方法，这时候如何选择？
```text
1.【类优先原则】如果一个父类提供了具体的实现方法，那么接口中具有相同名称和参数的方法会被忽略。-------->选择父类中方法
2.【存在冲突，收到解决】接口冲突的话（存在一个以上的相同名称和参数类型的方法），必须提醒开发者进行覆盖该方法来解决冲突
```
- 【类优先原则】如果一个类继承了一个父类，实现了一个接口，父类和接口中有两个一样的方法，此时父类中的方法会起作用，接口中的方法将被忽略
### 1.8 接口中的静态方法
- 接口中添加静态方法，能够避免为工具方法提供多余的辅助类
- 例子：COmparator.comparing()

### 问题
1.Array.sort方法中的比较器代码的线程与调用sort的线程是同一个
- 是同一个
```text
        public static <T> void sort(T[] a, Comparator<? super T> c) {
                if (c == null) {
                    sort(a);
                } else {
                    if (LegacyMergeSort.userRequested)
                        legacyMergeSort(a, c);
                    else   
                        TimSort.sort(a, 0, a.length, c, null, 0, 0); //使用了用户传入的compartor
                }
            }
```
2.使用java.io.File类的listFiles和isDirectory, 编写一个返回指定目录下所有子目录的方法，使用lambda表达式代替FileFilter对象，再将它改写成一个方法引用
```text
       File file = new File("/tmp/Samples.txt");
        if(file.exists()){
            //代替FileFilter方法
            File[] files1 = file.listFiles(File::isDirectory);
            //方法引用
            File[] files2 = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
        }
```
3.使用java.io.File类的list(FilenameFilter)方法编写一个返回指定目录下，具有指定扩展名的所有文件。使用lambda表达式（而不是FilenameFilter）来实现，他会捕获闭包作用域中那些变量？
```text
        File file = new File("/tmp");
        String[] files = file.list((dir, name) -> name.endsWith(".txt"));

```
4.对于一个指定file对象数组，首先按照目录的目录熟悉怒排序，然后对每组目录中的元素再按照路径名排序。请使用lambda表达式（不是Compator实现）来实现。
```text
 File file = new File("/tmp");
        File[] files = file.listFiles();
        if(Objects.nonNull(files)) {
            Arrays.sort(files);
            Arrays.sort(files,(f1,f2)->f1.getPath().compareTo(f2.getPath()));
            Arrays.sort(files,(f1,f2)->{
               if(f1.isDirectory() && f2.isDirectory() || file.isFile() && f2.isFile()){
                   return f1.getPath().compareTo(f2.getPath());
               }else{
                   if(f1.isDirectory()){
                       return -1;
                   }else{
                       return 1;
                   }
               }
            });
        }

```
5.从你的项目中选取一个包含一些action listener、Runnable或者其他类似。将他们替换为lambda表达式形式，这样能节省多少代码，替换后代码是否更具有可读性，这个过程中你使用了方法引用吗？
```text
     Thread thread = new Thread(() -> {
            System.out.println("线程内部");
        });
        thread.start();

```
6.Runnable中的异常检查,编写一个捕获所有异常的uncheck方法，再将它改造为不需要检查异常的方法
```text
        Thread thread = new Thread(FunctionLambdaClass.uncheck(() -> {
            System.out.println("function inside");
        }));
        thread.start();

```
7.编写一个静态方法 andThen 它接收两个Runnable实例作为参数，并返回一个分别运行两个实例的runnable对象，在main方法中，向andThen方法传递两个lambda表达式，并返回运行实例
```text
  Runnable runnable = FunctionLambdaClass.andThen(() -> {
             System.out.println("r1");
        }, () -> {
            System.out.println("r2");
        });
        Thread thread = new Thread(runnable);
        thread.start();
```
8.当一个lambda表达式捕获如下增强for中的循环的值是，会发生什么？
```text
     String[] names = {"a","b","c"};
        List<Runnable> list = new ArrayList<>();
        for(String name:names){
            list.add(() ->{System.out.println(name);});
        }
        for(Runnable r : list){
            r.run();
        }

- 增强for无异常，分别打印了a,b,c
- 如果是传统for循环，由于有一个i是变化而不被允许

```
9.编写一个集成Collection接口的子接口Collection2,并添加一个默认方法forEachIf(Consumer,Predicate),用来将action应用到所有filter返回到true的元素上
```text
public interface Collection2<T> extends Collection<T> {
    public default void forEachIf(Consumer<T> action, Predicate<T> filter) {
        forEach(item -> {
            filter.test(item) ? action.accept(item);
        });
    }
}
```
10.浏览Collection类中的方法，你会将每个方法放到哪个接口中，这个方法会是一个默认方法还是静态方法
11.假如你有一个实现了两个接口I和J的类，这两个接口都有一个void f()方法。如果I接口中的f方法是一个抽象的、默认或者静态方法，并且J接口中的f方法是也一个抽象的、默认或者静态方法，分别会发生什么？如果这个类继承自S类并实现了接口I，并且S和I中都有一个void f()方法，又会分别发生什么
```text
1.只要有一个接口中是抽象函数，那么这个类必须要重载这个函数重新实现。
2.如果一个是静态函数一个是默认函数，那么，最终显示出来的是默认函数的特性。
```
12.在过去，你知道向接口中添加方法是一种不好的形式，因为他会破坏已有的代码。现在你知道了可以像接口中添加新方法，同时能够提供一个默认的实现。这样做安全程度如何？描述一个Collection接口的新stream方法会导致遗留代码编译失败的场景。二进制的兼容性如何？JAR文件中的遗留代码是否还能运行？


### more
- 什么是类的默认方法
```text
在java8以后，接口中可以添加使用default或者static修饰的方法，
default修饰方法只能在接口中使用，在接口中被default标记的方法为普通方法，可以直接写方法体。
```
- lambda重构设计模式
[一些简单的设计模式实现思路](https://zhuanlan.zhihu.com/p/67199045)

