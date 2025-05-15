package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

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

    public int getGuestsInQueue(){
        return guests.size();
    }
    public ArrayList<Guest> getNextGuests(int amount){
        System.out.println(guests.size());
        List<Guest> sublist = guests.subList(0, amount);
        ArrayList<Guest> firstGuests = new ArrayList<>(sublist);

        guests.removeAll(firstGuests);

        return firstGuests;
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
