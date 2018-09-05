package ProxyHandler;

import Client.ClientHandler;
import Client.RPCClient;
import Message.Request;
import Message.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * Created by GEKL on 2018/9/3.
 */
public class NettyInvokeHandler implements InvocationHandler{

    private Class target;

    public NettyInvokeHandler(Class target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        RPCClient rpcClient = new RPCClient(getClientHandler(target,method,args));
        Response response = rpcClient.send();
        return response.getResult();
    }

    public ClientHandler getClientHandler(Class c, Method method, Object[] args){
        Request reqMessage = new Request();
        reqMessage.setClassName(c.getName());
        reqMessage.setMethodName(method.getName());
        reqMessage.setParametersVal(args);
        reqMessage.setTypeParameters(method.getParameterTypes());
        return new ClientHandler(reqMessage);
    }
}
