package com.nettyhepler.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 25 10:22
 */
public class ZeroCopyTest {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(9090);
        socketChannel.connect(address);

        RandomAccessFile file = new RandomAccessFile(ZeroCopyTest.class.getClassLoader().getResource("test.txt").getFile(),"rw");
        FileChannel channel= file.getChannel();
        channel.transferTo(0,channel.size(),socketChannel);
        channel.close();
        file.close();
        socketChannel.close();
    }
}
