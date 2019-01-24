package learning.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import learning.inpublic.RpcFuture;
import learning.inpublic.RpcResponse;


/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 24 16:52
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        RpcFuture remove = RpcNettyClient.futureMap.remove(response.getInvokeId());
        remove.setResponseResult(response.getResult());
        log.info("服务端的返回结果:"+response);
    }


    /**
     * 发生异常事件处理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 连接关闭事件处理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RpcNettyClient.reConnect();
        ctx.fireChannelInactive();
    }
}
