package learning.netty;

import io.netty.buffer.AbstractByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 9:58
 */
public class ServerIdleStateEventHandler  extends ChannelInboundHandlerAdapter {

    /**
     * 接受心跳包
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof AbstractByteBuf){
            //心跳是一个字节，不继续传递给其他handler处理了
            if(((AbstractByteBuf)msg).writerIndex()==1){
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }


    /**
     * 超时断开连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                System.out.println("客户端"+ctx.channel().remoteAddress()+"没有响应，关闭连接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
