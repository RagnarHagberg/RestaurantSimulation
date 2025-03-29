package restaurant_simulation;

import java.util.ArrayList;

public class HeadChef {
    private int x;
    private int y;
    private int diameter = 50;

    private int elapsedTime;

    private ArrayList<ArrayList<MenuItem>> orders = new ArrayList<>();
    HeadChef(int x, int y){
         this.x = x;
         this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void update(int delta) {
        elapsedTime += delta;
    }

    public void addOrder(ArrayList<MenuItem> orderList) {
        orders.add(orderList);
    }
}
