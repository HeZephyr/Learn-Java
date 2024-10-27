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

### 缺点

- 由于没有引入 `Selector`，每次仅能处理一个连接，未能体现 NIO 的多路复用能力。
- 适合小规模连接或测试非阻塞的基本概念，但不适用于高并发场景。

---
### Demo1 总结
通过 Demo 1，我们实现了基本的非阻塞服务器和客户端通信，为进一步学习 NIO 的多路复用机制（将在 Demo 2 中介绍）打下基础。

## NIO 示例项目 - Demo 2

该项目展示了使用非阻塞 I/O (NIO) 模型和 `Selector` 多路复用实现的多客户端服务器。服务器通过 `ServerSocketChannel` 监听端口，使用 `Selector` 管理多个客户端连接，实现高效的 I/O 操作。

### 项目结构

```
Learn-Java
├── src
│   └── main
│       └── java
│           └── example
│               └── nio
│                   └── demo2
│                       ├── NioServer.java  # 服务器端代码
│                       └── NioClient.java  # 客户端代码
└── pom.xml
```

### 运行说明

1. 启动 `NioServer` 服务器：
   ```bash
   mvn exec:java -Dexec.mainClass="example.nio.demo2.NioServer"
   ```

2. 启动多个 `NioClient` 客户端，模拟多个连接：
   ```bash
   mvn exec:java -Dexec.mainClass="example.nio.demo2.NioClient"
   ```

### NIO Demo 2 原理

在 Demo 2 中，服务器使用了 `Selector` 和非阻塞模式，通过一个线程管理多个客户端连接，实现了多路复用和高效的资源利用。

#### Demo 2 流程

1. **服务器启动并监听**：服务器通过 `ServerSocketChannel` 监听端口，设置为非阻塞模式，并注册到 `Selector` 监听 `OP_ACCEPT` 事件。
2. **多路复用选择器**：服务器通过 `Selector.select()` 等待任何一个注册的通道准备好进行 I/O 操作。当客户端连接请求或发送数据时，`Selector` 会触发相应的事件。
3. **事件处理**：
   - **接受连接**：当有新的客户端连接时，通过 `OP_ACCEPT` 事件触发 `handleAccept()` 方法，接受连接并将新的 `SocketChannel` 注册到 `Selector` 上，监听 `OP_READ` 事件。
   - **读取数据**：当有客户端发送数据时，通过 `OP_READ` 事件触发 `handleRead()` 方法，读取客户端数据并回传确认信息。
4. **数据传输与回传**：服务器从每个客户端读取数据后，将收到的消息回传给客户端，模拟简单的回显服务。
5. 正确关闭连接：在服务器检测到客户端主动关闭连接时，服务器也会关闭相应的 SocketChannel 并取消 SelectionKey，确保通道已从 Selector 中移除。这样可以避免在已关闭的通道上执行操作。

### `ServerChannel` 和 `ClientChannel` 的角色

- **ServerChannel (`ServerSocketChannel`)**：用于**监听新的客户端连接**。在 `NioServer` 中，`serverChannel` 绑定到固定端口，配置为非阻塞模式。服务器通过 `Selector` 等待 `OP_ACCEPT` 事件来接收新连接，每次接收新的客户端连接时返回一个 `SocketChannel`。

- **ClientChannel (`SocketChannel`)**：用于**与特定客户端进行数据传输**。当 `serverChannel` 接受连接后，为每个客户端创建一个新的 `clientChannel`。`clientChannel` 同样配置为非阻塞模式，并注册到 `Selector` 上，监听 `OP_READ` 事件，用于处理数据的读写操作。

### Selector 与轮询

在传统的阻塞 I/O 模型中，服务器可能需要频繁轮询每个通道，检查是否有 I/O 事件。但在 NIO 模型中：

- **`Selector` 的作用**：`Selector` 提供了事件驱动的多路复用机制，使服务器可以通过一个阻塞 `select()` 调用等待多个通道的 I/O 事件。`Selector.select()` 会阻塞直到至少一个通道就绪，然后返回已就绪通道的集合，避免了无效轮询的资源浪费。
- **事件通知机制**：`Selector` 通过 `SelectionKey` 标识事件类型 (`OP_ACCEPT`、`OP_READ` 等)，服务器仅在事件触发时处理相应通道，减少了阻塞调用的数量，提高了 CPU 的利用效率。

### 非阻塞的优势与应用场景

- **非阻塞 I/O 模式**：在非阻塞模式下，通道的 `accept()`、`read()` 和 `write()` 方法会立即返回，不会阻塞线程。这允许服务器线程同时处理其他任务，而不必在单一 I/O 操作上等待。

- **优势**：
   - **减少线程资源**：在高并发情况下，服务器可以通过一个线程集中管理多个客户端连接，避免了为每个连接创建独立线程的高成本。
   - **灵活的事件驱动**：通过 `Selector` 的多路复用机制，服务器在有事件发生时才会进行 I/O 操作，避免了 BIO 中线程等待 I/O 就绪的低效方式。

- **适用场景**：非阻塞 I/O 和 `Selector` 模型适用于需要处理大量并发连接的高性能服务器，如聊天系统、WebSocket 服务、文件传输服务器等。

---

通过 Demo 2，我们使用 `Selector` 实现了非阻塞、多路复用的服务器端，能够有效处理多个客户端连接，为深入理解 NIO 在高并发场景下的优势奠定了基础。

以下是 `NIO.md` 文件中关于 Demo 3 的文档说明，详细解释了回调机制和 NIO 的特性。

---

## NIO 示例项目 - Demo 3

该示例展示了如何使用 Java NIO 实现异步文件传输服务。服务器通过 `AsynchronousFileChannel` 从文件中读取数据块，并将每块数据发送给客户端，客户端则异步接收数据并保存到本地文件中。

### 项目结构

```
Learn-Java
├── src
│   └── main
│       └── java
│           └── example
│               └── nio
│                   └── demo3
│                       ├── AsyncFileServer.java  # 异步文件传输服务器
│                       └── FileClient.java       # 异步文件接收客户端
└── pom.xml
```

### 运行说明

1. 启动 `AsyncFileServer` 服务器：
   ```bash
   mvn exec:java -Dexec.mainClass="example.nio.demo3.AsyncFileServer"
   ```

2. 启动 `FileClient` 客户端，模拟文件请求和下载：
   ```bash
   mvn exec:java -Dexec.mainClass="example.nio.demo3.FileClient"
   ```

### Demo 3 原理

Demo 3 使用了 Java NIO 的异步 I/O 特性，包括 `AsynchronousSocketChannel` 和 `AsynchronousFileChannel`，实现非阻塞的文件传输。异步回调 (`CompletionHandler`) 是实现异步传输的核心，使文件数据可以分块读取并依次传输给客户端。

### Demo 3 的完整流程

```
Client                     Server
  |                           |
  | Connect to Server         |  -- Asynchronous connection --
  |-------------------------->|
  |                           |
  | Send filename request     |
  |-------------------------->|
  |                           |
  |        Receive filename   |  -- Asynchronous read --
  |                           |
  |                           | Open file asynchronously
  |                           |   using AsynchronousFileChannel
  |                           |
  |                           |
  |        Read file chunk    |  -- Asynchronous read --
  |                           |
  | Send file chunk           |
  |<--------------------------|
  | Write chunk to file       |
  |                           |
  |        (repeat)           |  -- Loop until EOF --
  |                           |
  |        Last file chunk    |
  |<--------------------------|
  | Write final chunk         |
  |                           |
  |                           | Close file & channel
  |                           |   after transfer completion
  |                           |
  | Disconnect & Close        |
  |                           |
```

1. **服务器启动和连接接受**：服务器使用 `AsynchronousServerSocketChannel` 异步监听端口 8080 等待客户端连接请求。通过调用 `accept()` 获取一个 `Future` 对象，服务器可以执行其他操作，而无需阻塞等待连接。
2. **读取客户端文件请求**：服务器与客户端连接后，服务器通过 `CompletionHandler` 异步读取客户端发送的文件名请求。
3. **异步读取文件内容并发送**：服务器收到文件名后，使用 `AsynchronousFileChannel` 异步读取文件内容，以分块的形式将数据发送到客户端。
4. **客户端异步接收数据**：客户端接收数据的每个块，并将其写入本地文件中，直到文件传输完成。
5. **资源释放与连接关闭**：文件传输完成后，服务器与客户端都会关闭相关资源和连接。服务器通过关闭 `AsynchronousFileChannel` 释放文件资源，同时关闭 `AsynchronousSocketChannel` 以断开与客户端的连接。客户端在确认接收完成后也关闭自身的通道和关联的 `AsynchronousChannelGroup`，确保所有后台线程正确终止，避免资源泄漏。

### 回调和异步机制详解

Demo 3 中的异步文件传输实现依赖于 **回调嵌套**，即读取文件的回调完成后，嵌套调用写入客户端的回调，以顺序完成传输流程。

#### `CompletionHandler` 及嵌套回调机制

1. **文件读取回调 (`fileChannel.read(...)`)**：
    - 每次从文件中读取数据时，触发 `CompletionHandler` 的 `completed` 方法。
    - 读取完成后，切换缓冲区为“读模式”（`flip()`），然后将数据写入客户端。
    - 当文件读取到结尾 (`bytesRead == -1`) 时，关闭文件通道和客户端通道，结束传输。

2. **数据写入回调 (`clientChannel.write(...)`)**：
    - 将数据块写入客户端时，触发嵌套的 `CompletionHandler` 的 `completed` 方法。
    - 写入完成后，清空缓冲区以准备下一次读取，并更新文件读取位置。
    - 再次调用 `readFileChunk` 读取下一个数据块，进入下一个回调循环，直到文件全部发送完成。

#### 回调嵌套的实现

这种嵌套回调模式使得每一个操作（读取或写入）完成后才能继续下一个操作，确保了传输顺序的正确性。同时，所有操作均为非阻塞，能够在等待完成时执行其他任务。

#### 回调中的异常处理

在每个 `CompletionHandler` 中，`failed` 方法用于处理读写失败的情况，如文件读取失败或网络写入失败。失败后，通道会被关闭以确保资源的正确释放。

### 使用的 NIO 特性

- **`AsynchronousFileChannel`**：用于从服务器端文件中异步读取数据，使读取操作不会阻塞主线程。
- **`AsynchronousSocketChannel`**：用于客户端和服务器的非阻塞数据传输，允许多个客户端同时连接服务器。
- **`CompletionHandler`**：Java NIO 的回调接口，用于异步操作完成或失败时触发相应的逻辑。Demo 3 中通过嵌套的 `CompletionHandler` 实现了顺序的非阻塞传输。
- **非阻塞 I/O (NIO)**：允许服务器和客户端以异步方式发送和接收数据块，提高资源利用率。

### Demo 3 总结

通过 Demo 3，我们展示了如何使用 Java NIO 的异步 I/O 和回调机制实现高效的文件传输。服务器使用 `AsynchronousFileChannel` 读取文件并通过 `AsynchronousSocketChannel` 将数据发送给客户端，客户端则异步接收文件并存储。整个过程无阻塞，实现了高效的数据传输。