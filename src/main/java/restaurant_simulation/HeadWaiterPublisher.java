package restaurant_simulation;

/**
 * A publisher interface for tables to signal to the headWaiter when the guests leave the table.
 */
public interface HeadWaiterPublisher {

    public void addListener(HeadWaiterListener newHeadWaiterListener);

    public void removeListener(HeadWaiterListener newHeadWaiterListener);

    public void notifyListeners(Enums.TableSignal tableSignal);

}
