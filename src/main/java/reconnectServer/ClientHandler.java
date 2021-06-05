package reconnectServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("hb_request", StandardCharsets.UTF_8));

    private int idleCount = 1;
    private int count = 1;
    private int fcount = 1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("create connection:" + new Date());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("close connectino:" + new Date());
        final EventLoop eventLoop = ctx.channel().eventLoop();
        Client.client.doConnect(new Bootstrap(), eventLoop);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(count + ". msg:" + msg);
        count++;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("cycle request time:" + new Date() + "\t\ttimes:" + fcount);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                if (idleCount <= 2) {
                    idleCount++;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE);
                } else {
                    System.out.println("dont send heartbeat request!");
                }
                fcount++;
            }
        }
    }
}
