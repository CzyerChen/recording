package loader.springframe.jar;

import loader.springframe.data.RandomAccessData;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:25
 */
interface CentralDirectoryVisitor {
    void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData);

    void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset);

    void visitEnd();
}