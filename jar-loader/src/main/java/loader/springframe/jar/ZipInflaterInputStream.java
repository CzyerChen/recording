package loader.springframe.jar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:27
 */
class ZipInflaterInputStream extends InflaterInputStream {
    private boolean extraBytesWritten;
    private int available;

    ZipInflaterInputStream(InputStream inputStream, int size) {
        super(inputStream, new Inflater(true), getInflaterBufferSize((long)size));
        this.available = size;
    }

    @Override
    public int available() throws IOException {
        return this.available < 0 ? super.available() : this.available;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = super.read(b, off, len);
        if (result != -1) {
            this.available -= result;
        }

        return result;
    }

    @Override
    protected void fill() throws IOException {
        try {
            super.fill();
        } catch (EOFException var2) {
            if (this.extraBytesWritten) {
                throw var2;
            }

            this.len = 1;
            this.buf[0] = 0;
            this.extraBytesWritten = true;
            this.inf.setInput(this.buf, 0, this.len);
        }

    }

    private static int getInflaterBufferSize(long size) {
        size += 2L;
        size = size > 65536L ? 8192L : size;
        size = size <= 0L ? 4096L : size;
        return (int)size;
    }
}
