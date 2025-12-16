package project;



import java.util.*;

public class RatingSystem {
    private Map<String, List<Rating>> routeRatings = new HashMap<>();

    public void addRating(String routeName, String userFin, int stars, String comment) {
        routeRatings.putIfAbsent(routeName, new ArrayList<>());
        routeRatings.get(routeName).add(new Rating(userFin, stars, comment, new Date()));
        System.out.println("⭐ Rating added successfully!");
    }

    public double getAverageRating(String routeName) {
        List<Rating> ratings = routeRatings.get(routeName);
        if (ratings == null || ratings.isEmpty()) return 0.0;

        return ratings.stream()
                .mapToInt(r -> r.stars)
                .average()
                .orElse(0.0);
    }

    public void displayRouteRatings(String routeName) {
        List<Rating> ratings = routeRatings.get(routeName);
        if (ratings == null || ratings.isEmpty()) {
            System.out.println("No ratings yet for " + routeName);
            return;
        }

        System.out.println("\n=== RATINGS FOR " + routeName + " ===");
        System.out.println("Average: " + String.format("%.1f", getAverageRating(routeName)) + " ⭐");
        System.out.println("\nReviews:");
        for (Rating r : ratings) {
            System.out.println(r.stars + " ⭐ - " + r.comment + " (" + r.date + ")");
        }
    }

    public void displayAllRatings() {
        if (routeRatings.isEmpty()) {
            System.out.println("No ratings available.");
            return;
        }

        System.out.println("\n=== ALL ROUTE RATINGS ===");
        for (Map.Entry<String, List<Rating>> entry : routeRatings.entrySet()) {
            double avg = getAverageRating(entry.getKey());
            System.out.println(entry.getKey() + ": " + String.format("%.1f", avg) + " ⭐ (" + entry.getValue().size() + " reviews)");
        }
    }
}