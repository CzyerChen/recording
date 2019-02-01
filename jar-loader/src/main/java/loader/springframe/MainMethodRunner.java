package loader.springframe;

import java.lang.reflect.Method;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:29
 */
public class MainMethodRunner {
    private final String mainClassName;
    private final String[] args;

    public MainMethodRunner(String mainClass, String[] args) {
        this.mainClassName = mainClass;
        this.args = args == null ? null : (String[])args.clone();
    }

    public void run() throws Exception {
        //找到类
        Class<?> mainClass = Thread.currentThread().getContextClassLoader().loadClass(this.mainClassName);
        //找到类中的main方法
        Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
        //调用
        mainMethod.invoke((Object)null, this.args);
    }
}
