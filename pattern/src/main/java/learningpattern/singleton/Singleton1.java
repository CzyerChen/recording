package learningpattern.singleton;


/**
 * 懒汉模式 内存泄露版 单例模式
 */
public class Singleton1 {
    private Singleton1() {
    }

    private static Singleton1 singleton = null;

    public static Singleton1 getInstance() {
        if (singleton == null) {
            singleton = new Singleton1();
        }
        return singleton;
    }
}
