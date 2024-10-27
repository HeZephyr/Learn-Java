# NIO

## NIO 示例项目 - Demo 1

该项目展示了使用非阻塞 I/O (NIO) 模型实现的简单客户端和服务器端应用程序。服务器监听特定端口，等待客户端连接。客户端连接后，服务器会向客户端发送一条消息。

### 项目结构

```
Learn-Java
├── src
│   └── main
│       └── java
│           └── example
│               └── nio
│                   └── demo1
│                       ├── NioServer.java  # 服务器端代码
│                       └── NioClient.java  # 客户端代码
└── pom.xml
```

### 运行说明

1. 启动 `NioServer` 服务器：
   ```bash
   mvn exec:java -Dexec.mainClass="example.nio.demo1.NioServer"
   ```

2. 启动 `NioClient` 客户端：
   ```bash
   mvn exec:java -Dexec.mainClass="example.nio.demo1.NioClient"
   ```

### NIO Demo 1 原理

NIO (Non-blocking I/O) 模型在非阻塞模式下允许服务器端和客户端的 I/O 操作不会阻塞当前线程，使其可以在没有数据到达时继续执行其他任务。在该 Demo 中，服务器使用 `ServerSocketChannel` 创建非阻塞的服务器，客户端使用 `SocketChannel` 连接服务器。

#### Demo 1 流程

1. **服务器启动并监听**：服务器端使用 `ServerSocketChannel` 监听端口，设置为非阻塞模式。
2. **客户端连接**：客户端使用 `SocketChannel` 连接服务器，设置为非阻塞模式。
3. **数据传输**：服务器发送一条消息给客户端，客户端读取后输出。

#### 缺点

- 由于没有引入 `Selector`，每次仅能处理一个连接，未能体现 NIO 的多路复用能力。
- 适合小规模连接或测试非阻塞的基本概念，但不适用于高并发场景。

---

通过 Demo 1，我们实现了基本的非阻塞服务器和客户端通信，为进一步学习 NIO 的多路复用机制（将在 Demo 2 中介绍）打下基础。
