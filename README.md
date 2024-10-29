# Learn-Java
Record my learning process of Java

## Learning Progress

- [x] BIO
- [x] NIO
- [x] Netty

---

## Project Overview

This project documents my learning journey in Java, focusing on various network programming models commonly used in backend development. Each section includes examples to illustrate the concepts, structured as follows:

### 1. BIO (Blocking I/O)
BIO, or Blocking I/O, is the traditional I/O model in Java. It uses blocking streams and sockets to handle network communication, where each connection is managed by a dedicated thread. This model is suitable for simpler applications but can be inefficient for high-concurrency scenarios.

#### Key Features:
- Blocking calls, where each client connection is handled in a dedicated thread.
- Suitable for small-scale applications with lower concurrency requirements.

#### Example:
In this project, the BIO example demonstrates a simple server-client interaction where the server accepts a client connection, processes the request, and responds. This approach is easy to understand but can become resource-intensive as the number of clients increases.

### 2. NIO (Non-blocking I/O)
Java NIO introduces non-blocking I/O and buffer-oriented data handling, suitable for applications that need to handle many connections simultaneously without dedicating a separate thread to each connection. NIO provides efficient mechanisms for I/O operations by using channels, buffers, and selectors.

#### Key Features:
- Non-blocking channels that allow a single thread to handle multiple connections.
- Selector-based model, enabling a single thread to manage multiple channels.
- Useful for high-concurrency applications, where the server needs to handle many connections without excessive thread management.

#### Examples:
- **Demo 1**: Basic non-blocking I/O with channels.
- **Demo 2**: Multiplexing with selectors to manage multiple channels in a single thread.
- **Demo 3**: Asynchronous file transfer using nested `CompletionHandler` callbacks.
- **Demo 4**: Data serialization using `ByteBuffer`, focusing on efficient object-to-byte array conversion for high-performance network protocols.

### 3. Netty
Netty is a high-performance, asynchronous event-driven framework for building scalable network applications. It builds on NIO and provides a simplified API with additional features for handling protocols, managing connections, and implementing custom encoding and decoding.

#### Key Features:
- Built on top of NIO, with added abstractions to manage complex I/O patterns.
- Supports high concurrency and scales well with non-blocking, asynchronous operations.
- Commonly used for building custom protocols, web servers, and distributed applications.

#### Examples:
- **Basic**: Simple Netty server-client setup to establish a basic connection and message exchange.
- **Echo**: Echo server that returns received messages to the client, demonstrating request-response patterns.
- **Heartbeat**: Heartbeat mechanism using `IdleStateHandler` to detect and handle idle connections for connection stability.
- **File Transfer**: Implements file transfer using zero-copy (`FileRegion`), allowing efficient file sending between server and client.
- **WebSocket Chat**: WebSocket-based chat server for real-time message broadcasting among multiple clients connected to a WebSocket endpoint.
---


## Running Examples

Each example in this project is organized into its own package and can be executed using Maven. For instance, to run the `BioServer` example, use:

```bash
mvn exec:java -Dexec.mainClass="example.net.bio.BioServer"
