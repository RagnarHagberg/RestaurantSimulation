package restaurant_simulation;

/**
 * A class that contains enum based types used for defining classes.
 */
public class Enums {

    enum ChefType {
        SOUS,
        GARDEMANGER,
        PASTRY
    }

    enum WaiterAction {
        REQUESTMENU,
        REQUESTTOORDER,
        DELIVERDISH
    }

    enum TableSignal{
        GUESTSLEFT
    }
}
