package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a queue of guests waiting to be seated in the restaurant.
 */
public class RestaurantQueue {
    /**
     * The list of guests currently in the queue.
     */
    private ArrayList<Guest> guests = new ArrayList<>();

    /**
     * Adds a guest to the end of the queue and updates guest positions.
     *
     * @param guest the guest to add
     */
    public void addGuest(Guest guest) {
        guests.add(guest);
        setGuestsPosition();
    }

    /**
     * Removes a guest from the queue and updates guest positions.
     *
     * @param guest the guest to remove
     */
    public void removeGuest(Guest guest) {
        guests.remove(guest);
        setGuestsPosition();
    }

    /**
     * Returns the number of guests currently in the queue.
     *
     * @return the number of guests in the queue
     */
    public int getGuestsInQueue() {
        return guests.size();
    }

    /**
     * Retrieves the specified number of guests from the front of the queue and
     * removes them from the queue.
     *
     * @param amount the number of guests to retrieve
     * @return a list containing the first amount guests in the queue
     */
    public ArrayList<Guest> getNextGuests(int amount) {
        List<Guest> sublist = guests.subList(0, amount);
        ArrayList<Guest> firstGuests = new ArrayList<>(sublist);

        guests.removeAll(firstGuests);
        setGuestsPosition();

        return firstGuests;
    }

    /**
     * Updates the position of each guest in the queue.
     * This is typically used to visually arrange guests in the simulation.
     */
    private void setGuestsPosition() {
        int index = 0;
        for (Guest guest : guests) {
            guest.setX(1130);
            guest.setY(300 + index * 50);
            index += 1;
        }
    }
}
