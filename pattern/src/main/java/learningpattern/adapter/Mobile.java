package learningpattern.adapter;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:06
 */
public class Mobile {

    public void inputPower(Power5V power5V){
        System.out.println("充电中------电流为："+power5V.ProvidePower());
    }
}
