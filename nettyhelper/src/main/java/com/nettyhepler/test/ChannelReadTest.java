package com.nettyhepler.test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 25 12:16
 */
public class ChannelReadTest {

    public static void main(String[] args) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("data.txt", "rw");
            FileChannel fileChannel = file.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(64);
            int byteRead = fileChannel.read(byteBuffer);
            while (byteRead != -1) {
                System.out.println("READ :" + byteRead);
                //数据写进buffer ,再反转一下，把数据再吐出来
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    System.out.println((char) byteBuffer.get());
                }
                byteBuffer.clear();
                byteRead = fileChannel.read(byteBuffer);
            }
            file.close();
        }catch (IOException e){
            try {
                file.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }


}
