package project;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Payment {

    public boolean validateCardInfo(String cardNumber, String expiry, String cvc) {
        // Kart nömrəsi: 16 rəqəm
        if (!Pattern.matches("\\d{16}", cardNumber)) {
            System.out.println("❌ Card number must be 16 digits");
            return false;
        }

        // Expiry: MM/YY formatı
        if (!Pattern.matches("(0[1-9]|1[0-2])/\\d{2}", expiry)) {
            System.out.println("❌ Expiry must be in MM/YY format");
            return false;
        }

        // CVC: 3 rəqəm
        if (!Pattern.matches("\\d{3}", cvc)) {
            System.out.println("❌ CVC must be 3 digits");
            return false;
        }

        return true;
    }

    public double calculatePayment(int distance, boolean hasLuggage, double luggageWeight) {
        double basePrice = distance * 0.5; // Hər km üçün 0.5 AZN

        if (hasLuggage && luggageWeight > 0) {
            if (luggageWeight > 20) {
                basePrice += (luggageWeight - 20) * 2; // 20 kg-dan çox hər kg üçün 2 AZN
            }
        }

        return basePrice;
    }

    public void processPayment(Scanner sc, double amount, User user) {
        System.out.println("\n💳 Payment Processing");
        System.out.println("Amount to pay: " + String.format("%.2f", amount) + " AZN");

        if (user.cardNumber != null && !user.cardNumber.isEmpty()) {
            System.out.println("Using saved card: **** **** **** " + user.cardNumber.substring(12));
            System.out.print("Confirm payment? (yes/no): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (confirm.equals("yes")) {
                System.out.println("✅ Payment successful!");
            } else {
                System.out.println("❌ Payment cancelled");
            }
        } else {
            System.out.println("❌ No saved card found. Please update your profile.");
        }
    }

    public static double calculateCityCardTotal(int cityCount, int months, boolean isGlobal) {
        if (isGlobal) {
            return 60.0 * months; // Global kart: 60 AZN/ay
        } else {
            double basePrice = 20.0; // İlk şəhər üçün 20 AZN
            double additionalPrice = (cityCount - 1) * 1.0; // Əlavə hər şəhər üçün +1 AZN
            return (basePrice + additionalPrice) * months;
        }
    }
}