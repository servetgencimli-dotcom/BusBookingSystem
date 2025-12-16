package project;

import java.util.*;

public class NotificationSystem {
    private Map<String, List<String>> userNotifications = new HashMap<>();

    public void sendNotification(String userFin, String message) {
        userNotifications.putIfAbsent(userFin, new ArrayList<>());
        userNotifications.get(userFin).add(message);
    }

    public List<String> getNotifications(String userFin) {
        List<String> notifs = userNotifications.getOrDefault(userFin, new ArrayList<>());
        userNotifications.put(userFin, new ArrayList<>()); // Clear after reading
        return notifs;
    }

    public void sendWelcomeNotification(User user) {
        sendNotification(user.fin, "Welcome to Bus Booking System, " + user.name + "!");
    }

    public void sendBookingConfirmation(String userFin, String route, Date date) {
        sendNotification(userFin, "Booking confirmed: " + route + " on " + date);
    }

    public void sendPromoAlert(String userFin, String promoCode) {
        sendNotification(userFin, "Special offer! Use code " + promoCode + " for discount!");
    }
}