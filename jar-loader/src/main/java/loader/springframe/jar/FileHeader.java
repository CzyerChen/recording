package loader.springframe.jar;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:25
 */
interface FileHeader {
    boolean hasName(CharSequence name, char suffix);

    long getLocalHeaderOffset();

    long getCompressedSize();

    long getSize();

    int getMethod();
}

