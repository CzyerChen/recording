package learning.handonrpc;

import learning.inpublic.RpcProducerService;
import learning.inpublic.RpcProducerServiceImpl;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 15:23
 */
public class ProducerMain {

    public static void main(String[] args){
     RpcProducerService rpcProducerService = new RpcProducerServiceImpl();
     RpcServer rpcServer = new RpcServer("127.0.0.1","8888");
     rpcServer.publish(rpcProducerService);
    }
}
