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

    private Chef prepChef;
    private Chef sousChef;
    private Chef pastryChef;
    private Chef gardemangerChef;

    HeadChef(int x, int y, Chef prepChef, Chef sousChef, Chef pastryChef, Chef gardemangerChef){
         this.x = x;
         this.y = y;

         this.prepChef = prepChef;
         this.sousChef = sousChef;
         this.pastryChef = pastryChef;
         this.gardemangerChef = gardemangerChef;
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
            switch (menuItem.getTargetChef()){
                case PREP:
                    prepChef.addDish(currentDish);
                    break;
                case SOUS:
                    sousChef.addDish(currentDish);
                    break;
                case GARDEMANGER:
                    gardemangerChef.addDish(currentDish);
                    break;
                case PASTRY:
                    pastryChef.addDish(currentDish);
                    break;
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

            // Potentially bad code, has reference to waiter instead of waiterListener

            // only assign waiter to deliver dish if it is the waiters table
            Waiter currentWaiter = (Waiter) waiterListener;
            if (currentWaiter.getAssignedTableIndexes().contains(instruction.getTableNumber())){
                waiterListener.receiveNotification(instruction);
            }

        }
    }

    // Receive that a dish is finished from the chefs
    @Override
    public void receiveNotification(Dish dish) {
        // Sent deliver dish notification
        notifyListeners(new WaiterInstruction(Enums.WaiterAction.DELIVERDISH, dish.getTableOriginIndex(), dish));
    }
}
