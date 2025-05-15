package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

public class GuestInstanceController implements GuestInstanceControllerListener{
    private int maxGuests = 16;
    private int elapsedTime = 0;
    private int previousCreationTimeStamp;

    private RestaurantQueue restaurantQueue;
    private HeadWaiter headWaiter;
    private ArrayList<Guest> guests = new ArrayList<>();

    private ArrayList<Guest> guestsToRemove = new ArrayList<>();


    GuestInstanceController(RestaurantQueue restaurantQueue, HeadWaiter headWaiter){
        this.restaurantQueue = restaurantQueue;
        this.headWaiter = headWaiter;
    }

    private void createGuest(){
        Guest guest = new Guest(0,0);
        guests.add(guest);
        guest.addInstanceListener(this);
        previousCreationTimeStamp = elapsedTime;

        // Add to line
        restaurantQueue.addGuest(guest);
    }


    public void update(int delta){
        elapsedTime += delta;

        if (elapsedTime - previousCreationTimeStamp > 2000){
            if (guests.size() < maxGuests){
                // create 4 guests
                for (int i = 0; i<4; i++){
                    createGuest();
                }
                headWaiter.guestsUpdated();
            }

            previousCreationTimeStamp = elapsedTime;
        }

        guests.removeAll(guestsToRemove);
        guestsToRemove.clear();
    }

    public ArrayList<Guest> getGuests() {
        return guests;
    }

    /**
     * @param guest
     */
    @Override
    public void guestRemoved(Guest guest) {
        guestsToRemove.add(guest);
    }
}
