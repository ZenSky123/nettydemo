package timeServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        int port = 5000;
        ServerBootstrap b = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerHandler());
        ChannelFuture f = b.bind(port).sync();
        f.channel().closeFuture().sync();
    }
}
