package loader.springframe;

import loader.springframe.archive.Archive;
import loader.springframe.archive.ExplodedArchive;
import loader.springframe.archive.JarFileArchive;
import loader.springframe.jar.JarFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:29
 */
public abstract class Launcher {
    public Launcher() {
    }

    protected void launch(String[] args) throws Exception {
        //在系统中注册一个自定义url处理器，org.springframework.boot.loader.jar.Handler。如果URL中没有指定处理器，会去系统属性中查询
        JarFile.registerUrlProtocolHandler();
        // getClassPathArchives方法在会去找lib目录下对应的第三方依赖JarFileArchive，同时也会项目自身的JarFileArchive
        // 根据getClassPathArchives得到的JarFileArchive集合去创建类加载器ClassLoader。这里会构造一个LaunchedURLClassLoader类加载器，这个类加载器继承URLClassLoader，并使用这些JarFileArchive集合的URL构造成URLClassPath
        // LaunchedURLClassLoader类加载器的父类加载器是当前执行类JarLauncher的类加载器
        ClassLoader classLoader = this.createClassLoader(this.getClassPathArchives());
        // getMainClass方法会去项目自身的Archive中的Manifest中找出key为Start-Class的类
        // 调用重载方法launch
        this.launch(args, this.getMainClass(), classLoader);
    }

    /**
     * 通过自定义类加载器加载，用处可用于比如在字节码层面需要通过加密解密技术处理的，需要自己重写类加载的方法
     */
    protected  void launch(){
        JarFile.registerUrlProtocolHandler();
// 构造LaunchedURLClassLoader类加载器，这里使用了2个URL，分别对应jar包中依赖包spring-boot-loader和spring-boot，使用 "!/" 分开，需要org.springframework.boot.loader.jar.Handler处理器处理
        LaunchedURLClassLoader classLoader = null;
        try {
            classLoader = new LaunchedURLClassLoader(
                    new URL[] {
                            new URL("jar:file:/Users/Format/Develop/gitrepository/springboot-analysis/springboot-executable-jar/target/executable-jar-1.0-SNAPSHOT.jar!/lib/spring-boot-loader-1.3.5.RELEASE.jar!/")
                            , new URL("jar:file:/Users/Format/Develop/gitrepository/springboot-analysis/springboot-executable-jar/target/executable-jar-1.0-SNAPSHOT.jar!/lib/spring-boot-1.3.5.RELEASE.jar!/")
                    },
                    LaunchedURLClassLoaderTest.class.getClassLoader());
// 加载类
// 这2个类都会在第二步本地查找中被找出(URLClassLoader的findClass方法)
        classLoader.loadClass("org.springframework.boot.loader.JarLauncher");
        classLoader.loadClass("org.springframework.boot.SpringApplication");
// 在第三步使用默认的加载顺序在ApplicationClassLoader中被找出
        classLoader.loadClass("org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected ClassLoader createClassLoader(List<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList(archives.size());
        Iterator var3 = archives.iterator();

        while(var3.hasNext()) {
            Archive archive = (Archive)var3.next();
            urls.add(archive.getUrl());
        }

        return this.createClassLoader((URL[])urls.toArray(new URL[0]));
    }

    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new LaunchedURLClassLoader(urls, this.getClass().getClassLoader());
    }

    protected void launch(String[] args, String mainClass, ClassLoader classLoader) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        this.createMainMethodRunner(mainClass, args, classLoader).run();
    }

    protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
        //主要是找到主类。并且通过一个主线程，将这个主类启动起来，里面包含设置类加载器，和参数
        return new MainMethodRunner(mainClass, args);
    }

    protected abstract String getMainClass() throws Exception;

    protected abstract List<Archive> getClassPathArchives() throws Exception;

    protected final Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = codeSource == null ? null : codeSource.getLocation().toURI();
        String path = location == null ? null : location.getSchemeSpecificPart();
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        } else {
            File root = new File(path);
            if (!root.exists()) {
                throw new IllegalStateException("Unable to determine code source archive from " + root);
            } else {
                return (Archive)(root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root));
            }
        }
    }
}
