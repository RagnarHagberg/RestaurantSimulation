package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a table in the restaurant simulation.
 * Tracks guests, orders, and interactions with waiters and head waiters.
 */
public class Table extends CanvasObject implements WaiterPublisher, HeadWaiterPublisher, Updatable {

    private static final int MAX_WAIT_TIME = Integer.MAX_VALUE;

    private int diameter = 50;
    private int tableNumber;

    private int elapsedTime;
    private int timeForOrder = MAX_WAIT_TIME;
    private int timeToLeave = MAX_WAIT_TIME;

    private int automaticLeaveTime = 130000;
    private int orderWaitTime = 10000;
    private int leaveWaitTime = 20000;

    private boolean empty = true;
    private boolean hasOrdered = false;
    private boolean hasSentMenuNotification = false;
    private boolean haveGuestsLeft = false;

    private Menu currentMenu;

    public List<WaiterListener> waiterListeners = new ArrayList<>();
    public List<HeadWaiterListener> headWaiterListeners = new ArrayList<>();

    private ArrayList<Dish> dishes = new ArrayList<>();
    private ArrayList<Guest> guests = new ArrayList<>();

    private Order savedOrder;

    /**
     * Constructs a new table at the specified location.
     *
     * @param x           the x-coordinate of the table
     * @param y           the y-coordinate of the table
     * @param tableNumber the table's unique identifier
     */
    Table(int x, int y, int tableNumber) {
        super(x, y);
        this.tableNumber = tableNumber;
    }

    public boolean isEmpty() {
        return empty;
    }

    /**
     * Adds a guest to the table and marks it as occupied.
     *
     * @param guest the guest to add
     */
    public void addGuest(Guest guest) {
        guests.add(guest);
        empty = false;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getTableNumber() {
        return tableNumber;
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
        for (WaiterListener waiterListener : waiterListeners) {
            waiterListener.receiveNotification(instruction);
        }
    }

    /**
     * Updates the table's state based on elapsed time and triggers notifications if needed.
     *
     * @param delta the amount of time that has passed since the last update (e.g., in milliseconds)
     */
    public void update(int delta) {
        elapsedTime += delta;

        if (empty) {
            return;
        }

        if (elapsedTime > 5000 && !hasSentMenuNotification) {
            if (currentMenu == null) {
                hasSentMenuNotification = true;
                notifyListeners(new WaiterInstruction(Enums.WaiterAction.REQUESTMENU, getTableNumber()));
            }
        }

        if (elapsedTime > timeForOrder && !hasOrdered) {
            notifyListeners(new WaiterInstruction(Enums.WaiterAction.REQUESTTOORDER, getTableNumber()));
            hasOrdered = true;
        }

        if (elapsedTime > timeToLeave) {
            System.out.println("Table " + getTableNumber() + " has reached time for guests to leave, after " + elapsedTime);
            makeGuestsLeave();
        }

        if (elapsedTime > automaticLeaveTime) {
            System.out.println("Table "+ getTableNumber() + " - dishes received " + dishes.size());
            System.out.println("Table "+ getTableNumber() + " - dishes ordered " + savedOrder.getDishes().size());
            System.out.println("Table: " + getTableNumber() + " - guests have to leave");
            makeGuestsLeave();
        }
    }

    /**
     * Makes the guests leave the table and resets the table's state.
     */
    private void makeGuestsLeave() {
        notifyListeners(Enums.TableSignal.GUESTSLEFT);

        for (Guest guest : guests) {
            guest.setTargetToSpawn(1500, 200);
        }
        resetTable();
    }

    /**
     * Sets the menu for this table and calculates when guests will order.
     *
     * @param menu the menu to assign to the table
     */
    public void setCurrentMenu(Menu menu) {
        currentMenu = menu;
        timeForOrder = elapsedTime + orderWaitTime;
    }

    /**
     * Generates a random order for all guests at the table based on the current menu.
     *
     * @return the generated order
     */
    public Order makeRandomOrder() {
        ArrayList<MenuItem> orderList = new ArrayList<>();

        for (int i = 0; i < guests.size(); i++) {
            MenuItem orderedItem = currentMenu.selectRandomItem();
            SimulationData.getInstance().addCrowns((int) orderedItem.getPrice());
            orderList.add(orderedItem);
        }

        Order order = new Order(orderList, getTableNumber());
        savedOrder = order;
        return order;
    }

    /**
     * Adds a dish to the table's collection and checks if the table has received all ordered dishes.
     *
     * @param newDish the dish to add
     */
    public void addDish(Dish newDish) {
        if (savedOrder == null) {
            System.out.println("Table " + tableNumber + ": Order handling has gone wrong");
            return;
        }

        dishes.add(newDish);

        if (dishes.size() == savedOrder.getDishes().size()) {
            timeToLeave = elapsedTime + leaveWaitTime;
            System.out.println("Table " + getTableNumber() + ": set time for guests to leave to " + timeToLeave);
        }
    }

    @Override
    public void addListener(HeadWaiterListener newHeadWaiterListener) {
        headWaiterListeners.add(newHeadWaiterListener);
    }

    @Override
    public void removeListener(HeadWaiterListener newHeadWaiterListener) {
        headWaiterListeners.remove(newHeadWaiterListener);
    }

    @Override
    public void notifyListeners(Enums.TableSignal tableSignal) {
        for (HeadWaiterListener headWaiterListener : headWaiterListeners) {
            headWaiterListener.receiveNotification(tableSignal);
        }
    }

    /**
     * Sets each guest's position around the table in a circular layout.
     */
    public void setGuestPosition() {
        int i = 0;
        double part = (2 * Math.PI) / guests.size();
        int r = getDiameter() / 2;
        int cx = getX() + r;
        int cy = getY() + r;

        for (Guest guest : guests) {
            guest.setTargetToNull();
            guest.setX(-guest.getDiameter() / 2 + cx + (int) (r * Math.cos(part * i)));
            guest.setY(-guest.getDiameter() / 2 + cy + (int) (r * Math.sin(part * i)));
            i++;
        }
    }

    /**
     * Resets the table to its initial empty state.
     */
    public void resetTable() {
        guests.clear();
        dishes.clear();
        savedOrder = null;
        elapsedTime = 0;
        timeForOrder = MAX_WAIT_TIME;
        timeToLeave = MAX_WAIT_TIME;
        hasSentMenuNotification = false;
        haveGuestsLeft = false;
        hasOrdered = false;
        empty = true;
        currentMenu = null;
    }
}
