package example.net.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(HeartbeatClientHandler.class.getName());

    // This method is triggered when a user-defined event occurs, such as an idle state
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // Check if the event is an instance of IdleStateEvent
        if (evt instanceof IdleStateEvent event) {
            // If the client has been idle for writing (sending data), send a heartbeat
            if (event.state() == IdleState.WRITER_IDLE) {
                logger.info("Sending heartbeat to server");
                // Send heartbeat message to keep connection alive
                ctx.writeAndFlush("HEARTBEAT");
            }
        }
    }

    // This method is called whenever a message is received from the server
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Log the server's response message
        logger.info("Received from server: " + msg);
    }

    // This method handles any exception thrown during the channel's operations
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Log the error and close the connection
        logger.log(Level.SEVERE, "Error in HeartbeatClientHandler", cause);
        ctx.close();
    }
}
