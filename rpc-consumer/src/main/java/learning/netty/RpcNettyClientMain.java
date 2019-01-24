package learning.netty;

import learning.inpublic.Person;
import learning.inpublic.RpcProducerService;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 24 16:48
 */
public class RpcNettyClientMain {

    public static void main(String[] args) {
        //引用接口
        RpcProducerService rpcProducerService = (RpcProducerService) RpcNettyClient.referService(RpcProducerService.class);
        RpcNettyClient.startClient("127.0.0.1",8998);
        //调用接口，获得结果
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                Person p = rpcProducerService.getPerson();
                System.out.println("获取结果数据："+p.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
