package restaurant_simulation;

public class WaiterInstruction {


    private Enums.WaiterAction action;
    private int tableNumber;
    private Dish dish;
    private Order currentOrder;


    WaiterInstruction(Enums.WaiterAction action, int tableNumber, Dish dish){
        this.action = action;
        this.tableNumber = tableNumber;
        this.dish = dish;
    }

    WaiterInstruction(Enums.WaiterAction action, int tableNumber){
        this.action = action;
        this.tableNumber = tableNumber;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    int getTableNumber() {
        return tableNumber;
    }

    Enums.WaiterAction getAction() {
        return action;
    }

    Dish getDish() {return dish;}
}
