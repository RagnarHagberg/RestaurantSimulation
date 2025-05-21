package restaurant_simulation;


// GuestManager som instansierar gäster

import java.util.ArrayList;

public class HeadWaiter extends CanvasObject implements  HeadWaiterListener {

    private int diameter = 60;
    private ArrayList<Table> tables = new ArrayList<>();
    private int freeTables = 0;

    private boolean isReady = true;

    private int walkSpeed = 5;
    private int targetX;
    private int targetY;

    private int elapsedTime = 0;

    private boolean hasWalkedToMiddle = false;


    private enum Target {
        /** A position outside the restaurant */
        SPAWN,
        /** A table's location */
        TABLE
    }

    private Table currentTable;
    private Target currentTarget;

    private RestaurantQueue restaurantQueue;


    HeadWaiter(int x, int y, RestaurantQueue restaurantQueue, ArrayList<Table> tables){
        super(x,y);
        this.tables = tables;
        this.restaurantQueue = restaurantQueue;
    }

    public void update(int delta) {
        elapsedTime += delta;

        if (currentTarget != null){
            walkToTarget();
        }

    }

    public int getDiameter() {
        return diameter;
    }
    private void setReady(boolean ready){
        isReady = ready;
        if (isReady){
            Target previousTarget = currentTarget;
            currentTarget = null; // clear it here to avoid conflicting updates
            assignGuestsToTable();

            // If assignGuestsToTable() didn’t set a new target
            if (currentTarget == null && previousTarget != null) {
                // Optional: log that there was no guest to assign
                //System.out.println("No new target set, remaining idle.");
            }
        }
    }

    private void setTarget(Target target, int x, int y){
        //System.out.println("Changed target");
        this.currentTarget = target;
        hasWalkedToMiddle = false;

        if (currentTarget != null) {
            isReady = false;
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
                    currentTable.setGuestPosition();
                    currentTable = null;
                    // set guest position
                    setTargetToSpawn(this.getSpawnX(), this.getSpawnY());
                    break;
                case SPAWN:
                    setReady(true);
                    break;
                default:
                    setTarget(null,0,0);
                    break;
            }
        }
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public void guestsUpdated(){
        assignGuestsToTable();

    }

    // also call when the queue has been updated
    private void assignGuestsToTable(){
        if (!isReady){
            return;
        }

        for(Table table : tables){
            if (table.isEmpty()){
                //System.out.println("found empty table");
                // tables is empty
                // this function also removes the guests from the queue.

                if (restaurantQueue.getGuestsInQueue() < 4){
                    return;
                }

                ArrayList<Guest> guests = restaurantQueue.getNextGuests(4);

                table.resetTable();

                for (int i = 0; i < guests.size(); i++) {
                    Guest guest = guests.get(i);
                    table.addGuest(guest);
                    guest.setTargetToTable(table.getX(), table.getY());
                    guest.setX(getX() - 50*i);
                    guest.setY(getY() + 100);
                }

                currentTable = table;
                setTargetToTable(table.getX(), table.getY());
                // add guests to table
                // walk to table
                break;
            }
        }
    }

    /**
     * @param tableSignal
     */
    @Override
    public void receiveNotification(Enums.TableSignal tableSignal) {
        switch (tableSignal){
            case GUESTSLEFT:
                assignGuestsToTable();
                break;
        }
    }
}
