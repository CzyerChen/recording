package learning;

import java.lang.reflect.Proxy;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 16:40
 */
public class RpcClientProxy {
    public <T> T clientProxy(final Class<T> interfaceCls, final String host, final int port) {
        // 使用到了动态代理。
        return (T) Proxy.newProxyInstance(interfaceCls.getClassLoader(), new Class[]{interfaceCls},
                new RemoteInvocationHandler(host, port));
    }
}
