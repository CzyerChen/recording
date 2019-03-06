package com.nettyhepler.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Desciption:同步阻塞IO ，流的接收形式在关闭之前都会一直阻塞线程，这个就不满足现在高并发非阻塞的要求
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 06 19:45
 */
public class BIOServer {

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);     //1
        try {
            for (;;) {
                final Socket clientSocket = socket.accept();    //2
                System.out.println("Accepted connection from " + clientSocket);

                //3
                new Thread(() -> {
                    OutputStream out;
                    try {
                        out = clientSocket.getOutputStream();
                        out.write("Hello World!\r\n".getBytes(Charset.forName("UTF-8")));                            //4
                        out.flush();
                        clientSocket.close();                //5

                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            clientSocket.close();
                        } catch (IOException ex) {
                          ex.printStackTrace();
                        }
                    }
                }).start();                                        //6
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
