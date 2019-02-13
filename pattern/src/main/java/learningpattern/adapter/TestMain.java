package learningpattern.adapter;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:13
 */
public class TestMain {
    public static  void main(String[] args){
        Mobile mobile = new Mobile();
        PowerAdapter powerAdapter = new PowerAdapter(new Power220V());
        mobile.inputPower(powerAdapter);
    }
}
