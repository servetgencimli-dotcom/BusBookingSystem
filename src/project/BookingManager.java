package project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    public ArrayList<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking b) {
        bookings.add(b);
        BookingDAO.saveBooking(b);
    }

    public void displayAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\n=== ALL BOOKINGS ===");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            System.out.println((i + 1) + ". " + b.getPassenger().getName() +
                    " | " + b.getRouteName() +
                    " | " + sdf.format(b.getTravelDate()) +
                    " | " + b.getPrice() + " AZN");
        }
    }

    // 🆕 İstifadəçiyə görə rezervasiyaları tap
    public List<Booking> getBookingsByUser(String userFin) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getPassenger().getFin().equals(userFin)) {
                result.add(b);
            }
        }
        return result;
    }

    // 🆕 Marşruta görə rezervasiyaları tap
    public List<Booking> getBookingsByRoute(String routeName) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getRouteName().equalsIgnoreCase(routeName)) {
                result.add(b);
            }
        }
        return result;
    }

    // 🆕 Ümumi gəlir
    public double getTotalRevenue() {
        return bookings.stream()
                .mapToDouble(Booking::getPrice)
                .sum();
    }

    // 🆕 Rezervasiyanı sil
    public boolean cancelBooking(int index) {
        if (index >= 0 && index < bookings.size()) {
            Booking removed = bookings.remove(index);
            System.out.println("✅ Booking cancelled: " + removed.getRouteName());
            return true;
        }
        return false;
    }
}