- Netty是一个高性能、异步事件驱动的NIO框架，基于JAVA NIO提供的API实现
- 它提供了对TCP、UDP和文件传输的支持，作为一个异步NIO框架
- Netty的所有IO操作都是异步非阻塞的，通过Future-Listener机制，用户可以方便的主动获取或者通过通知机制获得IO操作结果

### netty高性能
- 使用多路复用的NIO，使得一个selector拥有监听多通道处理多通道消息的能力，适合信道多但是消息不多的情况，如果每个通道的消息都很多，用这个模块并不合适
- 优势即使节省系统开销，单线程处理多个客户端请求，不需要额外的县城或者进程

#### 多路复用通讯方式
- netty架构按照reactor模式设计和实现,，它的服务端通信序列图如下：
```text
			NIOServer                   Reactor Thread                      IOHandler
   1.打开ServerSocketChannel
   2.绑定监听地址InetSocketAddress     3.创建Selector 启动线程
   4.将ServerSocketChannel注册到
   Selector,监听SelectionKey,OP_ACCEPT 5.Selector轮询key
                                       6.handleAccept处理新的客户端接入   7.设置新建客户端连接的Socket
                                       8.向Selector注册监听读操作
                                       SelectionKey.OP_READ
                                       9.handleRead异步都请求消息到       10.decode请求
                                       ByteBuffer 
                                       11.异步写ByteBuffer到SocketChannel

```
- 客户端时序图：
```text
NIO client                          Reactor Thread                      IOHandler
1.打开SocketChannel
2.设置SocketChannel为非阻塞模式，
同时设置TCP参数
3.异步链接服务端Server
4.判断连接结果，如果连接欸成功，
调到步骤10，否则执行步骤5
5.向Reactor县城的多路复用器注册   6.创建Selector 启动线程
OP_CONNECT事件                    7.Selector轮询就绪的key      
                                  8.handlerConnect()                 10.判断连接完成，执行步骤10
                                  10.向多路复用器注册读事件OP_READ
                                  11.handleRead异步读请求消息到      12.decode请求信息
                                  ByteBuffer
                                  13.异步写Bytebuffer到SocketChannel

```
- Netty的IO线程NioEventLoop由于聚合了多路复用器Selector，可以同时并发处理成百上千个客户端Channel，由于读写操作都是非阻塞的，这就可以充分提升IO线程的运行效率，避免由于频繁IO阻塞导致的线程挂起。


### 异步通信NIO
- 由于Netty采用了异步通信模式，一个IO线程可以并发处理N个客户端连接和读写操作，这从根本上解决了传统同步阻塞IO一连接一线程模型

### 零拷贝 -- DIRECT BUFFERS使用堆外直接内存
- Netty的接收和发送ByteBuffer采用DIRECT BUFFERS，使用堆外直接内存进行Socket读写，不需要进行字节缓冲区的二次拷贝。如果使用传统的堆内存（HEAP BUFFERS）进行Socket读写，JVM会将堆内存Buffer拷贝一份到直接内存中，然后才写入Socket中。相比于堆外直接内存，消息在发送过程中多了一次缓冲区的内存拷贝。
- Netty提供了组合Buffer对象，可以聚合多个ByteBuffer对象，用户可以像操作一个Buffer那样方便的对组合Buffer进行操作，避免了传统通过内存拷贝的方式将几个小Buffer合并成一个大的Buffer
- Netty的文件传输采用了transferTo方法，它可以直接将文件缓冲区的数据发送到目标Channel，避免了传统通过循环write方式导致的内存拷贝问题

### 内存池 -- 基于内存池的缓冲区重用机制
- 对于缓冲区Buffer，情况却稍有不同，特别是对于堆外直接内存的分配和回收，是一件耗时的操作。为了尽量重用缓冲区，Netty提供了基于内存池的缓冲区重用机制。

### Reactor模型 -- 高效的Reactor线程模型
- 常用的Reactor线程模型有三种，Reactor单线程模型, Reactor多线程模型, 主从Reactor多线程模型

#### Reactor单线程模型
#### Reactor多线程
#### Reactor主从多线程



