package learningpattern.springdesignpattern;

import learningpattern.springdesignpattern.interclass.ProxyBeforeClass;
import learningpattern.springdesignpattern.interclass.ProxyClass;
import learningpattern.springdesignpattern.interclass.ProxyImplClass;
import org.springframework.aop.framework.ProxyFactory;

public class ProxyModel {

    public static void main(String[] args){
        ProxyFactory proxyFactory = new ProxyFactory(new ProxyImplClass());
        proxyFactory.addInterface(ProxyClass.class);
        proxyFactory.addAdvice(new ProxyBeforeClass());
        proxyFactory.setExposeProxy(true);

        ProxyClass proxyClass = (ProxyClass)proxyFactory.getProxy();
        proxyClass.testBefore("hello world ");
    }
}
