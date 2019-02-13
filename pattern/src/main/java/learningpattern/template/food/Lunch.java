package learningpattern.template.food;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:32
 */
public class Lunch extends OneMeal{
    Lunch(String name){
        this.name = name;
    }
    @Override
    protected void cook() {
        System.out.println(this.name+": 煎一块牛排，吃西餐啦");
    }
}
