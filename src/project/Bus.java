package project;

import java.util.HashMap;
import java.util.Map;

public class Bus {
    String route;
    int capacity = 40;
    Map<String, boolean[]> seatMap = new HashMap<>();
    Map<String, String[]> genderMap = new HashMap<>();

    public Bus(String route) {
        this.route = route;
    }

    private boolean[] seatsFor(String key) {
        return seatMap.computeIfAbsent(key, k -> new boolean[capacity]);
    }

    private String[] gendersFor(String key) {
        return genderMap.computeIfAbsent(key, k -> new String[capacity]);
    }
    public void showSeats(String key, int floor) {
        boolean[] seats = seatsFor(key);
        String[] genders = gendersFor(key);
        int start = (floor == 1) ? 0 : 20;
        int end = start + 20;
        System.out.println("\n🚌 " + floor + "-ci mərtəbə oturacaqları:");
        for (int i = start; i < end; i += 4) {
            for (int j = 0; j < 2; j++) {
                if (i + j < end) {
                    System.out.print(seats[i + j] ? "[" + genders[i + j] + "]" : "[" + (i + j + 1) + "]");
                }
            }
            System.out.print("   ");
            for (int j = 2; j < 4; j++) {
                if (i + j < end) {
                    System.out.print(seats[i + j] ? "[" + genders[i + j] + "]" : "[" + (i + j + 1) + "]");
                }
            }
            System.out.println();
        }
    }

    public boolean reserveSeat(String key, int seatNumber, String gender) {
        boolean[] seats = seatsFor(key);
        String[] genders = gendersFor(key);
        if (seatNumber < 1 || seatNumber > capacity) return false;
        if (seats[seatNumber - 1]) return false;
        seats[seatNumber - 1] = true;
        genders[seatNumber - 1] = gender;
        return true;
    }
}
