package learningpattern.template.food;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:32
 */
public class Dinner extends OneMeal {
    Dinner(String name){
        this.name = name;
    }
    @Override
    protected void cook() {
        System.out.println(this.name+":组队吃火锅把");
    }
}
