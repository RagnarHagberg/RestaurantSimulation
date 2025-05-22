package restaurant_simulation;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Represents the packaged information of ordered dishes from a table.
 * Contains information about where the order stems, so it can be delivered back.
 */
public class Order {
    private ArrayList<MenuItem> orders = new ArrayList<>();
    private int tableOriginIndex;

    /**
     * Constructs an order made of menuItems and the origin table.
     * @param orders An arraylist of the ordered menuItems
     * @param tableOriginIndex the index of the table ordering.
     */
    Order(ArrayList<MenuItem> orders, int tableOriginIndex){
        this.orders = orders;
        this.tableOriginIndex = tableOriginIndex;
    }

    public ArrayList<MenuItem> getDishes() {
        return orders;
    }

    public int getTableOriginIndex() {
        return tableOriginIndex;
    }
}
