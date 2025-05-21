package restaurant_simulation;

import java.awt.*;

public class PrepChef extends CanvasObject implements Updatable{

    private int ingredients;
    private int batchSize = 100;
    private int ingredientToWorkstationDeliverySize;
    private int cookTime = 5000;
    private int finishTime;

    private boolean isPreparing;
    private boolean isDistributing;
    private boolean isWalkingToSpawn;

    private int elapsedTime = 0;

    private enum Target {
        SOUS,
        GARDEMANGER,
        PASTRY,
    }

    private Target target;

    private ChefWorkstation sousWorkstation;
    private ChefWorkstation pastryWorkstation;
    private ChefWorkstation gardemangerWorkstation;

    private int diameter;
    private Color bodyColor;


    PrepChef(int spawnX, int spawnY, int diameter, Color bodyColor, ChefWorkstation sousWorkstation, ChefWorkstation pastryWorkstation, ChefWorkstation gardemangerWorkstation) {
        super(spawnX, spawnY);
        this.diameter = diameter;
        this.bodyColor = bodyColor;
        this.sousWorkstation = sousWorkstation;
        this.pastryWorkstation = pastryWorkstation;
        this.gardemangerWorkstation = gardemangerWorkstation;
        prepareIngredients();
    }

    public Color getBodyColor() {
        return bodyColor;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getIngredients(){
        return ingredients;
    }

    private void prepareIngredients(){
        finishTime = elapsedTime + cookTime;
        isPreparing = true;
    }

    private void ingredientsFinished(){
        isPreparing = false;
        ingredientToWorkstationDeliverySize = Math.floorDiv(batchSize,3);
        startDelivery();
    }

    private void startDelivery(){
        isDistributing = true;
        target = Target.SOUS;
    }

    public void update(int delta){
        elapsedTime += delta;

        if (isPreparing){
            if (elapsedTime > finishTime){
                ingredientsFinished();
            }
        }

        if (isDistributing){
            distributeIngredients();
        }

        if (isWalkingToSpawn){
            walkToVector(new RVector(getSpawnX(), getSpawnY(), 0));
        }
    }

    private void arrivedAtWorkstation(){
        switch (target){
            case SOUS:
                target = Target.PASTRY;
                sousWorkstation.addIngredients(ingredientToWorkstationDeliverySize);
                break;
            case PASTRY:
                target = Target.GARDEMANGER;
                pastryWorkstation.addIngredients(ingredientToWorkstationDeliverySize);
                break;
            case GARDEMANGER:
                gardemangerWorkstation.addIngredients(ingredientToWorkstationDeliverySize);
                isWalkingToSpawn = true;
                isDistributing = false;
                target = null;
                break;
        }

        // workstation order [sous, pastry, gardemanger]

    }
    private void walkToVector(RVector otherVector){
        RVector chefVector = new RVector(getX(),getY(),0);
        RVector subtractedVector = otherVector.subtractVector(chefVector);

        // If the distance between the vectors is less than 10, the chef has arrived at the target
        if (subtractedVector.getLength() < 10){ // arrived
            // when at target
            if (isDistributing){
                arrivedAtWorkstation();
                return;
            }
            if (isWalkingToSpawn){
                isWalkingToSpawn = false;
                prepareIngredients();
                return;
            }
        }

        // Move based on the direction to the target
        RVector directionVector = subtractedVector.getUnitVector();
        RVector scaledVector = directionVector.getScaledVector(5); // Scale with walkspeed

        setX(getX() + (int) scaledVector.getA());
        setY(getY() + (int) scaledVector.getB());
    }

    private void distributeIngredients(){
        switch (target){
            case SOUS:
                walkToVector(new RVector(sousWorkstation.getX(), sousWorkstation.getY(), 0));
                break;
            case PASTRY:
                walkToVector(new RVector(pastryWorkstation.getX(), pastryWorkstation.getY(), 0));
                break;
            case GARDEMANGER:
                walkToVector(new RVector(gardemangerWorkstation.getX(), gardemangerWorkstation.getY(), 0));
                break;
        }

    }
}
