package project;

import java.util.List;

public class CityCard {
    public List<String> cities;
    public int monthsLeft;

    public CityCard(List<String> cities, int monthsLeft) {
        this.cities = cities;
        this.monthsLeft = monthsLeft;
    }

    public boolean isUsableFor(String cityName) {
        if (monthsLeft <= 0) return false;

        // GLOBAL kartı bütün şəhərlər üçün işləyir
        if (cities.contains("GLOBAL")) return true;

        // Konkret şəhər üçün yoxla
        for (String city : cities) {
            if (city.equalsIgnoreCase(cityName)) return true;
        }

        return false;
    }

    public void useOneMonth() {
        if (monthsLeft > 0) {
            monthsLeft--;
            System.out.println("✅ City card used. Months left: " + monthsLeft);
        }
    }

    public List<String> getCities() {
        return cities;
    }

    public int getMonthsLeft() {
        return monthsLeft;
    }

    public void setMonthsLeft(int monthsLeft) {
        this.monthsLeft = monthsLeft;
    }

    @Override
    public String toString() {
        return "CityCard{cities=" + cities + ", monthsLeft=" + monthsLeft + "}";
    }
}