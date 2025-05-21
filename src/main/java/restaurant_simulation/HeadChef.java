package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Head Chef in the restaurant simulation.
 * <p>
 * The HeadChef is responsible for managing dish orders, delegating cooking tasks
 * to specialized chefs (Sous, Pastry, Gardemanger), and notifying waiters when
 * dishes are ready for delivery.
 * </p>
 *
 */
public class HeadChef extends CanvasObject implements WaiterPublisher, HeadChefListener, Updatable {

    /** Diameter used for rendering this object visually. */
    private int diameter = 50;

    /** Time tracker for internal simulation use. */
    private int elapsedTime;

    /** List of listeners (waiters) subscribed to receive dish delivery instructions. */
    public List<WaiterListener> waiterListeners = new ArrayList<>();

    /** Queue of pending orders to be processed by chefs. */
    private ArrayList<Order> orders = new ArrayList<>();

    /** Reference to the Sous Chef instance. */
    private DishChef sousChef;

    /** Reference to the Pastry Chef instance. */
    private DishChef pastryChef;

    /** Reference to the Gardemanger Chef instance. */
    private DishChef gardemangerChef;

    /**
     * Constructs a HeadChef with references to all DishChefs.
     *
     * @param x                the x-position for rendering
     * @param y                the y-position for rendering
     * @param sousChef         the Sous Chef responsible for main dishes
     * @param pastryChef       the Pastry Chef responsible for desserts
     * @param gardemangerChef  the Gardemanger Chef responsible for cold dishes
     */
    HeadChef(int x, int y, DishChef sousChef, DishChef pastryChef, DishChef gardemangerChef) {
        super(x, y);
        this.sousChef = sousChef;
        this.pastryChef = pastryChef;
        this.gardemangerChef = gardemangerChef;
    }

    /**
     * Returns the diameter of the HeadChef's graphical representation.
     *
     * @return the diameter in pixels
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Updates the HeadChef's internal timer.
     *
     * @param delta the elapsed time since the last update (in ms or simulation units)
     */
    @Override
    public void update(int delta) {
        elapsedTime += delta;
    }

    /**
     * Assigns each dish in the first order in the queue to the appropriate chef,
     * based on the dish's required chef type. Once assigned, the order is removed
     * from the queue and the next order is processed (if any).
     */
    private void instructChefs() {
        Order currentOrder = orders.getFirst();

        for (MenuItem menuItem : currentOrder.getDishes()) {
            Dish currentDish = new Dish(menuItem.getCourseName(), currentOrder.getTableOriginIndex());

            // Determine which chef should handle this dish
            DishChef currentChef = switch (menuItem.getTargetChef()) {
                case SOUS -> sousChef;
                case GARDEMANGER -> gardemangerChef;
                case PASTRY -> pastryChef;
            };

            currentChef.addDish(currentDish);
        }

        orders.removeFirst();

        if (!orders.isEmpty()) {
            instructChefs(); // recursively handle remaining orders
        }
    }

    /**
     * Adds a new order to the processing queue. If it's the only order in the queue,
     * it will be processed immediately.
     *
     * @param order the new order to be handled by the HeadChef
     */
    public void addOrder(Order order) {
        orders.add(order);
        if (orders.size() == 1) {
            instructChefs();
        }
    }

    /**
     * Registers a waiter to be notified when a dish is ready for delivery.
     *
     * @param newWaiterListener the waiter listener to add
     */
    @Override
    public void addListener(WaiterListener newWaiterListener) {
        waiterListeners.add(newWaiterListener);
    }

    /**
     * Removes a waiter listener from notification list.
     *
     * @param waiterListener the waiter listener to remove
     */
    @Override
    public void removeListener(WaiterListener waiterListener) {
        waiterListeners.remove(waiterListener);
    }

    /**
     * Notifies the appropriate waiter (based on table number) to deliver a dish.
     *
     * @param instruction the instruction containing dish and table info
     */
    @Override
    public void notifyListeners(WaiterInstruction instruction) {
        for (WaiterListener waiterListener : waiterListeners) {
            Waiter currentWaiter = (Waiter) waiterListener;
            if (currentWaiter.getAssignedTableIndexes().contains(instruction.getTableNumber())) {
                System.out.println("Waiter assigned");
                waiterListener.receiveNotification(instruction);
                return;
            }
        }
    }

    /**
     * Receives a notification from a chef that a dish is finished, and forwards
     * the dish to the appropriate waiter via {@link #notifyListeners}.
     *
     * @param dish the completed dish ready for delivery
     */
    @Override
    public void receiveNotification(Dish dish) {
        notifyListeners(new WaiterInstruction(Enums.WaiterAction.DELIVERDISH, dish.getTableOriginIndex(), dish));
    }
}
