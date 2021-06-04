package simpleHTTPServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private String result = "";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            result = "invalid request!";
            send(ctx, result, HttpResponseStatus.BAD_REQUEST);
        }
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            String path = httpRequest.uri();
            String body = getBody(httpRequest);
            HttpMethod method = httpRequest.method();

            System.out.println("path = " + path);

            if (!"/test".equalsIgnoreCase(path)) {
                result = "invalid request!";
                send(ctx, result, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            System.out.println("received:" + method + " request");
            if (HttpMethod.GET.equals(method)) {
                System.out.println("body:" + body);
                result = "GET Request";
                send(ctx, result, HttpResponseStatus.OK);
            }

            if (HttpMethod.POST.equals(method)) {
                System.out.println("body:" + body);
                result = "POST request";
                send(ctx, result, HttpResponseStatus.CREATED);
            }
        } catch (Exception e) {
            System.out.println("handle failed!");
            e.printStackTrace();
        } finally {
            httpRequest.release();
        }
    }

    private String getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        return buf.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("Client" + InetAddress.getLocalHost().getHostName() + " connected successfully!");
        super.channelActive(ctx);
    }

    private void send(ChannelHandlerContext ctx, String content, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(content, StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
