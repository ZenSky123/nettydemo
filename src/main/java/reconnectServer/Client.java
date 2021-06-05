package reconnectServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class Client {
    public String host = "127.0.0.1";
    public int port = 5000;

    private EventLoopGroup group = new NioEventLoopGroup();
    public static Client client = new Client();

    private boolean initFlag = true;

    public static void main(String[] args) {
        client.run();
    }

    public void run() {
        doConnect(new Bootstrap(), group);
    }

    public void doConnect(Bootstrap bootstrap, EventLoopGroup group) {
        ChannelFuture f = null;
        try {
            if (bootstrap != null) {
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ClientFilter())
                        .remoteAddress(host, port);
                f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        System.out.println("disconnected with server! it's will reconnect after 10s");
                        eventLoop.schedule(() -> doConnect(new Bootstrap(), eventLoop), 10, TimeUnit.SECONDS);
                    }
                });
                if (initFlag) {
                    System.out.println("client start successfully!");
                    initFlag = false;
                }
                f.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            System.out.println("connect failed!");
        }
    }
}

