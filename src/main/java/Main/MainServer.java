package Main;

import MyServer.HelloService;
import MyServer.HelloServiceImpl;
import Server.RPCServer;
import Server.ServerHandler;

/**
 * Created by GEKL on 2018/9/5.
 */
public class MainServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if(args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e) {
                //采用默认值
            }
        }
        ServerHandler serverHandler = new ServerHandler();
        serverHandler.Register(HelloService.class.getName(), HelloServiceImpl.class);
        new RPCServer(serverHandler).bind(port);
    }
}
