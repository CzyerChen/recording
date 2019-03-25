- 今天看到一篇博文，记录一种Bean注入的方式，但是个人觉得好像不是很常用？？

```text
class A  implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
         
    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
         applicationContext = arg0;
    }
         
    @SuppressWarnings("unchecked")
     public static <T> T getBean(String name) {
           return (T) applicationContext.getBean(name);
     }
         
     public static <T> T getBean(Class<T> cls) {
          return applicationContext.getBean(cls);
    }
}
```
- 如果是在Spring的情况下，我会在父线程注入需要使用的类，然后通过子线程构造方法的方式传递进去使用
