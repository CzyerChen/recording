package springall.aop.usage;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Desciption:测试拦截
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 30 11:17
 */
public class TestIntercepter implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println(methodInvocation.getMethod().getName() + "调用前");
        Object result = methodInvocation.proceed();
        System.out.println(methodInvocation.getMethod().getName() + "调用后");
        return result;
    }
}
