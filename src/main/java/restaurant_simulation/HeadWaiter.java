package restaurant_simulation;


// GuestManager som instansierar gäster

public class HeadWaiter extends CanvasObject {

    private int diameter = 60;
    HeadWaiter(int x, int y){
        super(x,y);
    }

    public int getDiameter() {
        return diameter;
    }
}
