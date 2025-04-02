package restaurant_simulation;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Order {
    private ArrayList<MenuItem> orders = new ArrayList<>();
    private int tableOriginIndex;

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
