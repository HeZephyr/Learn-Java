package example.net.netty.filetransfer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransferClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(FileTransferClientHandler.class.getName());
    private final String fileName;
    private FileOutputStream fileOutputStream;
    private static final String CLIENT_DIRECTORY = "client_files";

    public FileTransferClientHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Requesting file: " + fileName);
        ctx.writeAndFlush(fileName);

        try {
            Path filePath = Paths.get(CLIENT_DIRECTORY, fileName);
            Files.createDirectories(filePath.getParent());
            fileOutputStream = new FileOutputStream(filePath.toFile());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to prepare output file", e);
            ctx.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        if (msg instanceof String responseMessage) {
            if ("TRANSFER_COMPLETE".equals(responseMessage)) {
                logger.info("File transfer completed successfully: " + fileName);
                ctx.close();
            } else {
                logger.info("Received message from server: " + responseMessage);
                fileOutputStream.write(responseMessage.getBytes());
            }
        } else {
            logger.warning("Unexpected message type: " + msg.getClass());
        }
    }

    private void closeFile() {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
                logger.info("File output stream closed.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close file output stream", e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeFile();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Error in FileTransferClientHandler", cause);
        ctx.close();
    }
}
