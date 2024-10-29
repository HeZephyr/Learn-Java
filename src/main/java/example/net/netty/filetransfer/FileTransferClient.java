package example.net.netty.filetransfer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.logging.Logger;

public class FileTransferClient {
    private static final Logger logger = Logger.getLogger(FileTransferClient.class.getName());
    private final String host;
    private final int port;
    private final String fileName;

    public FileTransferClient(String host, int port, String fileName) {
        this.host = host;
        this.port = port;
        this.fileName = fileName;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringEncoder(), new StringDecoder(), new FileTransferClientHandler(fileName));
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("Connected to server at " + host + ":" + port);
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new FileTransferClient("localhost", 8080, "example.txt").start();
    }
}
