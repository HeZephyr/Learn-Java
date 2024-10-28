package example.net.netty.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(EchoClientHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Connected to server. Type messages to echo:");

        // start a new thread to read user input and send to server
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if ("quit".equalsIgnoreCase(message)) {
                    ctx.close();
                    break;
                }
                ctx.writeAndFlush(message);
            }
            scanner.close();
        }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("Received echo: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Error in EchoClientHandler", cause);
        ctx.close();
    }
}
