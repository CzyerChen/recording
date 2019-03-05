- 学习Java I/O模型，从BIO到NIO和Reactor模式
- 参阅 http://www.jasongj.com/java/nio_reactor/
- 预留下详细学习的渠道：http://ifeve.com/java-nio-all/

### 一、java IO模型
#### 1.同步IO/异步IO
- 同步IO是指请求必须顺序被执行，没有并行并发的操作，一个处理完处理下一个
- 异步IO是指多请求可以并发并行地执行，用户线程发起IO请求，内核IO完成后通知用户线程，或者使用用户线程地回调函数返回结果

#### 2.阻塞IO/非阻塞IO
- 阻塞IO，某个请求发出后，请求操作条件不满足就一直阻塞，直到满足条件才执行
- 非阻塞IO，某个请求如果不满足请求操作条件，就会返回条件不满足地消息，不会阻塞执行
- 阻塞并不等价于同步，而非阻塞并非等价于异步
- 阻塞和非阻塞重点在于请求的方法是否立即返回

### 二、Unix五种IO模型
- 阻塞IO
- 非阻塞IO
- IO多路复用
- 信号驱动IO
- 异步IO

#### 1.阻塞IO
分为两个阶段：
- 阶段1：等待数据就绪
  - 网络IO ，等待远端数据抵达
  - 磁盘IO ，等待磁盘数据读取到内核态内存中
  
- 阶段2：数据拷贝
  - 数据拷贝，就是由内核操作的，将数据从内核态内存，拷贝到用户态内存
  - 这么做是为了考虑系统安全，用户态线程没有权限操作内核态内存


### 2.非阻塞IO
分为三个阶段
- 阶段1：通知内核
  - socket通知内核，当前请求为非阻塞，不要把用户线程睡眠，直接返回错误码即可
- 阶段2：数据准备
  - 客户端的IO操作线程，虽然会因为异步返回码不被阻塞，但是由于数据还未准备好，IO操作线程还是会不断请求数据，轮询、重试，直到2获取数据
- 阶段3：数据拷贝
  - 数据准备完毕，内核就负责把数据从内核态内存拷贝到用户态内存
- 其中第二阶段的轮询会消耗大量CPU，因而不建议单独使用

### 3.IO多路复用
- IO多路复用是一个很好的想法，能够在单线程情况下，监视多个IOchannel的请求
- 会用到select poll函数，这两个函数能够阻塞多路IO，和阻塞IO不同
- 可以应对多路写、多路读的检测，直到有数据可读可写为止，才调用真正的IO线程去读写数据，减少了大量的重试和轮询
- 也是分为两个阶段：
    - 等待数据就绪：通过select/ poll 函数，监听多路channel，直到发现某一路数据就绪，具备读写的可能
    - 数据拷贝，通过某一路channel能够读写数据后，就将数据从内核态内存拷贝到用户态内存

### 4.信号驱动IO
- 允许socket进行信号驱动I/O，并安装一个信号处理函数，线程继续运行并不阻塞。当数据准备好时，线程会收到一个SIGIO 信号，可以在信号处理函数中调用I/O操作函数处理数据

#### 5.异步IO
- 阶段1：调用aio_read 函数，告诉内核描述字，缓冲区指针，缓冲区大小，文件偏移以及通知的方式，然后立即返回
- 阶段2：当内核将数据拷贝到缓冲区后，再通知应用程序
- 以上两个阶段都由内核完成，因而用户线程根本不用参与


#### 6.Java中四种IO模型
- 和unix相比，Java中仅有信号驱动IO的模型没有实现，其余均支持。
- 早提供的blocking I/O即是阻塞I/O，BIO
- 非阻塞I/O,NIO
- 通过NIO实现的Reactor模式即是I/O复用模型
- 通过AIO实现的Proactor模式即是异步I/O模型

### 从IO到NIO
#### 1.面向流/面向缓冲
**Java IO 面向流**
- Java IO是面向流的，每次都是从流中读取数据，直到读完所有，或者异常终止
- 它不具备缓存
- 它不能在流中将数据前后移动处理

**Java NIO 面向缓冲**
- Java NIO是面向缓冲的，数据会被读取到一个缓冲区
- 在缓冲区中，可以对数据前后移动处理，处理灵活
- 读写请求并不会阻塞当前线程

#### 2.阻塞/非阻塞
**Java IO 阻塞**
- Java IO 会阻塞当前线程，read write操作的时候，阻塞直到数据被读入或者数据被写入，阻塞期间当前线程无法做其他操作

**Java NIO 非阻塞**
- 读写请求不会阻塞当前线程，可以做其他事情

#### 3.选择器Selector
- 选择器允许一个单独的线程同时监控多个通道到同一个选择器上，然后使用一个单独的线程监控多个通道

#### 4.零拷贝
- 零拷贝是指，数据从磁盘读取之后，无需经过内核态内存到用户态内存，用户态内存和内核态内存，这两次上下文切换
- 直接从磁盘读取到内核态内存，通过FileChannel，使用操作系统的sendFile方法，直接从内核态将数据发送出去

### NIO 之Channel、Buffer
#### 1. Buffer
- Buffer： 一块缓存区，内部使用字节数组存储数据，并维护几个特殊变量，实现数据的反复利用

- 几个概念
    - mark：初始值为-1，用于备份当前的position
    - position：初始值为0，position表示当前可以写入或读取数据的位置
    - limit：写模式，limit表示最多能往Buffer里写多少数据，等于capacity值；读模式，limit表示最多可以读取多少数据
    - capacity：缓存数组大小

- 介绍几个方法
    - mark：将当前的position赋给mark，mark做position的副本 
    - reset：将mark重置为当前position位置
    - clear：clear会恢复状态值，但是不会擦除数据，准备为下一次数据的读取/写入做准备
    - flip：做模式切换，将buffer 从写模式变成读模式
    - rewind：重置position为0，从头读写数据

- 类型
```text
ByteBuffer

CharBuffer

DoubleBuffer

FloatBuffer

IntBuffer

LongBuffer

ShortBuffer

MappedByteBuffer
```
- 实现类："HeapByteBuffer"和"DirectByteBuffer"
    - HeapByteBuffer：在堆空间上申请空间

    - DirectByteBuffer：在物理内存上申请空间（非jvm堆内存）
        - VM.isDirectMemoryPageAligned()
        - unsafe.allocateMemory(size)
        - unsafe.setMemory(base,size,(byte) 0)把新申请的内存数据清零
 
#### 2.Channel
- Channel :NIO把它支持的I/O对象抽象为Channel，Channel又称“通道”，类似于原I/O中的流（Stream）
- 流是单向的，通道是双向的，可读可写
- 流是阻塞的，通道时异步读写
- 流中数据可以选择读取到缓存，通道数据会自动读取到缓存

- 类型：
```text
FileChannel

DatagramChannel

SocketChannel

ServerSocketChannel
```
- FileChannel的read、write和map通过其实现类FileChannelImpl
- READ实现

FileChannelImpl--read
```text
public int read(ByteBuffer var1) throws IOException {
        this.ensureOpen();
        if (!this.readable) {
            throw new NonReadableChannelException();
        } else {
            Object var2 = this.positionLock;
            synchronized(this.positionLock) {
                int var3 = 0;
                int var4 = -1;

                try {
                    this.begin();
                    var4 = this.threads.add();
                    if (!this.isOpen()) {
                        byte var12 = 0;
                        return var12;
                    } else {
                        do {
                            var3 = IOUtil.read(this.fd, var1, -1L, this.nd);
                        } while(var3 == -3 && this.isOpen());

                        int var5 = IOStatus.normalize(var3);
                        return var5;
                    }
                } finally {
                    this.threads.remove(var4);
                    this.end(var3 > 0);

                    assert IOStatus.check(var3);

                }
            }
        }
    }
```
IOUtil---read
```text
  static int read(FileDescriptor var0, ByteBuffer var1, long var2, NativeDispatcher var4) throws IOException {
        if (var1.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        } else if (var1 instanceof DirectBuffer) {
            return readIntoNativeBuffer(var0, var1, var2, var4);
        } else {
            ByteBuffer var5 = Util.getTemporaryDirectBuffer(var1.remaining());

            int var7;
            try {
                int var6 = readIntoNativeBuffer(var0, var5, var2, var4);//数据读入var5
                var5.flip();
                if (var6 > 0) {
                    var1.put(var5); //读取数据到目标buffer
                }

                var7 = var6;
            } finally {
                Util.offerFirstTemporaryDirectBuffer(var5);
            }

            return var7;
        }
    }
```
流程：
1. 申请一块与缓存大小一致的物理内存DirectByteBuffer
2. 将数据读入var5，底层由NativeDispatcher的read实现：abstract int read(FileDescriptor var1, long var2, int var4) throws IOException;
3. 把数据var5的数据读取到var1，用户定义的缓存或者是jvm中分配的内存

- WRITE实现
FileChannelImpl ---write
```text
 public int write(ByteBuffer var1) throws IOException {
        this.ensureOpen();
        if (!this.writable) {
            throw new NonWritableChannelException();
        } else {
            Object var2 = this.positionLock;
            synchronized(this.positionLock) {
                int var3 = 0;
                int var4 = -1;

                try {
                    this.begin();
                    var4 = this.threads.add();
                    if (!this.isOpen()) {
                        byte var12 = 0;
                        return var12;
                    } else {
                        do {
                            var3 = IOUtil.write(this.fd, var1, -1L, this.nd);
                        } while(var3 == -3 && this.isOpen());

                        int var5 = IOStatus.normalize(var3);
                        return var5;
                    }
                } finally {
                    this.threads.remove(var4);
                    this.end(var3 > 0);

                    assert IOStatus.check(var3);

                }
            }
        }
    }
```
IOUtil---write
```text
 static int write(FileDescriptor var0, ByteBuffer var1, long var2, NativeDispatcher var4) throws IOException {
        if (var1 instanceof DirectBuffer) {
            return writeFromNativeBuffer(var0, var1, var2, var4);
        } else {
            int var5 = var1.position();
            int var6 = var1.limit();

            assert var5 <= var6;

            int var7 = var5 <= var6 ? var6 - var5 : 0;
            ByteBuffer var8 = Util.getTemporaryDirectBuffer(var7);

            int var10;
            try {
                var8.put(var1);
                var8.flip();
                var1.position(var5);
                int var9 = writeFromNativeBuffer(var0, var8, var2, var4);
                if (var9 > 0) {
                    var1.position(var5 + var9);
                }

                var10 = var9;
            } finally {
                Util.offerFirstTemporaryDirectBuffer(var8);
            }

            return var10;
        }
    }
```
流程：
1. 同样申请一块内存buffer  var8
2. 然后将写入的var1放入var8当中
3. 最后调用writeFromNativeBuffer，通过nativeDispatcher写入：abstract int write(FileDescriptor var1, long var2, int var4) throws IOException;

- 以上读写的方法到导致了两次数据的复制

### NIO之Selector实现原理
- selector作为实现NIO多路复用的关键，以下详细介绍它的原理
- NIO的核心部分，Buffer /Channel /Selector,前面两项已经做了介绍
- NIO采用选择器（Selector）返回已经准备好的socket，并按顺序处理，基于通道（Channel）和缓冲区（Buffer）来进行数据的传输
- 可以将监听数据和数据处理很好的分开
- selector的四种监听事件：
    - connect
    - accept
    - read 
    - write



### 阻塞IO下的服务器实现
1. 单线程逐个处理所有请求
使用阻塞I/O的服务器，一般使用循环，逐个接受连接请求并读取数据，然后处理下一个请求

2. 为每个请求创建一个线程
使用单线程逐个处理所有请求，同一时间只能处理一个请求，等待I/O的过程浪费大量CPU资源，同时无法充分使用多CPU的优势。下面是使用多线程对阻塞I/O模型的改进。一个连接建立成功后，创建一个单独的线程处理其I/O操作

3. 使用线程池处理请求
为了防止连接请求过多，导致服务器创建的线程数过多，造成过多线程上下文切换的开销。可以通过线程池来限制创建的线程数


### Reactor模式
- 经典Reactor模式
- 角色
    - Reactor 将I/O事件发派给对应的Handler
    - Acceptor 处理客户端连接请求
    - Handlers 执行非阻塞读/写

- 多工作线程Reactor模式
尽管一个线程可同时监控多个请求（Channel），但是所有读/写请求以及对新连接请求的处理都在同一个线程中处理，无法充分利用多CPU的优势，同时读/写操作也会阻塞对新连接请求的处理。因此可以引入多线程，并行处理多个读/写操作  


- 多Reactor
Netty中使用的Reactor模式，引入了多Reactor，也即一个主Reactor负责监控所有的连接请求，多个子Reactor负责监控并处理读/写请求，减轻了主Reactor的压力，降低了主Reactor压力太大而造成的延迟。