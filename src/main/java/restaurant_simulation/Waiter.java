package restaurant_simulation;

import java.util.ArrayList;

/**
 * Represents a waiter in a restaurant simulation.
 * <p>
 * The Waiter class implements the Listener interface to receive instructions through
 * notifications. Waiters can handle multiple instructions in a queue, move around the
 * restaurant to tables and the chef, and perform actions like delivering menus and
 * taking orders.
 * </p>
 *
 * @author Ragnar Hagberg
 */

public class Waiter implements WaiterListener {
    /** Represents the index of the waiter in relation to the other waiters created by the controller
    */
    private int waiterIndex;

    /** The current X position of the waiter */
    private int x;
    /** The current Y position of the waiter */
    private int y;
    /** The diameter of the waiter's visual representation */
    private int diameter = 50;

    /** The X coordinate of the waiter's spawn point */
    private int spawnX;
    /** The Y coordinate of the waiter's spawn point */
    private int spawnY;

    /** The X coordinate of the waiter's current target */
    private int targetX;
    /** The Y coordinate of the waiter's current target */
    private int targetY;

    /**
     * Enum representing possible target types for the waiter.
     */
    private enum Target {
        /** A specific coordinate in the restaurant */
        COORDINATE,
        /** The chef's location */
        CHEF,
        /** A table's location */
        TABLE
    }

    /** The current target type */
    private Target currentTarget;

    /** Text describing the waiter's current action and target, for the visual representation*/
    private String actionText = "WaiterAction: " + "No action. Target: No target";;

    /** Flag indicating whether the waiter is currently at a table */
    private boolean isAtTable = false;

    /** Flag indicating whether the waiter is currently at the chef */
    private boolean isAtChef = false;

    /** Queue of instructions to be executed by the waiter */
    private ArrayList<WaiterInstruction> instructionQueue = new ArrayList<>();

    /** The instruction that is currently being executed */
    private WaiterInstruction currentInstruction;

    /** References to all tables in the restaurant */
    private ArrayList<Table> tables = new ArrayList<>();
    /** Reference to the head chef */
    private HeadChef chef;

    /** The current menu being used by the waiter */
    private Menu currentMenu;

    /** List of menu items in the current order being handled */
    private Order currentOrder;

    private boolean hasWalkedToMiddle = false;

    /**
     * Constructs a new Waiter with the specified index.
     * Sets the spawn position based on the waiter index.
     *
     * @param waiterIndex The unique identifier for this waiter
     */
    Waiter(int waiterIndex){
        this.waiterIndex = waiterIndex;

        this.spawnX = 600 + 100*waiterIndex;
        this.spawnY = 300;
        setX(spawnX);
        setY(spawnY);
    }

    public void setChef(HeadChef chef) {
        this.chef = chef;
    }

    public void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public String getActionText() {
        return actionText;
    }

    /**
     * Updates the action text based on current instruction and target.
     * The text is used for display purposes.
     */
    private void updateActionText(){
        String targetString;
        if (currentTarget != null) {
            targetString = "Target: " + currentTarget.name();
        }
        else{targetString = "Target: No target.";}

        String actionString;
        if (currentInstruction != null){
            actionString = "WaiterAction: " + currentInstruction.getAction() + ". ";
        }
        else{
            actionString = "WaiterAction: " + "No action. ";

        }

        this.actionText = actionString + targetString;
    }


    /**
     * Gets the waiter's current X position.
     *
     * @return The X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the waiter's current Y position.
     *
     * @return The Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the waiter's X position.
     *
     * @param x The new X coordinate
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * Sets the waiter's Y position.
     *
     * @param y The new Y coordinate
     */
    public void setY(int y){
        this.y = y;
    }

    /**
     * Gets the diameter of the waiter's visual representation.
     *
     * @return The diameter in pixels
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Gets the waiter's unique index.
     *
     * @return The waiter index
     */
    public int getWaiterIndex() {
        return waiterIndex;
    }

    /**
     * Sets whether the waiter is at the chef's location and performs
     * different actions depending on the current Instruction
     *
     * @param isAtChef True if waiter is at chef, false otherwise
     */
    private void setIsAtChef(boolean isAtChef){
        this.isAtChef = isAtChef;

        switch (currentInstruction.getAction()){
            case REQUESTTOORDER:
                chef.addOrder(currentOrder);
                finishCurrentInstruction();
        }
    }

    /**
     * Sets whether the waiter is at a table and performs the appropriate actions
     * based on the current instruction.
     *
     * @param isAtTable True if waiter is at a table, false otherwise
     */
    private void setIsAtTable(boolean isAtTable) {
        this.isAtTable = isAtTable;

        Table table = tables.get(currentInstruction.getTableNumber());
        // potentially finish instruction if it is completed when at the table
        switch (currentInstruction.getAction()){
            case REQUESTMENU:
                table.setCurrentMenu(currentMenu);
                finishCurrentInstruction();
                isAtTable = false;
                break;

            case REQUESTTOORDER:
                takeOrder(table);

                // set target to head chef, and hand over the new orders.
                setTarget(Target.CHEF, chef.getX(), chef.getY());
                isAtTable = false;
                break;
        }
    }

    /**
     * Takes an order from a table by requesting a random order.
     * The order consists of multiple menuItems.
     * @param table The table to take an order from
     */
    private void takeOrder(Table table){
        currentOrder = table.makeRandomOrder();
        // orderList.forEach(menuItem -> System.out.println(menuItem.courseName));
    }

    /**
     * Sets the waiter's target location and type.
     *
     * @param target The type of target (TABLE, CHEF, COORDINATE)
     * @param x The X coordinate of the target
     * @param y The Y coordinate of the target
     */
    private void setTarget(Target target, int x, int y) {
        this.currentTarget = target;
        hasWalkedToMiddle = false;
        updateActionText();

        if (currentTarget != null){
            targetX = x;
            targetY = y;
        }
        else{
            targetX = 0;
            targetY = 0;
        }
    }

    /**
     * Sets the waiter's target to a specific table.
     *
     * @param tableNumber The index of the table to target
     */
    public void setTargetToTable(int tableNumber){
        setTarget(Target.TABLE, getTables().get(tableNumber).getX(), getTables().get(tableNumber).getY());

    }

    /**
     * Sets the waiter's target to specific coordinates.
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    public void setTargetToCoordinates(int x, int y){
        setTarget(Target.COORDINATE,x, y);
    }

    /**
     * Sets the waiter's target to their spawn point.
     */
    public void setTargetToSpawn(){
        setTarget(Target.COORDINATE, spawnX, spawnY);
    }

    /**
     * Moves the waiter toward their current target.
     * When closer than 50 pixels to a target, performs appropriate actions depending on the target type.
     */
    private void walkToTarget(){
        int xDirectionToTarget;
        int yDirectionToTarget;

        if (targetX < x) {
            xDirectionToTarget = -1;   //xDirectionToTarget = targetX < x: 1 ? -1;
        }else{
            xDirectionToTarget = 1;
        }

        if (targetY < y) {
            yDirectionToTarget = -1;
        }else{
            yDirectionToTarget = 1;
        }


        int yDirectionToMiddle = (y < 320) ? 1 : -1;

        if (Math.abs(y-320) > 25 && !hasWalkedToMiddle){
            setY(y + 5 * yDirectionToMiddle);
            return;
        }
        else{
            hasWalkedToMiddle = true;
        }

        // first walk the x direction
        if (Math.abs(x-targetX) > 50){
            setX(x + 5 * xDirectionToTarget);
        }
        // then walk in the y direction
        else if (Math.abs(y-targetY) > 50){
            setY(y + 5 * yDirectionToTarget);
            // walk y
        }

        // if close enough to the target
        else{
            switch (currentTarget) {
                case TABLE:
                    setIsAtTable(true);
                    return;
                case CHEF:
                    setIsAtChef(true);
                    return;
                default:
                    setTarget(null,0,0);
                }
        }
    }

    /**
     * Updates the waiter's position if a target exists.
     *
     * @param delta The time a frame is supposed to be
     */
    public void update(int delta){
        if (currentTarget != null){
            walkToTarget();
        }

    }

    /**
     * Executes the next instruction in the queue if available.
     * Sets appropriate targets based on the instruction type.
     * If queue is empty, the waiter is set to walk to the spawn point
     */
    private void executeNextInstruction(){
        if (instructionQueue.isEmpty()){
            currentInstruction = null;
            System.out.println("Waiter No. " + getWaiterIndex() + " completed all tasks");
            setTargetToSpawn();
            return;
        }

        // depending on the action, execute different functions.
        currentInstruction = instructionQueue.getFirst();
        updateActionText();
        switch (currentInstruction.getAction()){
            case REQUESTMENU:
                // If the instruction is ready to order, the waiter should walk to the table and hand over the menu
                setTargetToTable(currentInstruction.getTableNumber());
                break;

            case REQUESTTOORDER:
                setTargetToTable(currentInstruction.getTableNumber());
                break;

            case DELIVERDISH:
                currentInstruction.getDish();
        }

    }

    /**
     * Adds a new instruction to the queue and executes it if
     * it's the only instruction in the queue.
     *
     * @param instruction The instruction to add
     */
    private void addInstructionToQueue(WaiterInstruction instruction){
        instructionQueue.add(instruction);

        // if there previously was no instructions, execute the new instruction
        if (instructionQueue.size() == 1){
            executeNextInstruction();
        }
    }

    /**
     * Removes the completed instruction from the queue and moves to the next one.
     */
    private void finishCurrentInstruction(){
        instructionQueue.remove(currentInstruction);
        executeNextInstruction();
    }

    /**
     * Implements the Listener interface to receive waiter instructions.
     * Adds received instructions to the queue.
     *
     * @param instruction The instruction received through notification
     */
    @Override
    public void receiveNotification(WaiterInstruction instruction) {
        addInstructionToQueue(instruction);
    }
}
