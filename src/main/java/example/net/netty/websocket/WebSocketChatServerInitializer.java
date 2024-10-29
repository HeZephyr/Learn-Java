package example.net.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketChatServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new HttpServerCodec()); // HTTP 编解码器
        ch.pipeline().addLast(new HttpObjectAggregator(65536)); // 聚合 HTTP 请求
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws")); // WebSocket 协议处理器
        ch.pipeline().addLast(new WebSocketChatServerHandler()); // 自定义消息处理器
    }
}
