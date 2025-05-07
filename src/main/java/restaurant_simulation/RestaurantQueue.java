package restaurant_simulation;

import java.util.ArrayList;

public class RestaurantQueue {
    private ArrayList<Guest> guests = new ArrayList<>();

    public void addGuest(Guest guest){
        guests.add(guest);
        setGuestsPosition();
    }

    public void removeGuest(Guest guest){
        guests.remove(guest);
        setGuestsPosition();
    }

    private void setGuestsPosition(){
        int index = 0;
        for(Guest guest : guests){
            guest.setX(1130);
            guest.setY(300 + index*50);
            index += 1;
        }
    }
}
