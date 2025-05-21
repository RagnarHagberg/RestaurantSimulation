package restaurant_simulation;

import java.util.ArrayList;

/**
 * Controls creation and lifecycle of Guest instances in the simulation.
 * Periodically spawns guests and removes them when they exit the restaurant.
 */
public class GuestInstanceController implements GuestInstanceControllerListener, Updatable {

    private int maxGuests = 24;
    private int elapsedTime = 0;
    private int previousCreationTimeStamp;

    private static final int GUEST_SPAWN_INTERVAL_MS = 2000;
    private static final int GUESTS_PER_BATCH = 4;


    private RestaurantQueue restaurantQueue;
    private HeadWaiter headWaiter;
    private ArrayList<Guest> guests = new ArrayList<>();
    private ArrayList<Guest> guestsToRemove = new ArrayList<>();

    /**
     * Constructs a controller responsible for spawning and tracking guests.
     * @param restaurantQueue the queue guests join upon creation
     * @param headWaiter the head waiter to notify when new guests arrive
     */
    GuestInstanceController(RestaurantQueue restaurantQueue, HeadWaiter headWaiter) {
        this.restaurantQueue = restaurantQueue;
        this.headWaiter = headWaiter;
    }

    /**
     * Creates a new guest, adds them to the guest list, queue, and registers for removal notification.
     */
    private void createGuest() {
        Guest guest = new Guest(0, 0);
        guests.add(guest);
        guest.addInstanceListener(this);
        previousCreationTimeStamp = elapsedTime;

        restaurantQueue.addGuest(guest);
    }

    /**
     * Periodically spawns new guests if below the maximum and notifies the head waiter.
     * Removes guests marked for removal.
     * @param delta time passed since last update
     */
    public void update(int delta) {
        elapsedTime += delta;

        if (elapsedTime - previousCreationTimeStamp > GUEST_SPAWN_INTERVAL_MS) {
            if (guests.size() < maxGuests) {
                for (int i = 0; i < GUESTS_PER_BATCH; i++) {
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
     * Called when a guest exits the restaurant and should be removed.
     * @param guest the guest to remove
     */
    @Override
    public void guestRemoved(Guest guest) {
        guestsToRemove.add(guest);
    }
}
