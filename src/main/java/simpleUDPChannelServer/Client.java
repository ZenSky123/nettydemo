package simpleUDPChannelServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 5000;

        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ClientHandler());
            ChannelFuture f = b.connect("127.0.0.1", port).sync();

            Channel channel = f.channel();
            ByteBuffer writeBuffer = ByteBuffer.allocate(32);
            try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    writeBuffer.put(userInput.getBytes(StandardCharsets.UTF_8));
                    writeBuffer.flip();
                    writeBuffer.rewind();

                    ByteBuf buf = Unpooled.copiedBuffer(writeBuffer);

                    channel.writeAndFlush(new DatagramPacket(buf, new InetSocketAddress(host, port)));
                    writeBuffer.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
