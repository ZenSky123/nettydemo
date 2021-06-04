package heartbeatServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 5000;
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientFilter());
        Channel channel = b.connect(host, port).sync().channel();
        String str = "Hello Netty";
        channel.writeAndFlush(str);
        System.out.println("Client send:" + str);
    }
}
