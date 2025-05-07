package restaurant_simulation;

public class Guest extends CanvasObject{

    private int diameter = 30;
    private int elapsedTime = 0;
    Guest(int x, int y){
        super(x,y);
    }

    public int getDiameter() {
        return diameter;
    }

    public void update(int delta) {
        elapsedTime += delta;
    }

}
