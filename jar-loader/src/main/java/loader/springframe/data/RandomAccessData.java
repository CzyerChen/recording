package loader.springframe.data;

import java.io.IOException;
import java.io.InputStream;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:21
 */
public interface RandomAccessData {
    InputStream getInputStream() throws IOException;

    RandomAccessData getSubsection(long offset, long length);

    byte[] read() throws IOException;

    byte[] read(long offset, long length) throws IOException;

    long getSize();
}
