package learningpattern.springdesignpattern.interclass;

public class ProxyImplClass implements ProxyClass {
    @Override
    public void testBefore(String text) {
        System.out.println("proxy impl class:" +text);
    }
}
