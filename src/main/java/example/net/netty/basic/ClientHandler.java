package example.net.netty.basic;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send message to the server upon connection
        String message = "Hello from Netty Client!";
        logger.info("Client sending message: " + message);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Log the received message from the server
        logger.info("Client received: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Client encountered an error", cause);
        ctx.close();
    }
}
