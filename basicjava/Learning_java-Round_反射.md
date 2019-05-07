### 什么是java的反射机制？
- 在Java中的反射机制是指在运行状态中，对于任意一个类都能够知道这个类所有的属性和方法；
- 并且对于任意一个对象，都能够调用它的任意一个方法；
- 这种动态获取信息以及动态调用对象方法的功能成为Java语言的反射机制

### 反射API
1. Class类：反射的核心类，可以获取类的属性，方法等信息。 
2. Field类：Java.lang.reflec包中的类，表示类的成员变量，可以用来获取和设置类之中的属性值。 
3. Method类： Java.lang.reflec包中的类，表示类的方法，它可以用来获取类中的方法信息或者执行方法。 
4. Constructor类： Java.lang.reflec包中的类，表示类的构造方法。

### 获取Class对象的方法
```text
调用某个对象的getClass()方法 
Person p=new Person(); 
Class clazz=p.getClass(); 

调用某个类的class属性来获取该类对应的Class对象 
Class clazz=Person.class; 
使用Class类中的forName()

静态方法(最安全/性能最好) 
Class clazz=Class.forName("类的全路径"); (最常用)
```
```text
//获取Person类的Class对象 
Class clazz=Class.forName("reflection.Person");

//获取Person类的所有方法信息 
Method[] method=clazz.getDeclaredMethods(); 
for(Method m:method){ 
System.out.println(m.toString()); 
} 

//获取Person类的所有成员属性信息 
Field[] field=clazz.getDeclaredFields(); 
for(Field f:field){ 
System.out.println(f.toString()); 
} 

//获取Person类的所有构造方法信息 
Constructor[] constructor=clazz.getDeclaredConstructors(); 
for(Constructor c:constructor){ 
System.out.println(c.toString()); 
}

```

### 创建对象
```text
//获取Person类的Class对象 
Class clazz=Class.forName("reflection.Person"); 

//使用.newInstane方法创建对象 
Person p=(Person) clazz.newInstance(); 

//获取构造方法并创建对象 
Constructor c=clazz.getDeclaredConstructor(String.class,String.class,int.class); 

//创建对象并设置属性
Person p1=(Person) c.newInstance("李四","男",20);

```
