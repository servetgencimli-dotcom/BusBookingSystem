package project;

import java.util.ArrayList;
import java.util.List;

public class CityCard {
    List<String> cities;
    public int monthsLeft;

    public CityCard(List<String> cities, int months) {
        this.cities = new ArrayList<>(cities);
        this.monthsLeft = months;
    }

    public boolean isUsableFor(String city) {
        if (cities.contains("GLOBAL")) return monthsLeft > 0;
        for (String c : cities) if (c.equalsIgnoreCase(city)) return monthsLeft > 0;
        return false;
    }

    public void useOneMonth() {
        if (monthsLeft > 0) monthsLeft--;
    }

    public String toString() {
        return "Cities=" + cities + ", monthsLeft=" + monthsLeft;
    }
}
