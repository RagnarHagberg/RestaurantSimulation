package restaurant_simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DishChef extends CanvasObject {
    private int diameter;
    private Color bodyColor;

    // TODO get coordinates from constructor
    private int headChefX = 300;
    private int headChefY = 300;
    private boolean isWalkingToHeadChef;

    private boolean isWalkingToSpawn;

    private boolean isCooking;
    private int elapsedTime = 0;
    private int dishFinishedTime;

    private ArrayList<HeadChefListener> headChefListeners = new ArrayList<>();

    private Enums.ChefType chefType;

    private ArrayList<Dish> dishQueue = new ArrayList<>();
    private Dish currentDish;

    private float progressProportion;

    private int timeNeededForDish;

    private ChefWorkstation workstation;

    private int dishConsumptionPerDish = 10;

    //TODO add reaction when ingredients are added. If they are added and now enough, start cooking
    DishChef(int x, int y, int diameter, Color bodyColor, Enums.ChefType chefType, ChefWorkstation workstation){
        super(x,y);
        this.diameter = diameter;
        this.chefType = chefType;
        this.bodyColor = bodyColor;
        this.workstation = workstation;
    }


    public boolean isCooking() {
        return isCooking;
    }

    public float getProgressProportion() {
        return progressProportion;
    }

    public Color getBodyColor() {
        return bodyColor;
    }

    private void prepareNextDish(){
        if (dishQueue.isEmpty()){
            isCooking = false;
            return;
        }

        if (workstation.getIngredients() < dishConsumptionPerDish){
            isCooking = false;
            return;
        }

        isCooking = true;

        workstation.setIngredients(workstation.getIngredients() - dishConsumptionPerDish);
        // if ingredients < needed amount

        currentDish = dishQueue.getFirst();
        timeNeededForDish = 3000;
        setDishFinishedTime(timeNeededForDish);
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


    Enums.ChefType getChefType() {
        return chefType;
    }

    private void dishFinished(){
        // send finished dish to head chef
        progressProportion = 0;

        dishQueue.remove(currentDish);
        isCooking = false;

        // Walk to head chef and back before preparing next dish
        // The head chef will be noticed when the chef arrives
        isWalkingToHeadChef = true;

        // the next dish will be prepared when the character arrives at spawn
    }


    private void walkToVector(RVector otherVector){
        RVector chefVector = new RVector(getX(),getY(),0);
        RVector subtractedVector = otherVector.subtractVector(chefVector);

        // If the distance between the vectors is less than 10, the chef has arrived at the target
        // after arriving at the target, return to spawn
        if (subtractedVector.getLength() < 10){
            if (isWalkingToHeadChef){
                notifyListeners(currentDish);

                isWalkingToHeadChef = false;
                isWalkingToSpawn = true;
                return;
            }
            if (isWalkingToSpawn){
                isWalkingToSpawn = false;
                prepareNextDish();
                return;
            }

        }

        // Move based on the direction to the target
        RVector directionVector = subtractedVector.getUnitVector();
        RVector scaledVector = directionVector.getScaledVector(5); // Scale with walkspeed

        setX(getX() + (int) scaledVector.getA());
        setY(getY() + (int) scaledVector.getB());
    }

    public void update(int delta){
        elapsedTime += delta;


        if (workstation.getIngredients() > 10 && !isWalkingToHeadChef && !isWalkingToSpawn &&!isCooking){
            prepareNextDish();

        }

        if (isWalkingToHeadChef){
            RVector headChefVector = new RVector(headChefX, headChefY, 0);
            walkToVector(headChefVector);
        }

        if (isWalkingToSpawn){
            RVector spawnVector = new RVector(getSpawnX(),getSpawnY(), 0);
            walkToVector(spawnVector);
        }


        if (isCooking){
            // draw progress bar
            progressProportion = (float) (timeNeededForDish-(dishFinishedTime-elapsedTime)) / timeNeededForDish;

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
