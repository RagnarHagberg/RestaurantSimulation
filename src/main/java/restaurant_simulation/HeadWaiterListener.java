package restaurant_simulation;

/**
 * Listener for the head waiter to listen to tables, which notify the headwaiter when guests leave.
 */
public interface HeadWaiterListener {
    public void receiveNotification(Enums.TableSignal tableSignal);

}
