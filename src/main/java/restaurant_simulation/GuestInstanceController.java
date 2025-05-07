package restaurant_simulation;

import java.util.ArrayList;

public class GuestInstanceController {
    private int maxGuests = 16;
    private int elapsedTime = 0;
    private int previousCreationTimeStamp;

    private RestaurantQueue restaurantQueue;
    ArrayList<Guest> guests = new ArrayList<>();


    GuestInstanceController(RestaurantQueue restaurantQueue){
        this.restaurantQueue = restaurantQueue;
    }

    private void createGuest(){
        System.out.println("Added guest");
        Guest guest = new Guest(0,0);
        guests.add(guest);
        previousCreationTimeStamp = elapsedTime;

        // Add to line
        restaurantQueue.addGuest(guest);
    }

    public void update(int delta){
        elapsedTime += delta;

        if (elapsedTime - previousCreationTimeStamp > 10000){
            if (guests.size() < maxGuests){
                // create 4 guests
                for (int i = 0; i<4; i++){
                    createGuest();
                }
            }

            previousCreationTimeStamp = elapsedTime;
        }
    }

    public ArrayList<Guest> getGuests() {
        return guests;
    }
}
