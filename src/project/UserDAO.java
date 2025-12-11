package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static void saveUser(User u) {
        String sql = "INSERT INTO users(name, gender, age, fin, series, password, card, expiry, cvc, isAdmin) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.name);
            ps.setString(2, u.gender);
            ps.setInt(3, u.age);
            ps.setString(4, u.fin);
            ps.setString(5, u.idSeries);
            ps.setString(6, u.password);
            ps.setString(7, u.cardNumber);
            ps.setString(8, u.cardExpiry);
            ps.setString(9, u.cvc);
            ps.setInt(10, u.isAdmin ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("saveUser SQL error: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("saveUser error: " + e.getMessage());
        }
    }

    public static User loadUser(String fin, String pw) {
        String sql = "SELECT * FROM users WHERE fin=? AND password=?";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fin);
            ps.setString(2, pw);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User(
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getInt("age"),
                        rs.getString("fin"),
                        rs.getString("series"),
                        rs.getString("password"),
                        rs.getString("card"),
                        rs.getString("expiry"),
                        rs.getString("cvc"),
                        rs.getInt("isAdmin") == 1
                );
                u.cityCards = CityCardDAO.loadCityCardsForUser(u.fin);
                return u;
            }
        } catch (Exception e) {
            System.out.println("loadUser error: " + e.getMessage());
        }
        return null;
    }

    public static List<User> loadAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User(
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getInt("age"),
                        rs.getString("fin"),
                        rs.getString("series"),
                        rs.getString("password"),
                        rs.getString("card"),
                        rs.getString("expiry"),
                        rs.getString("cvc"),
                        rs.getInt("isAdmin") == 1
                );
                u.cityCards = CityCardDAO.loadCityCardsForUser(u.fin);
                list.add(u);
            }
        } catch (Exception e) {
            System.out.println("loadAllUsers error: " + e.getMessage());
        }
        return list;
    }
}
