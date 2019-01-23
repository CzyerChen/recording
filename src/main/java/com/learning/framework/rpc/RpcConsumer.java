package com.learning.framework.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 15:20
 */
public class RpcConsumer {


    public static Object testRpc(final Class clazz){
     return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
         @Override
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
             Socket socket = new Socket("1227.0.0.1",8888);
             String apiClassName = clazz.getName();
             String methodNmae = method.getName();
             Class[] parameterTypes = method.getParameterTypes();

             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             objectOutputStream.writeUTF(apiClassName);
             objectOutputStream.writeUTF(methodNmae);
             objectOutputStream.writeObject(parameterTypes);
             objectOutputStream.writeObject(args);
             objectOutputStream.flush();

             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             Object o = objectInputStream.readObject();
             objectInputStream.close();
             objectOutputStream.close();

             socket.close();
             return o;
         }
     });
    }
}
