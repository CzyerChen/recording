package springall.aop.usage;

import org.springframework.aop.framework.ProxyFactory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 30 11:20
 */
public class TestMain {

    public static void main(String[] args){
        /**
         * 未声明接口定义  --- 使用CGLIB代理
         */
        /**
         * saySomething调用前
         * CommonClass say some thing
         * saySomething调用后
         * com.learning.spring.aop.CommonClass$$EnhancerBySpringCGLIB$$d64ff73d
         *
         */
        ProxyFactory factory1 = new ProxyFactory();
        factory1.setTarget(new CommonClass());
        factory1.addAdvice(new TestIntercepter());
        CommonClass commonClass = (CommonClass)factory1.getProxy();
        commonClass.saySomething();
        System.out.println(commonClass.getClass().getName());


        /**
         * 声明接口定义  --使用JDK代理
         */
        /**
         *saySomething调用前
         * BaseInterfaceImpl say some thing
         * saySomething调用后
         * com.sun.proxy.$Proxy0
         */
        ProxyFactory factory2 = new ProxyFactory();
        factory2.setTarget(new BaseInterfaceImpl());
        factory2.addAdvice(new TestIntercepter());
        factory2.setInterfaces(BaseInterface.class);
        BaseInterface baseInterface = (BaseInterface)factory2.getProxy();
        baseInterface.saySomething();
        System.out.println(baseInterface.getClass().getName());

    }

}
