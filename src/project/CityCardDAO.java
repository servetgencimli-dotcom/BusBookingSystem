package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityCardDAO {

    public static void saveCityCard(String userFin, CityCard card) {
        String sql = "INSERT INTO cityCards(userFin, cities, monthsLeft) VALUES(?,?,?)";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userFin);
            ps.setString(2, String.join(",", card.cities)); // Şəhərləri vergüllə ayır
            ps.setInt(3, card.monthsLeft);

            ps.executeUpdate();
            System.out.println("✅ City card saved to database");

        } catch (SQLException e) {
            System.out.println("❌ Error saving city card: " + e.getMessage());
        }
    }

    public static ArrayList<CityCard> loadCityCardsForUser(String userFin) {
        ArrayList<CityCard> cards = new ArrayList<>();
        String sql = "SELECT * FROM cityCards WHERE userFin=?";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userFin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String citiesStr = rs.getString("cities");
                List<String> cities = Arrays.asList(citiesStr.split(","));
                int monthsLeft = rs.getInt("monthsLeft");

                CityCard card = new CityCard(cities, monthsLeft);
                cards.add(card);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading city cards: " + e.getMessage());
        }

        return cards;
    }

    public static void updateCityCard(String userFin, CityCard card) {
        String sql = "UPDATE cityCards SET cities=?, monthsLeft=? WHERE userFin=?";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, String.join(",", card.cities));
            ps.setInt(2, card.monthsLeft);
            ps.setString(3, userFin);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error updating city card: " + e.getMessage());
        }
    }

    public static void deleteCityCard(int cardId) {
        String sql = "DELETE FROM cityCards WHERE id=?";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cardId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error deleting city card: " + e.getMessage());
        }
    }
}