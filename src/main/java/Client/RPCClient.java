package Client;

import Message.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * Created by GEKL on 2018/9/5.
 */
public class RPCClient {

    private ClientHandler clientHandler ;
    public RPCClient(ClientHandler c){
        clientHandler = c;
    }

    public Response send() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        Response response = null;
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("localhost",8080))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(io.netty.channel.socket.SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectDecoder(1024 >> 2, ClassResolvers
                                            .cacheDisabled(getClass().getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(clientHandler);
                        }
                    });
            ChannelFuture f = b.connect().sync();
            clientHandler.getCountDownLatch().await();
            response = clientHandler.getResponse();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
        return response;
    }

}
