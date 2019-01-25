package learning.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 9:37
 */
public class ClientIdleStateEventHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT;

    static {
        //申请缓冲区内存空间
        ByteBuf buf = Unpooled.buffer(1);
        //写入数据
        buf.writeByte(0);
        // 释放内存空间
        HEARTBEAT = Unpooled.unreleasableBuffer(buf);
    }

    /**
     * 客户端心发送心跳包
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(HEARTBEAT.duplicate());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
