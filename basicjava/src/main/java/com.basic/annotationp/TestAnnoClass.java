package com.basic.annotationp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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