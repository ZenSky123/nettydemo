package heartbeatServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int idleCount = 1;
    private int count = 1;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("No" + count + ". Message：" + msg);
        String message = (String) msg;
        if ("hb_request".equals(message)) {
            ctx.writeAndFlush("server has been received heartbeat message.");
        }
        count++;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(event.state())) {
                System.out.println("it hasn't received it for 5s...");
                if (idleCount > 2) {
                    System.out.println("close inactive channel");
                    ctx.channel().close();
                }
                idleCount++;
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
