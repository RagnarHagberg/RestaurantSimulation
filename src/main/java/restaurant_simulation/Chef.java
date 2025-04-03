package restaurant_simulation;

import java.awt.*;
import java.util.ArrayList;

public class Chef {
    // Progress bar on every item

    // max size x
    // create rectangle with size (0, time/total * max_size)
    // time/total time  * max size = x

    private int x;
    private int y;
    private int diameter;
    private Color bodyColor;

    private boolean isCooking;
    private int elapsedTime = 0;
    private int dishFinishedTime;

    private ArrayList<HeadChefListener> headChefListeners = new ArrayList<>();

    private Enums.ChefType chefType;

    private ArrayList<Dish> dishQueue = new ArrayList<>();
    private Dish currentDish;


    Chef(int x, int y, int diameter, Color bodyColor, Enums.ChefType chefType){
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.chefType = chefType;
        this.bodyColor = bodyColor;
    }


    public Color getBodyColor() {
        return bodyColor;
    }

    private void prepareNextDish(){

        if (dishQueue.isEmpty()){
            isCooking = false;
            return;
        }

        currentDish = dishQueue.getFirst();
        isCooking = true;
        setDishFinishedTime(3000);
    }

    public void addDish(Dish dish){
        dishQueue.add(dish);
        if (dishQueue.size() ==  1){
            prepareNextDish();
        }
    }

    public int getDiameter() {
        return diameter;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    Enums.ChefType getChefType() {
        return chefType;
    }

    private void dishFinished(){
        // send finished dish to head chef
        notifyListeners(currentDish);

        dishQueue.remove(currentDish);
        isCooking = false;

        prepareNextDish();
    }

    public void update(int delta){
        elapsedTime += delta;
        if (isCooking){
            if (elapsedTime > dishFinishedTime){
                dishFinished();
            }
        }
    }

    private void setDishFinishedTime(int neededTime){
        this.dishFinishedTime = this.elapsedTime + neededTime;
    }

    public void addListener(HeadChefListener headChefListener){
        headChefListeners.add(headChefListener);
    }

    public void removeListener(WaiterListener waiterListener){
        headChefListeners.remove(waiterListener);
    };

    public void notifyListeners(Dish finishedDish){
        for (HeadChefListener headChefListener : headChefListeners){
            headChefListener.receiveNotification(finishedDish);
        }
    }

}
