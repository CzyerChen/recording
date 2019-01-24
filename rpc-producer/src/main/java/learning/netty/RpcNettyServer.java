package learning.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 24 16:55
 */
public class RpcNettyServer {
    public static ConcurrentHashMap<Class,Object> serviceMap = new ConcurrentHashMap<>();
    public static InternalLogger log = InternalLoggerFactory.getInstance(RpcNettyServer.class);

    public static  void startServer(String host,int port){
        //判断接口的合理性
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port " + port);
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()<< 1);
        bootstrap.group(parentGroup,childGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                pipeline.addLast(new ServerHandler());
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            log.info("服务端启动 ---------------");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("端口绑定遇到问题：" + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }

    /**
     * 注册接口服务，缓存起来
     * @param clazz
     * @param serviceImpl
     */
    public static void registerService(Class<?> clazz,Object serviceImpl){
        serviceMap.put(clazz,serviceImpl);
    }
}
