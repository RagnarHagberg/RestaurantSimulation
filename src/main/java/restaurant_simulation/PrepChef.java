package restaurant_simulation;

import java.awt.*;

/**
 * Represents the prepChef in the chef,
 * who is responsible for delivering ingredients to the other chefs.
 */
public class PrepChef extends CanvasObject implements Updatable, Progressbarable{

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

    private float progressProportion;


    /**
     * Constructs the prepChef based on the following properties.
     * Has references to the workstations where ingredients must be delivered.
     * The constructor instantly starts making ingredients after creation.
     * @param spawnX the original X-position of the prepchef, where it preps.
     * @param spawnY the original Y-position of the prepchef, where it preps.
     * @param diameter the diameter of the chef's body for rendering purposes.
     * @param bodyColor the color of the chef's body for rendering purposes.
     * @param sousWorkstation the reference to the workstation of the souschef.
     * @param pastryWorkstation the reference to the workstation of the pastry chef.
     * @param gardemangerWorkstation the reference to the workstation of the gardemanger.
     */
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

    /**
     * Starts a timer for creating ingredients
     */
    private void prepareIngredients(){
        finishTime = elapsedTime + cookTime;
        isPreparing = true;
    }

    /**
     * Called when the timer for creating ingredients has finished.
     * The function then starts the delivery process to the workstations.
     */
    private void ingredientsFinished(){
        progressProportion = 0;
        isPreparing = false;
        ingredientToWorkstationDeliverySize = Math.floorDiv(batchSize,3);
        startDelivery();
    }

    /**
     * Arbitrarily sets the first chef to deliver to the souschef
     * and changes state of the chef.
     */
    private void startDelivery(){
        isDistributing = true;
        target = Target.SOUS;
    }

    /**
     * Updates the chef's state every frame.
     * Calls the walking functionality depending on the state of the chef.
     * Calculates the progress of the dish-making if the chef is cooking, for rendering purposes.
     * @param delta time passed since last update
     */
    public void update(int delta){
        elapsedTime += delta;

        if (isPreparing){
            progressProportion = (float) (cookTime - (finishTime - elapsedTime)) / cookTime;

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

    /**
     * Helper-Function for changing the target after arriving at specific workstations,
     * in order to fulfill the delivery loop.
     */
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
    }

    /**
     * Calculate the direction to another vector and then
     * update the position of the chef in the direction of the vector, with a length of walkspeed.
     * @param otherVector
     */
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

    /**
     * Set the targetVector to the position of current target-workstations,
     * and walk to the targetVector.
     */
    private void distributeIngredients(){
        RVector targetVector = switch (target) {
            case SOUS -> new RVector(sousWorkstation.getX(), sousWorkstation.getY(), 0);
            case PASTRY -> new RVector(pastryWorkstation.getX(), pastryWorkstation.getY(), 0);
            case GARDEMANGER -> new RVector(gardemangerWorkstation.getX(), gardemangerWorkstation.getY(), 0);
        };
        walkToVector(targetVector);

    }

    public float getProgressProportion() { return progressProportion; }

}
