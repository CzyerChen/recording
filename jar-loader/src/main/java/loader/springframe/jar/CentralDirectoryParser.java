package loader.springframe.jar;

import loader.springframe.data.RandomAccessData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:24
 */
class CentralDirectoryParser {
    private static final int CENTRAL_DIRECTORY_HEADER_BASE_SIZE = 46;
    private final List<CentralDirectoryVisitor> visitors = new ArrayList();

    CentralDirectoryParser() {
    }

    public <T extends CentralDirectoryVisitor> T addVisitor(T visitor) {
        this.visitors.add(visitor);
        return visitor;
    }

    public RandomAccessData parse(RandomAccessData data, boolean skipPrefixBytes) throws IOException {
        CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
        if (skipPrefixBytes) {
            data = this.getArchiveData(endRecord, data);
        }

        RandomAccessData centralDirectoryData = endRecord.getCentralDirectory(data);
        this.visitStart(endRecord, centralDirectoryData);
        this.parseEntries(endRecord, centralDirectoryData);
        this.visitEnd();
        return data;
    }

    private void parseEntries(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) throws IOException {
        byte[] bytes = centralDirectoryData.read(0L, centralDirectoryData.getSize());
        CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
        int dataOffset = 0;

        for(int i = 0; i < endRecord.getNumberOfRecords(); ++i) {
            fileHeader.load(bytes, dataOffset, (RandomAccessData)null, 0, (JarEntryFilter)null);
            this.visitFileHeader(dataOffset, fileHeader);
            dataOffset += 46 + fileHeader.getName().length() + fileHeader.getComment().length() + fileHeader.getExtra().length;
        }

    }

    private RandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, RandomAccessData data) {
        long offset = endRecord.getStartOfArchive(data);
        return offset == 0L ? data : data.getSubsection(offset, data.getSize() - offset);
    }

    private void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
        Iterator var3 = this.visitors.iterator();

        while(var3.hasNext()) {
            CentralDirectoryVisitor visitor = (CentralDirectoryVisitor)var3.next();
            visitor.visitStart(endRecord, centralDirectoryData);
        }

    }

    private void visitFileHeader(int dataOffset, CentralDirectoryFileHeader fileHeader) {
        Iterator var3 = this.visitors.iterator();

        while(var3.hasNext()) {
            CentralDirectoryVisitor visitor = (CentralDirectoryVisitor)var3.next();
            visitor.visitFileHeader(fileHeader, dataOffset);
        }

    }

    private void visitEnd() {
        Iterator var1 = this.visitors.iterator();

        while(var1.hasNext()) {
            CentralDirectoryVisitor visitor = (CentralDirectoryVisitor)var1.next();
            visitor.visitEnd();
        }

    }
}
