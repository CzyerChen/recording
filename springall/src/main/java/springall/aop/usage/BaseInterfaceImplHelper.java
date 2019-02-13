package springall.aop.usage;

/**
 * Desciption：代理类
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 29 16:23
 */
public class BaseInterfaceImplHelper {

    private BaseInterfaceImpl baseInterfaceImpl;

    public String saySomething(){
        System.out.println("BaseInterfaceImplHelper say some thing ");
        return "true";
    }
}
