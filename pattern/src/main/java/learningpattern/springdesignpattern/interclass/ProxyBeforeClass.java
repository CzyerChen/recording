package learningpattern.springdesignpattern.interclass;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class ProxyBeforeClass implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("this is a before class");
    }
}
