package restaurant_simulation;

public interface HeadWaiterPublisher {

    public void addListener(HeadWaiterListener newHeadWaiterListener);

    public void removeListener(HeadWaiterListener newHeadWaiterListener);

    public void notifyListeners(Enums.TableSignal tableSignal);

}
