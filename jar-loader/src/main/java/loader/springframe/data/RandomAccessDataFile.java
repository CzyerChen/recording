package loader.springframe.data;

import java.io.*;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:22
 */
public class RandomAccessDataFile implements RandomAccessData {
    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final long offset;
    private final long length;

    public RandomAccessDataFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        } else {
            try {
                this.randomAccessFile = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException var3) {
                throw new IllegalArgumentException(String.format("File %s must exist", file.getAbsolutePath()));
            }

            this.file = file;
            this.offset = 0L;
            this.length = file.length();
        }
    }

    private RandomAccessDataFile(File file, RandomAccessFile randomAccessFile, long offset, long length) {
        this.file = file;
        this.offset = offset;
        this.length = length;
        this.randomAccessFile = randomAccessFile;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new RandomAccessDataFile.DataInputStream(this.randomAccessFile);
    }

    @Override
    public RandomAccessData getSubsection(long offset, long length) {
        if (offset >= 0L && length >= 0L && offset + length <= this.length) {
            return new RandomAccessDataFile(this.file, this.randomAccessFile, this.offset + offset, length);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public byte[] read() throws IOException {
        return this.read(0L, this.length);
    }

    @Override
    public byte[] read(long offset, long length) throws IOException {
        byte[] bytes = new byte[(int)length];
        RandomAccessFile var6 = this.randomAccessFile;
        synchronized(this.randomAccessFile) {
            this.randomAccessFile.seek(this.offset + offset);
            this.randomAccessFile.read(bytes, 0, (int)length);
            return bytes;
        }
    }

    @Override
    public long getSize() {
        return this.length;
    }

    public void close() throws IOException {
    }

    private class DataInputStream extends InputStream {
        private RandomAccessFile file;
        private int position;

        DataInputStream(RandomAccessFile file) throws IOException {
            this.file = file;
        }

        @Override
        public int read() throws IOException {
            return this.doRead((byte[])null, 0, 1);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.read(b, 0, b == null ? 0 : b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException("Bytes must not be null");
            } else {
                return this.doRead(b, off, len);
            }
        }

        public int doRead(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            } else {
                int cappedLen = this.cap((long)len);
                if (cappedLen <= 0) {
                    return -1;
                } else {
                    RandomAccessFile var5 = this.file;
                    synchronized(this.file) {
                        this.file.seek(RandomAccessDataFile.this.offset + (long)this.position);
                        if (b == null) {
                            int rtn = this.file.read();
                            this.moveOn(rtn == -1 ? 0 : 1);
                            return rtn;
                        } else {
                            return (int)this.moveOn(this.file.read(b, off, cappedLen));
                        }
                    }
                }
            }
        }

        @Override
        public long skip(long n) throws IOException {
            return n <= 0L ? 0L : this.moveOn(this.cap(n));
        }

        private int cap(long n) {
            return (int)Math.min(RandomAccessDataFile.this.length - (long)this.position, n);
        }

        private long moveOn(int amount) {
            this.position += amount;
            return (long)amount;
        }
    }
}
