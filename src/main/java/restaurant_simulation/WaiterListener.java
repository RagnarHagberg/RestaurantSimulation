package restaurant_simulation;

/**
 * Used for waiters to listen to other classes.
 * It is used for the headChef to signal the waiters aswell as the
 * tables to notify the waiter when the guests are ready for service.
 */
public interface WaiterListener {
    public void receiveNotification(WaiterInstruction instruction);
}
