package loader.springframe.jar;

import loader.springframe.data.RandomAccessData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:24
 */
final class CentralDirectoryFileHeader implements FileHeader {
    private static final AsciiBytes SLASH = new AsciiBytes("/");
    private static final byte[] NO_EXTRA = new byte[0];
    private static final AsciiBytes NO_COMMENT = new AsciiBytes("");
    private byte[] header;
    private int headerOffset;
    private AsciiBytes name;
    private byte[] extra;
    private AsciiBytes comment;
    private long localHeaderOffset;

    CentralDirectoryFileHeader() {
    }

    CentralDirectoryFileHeader(byte[] header, int headerOffset, AsciiBytes name, byte[] extra, AsciiBytes comment, long localHeaderOffset) {
        this.header = header;
        this.headerOffset = headerOffset;
        this.name = name;
        this.extra = extra;
        this.comment = comment;
        this.localHeaderOffset = localHeaderOffset;
    }

    void load(byte[] data, int dataOffset, RandomAccessData variableData, int variableOffset, JarEntryFilter filter) throws IOException {
        this.header = data;
        this.headerOffset = dataOffset;
        long nameLength = Bytes.littleEndianValue(data, dataOffset + 28, 2);
        long extraLength = Bytes.littleEndianValue(data, dataOffset + 30, 2);
        long commentLength = Bytes.littleEndianValue(data, dataOffset + 32, 2);
        this.localHeaderOffset = Bytes.littleEndianValue(data, dataOffset + 42, 4);
        dataOffset += 46;
        if (variableData != null) {
            data = variableData.read((long)(variableOffset + 46), nameLength + extraLength + commentLength);
            dataOffset = 0;
        }

        this.name = new AsciiBytes(data, dataOffset, (int)nameLength);
        if (filter != null) {
            this.name = filter.apply(this.name);
        }

        this.extra = NO_EXTRA;
        this.comment = NO_COMMENT;
        if (extraLength > 0L) {
            this.extra = new byte[(int)extraLength];
            System.arraycopy(data, (int)((long)dataOffset + nameLength), this.extra, 0, this.extra.length);
        }

        if (commentLength > 0L) {
            this.comment = new AsciiBytes(data, (int)((long)dataOffset + nameLength + extraLength), (int)commentLength);
        }

    }

    public AsciiBytes getName() {
        return this.name;
    }

    @Override
    public boolean hasName(CharSequence name, char suffix) {
        return this.name.matches(name, suffix);
    }

    public boolean isDirectory() {
        return this.name.endsWith(SLASH);
    }

    @Override
    public int getMethod() {
        return (int) Bytes.littleEndianValue(this.header, this.headerOffset + 10, 2);
    }

    public long getTime() {
        long datetime = Bytes.littleEndianValue(this.header, this.headerOffset + 12, 4);
        return this.decodeMsDosFormatDateTime(datetime);
    }

    private long decodeMsDosFormatDateTime(long datetime) {
        LocalDateTime localDateTime = LocalDateTime.of((int)((datetime >> 25 & 127L) + 1980L), (int)(datetime >> 21 & 15L), (int)(datetime >> 16 & 31L), (int)(datetime >> 11 & 31L), (int)(datetime >> 5 & 63L), (int)(datetime << 1 & 62L));
        return localDateTime.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(localDateTime)) * 1000L;
    }

    public long getCrc() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 16, 4);
    }

    @Override
    public long getCompressedSize() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 20, 4);
    }

    @Override
    public long getSize() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 24, 4);
    }

    public byte[] getExtra() {
        return this.extra;
    }

    public AsciiBytes getComment() {
        return this.comment;
    }

    @Override
    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }

    @Override
    public CentralDirectoryFileHeader clone() {
        byte[] header = new byte[46];
        System.arraycopy(this.header, this.headerOffset, header, 0, header.length);
        return new CentralDirectoryFileHeader(header, 0, this.name, header, this.comment, this.localHeaderOffset);
    }

    public static CentralDirectoryFileHeader fromRandomAccessData(RandomAccessData data, int offset, JarEntryFilter filter) throws IOException {
        CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
        byte[] bytes = data.read((long)offset, 46L);
        fileHeader.load(bytes, 0, data, offset, filter);
        return fileHeader;
    }
}

