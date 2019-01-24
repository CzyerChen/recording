package learning.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import learning.inpublic.RpcRequest;
import learning.inpublic.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 24 17:13
 */
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static ExecutorService executorService = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private static InternalLogger log = Slf4JLoggerFactory.getInstance(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                RpcResponse response = new RpcResponse(request);
                try {
                    Class<?> interfaceClass = request.getInterfaceClass();
                    String methodName = request.getMethodName();
                    Object[] arguments = request.getArguments();
                    Class[] parameterTypeClass = request.getParameterTypeClass();
                    Object serviceImpl = RpcNettyServer.serviceMap.get(interfaceClass);
                    Method method = serviceImpl.getClass().getMethod(methodName, parameterTypeClass);
                    Object result = method.invoke(serviceImpl, arguments);
                    response.setResult(result);
                } catch (NoSuchMethodException e) {
                    response.setResult(e);
                   log.error("遇到问题： NoSuchMethodException "+Arrays.toString(e.getStackTrace()));
                }  catch (InvocationTargetException e) {
                    response.setResult(e);
                    log.error("遇到问题： InvocationTargetException "+ Arrays.toString(e.getStackTrace()));
                } catch (IllegalAccessException e) {
                    response.setResult(e);
                    log.error("遇到问题： IllegalAccessException "+ Arrays.toString(e.getStackTrace()));
                }

                channelHandlerContext.writeAndFlush(response);
                log.info("服务端处理请求 ---> " + request);
            }
        });
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("远程地址 : " + ctx.channel().remoteAddress() + " 启动 !");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
