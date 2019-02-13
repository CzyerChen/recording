package springall.aop.usage;

/**
 * Desciption：接口实现类
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 29 16:22
 */
public class BaseInterfaceImpl implements BaseInterface {
    @Override
    public String saySomething() {
        System.out.println("BaseInterfaceImpl say some thing");
        return "true";
    }
}
