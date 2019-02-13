package learningpattern.template.food;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:34
 */
public class TestMain {
    public static void main(String[] args){
        OneMeal breakfast = new BreakFast("早餐");
        breakfast.haveMeal();

        OneMeal lunch = new Lunch("午餐");
        lunch.haveMeal();

        OneMeal dinner = new Dinner("晚餐");
        dinner.haveMeal();
    }
}
