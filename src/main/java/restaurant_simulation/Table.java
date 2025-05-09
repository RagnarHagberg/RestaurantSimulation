package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Table extends CanvasObject implements WaiterPublisher{


    //TODO
    /*

    Add waiters as listener
    notify them when the order is ready to be taken


     */
    private int diameter = 50;
    private int tableNumber;
    private int amountOfGuests = 4;

    private int elapsedTime;

    private int timeForOrder = 1000000;
    private boolean hasOrdered;
    private boolean hasSentMenuNotification = false;

    private Menu currentMenu;


    public List<WaiterListener> waiterListeners = new ArrayList<>();

    private ArrayList<Dish> dishes = new ArrayList<>();

    Table(int tableNumber){
        super(625 + Math.floorDiv(tableNumber,2) * 150, (tableNumber % 2) * 500);
        this.tableNumber = tableNumber;

        hasOrdered = false;
        elapsedTime = 0;

        //Random random = new Random();
        //amountOfGuests = random.nextInt(1,6);
    }

    public int getAmountOfGuests() {
        return amountOfGuests;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getTableNumber(){return tableNumber;}

    @Override
    public void addListener(WaiterListener newWaiterListener){
        waiterListeners.add(newWaiterListener);
    }

    @Override
    public void removeListener(WaiterListener waiterListener){
        waiterListeners.remove(waiterListener);
    }

    @Override
    public void notifyListeners(WaiterInstruction instruction){
        for(WaiterListener waiterListener : waiterListeners){
            waiterListener.receiveNotification(instruction);
        }
    }

    public void update(int delta){
        elapsedTime += delta;

        if (elapsedTime > 5000 && !hasSentMenuNotification){ // check to time after customer arrives
            if (currentMenu == null){
                hasSentMenuNotification = true;
                notifyListeners(new WaiterInstruction(Enums.WaiterAction.REQUESTMENU, getTableNumber()));
            }
        }

        if (elapsedTime > timeForOrder){
            if (!hasOrdered){
                notifyListeners(new WaiterInstruction(Enums.WaiterAction.REQUESTTOORDER, getTableNumber()));
                hasOrdered = true;
            }

        }

        // time for receiving dish + 20 000 time to levae

        // Get pastry menu if all dishes have been served and enough time has passed.
    }

    public void setCurrentMenu(Menu menu) {
        // set time to think through menu
        currentMenu = menu;
        timeForOrder = elapsedTime + 10000;
        //System.out.println(menu.selectRandomItem().courseName);
    }


    public Order makeRandomOrder(){
        ArrayList<MenuItem> orderList = new ArrayList<>();

        for (int i = 0; i < amountOfGuests; i++) {
            MenuItem orderedItem = currentMenu.selectRandomItem();
            System.out.println("Ordered: " + orderedItem.courseName);
            orderList.add(orderedItem);
        }

        Order order = new Order(orderList, getTableNumber());

        return order;
    }

    public void addDish(Dish newDish){
        dishes.add(newDish);
    }
}
