## 06_其他改进
### 1. 字符串
- 提供String.join(",",arr) 进行字符串的连接

### 2.数字
- Integer 和Long 新增了处理无符号值的comparedUnsigned、divideUnsigned
- Float Double 新增了静态方法infinite，用于判断数是否为无穷大
- BigInteger增加了实例方法 ValueExtract 

### 3.新的数学函数
- Exact
- floorMod floorDiv
- nextDown

### 4.集合
- stream parallelStream spliterator
- Iterable ---forEach
- Collection -- removeIf
- List -- replaceAll sort
- Map forEach replace replaceAll remove putIfAbsent compute computeIf.. merge
- 比较器 comparing   thenComparing  nullFirst reverseOrder

### 5.文件
- Files.lines 与FileReader不同，Files.lines或默认是使用utf-8的编码打开文件，也可通过Charset指定编码
- stream接口是AutoCloseable的
- 应当抛弃File 使用Path Files 的API，使用NIO的底层，简化了代码，也更高效

### 6.注解
- 可重复的注解：注解被标注为@Repeatable,并且提供一个父容器
```text
一个很常见的注解作为例子：
//定义一个注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//表述一个承载注解的容器 ComponentScans
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
}

//父容器注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScans {

	ComponentScan[] value();

}
```
- 可用于类型的注解，例如@NonNull @Nullable
- 方法参数反射

### 7.其他
- Null检查，新增Objects nonNull/ isNull的方法，检查对象是否为Null,在流处理中效果显著
- 延迟消息，之前介绍过得Logger能够接收一个延迟打印的消息，Objects.requireNonNull也可以有延迟计算版本
- 正则表达式，Matcher的start end group优化，Pattern的splitAsStream也在前文中使用过
- 语言环境
- JDBC java.sql中的时间类（Date Time Timestamp）都能与java.time中的LocalDate LocalTime LocalDateTime互相转换；
Statement新增一个executeLargeUpdate的方法，用于执行修改行数会超过Integer.max_vlaue的更新操作

### 问题
- @Repeatable寻找或者实现一个例子，并测试
- Pattern.asPredicate 如何实现，变化是什么？
```text
//从前
 Pattern pattern = Pattern.compile("^(.+)@example.com$");
    List<String> emails = Arrays.asList("@example.com", "b@yahoo.com", "c@google.com", "d@example.com")     ;
    for(String email : emails){
        Matcher matcher = pattern.matcher(email);  
        if(matcher.matches()){
            System.out.println(email);
        }
    }

//现在
//预定义
 Predicate<String> emailFilter = Pattern.compile("^(.+)@example.com$").asPredicate();
    List<String> desiredEmails = emails
                                     .stream()
                                     .filter(emailFilter)
                                     .collect(Collectors.<String>toList());
```
- Objects.requireNonNull有什么好处？
```text
//手动控制抛出空指针异常
public static <T> T requireNonNull(T obj) {
    if (obj == null)
        throw new NullPointerException();
    return obj;
}
//可能会问，系统如果有异常会抛出，为什么还要手动呢？
//是代码更具有可读性，是异常的抛出位置更可控，不会到真正遍历或者使用的时候才抛出，并且查看堆栈才可知

```


### 8.jdk7
- try-with-resources,内部资源必须实现AutoCloseable,如果异常会自动调用close方法，如果是Closeable会抛出IOException,也可以有catch和finally块，在资源关闭之后调用
- Path 和Files 来替代 File进行文件操作
- Files walk 和walkFileTree的区别
```text
walk和walkFileTree之间的区别在于它们为步行树提供了不同的接口：walkFileTree接受FileVisitor,walk给出Stream< Path>

```
- Logger.getGlobal() 全局日志对象