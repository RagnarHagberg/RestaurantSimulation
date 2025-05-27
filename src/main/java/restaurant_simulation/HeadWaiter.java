package restaurant_simulation;

import java.util.ArrayList;

/**
 * Handles guest seating by assigning them to tables when available.
 * Navigates between the queue (spawn) and tables to manage guest placement.
 */
public class HeadWaiter extends CanvasObject implements  HeadWaiterListener, Updatable {

    private int diameter = 60;
    private ArrayList<Table> tables = new ArrayList<>();
    private int freeTables = 0;

    private boolean isReady = true;

    private int walkSpeed = 5;
    private int targetX;
    private int targetY;

    private int elapsedTime = 0;

    private boolean hasWalkedToMiddle = false;

    private static final int MIDDLE_Y = 320;
    private static final int TARGET_THRESHOLD = 10;

    private enum Target {
        /** A position outside the restaurant */
        SPAWN,
        /** A table's location */
        TABLE
    }

    private Table currentTable;
    private Target currentTarget;

    private RestaurantQueue restaurantQueue;

    /**
     * Constructs the HeadWaiter with a reference to the queue and list of tables.
     * @param x X-coordinate of spawn
     * @param y Y-coordinate of spawn
     * @param restaurantQueue guest queue
     * @param tables table list to assign guests to
     */
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

    /**
     * Moves the head waiter towards the current target location.
     * The movement prioritizes walking vertically to a middle line (y=320) first,
     * then horizontally towards the target x-coordinate, and finally vertically
     * to the target y-coordinate. Once close enough to the target, it performs
     * actions depending on the target type (TABLE or SPAWN).
     */
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


        int yDirectionToMiddle = (getY() < MIDDLE_Y) ? 1 : -1;

        if (Math.abs(getY()-MIDDLE_Y) > 25 && !hasWalkedToMiddle){
            setY(getY() + walkSpeed * yDirectionToMiddle);
            return;
        }
        else{
            hasWalkedToMiddle = true;
        }

        // first walk the x direction
        if (Math.abs(getX()-targetX) > TARGET_THRESHOLD){
            setX(getX() + walkSpeed * xDirectionToTarget);
        }
        // then walk in the y direction
        else if (Math.abs(getY()-targetY) > TARGET_THRESHOLD){
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

    /**
     * Called when the queue has changed or guests left.
     * Attempts to assign guests to an empty table.
     */
    public void guestsUpdated(){
        assignGuestsToTable();

    }

    /**
     * Attempts to assign the next guests to an available empty table.
     * If assigned, begins walking toward the table.
     */
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

                if (restaurantQueue.getGuestsInQueue() < SimulationData.getInstance().getGUESTS_PER_TABLE()){
                    return;
                }

                ArrayList<Guest> guests = restaurantQueue.getNextGuests(SimulationData.getInstance().getGUESTS_PER_TABLE());

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
     * Receives table event notifications and reacts if guests have left.
     * @param tableSignal signal from a table (e.g., guests have left)
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
