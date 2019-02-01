package loader.springframe.jar;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:24
 */
final class Bytes {
    private Bytes() {
    }

    public static long littleEndianValue(byte[] bytes, int offset, int length) {
        long value = 0L;

        for(int i = length - 1; i >= 0; --i) {
            value = value << 8 | (long)(bytes[offset + i] & 255);
        }

        return value;
    }
}