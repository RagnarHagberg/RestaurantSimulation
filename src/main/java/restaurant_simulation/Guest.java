package restaurant_simulation;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a guest in the restaurant who moves toward a target (e.g., table or spawn point)
 * and notifies its controller upon reaching the spawn (exit).
 */
public class Guest extends CanvasObject implements Updatable {

    private int diameter = 17;
    private int elapsedTime = 0;

    private int targetX;
    private int targetY;

    private Color color;

    private ArrayList<GuestInstanceControllerListener> instanceListener = new ArrayList<>();

    private enum Target {
        /** A position outside the restaurant */
        SPAWN,
        /** A table's location */
        TABLE
    }

    private Target currentTarget;
    private boolean hasWalkedToMiddle = false;
    private int walkSpeed = 3;

    private static final int MIDDLE_Y = 320;
    private static final int TARGET_THRESHOLD = 50;


    Guest(int x, int y){
        super(x, y);
        color = new Color((int)(Math.random() * 0x1000000));
    }

    /**
     * Registers a listener for this guest instance, e.g., for removal notification.
     * @param newInstanceListener listener to be notified when guest reaches spawn
     */
    public void addInstanceListener(GuestInstanceControllerListener newInstanceListener){
        instanceListener.add(newInstanceListener);
    }

    /**
     * Notifies the first registered listener that the guest should be removed.
     */
    private void notifyListeners(){
        instanceListener.getFirst().guestRemoved(this);
    }

    public int getDiameter() {
        return diameter;
    }

    public void setTargetToNull(){
        currentTarget = null;
    }

    public void update(int delta) {
        elapsedTime += delta;

        if (currentTarget != null){
            walkToTarget();
        }
    }

    /**
     * Sets the guest's target position and type.
     * @param target destination type (SPAWN or TABLE)
     * @param x target X coordinate
     * @param y target Y coordinate
     */
    private void setTarget(Target target, int x, int y){
        this.currentTarget = target;
        hasWalkedToMiddle = false;

        if (currentTarget != null){
            targetX = x;
            targetY = y;
        } else {
            targetX = 0;
            targetY = 0;
        }
    }

    /**
     * Sets the guest's target to a table location.
     */
    public void setTargetToTable(int x, int y){
        setTarget(Target.TABLE, x, y);
    }

    /**
     * Sets the guest's target to the spawn (exit) location.
     */
    public void setTargetToSpawn(int x, int y){
        setTarget(Target.SPAWN, x, y);
    }

    /**
     * Moves the guest toward the target, optionally via a "middle" corridor area.
     * Upon reaching the target, notifies listeners or halts movement.
     */
    private void walkToTarget(){
        int xDirectionToTarget = (targetX < getX()) ? -1 : 1;
        int yDirectionToTarget = (targetY < getY()) ? -1 : 1;

        int yDirectionToMiddle = (getY() < MIDDLE_Y) ? 1 : -1;

        // Walk toward the middle corridor if not yet passed through it
        if (Math.abs(getY() - MIDDLE_Y) > 25 && !hasWalkedToMiddle){
            setY(getY() + walkSpeed * yDirectionToMiddle);
            return;
        } else {
            hasWalkedToMiddle = true;
        }

        // Move in X direction first
        if (Math.abs(getX() - targetX) > TARGET_THRESHOLD){
            setX(getX() + walkSpeed * xDirectionToTarget);
        }
        // Then move in Y direction
        else if (Math.abs(getY() - targetY) > TARGET_THRESHOLD){
            setY(getY() + walkSpeed * yDirectionToTarget);
        }
        // Reached the target
        else {
            switch (currentTarget) {
                case TABLE:
                    return;
                case SPAWN:
                    notifyListeners();
                    break;
            }
        }
    }

    public Color getColor() {
        return color;
    }
}
