package com.basic;

import java.io.*;

public class FileTest {
    public static void main(String[] args) {

    }


    public void chartest() {
        BufferedReader bufferedReader = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
           bufferedReader = new BufferedReader(new FileReader(new File("D:/c.txt")));
           outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File("D:/d.txt")));
           while (bufferedReader.read()!= -1){
               String line = bufferedReader.readLine();
               System.out.print(line);
               outputStreamWriter.write(line);
           }
           outputStreamWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void streamtest() {
        File file = new File("D:/a.txt");
        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;

        OutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            byte[] buf = new byte[1024];   //代表一次最多读取1KB的内容
            int length = 0; //代表实际读取的字节数

            FileInputStream fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("D:/b.txt")));
            while (bufferedInputStream.read(buf) != -1) {
                bufferedOutputStream.write(buf, 0, length);
            }
            bufferedOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
