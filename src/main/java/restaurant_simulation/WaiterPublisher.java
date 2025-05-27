package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for the headchef and tables to communicate with the waiter.
 */
public interface WaiterPublisher {

    public void addListener(WaiterListener newWaiterListener);

    public void removeListener(WaiterListener waiterListener);

    public void notifyListeners(WaiterInstruction instruction);
}
