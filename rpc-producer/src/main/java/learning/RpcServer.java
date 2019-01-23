package learning;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 16:11
 */
public class RpcServer {

    private  static final ExecutorService executor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    private String ip;
    private String port;

    public RpcServer(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public void publish(Object service) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.valueOf(port));

            while (true){
                Socket socket = serverSocket.accept();
                executor.submit(new ProcessorHandler(socket,service));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

}
