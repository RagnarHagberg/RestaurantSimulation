package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

public class HeadChef implements WaiterPublisher, HeadChefListener{
    private int x;
    private int y;
    private int diameter = 50;

    private int elapsedTime;

    public List<WaiterListener> waiterListeners = new ArrayList<>();

    private ArrayList<Order> orders = new ArrayList<>();

    HeadChef(int x, int y){
         this.x = x;
         this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void update(int delta) {
        elapsedTime += delta;
    }

    private void instructChefs(){
        Order currentOrder = orders.getFirst();
        for (MenuItem menuItem : currentOrder.getDishes()){
            Dish currentDish = new Dish(menuItem.courseName, currentOrder.getTableOriginIndex());
            switch (menuItem.targetChef){
                case PREP:
                    //Prep.addDish(currentDish)
                case SOUS:
                    //Sous.addDish(currentDish)
                case GARDEMANGER:
                    //Gardemanger.addDish(currentDish)
                case PASTRY:
                    //Pastry.AddDish(currentDish)
            }
        }
        orders.remove(currentOrder);
        if (!orders.isEmpty()){
            instructChefs();
        }
    }

    public void addOrder(Order order) {
        orders.add(order);
        if (orders.size() == 1){
            instructChefs();
        }
    }


    @Override
    public void addListener(WaiterListener newWaiterListener) {
        waiterListeners.add(newWaiterListener);
    }

    @Override
    public void removeListener(WaiterListener waiterListener) {
        waiterListeners.remove(waiterListener);
    }

    @Override
    public void notifyListeners(WaiterInstruction instruction) {
        for(WaiterListener waiterListener : waiterListeners){
            waiterListener.receiveNotification(instruction);
        }
    }

    // Receive that a dish is finished from the chefs
    @Override
    public void receiveNotification(Dish dish) {
        notifyListeners(new WaiterInstruction(Enums.WaiterAction.DELIVERDISH, dish.getTableOriginIndex(), dish));
    }
}
