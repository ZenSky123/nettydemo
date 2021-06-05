package reconnectServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int idleCount = 1;
    private int count = 1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(count + ". msg:" + msg);
        String message = (String) msg;
        if ("hb_request".equals(message)) {
            ctx.writeAndFlush("receive heartbeat successfully!");
        }
        count++;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(event.state())) {
                System.out.println("hasn't been 5s for receiving msg");
                if (idleCount > 1) {
                    System.out.println("close the inactive channel");
                    ctx.channel().close();
                }
            }
            idleCount++;
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
