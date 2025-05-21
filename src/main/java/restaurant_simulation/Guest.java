package restaurant_simulation;

import java.util.ArrayList;

public class Guest extends CanvasObject{

    private int diameter = 17;
    private int elapsedTime = 0;

    private int targetX;
    private int targetY;

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

    Guest(int x, int y){
        super(x,y);
    }

    public void addInstanceListener(GuestInstanceControllerListener newInstanceListener){
        instanceListener.add(newInstanceListener);
    }

    private void notifyListeners(){
        //System.out.println("Removed guest from self");
        instanceListener.getFirst().guestRemoved(this);
    }

    public int getDiameter() {
        return diameter;
    }

    public void update(int delta) {
        elapsedTime += delta;

        if (currentTarget != null){
            walkToTarget();
        }
    }


    private void setTarget(Target target, int x, int y){
        this.currentTarget = target;
        hasWalkedToMiddle = false;

        if (currentTarget != null){
            targetX = x;
            targetY = y;
        }
        else{
            targetX = 0;
            targetY = 0;
        }
    }
    public void setTargetToTable(int x, int y){
        setTarget(Target.TABLE, x, y);
    }

    public void setTargetToSpawn(int x, int y){
        setTarget(Target.SPAWN, x, y);
    }

    private void walkToTarget(){
        int xDirectionToTarget;
        int yDirectionToTarget;

        if (targetX < getX()) {
            xDirectionToTarget = -1;   //xDirectionToTarget = targetX < x: 1 ? -1;
        }else{
            xDirectionToTarget = 1;
        }

        if (targetY < getY()) {
            yDirectionToTarget = -1;
        }else{
            yDirectionToTarget = 1;
        }


        int yDirectionToMiddle = (getY() < 320) ? 1 : -1;

        if (Math.abs(getY()-320) > 25 && !hasWalkedToMiddle){
            setY(getY() + walkSpeed * yDirectionToMiddle);
            return;
        }
        else{
            hasWalkedToMiddle = true;
        }

        // first walk the x direction
        if (Math.abs(getX()-targetX) > 50){
            setX(getX() + walkSpeed * xDirectionToTarget);
        }
        // then walk in the y direction
        else if (Math.abs(getY()-targetY) > 50){
            setY(getY() + walkSpeed * yDirectionToTarget);
            // walk y
        }

        // if close enough to the target
        else{
            switch (currentTarget) {
                case TABLE:
                    //currentTarget = null;
                    return;
                case SPAWN:
                    notifyListeners();
                    // remove guest self
                    return;
                default:
                    setTarget(null,0,0);
            }
        }
    }

}
