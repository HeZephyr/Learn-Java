package example.net.netty.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(HeartbeatServerHandler.class.getName());

    // This method is triggered when a user-defined event occurs, such as an idle state
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // Check if the event is an instance of IdleStateEvent
        if (evt instanceof IdleStateEvent event) {
            // If the client has been idle for too long, close the connection
            if (event.state() == IdleState.READER_IDLE) {
                logger.warning("Client idle for too long, closing connection...");
                ctx.close();
            }
        }
    }

    // This method is called whenever a message is read from the client
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Check if the message is of type ByteBuf
        if (msg instanceof ByteBuf byteBuf) {
            // Convert the ByteBuf content to a string using UTF-8 encoding
            String message = byteBuf.toString(CharsetUtil.UTF_8);
            logger.info("Received message from client: " + message);
        }
        // Send a response back to the client
        ctx.writeAndFlush("Server response: " + msg);
    }

    // This method handles any exception thrown during the channel's operations
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Error in HeartbeatServerHandler", cause);
        ctx.close(); // Close the connection on exception
    }
}
