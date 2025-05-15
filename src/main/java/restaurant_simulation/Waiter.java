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

public class Waiter extends CanvasObject implements WaiterListener {
    /** Represents the index of the waiter in relation to the other waiters created by the controller
    */
    private int waiterIndex;

    /** The diameter of the waiter's visual representation */
    private int diameter = 50;

    private int walkSpeed = 10;


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

    private ArrayList<Integer> assignedTableIndexes = new ArrayList<>();
    /**
     * Constructs a new Waiter with the specified index.
     * Sets the spawn position based on the waiter index by calling the canvasObject constructor.
     *
     * @param waiterIndex The unique identifier for this waiter
     */
    Waiter(int waiterIndex){
        super(600 + 100*waiterIndex, 300);
        this.waiterIndex = waiterIndex;
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

    public void addTableIndexToAssignedTableIndexes(int tableIndex){
        assignedTableIndexes.add(tableIndex);
    }

    public ArrayList<Integer> getAssignedTableIndexes() {
        return assignedTableIndexes;
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

    public int getDiameter() {
        return diameter;
    }

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
                break;
            case DELIVERDISH:
                setTargetToTable(currentInstruction.getTableNumber());
                break;

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

            case DELIVERDISH:
                table.addDish(currentInstruction.getDish());
                finishCurrentInstruction();
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
        setTarget(Target.COORDINATE, getSpawnX(), getSpawnY());
    }

    /**
     * Moves the waiter toward their current target.
     * When closer than 50 pixels to a target, performs appropriate actions depending on the target type.
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
                setTarget(Target.CHEF, chef.getX(), chef.getY());
                break;
                //currentInstruction.getDish();
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
