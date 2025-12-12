package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {
     public List<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking b) {
        bookings.add(b);
        System.out.println("✅ Bus_booking_project.project.Booking added successfully!");
        saveBookingToDB(b);
    }

    public List<Booking> getBookings() {
        return bookings;
    }



    public void displayAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }
        for (Booking b : bookings) b.displayBooking();
    }

    private void saveBookingToDB(Booking b) {
        String sql = "INSERT INTO bookings(passengerName, passengerGender, passengerAge, luggage, busNo, routeName, intervalTime, travelDate, price, userFin) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getPassenger().getName());
            ps.setString(2, b.getPassenger().getGender());
            ps.setInt(3, b.getPassenger().getAge());
            ps.setDouble(4, b.getPassenger().getLuggageWeight());
            ps.setInt(5, b.getBusNo());
            ps.setString(6, b.getRouteName());
            ps.setString(7, b.getInterval());
            ps.setString(8, new SimpleDateFormat("yyyy-MM-dd").format(b.getTravelDate()));
            ps.setDouble(9, b.getPrice());
            ps.setString(10, b.getPassenger().getFin());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("saveBookingToDB error: " + e.getMessage());
        }
    }
}
