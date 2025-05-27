package restaurant_simulation;

/**
 * Represents an instruction for a waiter to perform a specific action at a table.
 * Can include additional information such as a dish or an order.
 */
public class WaiterInstruction {

    private Enums.WaiterAction action;
    private int tableNumber;
    private Dish dish;
    private Order currentOrder;

    /**
     * Creates a waiter instruction with the specified action, table number, and dish.
     *
     * @param action      the action to be performed by the waiter
     * @param tableNumber the table number associated with the action
     * @param dish        the dish involved in the action (if applicable)
     */
    WaiterInstruction(Enums.WaiterAction action, int tableNumber, Dish dish) {
        this.action = action;
        this.tableNumber = tableNumber;
        this.dish = dish;
    }

    /**
     * Creates a waiter instruction with the specified action and table number.
     *
     * @param action      the action to be performed by the waiter
     * @param tableNumber the table number associated with the action
     */
    WaiterInstruction(Enums.WaiterAction action, int tableNumber) {
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

    Dish getDish() {
        return dish;
    }
}
