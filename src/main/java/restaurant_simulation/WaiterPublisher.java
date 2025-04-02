package restaurant_simulation;

import java.util.ArrayList;
import java.util.List;

public interface WaiterPublisher {

    public void addListener(WaiterListener newWaiterListener);

    public void removeListener(WaiterListener waiterListener);

    public void notifyListeners(WaiterInstruction instruction);
}
