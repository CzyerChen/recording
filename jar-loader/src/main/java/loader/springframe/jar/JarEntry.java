package loader.springframe.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:25
 */
class JarEntry extends java.util.jar.JarEntry implements FileHeader {
    private final AsciiBytes name;
    private Certificate[] certificates;
    private CodeSigner[] codeSigners;
    private final JarFile jarFile;
    private long localHeaderOffset;

    JarEntry(JarFile jarFile, CentralDirectoryFileHeader header) {
        super(header.getName().toString());
        this.name = header.getName();
        this.jarFile = jarFile;
        this.localHeaderOffset = header.getLocalHeaderOffset();
        this.setCompressedSize(header.getCompressedSize());
        this.setMethod(header.getMethod());
        this.setCrc(header.getCrc());
        this.setComment(header.getComment().toString());
        this.setSize(header.getSize());
        this.setTime(header.getTime());
        this.setExtra(header.getExtra());
    }

    AsciiBytes getAsciiBytesName() {
        return this.name;
    }

    @Override
    public boolean hasName(CharSequence name, char suffix) {
        return this.name.matches(name, suffix);
    }

    URL getUrl() throws MalformedURLException {
        return new URL(this.jarFile.getUrl(), this.getName());
    }

    @Override
    public Attributes getAttributes() throws IOException {
        Manifest manifest = this.jarFile.getManifest();
        return manifest == null ? null : manifest.getAttributes(this.getName());
    }

    @Override
    public Certificate[] getCertificates() {
        if (this.jarFile.isSigned() && this.certificates == null) {
            this.jarFile.setupEntryCertificates(this);
        }

        return this.certificates;
    }

    @Override
    public CodeSigner[] getCodeSigners() {
        if (this.jarFile.isSigned() && this.codeSigners == null) {
            this.jarFile.setupEntryCertificates(this);
        }

        return this.codeSigners;
    }

    void setCertificates(java.util.jar.JarEntry entry) {
        this.certificates = entry.getCertificates();
        this.codeSigners = entry.getCodeSigners();
    }

    @Override
    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }
}
