package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

public class HeadChef extends CanvasObject implements WaiterPublisher, HeadChefListener{
    private int diameter = 50;

    private int elapsedTime;

    public List<WaiterListener> waiterListeners = new ArrayList<>();

    private ArrayList<Order> orders = new ArrayList<>();

    private DishChef sousChef;
    private DishChef pastryChef;
    private DishChef gardemangerChef;

    HeadChef(int x, int y, DishChef sousChef, DishChef pastryChef, DishChef gardemangerChef){
         super(x,y);
         this.sousChef = sousChef;
         this.pastryChef = pastryChef;
         this.gardemangerChef = gardemangerChef;
    }

    public int getDiameter() {
        return diameter;
    }

    public void update(int delta) {
        elapsedTime += delta;
    }

    private void instructChefs(){
        Order currentOrder = orders.getFirst();
        // Sort the dishes in the order to the correct Chef and task the chef to make the dish
        for (MenuItem menuItem : currentOrder.getDishes()){
            Dish currentDish = new Dish(menuItem.getCourseName(), currentOrder.getTableOriginIndex());

            // set strategy to chef strategy
            DishChef currentChef = switch (menuItem.getTargetChef()) {
                case SOUS -> sousChef;
                case GARDEMANGER -> gardemangerChef;
                case PASTRY -> pastryChef;
            };

            currentChef.addDish(currentDish);
        }

        orders.removeFirst();
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
            Waiter currentWaiter = (Waiter) waiterListener;
            if (currentWaiter.getAssignedTableIndexes().contains(instruction.getTableNumber())){
                System.out.println("Waiter assigned");
                waiterListener.receiveNotification(instruction);
                return;
            }

        }
        System.out.println("WAITER NOT ASSIGNED");

        System.out.println("WAITER NOT ASSIGNED");

        System.out.println("WAITER NOT ASSIGNED");
        System.out.println("WAITER NOT ASSIGNED");


    }

    // Receive that a dish is finished from the chefs
    @Override
    public void receiveNotification(Dish dish) {
        // Sent deliver dish notification
        notifyListeners(new WaiterInstruction(Enums.WaiterAction.DELIVERDISH, dish.getTableOriginIndex(), dish));
    }
}
