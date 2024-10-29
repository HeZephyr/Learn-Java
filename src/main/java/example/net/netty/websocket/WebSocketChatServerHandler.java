package example.net.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocketChatServerHandler is a handler for managing WebSocket messages
 * and broadcasting them to all connected clients in a chat server.
 */
public class WebSocketChatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(WebSocketChatServerHandler.class.getName());

    // Maintains a group of all active WebSocket channels
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * Called when a new client connects to the chat server.
     * Adds the new client's channel to the channel group and broadcasts a join message to all clients.
     *
     * @param ctx The ChannelHandlerContext for the new client
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());  // Adds the client's channel to the group
        broadcastMessage("User joined the chat");
    }

    /**
     * Called when a client disconnects from the chat server.
     * Removes the client's channel from the channel group and broadcasts a leave message to all clients.
     *
     * @param ctx The ChannelHandlerContext for the disconnecting client
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        channels.remove(ctx.channel());  // Removes the client's channel from the group
        broadcastMessage("User left the chat");
    }

    /**
     * Handles incoming WebSocket messages from clients.
     * If the message is a TextWebSocketFrame, it broadcasts the message text to all connected clients.
     *
     * @param ctx The ChannelHandlerContext of the sender
     * @param msg The received message
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof TextWebSocketFrame frame) {
            String receivedText = frame.text();  // Extracts the text message from the WebSocket frame
            logger.info("Received message: " + receivedText);
            broadcastMessage(receivedText);  // Broadcasts the message to all clients
        }
    }

    /**
     * Broadcasts a message to all clients in the chat.
     *
     * @param message The message to broadcast
     */
    private void broadcastMessage(String message) {
        for (var channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame(message));  // Sends the message as a TextWebSocketFrame to each client
        }
    }

    /**
     * Called when an exception occurs, logging the error and closing the client's channel.
     *
     * @param ctx The ChannelHandlerContext of the channel with an error
     * @param cause The Throwable representing the error
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Error in WebSocketChatServerHandler", cause);
        ctx.close();  // Closes the channel upon encountering an error
    }
}
