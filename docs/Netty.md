# Netty

Netty 是一个高性能、异步事件驱动的网络应用框架，广泛应用于高并发、低延迟的网络服务开发中。Netty 基于 Java NIO，为开发者提供了便捷的 API 和强大的组件，使得网络应用的开发更加高效和灵活。

## Netty 的核心组件

在 Netty 中，以下几个核心组件是构建网络应用的基础：

### 1. **EventLoopGroup**
- **功能**：`EventLoopGroup` 管理并分配 I/O 线程，负责处理网络事件（如连接、读写数据等）。
- **作用**：服务器端通常使用两个 `EventLoopGroup`，分别处理连接的接收和数据传输；而客户端则仅需一个 `EventLoopGroup`。
- **常用实现**：
    - `NioEventLoopGroup`：基于 NIO 的实现，适用于非阻塞 I/O。
    - `EpollEventLoopGroup`：基于 Linux `epoll` 的实现，用于更高性能的 I/O 操作（Linux 平台专用）。

### 2. **Channel**
- **功能**：`Channel` 是 Netty 中数据传输的抽象，可以理解为数据的通道。Netty 使用不同的 `Channel` 实现来支持不同的传输方式和协议。
- **常用实现**：
    - `NioServerSocketChannel`：非阻塞的 TCP 服务器通道。
    - `NioSocketChannel`：非阻塞的 TCP 客户端通道。
- **作用**：服务器端通常使用 `NioServerSocketChannel` 监听端口并接受连接，客户端使用 `NioSocketChannel` 连接服务器并发送数据。

### 3. **ChannelPipeline 和 ChannelHandler**
- **功能**：`ChannelPipeline` 是一个处理器链，负责管理 `ChannelHandler`。每个 `Channel` 都包含一个 `ChannelPipeline`，在数据进入或离开 `Channel` 时依次通过 `ChannelPipeline` 中的 `ChannelHandler` 进行处理。
- **ChannelHandler**：用于处理具体的事件和数据，包括数据的解码、编码和业务逻辑处理。
- **常用的 `ChannelHandler`**：
    - `StringEncoder` 和 `StringDecoder`：将字符串和字节数据互相转换，适用于文本数据传输。
    - `LoggingHandler`：用于日志记录，可以在调试时查看事件的触发和数据流动情况。

### 4. **Bootstrap 和 ServerBootstrap**
- **功能**：`Bootstrap` 和 `ServerBootstrap` 是 Netty 的引导类，负责初始化和配置客户端或服务器。
- **ServerBootstrap**：用于配置服务器端，例如指定 `bossGroup` 和 `workerGroup`，设置监听端口、管道处理器等。
- **Bootstrap**：用于配置客户端，通常需要指定目标服务器地址和端口，并添加 `ChannelInitializer` 配置处理器。

---

## Basic 项目

### 项目结构

```
Learn-Java
├── src
│   └── main
│       └── java
│           └── example
│               └── net
│                   └── netty
│                       └── basic
│                           ├── BasicNettyServer.java  # Netty服务器端代码
│                           ├── BasicNettyClient.java  # Netty客户端代码
│                           └── ServerHandler.java     # 服务器的消息处理器
|                           └── ClientHandler.java     # 客户端的消息处理器  
└── pom.xml
```

### 项目介绍

`basic` 项目展示了 Netty 的基础服务器和客户端通信模型。服务器配置 `ServerBootstrap` 接收连接，并通过 `ServerHandler` 处理客户端请求；客户端配置 `Bootstrap` 连接服务器，并通过 `ClientHandler` 发送消息。该项目使用了 Netty 的 `StringEncoder` 和 `StringDecoder` 进行字符串的编码和解码。

### BasicNettyServer

`BasicNettyServer` 是一个基础的 Netty 服务器实现，包含以下关键步骤：

1. **配置线程组**：
    - `bossGroup`：处理客户端连接请求。
    - `workerGroup`：处理已连接客户端的数据传输。

   ```java
   EventLoopGroup bossGroup = new NioEventLoopGroup(1);
   EventLoopGroup workerGroup = new NioEventLoopGroup();
   ```

2. **指定通道类型**：`NioServerSocketChannel` 用于非阻塞 TCP 连接。

   ```java
   bootstrap.channel(NioServerSocketChannel.class);
   ```

3. **配置处理器链**：使用 `childHandler` 添加 `StringDecoder`、`StringEncoder` 和自定义 `ServerHandler`。

   ```java
   bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
       @Override
       protected void initChannel(SocketChannel ch) {
           ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ServerHandler());
       }
   });
   ```

4. **启动服务器**：通过 `bind` 方法绑定端口并启动服务器。

   ```java
   ChannelFuture future = bootstrap.bind(port).sync();
   ```

#### 服务器工作流程

1. **监听端口**：`ServerBootstrap` 监听端口，等待客户端连接。
2. **处理请求**：连接建立后，通过 `ServerHandler` 处理客户端请求。
3. **发送响应**：接收消息后，服务器通过 `ServerHandler` 将响应回传给客户端。

### BasicNettyClient

`BasicNettyClient` 是一个简单的 Netty 客户端，用于连接服务器、发送消息并接收响应。主要步骤如下：

1. **配置线程组**：客户端仅需一个 `NioEventLoopGroup`。

   ```java
   EventLoopGroup group = new NioEventLoopGroup();
   ```

2. **指定通道类型**：使用 `NioSocketChannel` 作为非阻塞客户端通道。

   ```java
   bootstrap.channel(NioSocketChannel.class);
   ```

3. **配置处理器链**：在 `handler` 中添加 `StringEncoder`、`StringDecoder` 和自定义 `ClientHandler`。

   ```java
   bootstrap.handler(new ChannelInitializer<SocketChannel>() {
       @Override
       protected void initChannel(SocketChannel ch) {
           ch.pipeline().addLast(new StringEncoder(), new StringDecoder(), new ClientHandler());
       }
   });
   ```

4. **连接服务器**：通过 `connect` 方法连接到服务器。

   ```java
   ChannelFuture future = bootstrap.connect(host, port).sync();
   ```

#### 客户端工作流程

1. **连接服务器**：客户端连接到指定服务器地址和端口。
2. **发送消息**：连接成功后，`ClientHandler` 的 `channelActive` 方法被触发并发送消息。
3. **接收响应**：客户端接收服务器返回的响应消息。

---

### Basic 总结

该 `basic` 项目展示了一个最简化的 Netty 服务器和客户端通信模型，包括配置 `EventLoopGroup`、通道类型、编码解码器和处理器。它提供了一个清晰的流程，用于理解 Netty 的核心配置和通信机制，为进一步开发复杂的网络应用奠定基础。

## Echo Server 项目

`Echo Server` 项目展示了一个简单的回显服务器和客户端的实现。当客户端发送消息时，服务器会将收到的消息原样返回给客户端。

### 项目结构

```
Learn-Java
├── src
│   └── main
│       └── java
│           └── example
│               └── net
│                   └── netty
│                       └── echo
│                           ├── EchoServer.java         # Echo服务器端代码
│                           ├── EchoServerHandler.java  # 服务器消息回显处理器
│                           ├── EchoClient.java         # Echo客户端代码
│                           └── EchoClientHandler.java  # 客户端消息处理器
└── pom.xml
```

### 项目介绍

该项目展示了一个基础的 Netty 回显服务器，客户端发送的每条消息都会被服务器回显。Echo Server 使用了 `ChannelHandler` 来实现简单的请求响应机制。

### EchoServer

`EchoServer` 使用 `ServerBootstrap` 来配置和启动服务器。

1. **配置线程组**：
    - `bossGroup`：处理客户端的连接请求。
    - `workerGroup`：处理已连接客户端的 I/O 事件。

   ```java
   EventLoopGroup bossGroup = new NioEventLoopGroup(1);
   EventLoopGroup workerGroup = new NioEventLoopGroup();
   ```

2. **指定通道类型**：使用 `NioServerSocketChannel` 处理非阻塞 TCP 连接。

3. **配置处理器链**：添加 `StringDecoder` 和 `StringEncoder` 编解码器以处理字符串数据，并添加 `EchoServerHandler` 来回显消息。

4. **启动服务器**：通过 `bind` 绑定端口并启动服务器。

#### 服务器工作流程

1. **连接建立**：`ServerBootstrap` 监听端口，客户端连接成功后，触发 `channelActive` 事件。
2. **消息处理**：客户端发送消息时，`EchoServerHandler` 的 `channelRead` 方法被调用，并将消息写回客户端。
3. **断开连接**：当客户端断开连接时，服务器清理资源。

### EchoServerHandler

`EchoServerHandler` 是服务器的核心处理器，负责接收和回显消息。

- **channelRead**：当接收到客户端消息时，直接将消息写回到客户端的通道。
- **exceptionCaught**：在处理过程中出现异常时，关闭通道以释放资源。

### EchoClient

`EchoClient` 使用 `Bootstrap` 来配置和连接服务器。

1. **配置线程组**：使用单独的 `NioEventLoopGroup` 处理客户端 I/O 操作。
2. **指定通道类型**：使用 `NioSocketChannel` 作为客户端的非阻塞通道。
3. **配置处理器链**：添加 `StringEncoder`、`StringDecoder` 和 `EchoClientHandler`。
4. **连接服务器**：通过 `connect` 方法连接到服务器。

#### 客户端工作流程

1. **连接服务器**：连接服务器后，`EchoClientHandler` 的 `channelActive` 事件触发，开始接受用户输入。
2. **发送消息**：用户输入消息并发送到服务器。
3. **接收回显**：服务器回显消息时，`EchoClientHandler` 的 `channelRead` 被调用。

### EchoClientHandler

`EchoClientHandler` 负责读取用户输入并发送消息到服务器，以及处理来自服务器的回显消息。

- **channelActive**：在通道激活时，启动一个新的线程等待用户输入，以避免阻塞 Netty 的 I/O 线程。
- **channelRead**：当接收到服务器的回显消息时，输出到控制台。
- **exceptionCaught**：在出现异常时关闭通道。

### 注意事项

- `channelActive` 方法中使用新线程等待用户输入，以避免阻塞主线程。若不使用新线程，客户端会在用户输入阻塞，无法响应服务器的回显消息。

### 运行说明

1. 启动 `EchoServer` 服务器：
   ```bash
   mvn exec:java -Dexec.mainClass="example.net.netty.echo.EchoServer"
   ```

2. 启动 `EchoClient` 客户端并发送消息：
   ```bash
   mvn exec:java -Dexec.mainClass="example.net.netty.echo.EchoClient"
   ```

输入消息后，服务器会回显相同的消息。输入 `quit` 可断开客户端连接。

---

### Echo Server 项目总结

Echo Server 项目展示了 Netty 的回显通信模型，包括客户端到服务器的请求、服务器的回显和响应。它演示了如何配置 `EventLoopGroup`、`ChannelPipeline` 和 `ChannelHandler`，并通过多线程支持同时处理输入和接收服务器消息。

## Heartbeat 项目

`Heartbeat` 项目展示了 Netty 中的心跳机制，确保客户端和服务器间的连接有效。该机制通过客户端定期发送心跳消息，服务器根据空闲检测机制关闭长时间无活动的连接，避免资源浪费。

### 项目结构

```
Learn-Java
├── src
│   └── main
│       └── java
│           └── example
│               └── net
│                   └── netty
│                       └── heartbeat
│                           ├── HeartbeatServer.java         # 服务器端代码
│                           ├── HeartbeatServerHandler.java  # 服务器处理器
│                           ├── HeartbeatClient.java         # 客户端代码
│                           └── HeartbeatClientHandler.java  # 客户端处理器
└── pom.xml
```

### Heartbeat 项目流程

```plaintext
Client                             Server
  |                                   |
  | ---- Connect to Server ---------->|
  |                                   |
  | <-- Connection Established -------|
  |                                   |
  | --- Periodic "HEARTBEAT" -------> |
  |                                   |
  | <-- "Server response: HEARTBEAT" -|
  |                                   |
  | (Repeat heartbeat every 4 secs)   |
  |                                   |
  | If no heartbeat after 5 secs      |
  | --> Close connection               |
  |                                   |
  |<-- Connection closed by server   |
```

### 注意事项

- **`IdleStateHandler` 配置**：客户端 `IdleStateHandler` 设置为 4 秒写空闲，服务器为 5 秒读空闲。超过指定时间没有活动的连接将被关闭。
- **适用场景**：在长连接需求的应用场景（如即时通讯、游戏）中，心跳机制能有效检测连接状态，确保资源合理分配。

### 运行说明

1. 启动服务器：
   ```bash
   mvn exec:java -Dexec.mainClass="example.net.netty.heartbeat.HeartbeatServer"
   ```

2. 启动客户端并观察心跳通信：
   ```bash
   mvn exec:java -Dexec.mainClass="example.net.netty.heartbeat.HeartbeatClient"
   ```

---

### Heartbeat 项目总结

Heartbeat 项目展示了 Netty 中使用 `IdleStateHandler` 和定期心跳消息进行连接检测的方式，是长连接应用中的有效资源管理方法。