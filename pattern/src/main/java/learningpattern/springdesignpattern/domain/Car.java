package learningpattern.springdesignpattern.domain;

import java.util.ArrayList;
import java.util.List;

public class Car extends CarComponent {
    private boolean checked = false;
    private List<CarComponent> list =new ArrayList<>();

    public Car(){
        list.add(new Window());
    }
    @Override
    public void beChecked(Worker worker) {
        if("qualified".equals(worker.getName())){
            for(CarComponent carComponent : list){
                worker.checkComponents(carComponent);
                this.checked = carComponent.isCorrect();
                System.out.println("零件是否正常："+this.checked);
            }
        }
    }
}
