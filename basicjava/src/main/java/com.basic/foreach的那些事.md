> foreach是现在循环很常用的方式,采用for（A a : As）{}的表现方式

> 阿里开发规范中有禁止在foreach循环中使用remove/add元素，需要用iterator来代替，这是为什么呢？


- 要知道以上的原因，首先要知道增强for的实现方式是什么？
- 是最终转化为简单的for而只是表达简介吗，可以通过查看运行后的class文件发现其中的奥秘
```text
for(String name : names){}

Interator iterator = names.iterator()
do{
if(!iterator.hasNext()){
String name = (String)iterator,next()
if(name.equal("haha)){
names.remove(name)
}
}
}while(true);
```
- 以上就是一个增强for的实现，可以看出增强for是最终转换为迭代器和while循环的方式去实现便利查看值和删除等
- 以上描述了增强for是java中的一个语法糖，利用编译器来实现我们需要的功能，也解放了程序员的开发，使表达简洁
- 但是它与普通for相比较也有区别，在add 和remove方面的问题


- 同样的普通for和增强for，操作names.remove或者name.add 能够看到增强for会有异常，实际使用的迭代器与集合自带的删除会在版本号的判断上产生异常，会触发java集合的错误检测机制-fail-fast

- fail-fast 快速失败，他是java集合的一种错误检测机制，多线程对集合进行结构上的改变是会抛出异常，当方法检测到对象的并发修改，但不允许这种修改的时候就会抛错。即使不是多线程，单线程的操作违反规则，也会抛错

- 错误的原因是因为在remove或者add的时候，会对modeCount和expectedModCount进行判断

- modCount是ArrayList中的集合实际被修改的次数，expectedModCount 是ArrayList的内部类，Itr迭代器成员的期望集合修改次数，在调用.iterator的初始化，只有进行迭代器操作的时候才会改变，那么在调用list本身的remove操作的时候不会对iterator的expectedModCount进行修改，因而expectedModCount和ModeCount会发生不一致


- 避免的方式：
    - 使用普通for代替增强for
    - 使用iterator进行操作，避免iterator和list混用
    - 使用fail safe的集合类 ConcurrentLinkedDeque<>,对原数据进行拷贝，然后在副本上进行操作，不会出现对原数据expectedModCount的检测

