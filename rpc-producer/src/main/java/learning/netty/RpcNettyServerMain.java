package learning.netty;

import learning.inpublic.RpcProducerService;
import learning.inpublic.RpcProducerServiceImpl;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 24 16:54
 */
public class RpcNettyServerMain {

    public static  void main(String [] args){
        //手动生成接口实现类
        RpcProducerService rpcProducerService = new RpcProducerServiceImpl();
        //发布接口
        RpcNettyServer.registerService(RpcProducerService.class,rpcProducerService);
        //开启服务
        RpcNettyServer.startServer("127.0.0.1",8998);
    }
}
