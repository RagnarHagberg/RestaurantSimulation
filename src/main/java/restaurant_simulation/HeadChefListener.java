package restaurant_simulation;

/**
 * Listener for headchef to listen to dish chefs, when a dish is finished.
 */
public interface HeadChefListener {
    public void receiveNotification(Dish dish);
}
