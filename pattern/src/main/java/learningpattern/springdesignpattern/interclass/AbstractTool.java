package learningpattern.springdesignpattern.interclass;

public abstract class AbstractTool {
    public abstract void material();
    public abstract void makeDeal();

    public  final void construct(){
        material();
        makeDeal();
    }
}
