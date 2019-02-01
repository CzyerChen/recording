package loader.springframe.jar;

import java.io.*;
import java.net.*;
import java.security.Permission;


/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:27
 */
final class JarURLConnection extends java.net.JarURLConnection {
    private static ThreadLocal<Boolean> useFastExceptions = new ThreadLocal();
    private static final FileNotFoundException FILE_NOT_FOUND_EXCEPTION = new FileNotFoundException("Jar file or entry not found");
    private static final IllegalStateException NOT_FOUND_CONNECTION_EXCEPTION;
    private static final String SEPARATOR = "!/";
    private static final URL EMPTY_JAR_URL;
    private static final JarURLConnection.JarEntryName EMPTY_JAR_ENTRY_NAME;
    private static final String READ_ACTION = "read";
    private static final JarURLConnection NOT_FOUND_CONNECTION;
    private final JarFile jarFile;
    private Permission permission;
    private URL jarFileUrl;
    private final JarURLConnection.JarEntryName jarEntryName;
    private JarEntry jarEntry;

    private JarURLConnection(URL url, JarFile jarFile, JarURLConnection.JarEntryName jarEntryName) throws IOException {
        super(EMPTY_JAR_URL);
        this.url = url;
        this.jarFile = jarFile;
        this.jarEntryName = jarEntryName;
    }

    @Override
    public void connect() throws IOException {
        if (this.jarFile == null) {
            throw FILE_NOT_FOUND_EXCEPTION;
        } else {
            if (!this.jarEntryName.isEmpty() && this.jarEntry == null) {
                this.jarEntry = this.jarFile.getJarEntry(this.getEntryName());
                if (this.jarEntry == null) {
                    this.throwFileNotFound(this.jarEntryName, this.jarFile);
                }
            }

            this.connected = true;
        }
    }

    @Override
    public JarFile getJarFile() throws IOException {
        this.connect();
        return this.jarFile;
    }

    @Override
    public URL getJarFileURL() {
        if (this.jarFile == null) {
            throw NOT_FOUND_CONNECTION_EXCEPTION;
        } else {
            if (this.jarFileUrl == null) {
                this.jarFileUrl = this.buildJarFileUrl();
            }

            return this.jarFileUrl;
        }
    }

    private URL buildJarFileUrl() {
        try {
            String spec = this.jarFile.getUrl().getFile();
            if (spec.endsWith("!/")) {
                spec = spec.substring(0, spec.length() - "!/".length());
            }

            return spec.indexOf("!/") == -1 ? new URL(spec) : new URL("jar:" + spec);
        } catch (MalformedURLException var2) {
            throw new IllegalStateException(var2);
        }
    }

    @Override
    public JarEntry getJarEntry() throws IOException {
        if (this.jarEntryName != null && !this.jarEntryName.isEmpty()) {
            this.connect();
            return this.jarEntry;
        } else {
            return null;
        }
    }

    @Override
    public String getEntryName() {
        if (this.jarFile == null) {
            throw NOT_FOUND_CONNECTION_EXCEPTION;
        } else {
            return this.jarEntryName.toString();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.jarFile == null) {
            throw FILE_NOT_FOUND_EXCEPTION;
        } else if (this.jarEntryName.isEmpty() && this.jarFile.getType() == JarFile.JarFileType.DIRECT) {
            throw new IOException("no entry name specified");
        } else {
            this.connect();
            InputStream inputStream = this.jarEntryName.isEmpty() ? this.jarFile.getData().getInputStream() : this.jarFile.getInputStream(this.jarEntry);
            if (inputStream == null) {
                this.throwFileNotFound(this.jarEntryName, this.jarFile);
            }

            return inputStream;
        }
    }

    private void throwFileNotFound(Object entry, JarFile jarFile) throws FileNotFoundException {
        if (Boolean.TRUE.equals(useFastExceptions.get())) {
            throw FILE_NOT_FOUND_EXCEPTION;
        } else {
            throw new FileNotFoundException("JAR entry " + entry + " not found in " + jarFile.getName());
        }
    }

    @Override
    public int getContentLength() {
        long length = this.getContentLengthLong();
        return length > 2147483647L ? -1 : (int)length;
    }

    @Override
    public long getContentLengthLong() {
        if (this.jarFile == null) {
            return -1L;
        } else {
            try {
                if (this.jarEntryName.isEmpty()) {
                    return (long)this.jarFile.size();
                } else {
                    JarEntry entry = this.getJarEntry();
                    return (long)(entry == null ? -1 : (int)entry.getSize());
                }
            } catch (IOException var2) {
                return -1L;
            }
        }
    }

    @Override
    public Object getContent() throws IOException {
        this.connect();
        return this.jarEntryName.isEmpty() ? this.jarFile : super.getContent();
    }

    @Override
    public String getContentType() {
        return this.jarEntryName == null ? null : this.jarEntryName.getContentType();
    }

    @Override
    public Permission getPermission() throws IOException {
        if (this.jarFile == null) {
            throw FILE_NOT_FOUND_EXCEPTION;
        } else {
            if (this.permission == null) {
                this.permission = new FilePermission(this.jarFile.getRootJarFile().getFile().getPath(), "read");
            }

            return this.permission;
        }
    }

    @Override
    public long getLastModified() {
        if (this.jarFile != null && !this.jarEntryName.isEmpty()) {
            try {
                JarEntry entry = this.getJarEntry();
                return entry == null ? 0L : entry.getTime();
            } catch (IOException var2) {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    static void setUseFastExceptions(boolean useFastExceptions) {
        //useFastExceptions.set(useFastExceptions);
    }

    static JarURLConnection get(URL url, JarFile jarFile) throws IOException {
        StringSequence spec = new StringSequence(url.getFile());

        int index;
        int separator;
        for(index = indexOfRootSpec(spec, jarFile.getPathFromRoot()); (separator = spec.indexOf("!/", index)) > 0; index = separator + "!/".length()) {
            StringSequence entryName = spec.subSequence(index, separator);
            JarEntry jarEntry = jarFile.getJarEntry(entryName);
            if (jarEntry == null) {
                return notFound(jarFile, JarURLConnection.JarEntryName.get(entryName));
            }

            jarFile = jarFile.getNestedJarFile(jarEntry);
        }

        JarURLConnection.JarEntryName jarEntryName = JarURLConnection.JarEntryName.get(spec, index);
        if (Boolean.TRUE.equals(useFastExceptions.get()) && !jarEntryName.isEmpty() && !jarFile.containsEntry(jarEntryName.toString())) {
            return NOT_FOUND_CONNECTION;
        } else {
            return new JarURLConnection(url, jarFile, jarEntryName);
        }
    }

    private static int indexOfRootSpec(StringSequence file, String pathFromRoot) {
        int separatorIndex = file.indexOf("!/");
        return separatorIndex < 0 ? -1 : separatorIndex + "!/".length() + pathFromRoot.length();
    }

    private static JarURLConnection notFound() {
        try {
            return notFound((JarFile)null, (JarURLConnection.JarEntryName)null);
        } catch (IOException var1) {
            throw new IllegalStateException(var1);
        }
    }

    private static JarURLConnection notFound(JarFile jarFile, JarURLConnection.JarEntryName jarEntryName) throws IOException {
        return Boolean.TRUE.equals(useFastExceptions.get()) ? NOT_FOUND_CONNECTION : new JarURLConnection((URL)null, jarFile, jarEntryName);
    }

    static {
        NOT_FOUND_CONNECTION_EXCEPTION = new IllegalStateException(FILE_NOT_FOUND_EXCEPTION);

        try {
            EMPTY_JAR_URL = new URL("jar:", (String)null, 0, "file:!/", new URLStreamHandler() {
                protected URLConnection openConnection(URL u) throws IOException {
                    return null;
                }
            });
        } catch (MalformedURLException var1) {
            throw new IllegalStateException(var1);
        }

        EMPTY_JAR_ENTRY_NAME = new JarURLConnection.JarEntryName(new StringSequence(""));
        NOT_FOUND_CONNECTION = notFound();
    }

    static class JarEntryName {
        private final StringSequence name;
        private String contentType;

        JarEntryName(StringSequence spec) {
            this.name = this.decode(spec);
        }

        private StringSequence decode(StringSequence source) {
            if (!source.isEmpty() && source.indexOf('%') >= 0) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length());
                this.write(source.toString(), bos);
                return new StringSequence(AsciiBytes.toString(bos.toByteArray()));
            } else {
                return source;
            }
        }

        private void write(String source, ByteArrayOutputStream outputStream) {
            int length = source.length();

            for(int i = 0; i < length; ++i) {
                int c = source.charAt(i);
                if (c > 127) {
                    try {
                        String encoded = URLEncoder.encode(String.valueOf((char)c), "UTF-8");
                        this.write(encoded, outputStream);
                    } catch (UnsupportedEncodingException var7) {
                        throw new IllegalStateException(var7);
                    }
                } else {
                    if (c == '%') {
                        if (i + 2 >= length) {
                            throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                        }

                        c = this.decodeEscapeSequence(source, i);
                        i += 2;
                    }

                    outputStream.write(c);
                }
            }

        }

        private char decodeEscapeSequence(String source, int i) {
            int hi = Character.digit(source.charAt(i + 1), 16);
            int lo = Character.digit(source.charAt(i + 2), 16);
            if (hi != -1 && lo != -1) {
                return (char)((hi << 4) + lo);
            } else {
                throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
            }
        }

        @Override
        public String toString() {
            return this.name.toString();
        }

        public boolean isEmpty() {
            return this.name.isEmpty();
        }

        public String getContentType() {
            if (this.contentType == null) {
                this.contentType = this.deduceContentType();
            }

            return this.contentType;
        }

        private String deduceContentType() {
            String type = this.isEmpty() ? "x-java/jar" : null;
            type = type != null ? type : URLConnection.guessContentTypeFromName(this.toString());
            type = type != null ? type : "content/unknown";
            return type;
        }

        public static JarURLConnection.JarEntryName get(StringSequence spec) {
            return get(spec, 0);
        }

        public static JarURLConnection.JarEntryName get(StringSequence spec, int beginIndex) {
            return spec.length() <= beginIndex ? JarURLConnection.EMPTY_JAR_ENTRY_NAME : new JarURLConnection.JarEntryName(spec.subSequence(beginIndex));
        }
    }
}

