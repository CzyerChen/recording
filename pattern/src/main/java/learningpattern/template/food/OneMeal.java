package learningpattern.template.food;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:26
 */
public abstract class OneMeal {
    protected String name;

    protected void haveMeal(){
        System.out.println("---------------------------------");
        preCook();
        cook();
        afterCook();
        System.out.println("---------------------------------");
    }

    protected void preCook(){
        System.out.println("洗锅子，准备做饭");
    }
    protected abstract void cook();

    protected void afterCook(){
        System.out.println("刷锅洗碗，吃饱喝足");
    }
}
