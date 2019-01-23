package learning;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 15:54
 */
public class ConsumerMain {


    public  static  void  main(String[] args){
        RpcClientProxy rpcClientProxy=new RpcClientProxy();
        RpcProducerService hello=rpcClientProxy.clientProxy
                (RpcProducerService.class,"127.0.0.1",8888);
        System.out.println("person entry is "+hello.getPerson().toString());
    }
}
