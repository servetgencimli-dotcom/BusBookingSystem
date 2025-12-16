package project;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingManagerExtension {
    public static List<Booking> getBookingsByUser(List<Booking> allBookings, String userFin) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : allBookings) {
            if (b.getPassenger().getFin().equals(userFin)) {
                result.add(b);
            }
        }
        return result;
    }

    public static Map<String, Integer> getRouteStatistics(List<Booking> allBookings) {
        Map<String, Integer> stats = new HashMap<>();
        for (Booking b : allBookings) {
            String route = b.getRouteName();
            stats.put(route, stats.getOrDefault(route, 0) + 1);
        }
        return stats;
    }

    public static double getTotalRevenue(List<Booking> allBookings) {
        return allBookings.stream()
                .mapToDouble(Booking::getPrice)
                .sum();
    }
}