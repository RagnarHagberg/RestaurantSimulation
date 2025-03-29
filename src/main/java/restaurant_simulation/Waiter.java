package restaurant_simulation;

import java.util.ArrayList;

public class Waiter implements Listener{
    private int waiterIndex;
    private int x;
    private int y;
    private int diameter = 50;

    private int spawnX;
    private int spawnY;

    private int targetX;
    private int targetY;

    private enum Target {
        COORDINATE,
        CHEF,
        TABLE
    }
    private Target currentTarget;
    private String actionText = "Action: " + "No action. Target: No target";;

    private boolean isAtTable = false;
    private boolean isAtChef = false;

    private ArrayList<WaiterInstruction> instructionQueue = new ArrayList<>();
    private WaiterInstruction currentInstruction;

    // references from main to other classes
    private ArrayList<Table> tables = new ArrayList<>();
    private Menu currentMenu;
    private HeadChef chef;

    private ArrayList<MenuItem> orderList = new ArrayList<>();

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

    private void updateActionText(){
        String targetString;
        if (currentTarget != null) {
            targetString = "Target: " + currentTarget.name();
        }
        else{targetString = "Target: No target.";}

        String actionString;
        if (currentInstruction != null){
            actionString = "Action: " + currentInstruction.getAction() + ". ";
        }
        else{
            actionString = "Action: " + "No action. ";

        }

        this.actionText = actionString + targetString;

    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getWaiterIndex() {
        return waiterIndex;
    }

    private void setIsAtChef(boolean isAtChef){
        this.isAtChef = isAtChef;

        switch (currentInstruction.getAction()){
            case REQUESTTOORDER:
                chef.addOrder(orderList);
                finishCurrentInstruction();
        }
    }

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

    private void takeOrder(Table table){
        orderList = table.makeRandomOrder();
        // orderList.forEach(menuItem -> System.out.println(menuItem.courseName));
    }

    private void setTarget(Target target, int x, int y) {
        this.currentTarget = target;
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

    public void setTargetToTable(int tableNumber){
        setTarget(Target.TABLE, getTables().get(tableNumber).getX(), getTables().get(tableNumber).getY());

    }

    public void setTargetToCoordinates(int x, int y){
        setTarget(Target.COORDINATE,x, y);
    }

    public void setTargetToSpawn(){
        setTarget(Target.COORDINATE, spawnX, spawnY);
    }


    private void walkToTarget(){
        int xDirectionToTarget;
        int yDirectionToTarget;

        if (targetX < x) {
            xDirectionToTarget = -1;
        }else{
            xDirectionToTarget = 1;
        }

        if (targetY < y) {
            yDirectionToTarget = -1;
        }else{
            yDirectionToTarget = 1;
        }

        // first walk the x direction
        if (Math.abs(x-targetX) > 50){
            setX(x + 5 * xDirectionToTarget);
        }
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

    public void update(int delta){
        if (currentTarget != null){
            walkToTarget();
        }

    }

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
        }

    }

    private void addInstructionToQueue(WaiterInstruction instruction){
        instructionQueue.add(instruction);

        // if there previously was no instructions, execute the new instruction
        if (instructionQueue.size() == 1){
            executeNextInstruction();
        }
    }

    private void finishCurrentInstruction(){
        instructionQueue.remove(currentInstruction);
        executeNextInstruction();
    }

    @Override
    public void receiveNotification(WaiterInstruction instruction) {
        addInstructionToQueue(instruction);
    }
}
