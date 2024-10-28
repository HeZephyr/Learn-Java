package example.net.netty.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(EchoServerHandler.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("Server received: " + msg);
        ctx.writeAndFlush(msg);  // Echo the received message back to client
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Error in EchoServerHandler", cause);
        ctx.close();
    }
}
