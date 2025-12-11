package project;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SeatDAO {
    public static void saveSeat(String route, String date, String interval, int seatNo, String gender) {
        String sql = "INSERT INTO seats(route, date, intervalTime, seatNo, gender) VALUES(?,?,?,?,?)";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, route);
            ps.setString(2, date);
            ps.setString(3, interval);
            ps.setInt(4, seatNo);
            ps.setString(5, gender);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("saveSeat error: " + e.getMessage());
        }
    }
}
