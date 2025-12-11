package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CityCardDAO {
    public static void saveCityCard(String userFin, CityCard card) {
        String sql = "INSERT INTO citycards(userFin, cities, monthsLeft) VALUES(?,?,?)";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userFin);
            ps.setString(2, String.join(",", card.cities));
            ps.setInt(3, card.monthsLeft);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("saveCityCard error: " + e.getMessage());
        }
    }

    public static ArrayList<CityCard> loadCityCardsForUser(String userFin) {
        ArrayList<CityCard> list = new ArrayList<>();
        String sql = "SELECT * FROM citycards WHERE userFin=?";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String citiesStr = rs.getString("cities");
                List<String> cities = new ArrayList<>();
                if (citiesStr != null && !citiesStr.trim().isEmpty()) {
                    String[] parts = citiesStr.split(",");
                    for (String p : parts) cities.add(p.trim());
                }
                CityCard c = new CityCard(cities, rs.getInt("monthsLeft"));
                list.add(c);
            }
        } catch (Exception e) {
            System.out.println("loadCityCardsForUser error: " + e.getMessage());
        }
        return list;
    }
}
