### 注解的定义
@Target修饰的对象范围 
@Target说明了Annotation所修饰的对象范围： 
Annotation可被用于 packages、types（类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数和本地变量（如循环变量、catch参数）。
在Annotation类型的声明中使用了target可更加明晰其修饰的目标

@Retention定义 被保留的时间长短 Retention 定义了该Annotation被保留的时间长短：
表示需要在什么级别保存注解信息，用于描述注解的生命周期（即：被描述的注解在什么范围内有效），
取值（RetentionPoicy）由： 
 SOURCE:在源文件中有效（即源文件保留） 
 CLASS:在class文件中有效（即class保留） 
 RUNTIME:在运行时有效（即运行时保留） 

@Documented描述-javadoc
@ Documented用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化。

@Inherited阐述了某个被标注的类型是被继承的 
@Inherited 元注解是一个标记注解，@Inherited阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited修饰的annotation类型被用于一个class，
则这个annotation将被用于该class的子类。

### 注解的使用
```text
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestAnno {
    public int num() default 1;
    public String name() default "";
}

public class TestAnnoClass {

    @TestAnno(num = 10,name = "user1")
    public  void test(){
        System.out.print("user1 annotaion test");
    }

    public static void main(String[] args){
        TransProvider.getInfo(TestAnnoClass.class);
    }


}
class TransProvider{
    public static void getInfo(Class<?> clazz){
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for(Method method: declaredMethods){
            if(method.isAnnotationPresent(TestAnno.class)){
                TestAnno annotation = method.getAnnotation(TestAnno.class);
                int num = annotation.num();
                String name = annotation.name();
                System.out.print("num:"+num+"  name:"+name);

            }
        }
    }
}
```