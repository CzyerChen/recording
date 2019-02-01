package loader.springframe;

import loader.springframe.jar.Handler;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.jar.JarFile;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:28
 */
public class LaunchedURLClassLoader extends URLClassLoader {
    public LaunchedURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public URL findResource(String name) {
        Handler.setUseFastConnectionExceptions(true);

        URL var2;
        try {
            var2 = super.findResource(name);
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }

        return var2;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        Handler.setUseFastConnectionExceptions(true);

        LaunchedURLClassLoader.UseFastConnectionExceptionsEnumeration var2;
        try {
            var2 = new LaunchedURLClassLoader.UseFastConnectionExceptionsEnumeration(super.findResources(name));
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }

        return var2;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Handler.setUseFastConnectionExceptions(true);

        Class var3;
        try {
            try {
                this.definePackageIfNecessary(name);
            } catch (IllegalArgumentException var7) {
                if (this.getPackage(name) == null) {
                    throw new AssertionError("Package " + name + " has already been defined but it could not be found");
                }
            }

            var3 = super.loadClass(name, resolve);
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }

        return var3;
    }

    private void definePackageIfNecessary(String className) {
        int lastDot = className.lastIndexOf(46);
        if (lastDot >= 0) {
            String packageName = className.substring(0, lastDot);
            if (this.getPackage(packageName) == null) {
                try {
                    this.definePackage(className, packageName);
                } catch (IllegalArgumentException var5) {
                    if (this.getPackage(packageName) == null) {
                        throw new AssertionError("Package " + packageName + " has already been defined but it could not be found");
                    }
                }
            }
        }

    }

    private void definePackage(String className, String packageName) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            String packageEntryName = packageName.replace('.', '/') + "/";
            String classEntryName = className.replace('.', '/') + ".class";
            URL[] var5 = this.getURLs();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                URL url = var5[var7];

                try {
                    URLConnection connection = url.openConnection();
                    if (connection instanceof JarURLConnection) {
                        JarFile jarFile = ((JarURLConnection)connection).getJarFile();
                        if (jarFile.getEntry(classEntryName) != null && jarFile.getEntry(packageEntryName) != null && jarFile.getManifest() != null) {
                            this.definePackage(packageName, jarFile.getManifest(), url);
                            return null;
                        }
                    }
                } catch (IOException var11) {
                    ;
                }
            }

            return null;
        }, AccessController.getContext());

    }

    public void clearCache() {
        URL[] var1 = this.getURLs();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            URL url = var1[var3];

            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection) {
                    this.clearCache(connection);
                }
            } catch (IOException var6) {
                ;
            }
        }

    }

    private void clearCache(URLConnection connection) throws IOException {
        Object jarFile = ((JarURLConnection)connection).getJarFile();
        if (jarFile instanceof loader.springframe.jar.JarFile) {
            ((loader.springframe.jar.JarFile)jarFile).clearCache();
        }

    }

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private static class UseFastConnectionExceptionsEnumeration implements Enumeration<URL> {
        private final Enumeration<URL> delegate;

        UseFastConnectionExceptionsEnumeration(Enumeration<URL> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasMoreElements() {
            Handler.setUseFastConnectionExceptions(true);

            boolean var1;
            try {
                var1 = this.delegate.hasMoreElements();
            } finally {
                Handler.setUseFastConnectionExceptions(false);
            }

            return var1;
        }

        @Override
        public URL nextElement() {
            Handler.setUseFastConnectionExceptions(true);

            URL var1;
            try {
                var1 = (URL)this.delegate.nextElement();
            } finally {
                Handler.setUseFastConnectionExceptions(false);
            }

            return var1;
        }
    }

    /**
     * 需要自己重写
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(final String name)
            throws ClassNotFoundException
    {
        /*  return AccessController.doPrivileged(
                  new PrivilegedExceptionAction<Class<?>>() {
                      @Override
                      public Class<?> run() throws ClassNotFoundException {
                          // 把类名解析成路径并加上.class后缀
                          String path = name.replace('.', '/').concat(".class");
                          // 基于之前得到的第三方jar包依赖以及自己的jar包得到URL数组，进行遍历找出对应类名的资源
                          // 比如path是org/springframework/boot/loader/JarLauncher.class，它在jar:file:/Users/Format/Develop/gitrepository/springboot-analysis/springboot-executable-jar/target/executable-jar-1.0-SNAPSHOT.jar!/lib/spring-boot-loader-1.3.5.RELEASE.jar!/中被找出
                          // 那么找出的资源对应的URL为jar:file:/Users/Format/Develop/gitrepository/springboot-analysis/springboot-executable-jar/target/executable-jar-1.0-SNAPSHOT.jar!/lib/spring-boot-loader-1.3.5.RELEASE.jar!/org/springframework/boot/loader/JarLauncher.class
                          Resource res = ucp.getResource(path, false);
                          if (res != null) { // 找到了资源
                              try {
                                  return (Class<?>) defineClass(name, res);
                              } catch (IOException e) {
                                  throw new ClassNotFoundException(name, e);
                              }
                          } else { // 找不到资源的话直接抛出ClassNotFoundException异常
                              throw new ClassNotFoundException(name);
                          }
                      }
                  }, acc);*/
        return null;
    }
}
