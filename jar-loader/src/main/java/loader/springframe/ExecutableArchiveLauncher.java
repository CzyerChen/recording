package loader.springframe;

import loader.springframe.archive.Archive;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 10:43
 */
public abstract class ExecutableArchiveLauncher extends Launcher {
    private final Archive archive;

    public ExecutableArchiveLauncher() {
        try {
            this.archive = this.createArchive();
        } catch (Exception var2) {
            throw new IllegalStateException(var2);
        }
    }

    protected ExecutableArchiveLauncher(Archive archive) {
        this.archive = archive;
    }

    protected final Archive getArchive() {
        return this.archive;
    }

    @Override
    protected String getMainClass() throws Exception {
        Manifest manifest = this.archive.getManifest();
        String mainClass = null;
        if (manifest != null) {
            mainClass = manifest.getMainAttributes().getValue("Start-Class");
        }

        if (mainClass == null) {
            throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
        } else {
            return mainClass;
        }
    }

    @Override
    protected List<Archive> getClassPathArchives() throws Exception {
        List<Archive> archives = new ArrayList(this.archive.getNestedArchives(this::isNestedArchive));
        this.postProcessClassPathArchives(archives);
        return archives;
    }

    protected abstract boolean isNestedArchive(Archive.Entry entry);

    protected void postProcessClassPathArchives(List<Archive> archives) throws Exception {
    }
}