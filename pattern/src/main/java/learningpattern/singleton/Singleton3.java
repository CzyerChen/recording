package learningpattern.singleton;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 14 10:40
 */

/**
 * 双重锁机制很好的解决了内存可见性，减少了加锁的几率，线程安全
 */
public class Singleton3 {

    public volatile  Singleton3 _INSTANCE = null;

    public Singleton3 getInstance(){
        if(_INSTANCE == null){
            synchronized (Singleton3.class){
                if(_INSTANCE == null){
                    _INSTANCE =  new Singleton3();
                }
            }
        }
        return  _INSTANCE;
    }
}
