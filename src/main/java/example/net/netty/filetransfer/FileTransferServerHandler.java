package example.net.netty.filetransfer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransferServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(FileTransferServerHandler.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String fileName) {
            sendFile(ctx, fileName);
        } else {
            logger.warning("Unexpected message type received: " + msg.getClass());
        }
    }

    private void sendFile(ChannelHandlerContext ctx, String fileName) {
        Path filePath = Paths.get("server_files", fileName);

        if (!filePath.toFile().exists()) {
            logger.warning("Requested file not found: " + fileName);
            ctx.writeAndFlush("File not found");
            return;
        }

        try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();
            FileRegion region = new DefaultFileRegion(fileChannel, 0, fileSize);

            // Send file using zero-copy
            ctx.writeAndFlush(region).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("File sent successfully using zero-copy: " + fileName);
                    // After file transfer is complete, send completion message
                    ctx.writeAndFlush("TRANSFER_COMPLETE");
                } else {
                    logger.log(Level.SEVERE, "Failed to send file: " + fileName, future.cause());
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send file: " + fileName, e);
            ctx.writeAndFlush("Error sending file");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Error in FileTransferServerHandler", cause);
        ctx.close();
    }
}
