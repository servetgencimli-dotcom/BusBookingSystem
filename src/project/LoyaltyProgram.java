package project;

import java.util.HashMap;
import java.util.Map;

public class LoyaltyProgram {
    private Map<String, Integer> userPoints = new HashMap<>();
    private Map<String, String> membershipTiers = new HashMap<>();

    public void registerUser(String userFin) {
        userPoints.put(userFin, 0);
        membershipTiers.put(userFin, "Bronze");
    }

    public void addPoints(String userFin, int points) {
        int current = userPoints.getOrDefault(userFin, 0);
        int newTotal = current + points;
        userPoints.put(userFin, newTotal);

        // Tier yüksəltmə
        if (newTotal >= 1000 && membershipTiers.get(userFin).equals("Bronze")) {
            membershipTiers.put(userFin, "Silver");
            System.out.println("🎉 Congratulations! You've been upgraded to Silver tier!");
        } else if (newTotal >= 2500 && membershipTiers.get(userFin).equals("Silver")) {
            membershipTiers.put(userFin, "Gold");
            System.out.println("🎉 Congratulations! You've been upgraded to Gold tier!");
        }
    }

    public int getPoints(String userFin) {
        return userPoints.getOrDefault(userFin, 0);
    }

    public void redeemPoints(String userFin, int points) {
        int current = getPoints(userFin);
        if (current >= points) {
            userPoints.put(userFin, current - points);
            System.out.println("✅ " + points + " points redeemed!");
        }
    }

    public String getTier(String userFin) {
        return membershipTiers.getOrDefault(userFin, "Bronze");
    }
}