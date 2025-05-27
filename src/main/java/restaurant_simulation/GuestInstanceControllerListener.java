package restaurant_simulation;


/**
 * An interface for GuestInstanceControllers to listen to created guests.
 */
public interface GuestInstanceControllerListener {
    public void guestRemoved(Guest guest);

}
