package project;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BusManager {
    Map<String, Bus> buses = new HashMap<>();

    public void addBus(String routeName, Bus bus) {
        buses.put(routeName, bus);
    }

    public void removeBus(String routeName) {
        buses.remove(routeName);
    }

    public Bus getBus(String routeName) {
        return buses.get(routeName);
    }

    public Set<String> routes() {
        return buses.keySet();
    }
}
