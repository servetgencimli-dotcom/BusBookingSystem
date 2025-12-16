package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingDAO {

    public static void saveBooking(Booking booking) {
        String sql = "INSERT INTO bookings(passengerName, passengerAge, passengerGender, luggage, userFin, busNo, travelDate, price, routeName, intervalTime) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, booking.getPassenger().getName());
            ps.setInt(2, booking.getPassenger().getAge());
            ps.setString(3, booking.getPassenger().getGender());
            ps.setDouble(4, booking.getPassenger().getLuggage());
            ps.setString(5, booking.getPassenger().getFin());
            ps.setInt(6, booking.getBusNo());
            ps.setString(7, new SimpleDateFormat("yyyy-MM-dd").format(booking.getTravelDate()));
            ps.setDouble(8, booking.getPrice());
            ps.setString(9, booking.getRouteName());
            ps.setString(10, booking.getInterval());

            ps.executeUpdate();
            System.out.println("✅ Booking saved to database");

        } catch (SQLException e) {
            System.out.println("❌ Error saving booking: " + e.getMessage());
        }
    }

    public static List<Booking> loadAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Passenger passenger = new Passenger(
                        rs.getString("passengerName"),
                        rs.getInt("passengerAge"),
                        rs.getString("passengerGender"),
                        rs.getDouble("luggage"),
                        rs.getString("userFin")
                );

                Date travelDate = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("travelDate"));

                Booking booking = new Booking(
                        passenger,
                        rs.getInt("busNo"),
                        travelDate,
                        rs.getDouble("price"),
                        rs.getString("routeName"),
                        rs.getString("intervalTime")
                );

                bookings.add(booking);
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading bookings: " + e.getMessage());
        }

        return bookings;
    }

    public static List<Booking> loadBookingsByUser(String userFin) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE userFin=?";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userFin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Passenger passenger = new Passenger(
                        rs.getString("passengerName"),
                        rs.getInt("passengerAge"),
                        rs.getString("passengerGender"),
                        rs.getDouble("luggage"),
                        rs.getString("userFin")
                );

                Date travelDate = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("travelDate"));

                Booking booking = new Booking(
                        passenger,
                        rs.getInt("busNo"),
                        travelDate,
                        rs.getDouble("price"),
                        rs.getString("routeName"),
                        rs.getString("intervalTime")
                );

                bookings.add(booking);
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading user bookings: " + e.getMessage());
        }

        return bookings;
    }

    public static boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id=?";

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Booking deleted");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error deleting booking: " + e.getMessage());
        }

        return false;
    }
}