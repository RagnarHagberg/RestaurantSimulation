package restaurant_simulation;

public class WaiterInstruction {
    enum Action{
        REQUESTMENU,
        REQUESTTOORDER
    }

    private Action action;
    private int tableNumber;


    WaiterInstruction(Action action, int tableNumber){
        this.action = action;
        this.tableNumber = tableNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Action getAction() {
        return action;
    }
}
