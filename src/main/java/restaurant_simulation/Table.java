package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Table extends CanvasObject implements WaiterPublisher, HeadWaiterPublisher, Updatable{


    //TODO
    /*

    Add waiters as listener
    notify them when the order is ready to be taken


     */
    private int diameter = 50;
    private int tableNumber;
    private int amountOfGuests = 4;

    private int elapsedTime;

    private int timeForOrder = 1000000000;
    private int timeToLeave = 1000000000;

    private int automaticLeaveTime = 300000;
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

    Table(int tableNumber){
        super(625 + Math.floorDiv(tableNumber,2) * 150, (tableNumber % 2) * 500);
        this.tableNumber = tableNumber;

        //Random random = new Random();
        //amountOfGuests = random.nextInt(1,6);
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getAmountOfGuests() {
        return amountOfGuests;
    }

    public void addGuest(Guest guest){
        guests.add(guest);
        empty = false;
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

        if (empty) {
            return;
        }
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

        if (elapsedTime > timeToLeave){

            System.out.println("Table" + getTableNumber() + "has reached time for guests to leave" + "Time to leave!");
            makeGuestsLeave();
        }

        if (elapsedTime > automaticLeaveTime){
            System.out.println("Table : " + getTableNumber() + " - guests have to leave");
            makeGuestsLeave();
        }
    }

    private void makeGuestsLeave(){
        notifyListeners(Enums.TableSignal.GUESTSLEFT);

        // set guests target to leave restaurant and reset guests.
        for(Guest guest : guests){
            // Guest leave
            guest.setTargetToSpawn(1500,200);
        }
        resetTable();
    }

    public void setCurrentMenu(Menu menu) {
        // set time to think through menu
        currentMenu = menu;
        timeForOrder = elapsedTime + orderWaitTime;
        //System.out.println(menu.selectRandomItem().courseName);
    }


    public Order makeRandomOrder(){
        ArrayList<MenuItem> orderList = new ArrayList<>();

        for (int i = 0; i < amountOfGuests; i++) {
            MenuItem orderedItem = currentMenu.selectRandomItem();
            SimulationData.getInstance().addCrowns((int) orderedItem.getPrice());
            orderList.add(orderedItem);
        }

        Order order = new Order(orderList, getTableNumber());
        savedOrder = order;

        return order;
    }

    public void addDish(Dish newDish){
        dishes.add(newDish);

        System.out.println("New dish arrived to table " + getTableNumber() + "Dishes received are now: " + dishes.size());
        System.out.println("Wanted amount of dishes at table " + getTableNumber() + "is : " + savedOrder.getDishes().size());

        for (Dish dish : dishes){
            System.out.println("Has received: " + dish.courseName);
        }
        for (MenuItem menuItem : savedOrder.getDishes()){
            System.out.println("Has ordered: " + menuItem.getCourseName());
        }

        // check if every ordered dish has arrived
        if (dishes.size() == savedOrder.getDishes().size()){
            timeToLeave = elapsedTime + leaveWaitTime;
            System.out.println("Table " + getTableNumber() + ": set time for guests to leave to" + timeToLeave);
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
        for(HeadWaiterListener headWaiterListener : headWaiterListeners){
            headWaiterListener.receiveNotification(tableSignal);
        }
    }

    public void setGuestPosition(){
        int i = 0;
        for(Guest guest: guests){
            guest.setX(getX()-30 + (i % 2) * 80);
            guest.setY(getY() + Math.floorDiv(i,2) * 80);
            i++;
        }
    }
    // reset the order, dish, menu state, time stamps
    public void resetTable(){
        guests.clear();
        dishes.clear();
        savedOrder = null;
        elapsedTime = 0;
        timeForOrder = 1000000000;
        timeToLeave = 1000000000;
        hasSentMenuNotification = false;
        haveGuestsLeft = false;
        hasOrdered = false;
        empty = true;
        currentMenu = null;
    };
}
