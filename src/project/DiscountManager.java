package project;



import java.util.*;

public class DiscountManager {
    private Map<String, List<Discount>> userDiscounts = new HashMap<>();
    private Map<String, Integer> promoCodes = new HashMap<>();

    public void addDiscount(String userFin, int percentage, String reason) {
        userDiscounts.putIfAbsent(userFin, new ArrayList<>());
        userDiscounts.get(userFin).add(new Discount(percentage, reason, new Date()));
    }

    public List<Discount> getActiveDiscounts(String userFin) {
        return userDiscounts.getOrDefault(userFin, new ArrayList<>());
    }

    public double applyBestDiscount(String userFin, double price) {
        List<Discount> discounts = getActiveDiscounts(userFin);
        if (discounts.isEmpty()) return 0;

        int maxPercent = discounts.stream()
                .mapToInt(d -> d.percentage)
                .max().orElse(0);

        return price * maxPercent / 100.0;
    }

    public void createPromoCode(String code, int percent) {
        promoCodes.put(code.toUpperCase(), percent);
    }

    public boolean applyPromoCode(String code, String userFin) {
        Integer percent = promoCodes.get(code.toUpperCase());
        if (percent != null) {
            addDiscount(userFin, percent, "Promo: " + code);
            return true;
        }
        return false;
    }

    public void showAllPromoCodes() {
        System.out.println("\n=== ACTIVE PROMO CODES ===");
        for (Map.Entry<String, Integer> entry : promoCodes.entrySet()) {
            System.out.println("🎟️ " + entry.getKey() + " - " + entry.getValue() + "% OFF");
        }
    }
}