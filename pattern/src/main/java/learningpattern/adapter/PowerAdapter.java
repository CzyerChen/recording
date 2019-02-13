package learningpattern.adapter;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:10
 */
public class PowerAdapter implements Power5V {
    private Power220V power220V;

    PowerAdapter(Power220V power220V){
        this.power220V = power220V;
    }
    @Override
    public int ProvidePower() {
       return power220V.providePower();
    }
}
