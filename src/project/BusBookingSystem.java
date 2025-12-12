package project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class BusBookingSystem {
    static Scanner sc = new Scanner(System.in);
    public static ArrayList<User> users = new ArrayList<>();
    static final String ADMIN_FIN = "XXXXXXX";
    public static final String ADMIN_PASS = "ADMINADMIN";
    public static Map<String, Integer> globalRoutes = new LinkedHashMap<>();
    public static Map<String, Integer> domesticRoutes = new LinkedHashMap<>();
    public static String[] cityIntervals = {"06:00-07:00", "10:00-11:00", "14:00-15:00", "18:00-19:00", "22:00-23:00"};

    public static BusManager busManager = new BusManager();
    public static BookingManager bookingManager = new BookingManager();
    public static Payment payment = new Payment();

    public static void main(String[] args) {
        // users = new ArrayList<>(Bus_booking_project.UserDAO.loadAllUsers());
        seedRoutes();
        users = new ArrayList<>(UserDAO.loadAllUsers());
        loadBookingsFromDB();


        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Select option: ");
            String op = sc.nextLine();

            User currentUser = null;
            switch (op) {
                case "1":
                    currentUser = loginMenu();
                    break;
                case "2":
                    currentUser = registerUser();
                    break;
                case "3":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option!");
            }

            if (currentUser != null) {
                if (currentUser.isAdmin) {
                    adminMenu(currentUser);
                } else {
                    userMenu(currentUser);
                }
            }
        }
    }


    static void seedRoutes() {
        try (Connection conn = DB.connect()) {
            conn.setAutoCommit(false); // transaction başlat

            String[] countries = {"Turkey", "Germany", "France", "Italy", "Spain"};
            for (String country : countries) {
                for (int i = 1; i <= 3; i++) {
                    String city = country + " City " + i;
                    String route = "Baku-" + city;
                    busManager.addBus(route, new Bus(route));
                    globalRoutes.put(route, 100 + i * 10);

                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT OR IGNORE INTO routes(routeName, distance) VALUES(?, ?)")) {
                        ps.setString(1, route);
                        ps.setInt(2, 100 + i * 10);
                        ps.executeUpdate();
                    }
                }
            }

            String[] cities = {"Ganja", "Qabala", "Sumgait", "Shaki", "Lankaran", "Balaken", "Khachmaz"};
            for (String city : cities) {
                String route = "Baku-" + city;
                busManager.addBus(route, new Bus(route));
                domesticRoutes.put(route, 50 + city.length());

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT OR IGNORE INTO routes(routeName, distance) VALUES(?, ?)")) {
                    ps.setString(1, route);
                    ps.setInt(2, 50 + city.length());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error seeding routes: " + e.getMessage());
        }
    }


    public void mainMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Select: ");

            String op = sc.nextLine();

            switch (op) {
                case "1":
                    loginMenu();
                    break;
                case "2":
                    registerUser();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static User loginMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== LOGIN MENU =====");
            System.out.println("1. Bus_booking_project.User Login");
            System.out.println("2. Admin Login");
            System.out.println("3. Back");
            System.out.print("Select option: ");

            String op = sc.nextLine();

            switch (op) {
                case "1":
                    return userLogin();
                case "2":
                    return adminLogin();
                case "3":
                    return null;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static User userLogin() {
        Scanner sc = new Scanner(System.in);

        System.out.print("FIN: ");
        String fin = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User u = UserDAO.loadUser(fin, password);

        if (u != null) {
            if (u.isAdmin) {
                System.out.println("Incorrect FIN or password!");
                return null;
            } else {
                System.out.println("Login successful!");
                return u;

            }

        } else {
            System.out.println("Incorrect FIN or password!");
            return null;
        }
    }

    public static User adminLogin() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Admin FIN: ");
        String fin = sc.nextLine();

        System.out.print("Admin Password: ");
        String password = sc.nextLine();

        User d = UserDAO.loadUser(fin, password);


        if (d != null) {
            System.out.println("Admin logged in!");
            User admin = new User(
                    "Admin", "N/A", 0,
                    ADMIN_FIN, "N/A",
                    ADMIN_PASS, "N/A", "N/A", "N/A", true
            );
            admin.isAdmin = true;
            return admin;
        } else {
            System.out.println("Wrong admin credentials!");
            return null;
        }

    }

    public static User registerUser() {
        System.out.print("Adınız: ");
        String name = sc.nextLine().trim();


        int age;
        System.out.print("Yaş: ");
        try {
            age = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid age.");
            return null;
        }

        String gender = "";
        if (age < 18) {

            gender = "U";
            System.out.println("🔔 18 yaşdan kiçik olduğunuz üçün kateqoriya 'U' (Uşaq) olaraq təyin edildi.");
        } else {
            while (true) {
                System.out.print("Cins (K/Q): ");
                gender = sc.nextLine().trim().toUpperCase();
                if (gender.equals("K") || gender.equals("Q")) break;
                System.out.println("❌ Cins yalnız 'K' və ya 'Q' ola bilər!");
            }
        }

        System.out.println("Register as:");
        System.out.println("1. Bus_booking_project.User");
        System.out.println("2. Admin");
        System.out.print("Choice: ");
        int rtype;
        try {
            rtype = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid");
            return null;
        }

        boolean isAdmin = false;
        if (rtype == 2) {

            if (age < 18) {
                System.out.println("❌ Admin qeydiyyatı yalnız 18 yaşdan yuxarı şəxslər üçün mümkündür.");
                return null;
            }
            System.out.print("Enter Admin Master Code: ");
            String code = sc.nextLine().trim();
            if (!code.equals(ADMIN_PASS)) {
                System.out.println("❌ Wrong Admin Code!");
                return null;
            }
            isAdmin = true;
        }

        String fin = "";
        while (true) {
            System.out.print("FIN kod (7 simvol, yalnız böyük hərf və rəqəm): ");
            fin = sc.nextLine().trim().toUpperCase();
            if (fin.matches("^[A-Z0-9]{7}$")) break;
            System.out.println("❌ FIN kod səhvdir!");
        }

        String series = "";
        while (true) {
            System.out.print("Seriya nömrəsi (AA1234567): ");
            series = sc.nextLine().trim();
            if (Pattern.matches("AA\\d{7}", series)) break;
            System.out.println("❌ Seriya səhv format!");
        }

        System.out.print("Şifrə: ");
        String password = sc.nextLine().trim();

        String card = "", exp = "", cvc = "";

        if (!isAdmin) {
            while (true) {
                System.out.print("Kart nömrəsi (16 rəqəm): ");
                card = sc.nextLine().trim();
                System.out.print("Kart bitmə tarixi (MM/YY): ");
                exp = sc.nextLine().trim();
                System.out.print("CVC (3 rəqəm): ");
                cvc = sc.nextLine().trim();
                if (payment.validateCardInfo(card, exp, cvc)) break;
                System.out.println("❌ Kart məlumatları səhvdir, yenidən daxil edin!");
            }
        }

        User u = new User(name, gender, age, fin, series, password, card, exp, cvc, isAdmin);
        UserDAO.saveUser(u);
        users.add(u);
        System.out.println("✅ Registered successfully!");
        return u;
    }


    public static User login() {
        System.out.print("Enter FIN: ");
        String fin = sc.nextLine();

        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        if (fin.equals(ADMIN_FIN) && pass.equals(ADMIN_PASS)) {
            User admin = new User(
                    "Admin", "N/A", 0,
                    ADMIN_FIN, "N/A",
                    ADMIN_PASS, "N/A", "N/A"
            );
            admin.isAdmin = true;
            System.out.println("Welcome, Admin!");
            return admin;
        }

        for (User u : users) {
            if (u.fin.equals(fin) && u.password.equals(pass)) {
                System.out.println("Welcome " + u.name + "!");
                return u;
            }
        }

        System.out.println("FIN or password incorrect.");
        return null;
    }

    public static void loadBookingsFromDB() {
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Passenger p = new Passenger(
                        rs.getString("passengerName"),
                        rs.getInt("passengerAge"),
                        rs.getString("passengerGender"),
                        rs.getDouble("luggage"),
                        rs.getString("userFin")
                );
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("travelDate"));
                Booking b = new Booking(
                        p,
                        rs.getInt("busNo"),
                        date,
                        rs.getDouble("price"),
                        rs.getString("routeName"),
                        rs.getString("intervalTime")
                );
                bookingManager.bookings.add(b);
            }
        } catch (Exception e) {
            System.out.println("loadBookingsFromDB error: " + e.getMessage());
        }
    }

    public static User findUserByName(String name) {
        if (name == null) return null;
        for (User u : users) {
            if (u.name != null && u.name.equalsIgnoreCase(name.trim())) return u;
        }
        List<User> dbUsers = UserDAO.loadAllUsers();
        for (User u : dbUsers) {
            if (u.name != null && u.name.equalsIgnoreCase(name.trim())) return u;
        }
        return null;
    }


    public static void userMenu(User user) {
        while (true) {
            System.out.println("\n=== USER MENU ===");
            System.out.println("1. Global Travel");
            System.out.println("2. Domestic Travel");
            System.out.println("3. City Travel");
            System.out.println("4. Buy City Card");
            System.out.println("5. Update Profile");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    handleBooking("global", user);
                    break;
                case "2":
                    handleBooking("domestic", user);
                    break;
                case "3":
                    handleCityTravel(user);
                    break;
                case "4":
                    handleCityCard(user);
                    break;
                case "5":
                    updateProfile(user);
                    break;
                case "6":
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static void adminMenu(User admin) {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Global Routes");
            System.out.println("2. Manage Domestic Routes");
            System.out.println("3. Manage City Routes");
            System.out.println("4. View All Bookings");
            System.out.println("5. View Users");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1":
                    manageRoutes("global");
                    break;
                case "2":
                    manageRoutes("domestic");
                    break;
                case "3":
                    manageRoutes("city");
                    break;
                case "4":
                    bookingManager.displayAllBookings();
                    break;
                case "5":
                    viewUsers();
                    break;
                case "6":
                    System.out.println("Admin logged out.");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static void manageRoutes(String type) {
        Map<String, Integer> map;
        if (type.equals("global")) map = globalRoutes;
        else if (type.equals("domestic")) map = domesticRoutes;
        else map = new LinkedHashMap<>();

        while (true) {
            System.out.println("\n=== MANAGE " + type.toUpperCase() + " ROUTES ===");
            System.out.println("1. Add Route");
            System.out.println("2. Remove Route");
            System.out.println("3. Rename Route");
            System.out.println("4. Change Time Intervals");
            System.out.println("5. Back");
            System.out.print("Choose: ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1":
                    addRouteSafe(type, map);
                    break;
                case "2":
                    removeRouteSafe(type, map);
                    break;
                case "3":
                    renameRouteSafe(type, map);
                    break;
                case "4":
                    changeIntervals();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static void addRouteSafe(String type, Map<String, Integer> map) {
        System.out.println("\n📌 Route format example: Baku-Ganja, Baku-Tbilisi");
        System.out.print("Enter route name: ");
        String route = sc.nextLine().trim();
        if (!route.contains("-")) {
            System.out.println("❌ Wrong format! Use format like: Baku-Ganja");
            return;
        }

        int dist = 120;

        try (Connection conn = DB.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO routes(routeName,distance) VALUES(?,?)")) {
                ps.setString(1, route);
                ps.setInt(2, dist);
                ps.executeUpdate();
            }

            busManager.addBus(route, new Bus(route)); // Əgər busManager Bus_booking_project.DB istifadə edirsə, bunu eyni conn daxilinə inteqrasiya et

            map.put(route, dist);

            conn.commit();
            System.out.println("✅ Route added to " + type.toUpperCase());
        } catch (SQLException e) {
            System.out.println("Bus_booking_project.DB error: " + e.getMessage());
        }
    }

    public static void removeRouteSafe(String type, Map<String, Integer> map) {
        if (map.isEmpty()) {
            System.out.println("No routes to remove.");
            return;
        }

        ArrayList<String> list = new ArrayList<>(map.keySet());
        System.out.println("\n📍 Available routes:");
        for (int i = 0; i < list.size(); i++)
            System.out.println((i + 1) + ". " + list.get(i));

        System.out.print("Choose route number: ");
        int ch;
        try {
            ch = Integer.parseInt(sc.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid choice!");
            return;
        }
        if (ch < 0 || ch >= list.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        String route = list.get(ch);

        try (Connection conn = DB.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM routes WHERE routeName=?")) {
                ps.setString(1, route);
                ps.executeUpdate();
            }

            busManager.removeBus(route); // Əgər Bus_booking_project.DB istifadə edirsə eyni conn daxilində et
            map.remove(route);

            conn.commit();
            System.out.println("❌ Route removed: " + route);
        } catch (SQLException e) {
            System.out.println("Bus_booking_project.DB error: " + e.getMessage());
        }
    }

    public static void renameRouteSafe(String type, Map<String, Integer> map) {
        if (map.isEmpty()) {
            System.out.println("No routes to rename.");
            return;
        }

        ArrayList<String> list = new ArrayList<>(map.keySet());
        System.out.println("\n📍 Available routes:");
        for (int i = 0; i < list.size(); i++)
            System.out.println((i + 1) + ". " + list.get(i));

        System.out.print("Choose route number: ");
        int ch;
        try {
            ch = Integer.parseInt(sc.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid!");
            return;
        }
        if (ch < 0 || ch >= list.size()) {
            System.out.println("Invalid!");
            return;
        }

        String oldName = list.get(ch);

        System.out.println("\n📌 Format example: Baku-Ganja, Baku-Tbilisi");
        System.out.print("Enter new route name: ");
        String newName = sc.nextLine().trim();
        if (!newName.contains("-")) {
            System.out.println("❌ Wrong format!");
            return;
        }

        int dist = map.get(oldName);

        try (Connection conn = DB.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE routes SET routeName=? WHERE routeName=?")) {
                ps.setString(1, newName);
                ps.setString(2, oldName);
                ps.executeUpdate();
            }

            busManager.removeBus(oldName); // Eyni conn daxilində safe edilsə daha yaxşıdır
            busManager.addBus(newName, new Bus(newName));

            map.remove(oldName);
            map.put(newName, dist);

            conn.commit();
            System.out.println("✏️ Renamed: " + oldName + " → " + newName);
        } catch (SQLException e) {
            System.out.println("Bus_booking_project.DB error: " + e.getMessage());
        }
    }

    public static void changeIntervals() {
        System.out.println("\nCurrent intervals:");
        for (int i = 0; i < cityIntervals.length; i++)
            System.out.println((i + 1) + ". " + cityIntervals[i]);

        System.out.print("Which one to change: ");
        int ch;
        try {
            ch = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (ch < 0 || ch >= cityIntervals.length) {
                System.out.println("Invalid choice!");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid input!");
            return;
        }

        System.out.print("Enter new interval (HH:MM-HH:MM): ");
        String newInterval = sc.nextLine().trim();

        // sadə regex yoxlaması
        if (!newInterval.matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}$")) {
            System.out.println("❌ Wrong format! Use HH:MM-HH:MM");
            return;
        }

        cityIntervals[ch] = newInterval;
        System.out.println("⏱️ Interval updated!");

        // Əgər Bus_booking_project.DB-yə də yazmaq lazımdırsa

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE cityIntervals SET intervalTime=? WHERE id=?")) {
            ps.setString(1, newInterval);
            ps.setInt(2, ch + 1); // id varsa 1-dən başlayır
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Bus_booking_project.DB error: " + e.getMessage());
        }

    }


    public static void viewUsers() {
        System.out.println("\n--- Registered Users ---");
        for (User u : users) {
            System.out.println("Name: " + u.name + " | FIN: " + u.fin + " | Admin: " + u.isAdmin + " | CityCards: " + u.cityCards.size());
        }
        // also list Bus_booking_project.DB users
        List<User> dbUsers = UserDAO.loadAllUsers();
        System.out.println("\n--- Bus_booking_project.DB Users ---");
        for (User u : dbUsers) {
            System.out.println("Name: " + u.name + " | FIN: " + u.fin + " | Admin: " + u.isAdmin + " | CityCards: " + u.cityCards.size());
        }
    }

    public static void updateProfile(User u) {
        System.out.println("\n--- Update Profile ---");
        System.out.println("Choose fields to edit (comma separated): name,gender,password,card");
        System.out.print("Enter choices: ");
        String line = sc.nextLine().trim().toLowerCase();
        String[] parts = line.split(",");
        Map<String, String> newValues = new HashMap<>();
        for (String p : parts) {
            p = p.trim();
            if (p.equals("name")) {
                System.out.print("New name: ");
                newValues.put("name", sc.nextLine().trim());
            } else if (p.equals("gender")) {
                String g = "";
                while (true) {
                    System.out.print("New gender (K/Q/U): ");
                    g = sc.nextLine().trim().toUpperCase();
                    if (g.equals("K") || g.equals("Q") || g.equals("U")) break;
                    System.out.println("❌ Only K, Q or U allowed!");
                }
                newValues.put("gender", g);
            } else if (p.equals("password")) {
                System.out.print("New password: ");
                newValues.put("password", sc.nextLine().trim());
            } else if (p.equals("card")) {
                String card, exp, cvc;
                while (true) {
                    System.out.print("New Card number (16 digits): ");
                    card = sc.nextLine().trim();
                    System.out.print("Expiry (MM/YY): ");
                    exp = sc.nextLine().trim();
                    System.out.print("CVC (3 digits): ");
                    cvc = sc.nextLine().trim();
                    if (payment.validateCardInfo(card, exp, cvc)) {
                        newValues.put("card", card);
                        newValues.put("exp", exp);
                        newValues.put("cvc", cvc);
                        break;
                    }
                    System.out.println("❌ Invalid card info, try again.");
                }
            }
        }
        System.out.print("Save changes? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("yes")) {
            if (newValues.containsKey("name")) u.name = newValues.get("name");
            if (newValues.containsKey("gender")) u.gender = newValues.get("gender");
            if (newValues.containsKey("password")) u.password = newValues.get("password");
            if (newValues.containsKey("card")) {
                u.cardNumber = newValues.get("card");
                u.cardExpiry = newValues.get("exp");
                u.cvc = newValues.get("cvc");
            }
            try (Connection conn = DB.connect();
                 PreparedStatement ps = conn.prepareStatement("UPDATE users SET name=?, gender=?, password=?, card=?, expiry=?, cvc=? WHERE fin=?")) {
                ps.setString(1, u.name);
                ps.setString(2, u.gender);
                ps.setString(3, u.password);
                ps.setString(4, u.cardNumber);
                ps.setString(5, u.cardExpiry);
                ps.setString(6, u.cvc);
                ps.setString(7, u.fin);
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("updateProfile Bus_booking_project.DB error: " + e.getMessage());
            }
            System.out.println("✅ Profile updated.");
        } else {
            System.out.println("Changes discarded.");
        }
    }

    public static void handleBooking(String type, User user) {
        Map<String, Integer> routeMap = (type.equals("global") ? globalRoutes : domesticRoutes);
        List<String> keys = new ArrayList<>(routeMap.keySet());
        if (keys.isEmpty()) {
            System.out.println("No routes available.");
            return;
        }
        System.out.println("\nAvailable " + type.toUpperCase() + " routes:");
        for (int i = 0; i < keys.size(); i++) System.out.println((i + 1) + ". " + keys.get(i));
        System.out.print("Choose route: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid");
            return;
        }
        if (choice < 1 || choice > keys.size()) {
            System.out.println("Invalid choice!");
            return;
        }
        String routeName = keys.get(choice - 1);
        Bus bus = busManager.getBus(routeName);

        System.out.print("Number of tickets (1-4): ");
        int ticketCount;
        try {
            ticketCount = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid");
            return;
        }
        if (ticketCount < 1 || ticketCount > 4) {
            System.out.println("Invalid number");
            return;
        }

        System.out.print("Travel Date (yyyy-MM-dd): ");
        String dateStr = sc.nextLine().trim();
        Date travelDate;
        try {
            travelDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            System.out.println("Invalid date.");
            return;
        }

        System.out.println("Available Time Intervals:");
        for (int i = 0; i < cityIntervals.length; i++) System.out.println((i + 1) + ". " + cityIntervals[i]);
        System.out.print("Choose interval: ");
        int intervalChoice;
        try {
            intervalChoice = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid");
            return;
        }
        if (intervalChoice < 1 || intervalChoice > cityIntervals.length) {
            System.out.println("Invalid interval");
            return;
        }
        String selectedInterval = cityIntervals[intervalChoice - 1];
        String key = new SimpleDateFormat("yyyy-MM-dd").format(travelDate) + "_" + selectedInterval;

        for (int t = 0; t < ticketCount; t++) {
            System.out.println("\n--- Ticket " + (t + 1) + " ---");
            int floorChoice;
            while (true) {
                System.out.print("Which floor (1/2)? ");
                try {
                    floorChoice = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Invalid");
                    continue;
                }
                if (floorChoice == 1 || floorChoice == 2) break;
                System.out.println("Invalid floor!");
            }
            bus.showSeats(key, floorChoice);

            String pname;
            String pgender = "";
            String pfin;
            int page = 0;
            double luggage;
            int seatChoice;

            while (true) {
                System.out.print("Bus_booking_project.Passenger name: ");
                pname = sc.nextLine().trim();


                User matched = findUserByName(pname);
                boolean autoAssignedChild = false;
                if (matched != null && matched.age < 18) {
                    pgender = "U";
                    autoAssignedChild = true;
                    page = matched.age;
                    System.out.println("🔔 Qeydiyyatda eyni adla istifadəçi 18 yaşdan kiçik olduğu üçün gender avtomatik olaraq 'U' seçildi. Yaş soruşulmur.");
                } else {
                    while (true) {
                        System.out.print("Gender (K/Q/U): ");
                        pgender = sc.nextLine().trim().toUpperCase();
                        if (pgender.equals("K") || pgender.equals("Q") || pgender.equals("U")) break;
                        System.out.println("Invalid gender.");
                    }
                }

                System.out.print("FIN: ");
                pfin = sc.nextLine().trim();

                if (!autoAssignedChild) {
                    System.out.print("Age: ");
                    try {
                        page = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid age.");
                        continue;
                    }
                }

                System.out.print("Luggage weight (kg): ");
                try {
                    luggage = Double.parseDouble(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Invalid luggage.");
                    continue;
                }

                System.out.print("Select seat (" + ((floorChoice == 1) ? "1-20" : "21-40") + "): ");
                try {
                    seatChoice = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Invalid seat number.");
                    continue;
                }

                if (bus.reserveSeat(key, seatChoice, pgender)) {
                    SeatDAO.saveSeat(routeName, new SimpleDateFormat("yyyy-MM-dd").format(travelDate), selectedInterval, seatChoice, pgender);

                    double price = payment.calculatePayment(routeMap.get(routeName), true, luggage);
                    payment.processPayment(sc, price, user);
                    Passenger p = new Passenger(pname, page, pgender, luggage, pfin);
                    Booking b = new Booking(p, 0, travelDate, price, routeName, selectedInterval);
                    bookingManager.addBooking(b);
                    printReceipt(b);
                    break;
                } else {
                    System.out.println("❌ Seat occupied or invalid. Try again.");
                }
            }
        }
    }

    public static void handleCityTravel(User user) {
        System.out.println("\nCity Travel Intervals:");
        for (int i = 0; i < cityIntervals.length; i++) System.out.println((i + 1) + ". " + cityIntervals[i]);
        System.out.print("Choose interval: ");
        int intervalChoice;
        try {
            intervalChoice = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid");
            return;
        }
        if (intervalChoice < 1 || intervalChoice > cityIntervals.length) {
            System.out.println("Invalid interval");
            return;
        }
        String selectedInterval = cityIntervals[intervalChoice - 1];

        System.out.print("Enter city name for travel: ");
        String travelCity = sc.nextLine().trim();
        CityCard usable = null;
        for (CityCard c : user.cityCards) {
            if (c.isUsableFor(travelCity) && c.monthsLeft > 0) {
                usable = c;
                break;
            }
        }

        double price = 0.0;
        if (usable != null) {
            System.out.println("✅ You have a usable City Card for " + travelCity + ". This trip will be free and one month will be consumed.");
            usable.useOneMonth();
            price = 0.0;

            Passenger p = new Passenger(user.name, user.age, user.gender, 0, user.fin);
            Booking b = new Booking(p, 0, new Date(), price, "City Travel (" + travelCity + ")", selectedInterval);
            bookingManager.addBooking(b);
            printReceipt(b);
            return;
        } else {
            System.out.print("Bus_booking_project.Passenger name: ");
            String pname = sc.nextLine().trim();


            User matched = findUserByName(pname);
            boolean autoAssignedChild = false;
            String pgender = "";
            int page = 0;
            if (matched != null && matched.age < 18) {
                pgender = "U";
                autoAssignedChild = true;
                page = matched.age;
                System.out.println("🔔 Qeydiyyatda eyni adla istifadəçi 18 yaşdan kiçik olduğu üçün gender avtomatik olaraq 'U' seçildi. Yaş soruşulmur.");
            } else {
                while (true) {
                    System.out.print("Gender (K/Q/U): ");
                    pgender = sc.nextLine().trim().toUpperCase();
                    if (pgender.equals("K") || pgender.equals("Q") || pgender.equals("U")) break;
                    System.out.println("Invalid gender.");
                }
            }

            System.out.print("FIN: ");
            String pfin = sc.nextLine().trim();

            if (!autoAssignedChild) {
                System.out.print("Age: ");
                try {
                    page = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Invalid age.");
                    return;
                }
            }

            System.out.print("Luggage weight (kg): ");
            double luggage;
            try {
                luggage = Double.parseDouble(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid luggage.");
                return;
            }

            price = payment.calculatePayment(10, true, luggage);
            payment.processPayment(sc, price, user);
            Passenger p = new Passenger(pname, page, pgender, luggage, pfin);
            Booking b = new Booking(p, 0, new Date(), price, "City Travel (" + travelCity + ")", selectedInterval);
            bookingManager.addBooking(b);
            printReceipt(b);
            return;
        }
    }

    public static void handleCityCard(User user) {
        System.out.println("\nCity Card Options:");
        System.out.println("1. GLOBAL (usable in all cities) - 60 AZN/month");
        System.out.println("2. Custom cities (base 20 AZN/month for first city, +1 AZN for each additional city)");
        System.out.print("Choose option: ");
        String opt = sc.nextLine().trim();
        if (opt.equals("1")) {
            System.out.print("How many months? ");
            int months;
            try {
                months = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid");
                return;
            }
            double total = Payment.calculateCityCardTotal(0, months, true);
            payment.processPayment(sc, total, user);
            CityCard cc = new CityCard(Arrays.asList("GLOBAL"), months);
            user.cityCards.add(cc);
            CityCardDAO.saveCityCard(user.fin, cc);
            System.out.println("✅ GLOBAL City Card purchased for " + months + " months. Total: " + total + " AZN");
        } else if (opt.equals("2")) {
            System.out.print("How many cities? ");
            int cnt;
            try {
                cnt = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid");
                return;
            }
            List<String> cities = new ArrayList<>();
            for (int i = 0; i < cnt; i++) {
                System.out.print("Enter city " + (i + 1) + " name: ");
                cities.add(sc.nextLine().trim());
            }
            System.out.print("How many months? ");
            int months;
            try {
                months = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid");
                return;
            }
            double total = Payment.calculateCityCardTotal(cities.size(), months, false);
            payment.processPayment(sc, total, user);
            CityCard cc = new CityCard(cities, months);
            user.cityCards.add(cc);
            CityCardDAO.saveCityCard(user.fin, cc);
            System.out.println("✅ City Card purchased for " + cities + " for " + months + " months. Total: " + total + " AZN");
        } else {
            System.out.println("Invalid option");
        }
    }

    public static void printReceipt(Booking b) {
        System.out.println("\n--- RECEIPT ---");
        System.out.println("Bus_booking_project.Passenger: " + b.getPassenger().getName());
        System.out.println("Route: " + b.getRouteName());
        System.out.println("Interval: " + b.getInterval());
        System.out.println("Price: " + b.getPrice() + " AZN");
        System.out.println("-----------------\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipt.txt", true))) {
            writer.write("Bus_booking_project.Passenger: " + b.getPassenger().getName() + " | Route: " + b.getRouteName() + " | Interval: " + b.getInterval() + " | Price: " + b.getPrice() + " AZN\n");
        } catch (Exception e) {
            System.out.println("Error writing receipt: " + e.getMessage());
        }
    }

}
