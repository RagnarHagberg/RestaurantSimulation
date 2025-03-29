package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Table {


    //TODO
    /*

    Add waiters as listener
    notify them when the order is ready to be taken


     */
    private int x;
    private int y;
    private int diameter = 50;
    private int tableNumber;
    private int amountOfGuests;

    private int elapsedTime;

    private int timeForOrder = 1000000;
    private boolean hasOrdered;
    private boolean hasSentMenuNotification = false;

    private Menu currentMenu;


    public List<Listener> listeners = new ArrayList<>();

    Table(int tableNumber){
        this.tableNumber = tableNumber;

        this.x = 625 + Math.floorDiv(getTableNumber(),2) * 150;
        this.y = (getTableNumber() % 2) * 500;

        hasOrdered = false;
        elapsedTime = 0;

        Random random = new Random();
        amountOfGuests = random.nextInt(1,6);
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getTableNumber(){return tableNumber;}

    public void addListener(Listener newListener){
        listeners.add(newListener);
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public void notifyListeners(WaiterInstruction instruction){
        for(Listener listener : listeners){
            listener.receiveNotification(instruction);
        }
    }

    public void update(int delta){
        elapsedTime += delta;

        if (elapsedTime > 5000 && !hasSentMenuNotification){ // check to time after customer arrives
            if (currentMenu == null){
                hasSentMenuNotification = true;
                notifyListeners(new WaiterInstruction(WaiterInstruction.Action.REQUESTMENU, getTableNumber()));
            }
        }

        if (elapsedTime > timeForOrder){
            if (!hasOrdered){
                notifyListeners(new WaiterInstruction(WaiterInstruction.Action.REQUESTTOORDER, getTableNumber()));
                hasOrdered = true;
            }

        }
    }

    public void setCurrentMenu(Menu menu) {
        // set time to think through menu
        currentMenu = menu;
        timeForOrder = elapsedTime + 10000;
        //System.out.println(menu.selectRandomItem().courseName);
    }


    public ArrayList<MenuItem> makeRandomOrder(){
        ArrayList<MenuItem> orderList = new ArrayList<>();
        for (int i = 0; i < amountOfGuests; i++) {
            orderList.add(currentMenu.selectRandomItem());
        }

        return orderList;
    }
}
