package loader.springframe;

import loader.springframe.archive.Archive;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 10:42
 */
public class JarLauncher extends ExecutableArchiveLauncher {
    static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
    static final String BOOT_INF_LIB = "BOOT-INF/lib/";

    public JarLauncher() {
    }

    protected JarLauncher(Archive archive) {
        super(archive);
    }

    @Override
    protected boolean isNestedArchive(Archive.Entry entry) {
        return entry.isDirectory() ? entry.getName().equals("BOOT-INF/classes/") : entry.getName().startsWith("BOOT-INF/lib/");
    }

    public static void main(String[] args) throws Exception {
        (new JarLauncher()).launch(args);
    }
}

