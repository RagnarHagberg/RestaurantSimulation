package restaurant_simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a chef responsible for preparing dishes of a specific type (e.g., pastry, sous, garde-manger).
 * Handles movement between the spawn point and the head chef, manages a queue of dishes,
 * and simulates cooking with time-based updates.
 */
public class DishChef extends CanvasObject implements Updatable {
    private int diameter;
    private Color bodyColor;

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

    /**
     * Constructs a DishChef with given properties.
     *
     * @param x         initial x-position
     * @param y         initial y-position
     * @param diameter  diameter used for drawing/visual representation
     * @param bodyColor color of the chef
     * @param chefType  type of chef
     * @param workstation workstation this chef uses
     */
    DishChef(int x, int y, int diameter, Color bodyColor, Enums.ChefType chefType, ChefWorkstation workstation, int headChefX, int headChefY) {
        super(x, y);
        this.diameter = diameter;
        this.chefType = chefType;
        this.bodyColor = bodyColor;
        this.workstation = workstation;
        this.headChefX = headChefX;
        this.headChefY = headChefY;
    }

    /**
     * Adds a dish to the queue and starts preparation if idle.
     */
    public void addDish(Dish dish) {
        dishQueue.add(dish);
        if (dishQueue.size() == 1) {
            prepareNextDish();
        }
    }

    /**
     * Called when a dish finishes cooking. Triggers walk to head chef.
     */
    private void dishFinished() {
        progressProportion = 0;
        dishQueue.removeFirst();
        isCooking = false;

        isWalkingToHeadChef = true;
        isWalkingToSpawn = false;
    }

    /**
     * Walks the chef toward a target vector.
     * Handles arrival logic (notifying head chef or returning to spawn).
     */
    private void walkToVector(RVector otherVector) {
        RVector chefVector = new RVector(getX(), getY(), 0);
        RVector subtractedVector = otherVector.subtractVector(chefVector);

        if (subtractedVector.getLength() < 10) {
            if (isWalkingToHeadChef) {
                notifyListeners(currentDish);
                isWalkingToHeadChef = false;
                isWalkingToSpawn = true;
                return;
            }
            if (isWalkingToSpawn) {
                isWalkingToHeadChef = false;
                isWalkingToSpawn = false;
                prepareNextDish();
                return;
            }
        }

        RVector directionVector = subtractedVector.getUnitVector();
        RVector scaledVector = directionVector.getScaledVector(5); // 5 = walk speed

        setX(getX() + (int) scaledVector.getA());
        setY(getY() + (int) scaledVector.getB());
    }

    /**
     * Updates the chef's state every frame.
     *
     * @param delta time passed since last update
     */
    public void update(int delta) {
        elapsedTime += delta;

        if (workstation.getIngredients() >= dishConsumptionPerDish &&
                !isWalkingToHeadChef &&
                !isWalkingToSpawn &&
                !isCooking) {
            prepareNextDish();
        }

        if (isWalkingToHeadChef) {
            walkToVector(new RVector(headChefX, headChefY, 0));
        } else if (isWalkingToSpawn) {
            walkToVector(new RVector(getSpawnX(), getSpawnY(), 0));
        }

        if (isCooking) {
            progressProportion = (float) (timeNeededForDish - (dishFinishedTime - elapsedTime)) / timeNeededForDish;
            if (elapsedTime > dishFinishedTime) {
                dishFinished();
            }
        }
    }

    /**
     * Attempts to start cooking the next dish in the queue.
     * <p>
     * If the dish queue is empty or there are not enough ingredients at the workstation,
     * the method does nothing and sets the chef to idle. Otherwise, it sets the current dish,
     * consumes the required ingredients, and initializes the cooking timer.
     */
    private void prepareNextDish() {
        if (dishQueue.isEmpty()) {
            isCooking = false;
            return;
        }

        if (workstation.getIngredients() <= dishConsumptionPerDish) {
            isCooking = false;
            return;
        }

        isCooking = true;
        workstation.setIngredients(workstation.getIngredients() - dishConsumptionPerDish);
        currentDish = dishQueue.getFirst();
        timeNeededForDish = 3000;
        setDishFinishedTime(timeNeededForDish);
    }

    private void setDishFinishedTime(int neededTime) {
        this.dishFinishedTime = this.elapsedTime + neededTime;
    }

    /**
     * Notifies all registered head chef listeners that a dish is ready.
     */
    public void notifyListeners(Dish finishedDish) {
        for (HeadChefListener headChefListener : headChefListeners) {
            headChefListener.receiveNotification(finishedDish);
        }
    }

    public void addListener(HeadChefListener headChefListener) {
        headChefListeners.add(headChefListener);
    }

    public void removeListener(HeadChefListener headChefListener) {
        headChefListeners.remove(headChefListener);
    }

    // === Accessors ===
    public boolean isCooking() { return isCooking; }
    public float getProgressProportion() { return progressProportion; }
    public Color getBodyColor() { return bodyColor; }
    public int getDiameter() { return diameter; }
    Enums.ChefType getChefType() { return chefType; }
}
