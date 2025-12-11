package project;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Payment {
    private double pricePerKm = 0.2;

    public double calculatePayment(int distance, boolean ac, double luggageWeight) {
        double total = distance * pricePerKm;
        if (ac) total += 5;
        if (luggageWeight > 20) total += (luggageWeight - 20) * 0.5;
        return Math.round(total * 100.0) / 100.0;
    }

    public boolean processPayment(Scanner sc, double amount, User user) {
        if (user != null && user.cardNumber != null && user.cardNumber.length() == 16) {
            System.out.println("💰 Bus_booking_project.Payment of " + amount + " AZN successful using saved card!");
            return true;
        }
        String card, exp, cvc;
        while (true) {
            System.out.print("💳 Enter Card Number (16 digits): ");
            card = sc.nextLine().trim();
            System.out.print("Expiry (MM/YY): ");
            exp = sc.nextLine().trim();
            System.out.print("CVC (3 digits): ");
            cvc = sc.nextLine().trim();
            if (validateCardInfo(card, exp, cvc)) break;
            System.out.println("❌ Card info invalid, try again.");
        }
        System.out.println("💰 Bus_booking_project.Payment of " + amount + " AZN successful!");
        return true;
    }

    public boolean validateCardInfo(String card, String exp, String cvc) {
        if (!Pattern.matches("\\d{16}", card)) return false;
        if (!Pattern.matches("(0[1-9]|1[0-2])/\\d{2}", exp)) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
            sdf.setLenient(false);
            Date expiry = sdf.parse(exp);
            if (expiry.before(new Date())) return false;
        } catch (Exception e) {
            return false;
        }
        if (!Pattern.matches("\\d{3}", cvc)) return false;
        return true;
    }

    public static double calculateCityCardTotal(int cityCount, int months, boolean isGlobal) {
        if (isGlobal) {
            double monthly = 60.0;
            return monthly * months;
        } else {
            double monthly = 20.0 + Math.max(0, cityCount - 1) * 1.0;
            return monthly * months;
        }
    }
}
