package restaurant_simulation;

public class WaiterInstruction {


    private Enums.WaiterAction action;
    private int tableNumber;
    private Dish dish;

    WaiterInstruction(Enums.WaiterAction action, int tableNumber, Dish dish){
        this.action = action;
        this.tableNumber = tableNumber;
        this.dish = dish;
    }

    WaiterInstruction(Enums.WaiterAction action, int tableNumber){
        this.action = action;
        this.tableNumber = tableNumber;
    }

    int getTableNumber() {
        return tableNumber;
    }

    Enums.WaiterAction getAction() {
        return action;
    }

    Dish getDish() {return dish;}
}
