package learning.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import learning.inpublic.RpcFuture;
import learning.inpublic.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 24 16:50
 */
public class RpcNettyClient {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(RpcNettyClient.class);
    private static Bootstrap bootstrap;
    private static Channel clientChannel;
    //锁，用于断线重连
    private static CountDownLatch channelActiveLock = new CountDownLatch(1);
    //计时器
    private static Timer timer = new HashedWheelTimer();

    public static ConcurrentHashMap<Long, RpcFuture> futureMap = new ConcurrentHashMap<>();

    public static <T> T referService(final Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setInterfaceClass(interfaceClass);
                request.setMethodName(method.getName());
                request.setParameterTypeClass(method.getParameterTypes());
                request.setArguments(args);
                RpcFuture future = new RpcFuture(request);
                futureMap.put(request.getInvokeId(), future);
                getClientChannel().writeAndFlush(request);
                return future.get();
            }
        });
    }


    /**
     * 启动client端
     *
     * @param host
     * @param port
     */
    public static void startClient(String host, int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port " + port);
        }
        bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 1);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverConnect();
    }


    /**
     * 连接服务端
     */
    public static void serverConnect() {
        ChannelFuture future;
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    ChannelHandler[] handles = new ChannelHandler[]{
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())),
                            new ClientHandler(),
                            new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS),
                            new ClientIdleStateEventHandler(),
                    };
                    channel.pipeline().addLast(handles);
                }
            });
            future = bootstrap.connect();
        }
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();
                if (!succeed) {
                    f.channel().pipeline().fireChannelInactive();
                    log.error("连接服务端  失败---------");
                } else {
                    clientChannel = f.channel();
                    channelActiveLock.countDown();
                    log.info("连接服务端  成功---------");
                }
            }
        });
    }

    /**
     * 定时重连，统一在IO线程执行重连操作
     */
    public static void reConnect() {
        channelActiveLock = new CountDownLatch(1);
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                RpcNettyClient.serverConnect();
            }
        }, 1000, TimeUnit.MILLISECONDS);
        log.info("----------------重新连接中------------------");
    }

    /**
     * 获取唯一连接
     *
     * @return
     */
    private static Channel getClientChannel() {
        if (clientChannel == null) {
            try {
                channelActiveLock.await(60000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return clientChannel;
    }
}
