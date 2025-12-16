package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static final String DB_URL = "jdbc:sqlite:C:/sss/bus_booking.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
            return null;
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Users table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "gender TEXT, " +
                            "age INTEGER, " +
                            "fin TEXT UNIQUE NOT NULL, " +
                            "series TEXT, " +
                            "password TEXT NOT NULL, " +
                            "card TEXT, " +
                            "expiry TEXT, " +
                            "cvc TEXT, " +
                            "isAdmin INTEGER DEFAULT 0)"
            );

            // Routes table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS routes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "routeName TEXT UNIQUE NOT NULL, " +
                            "distance INTEGER)"
            );

            // Bookings table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "passengerName TEXT NOT NULL, " +
                            "passengerAge INTEGER, " +
                            "passengerGender TEXT, " +
                            "luggage REAL, " +
                            "userFin TEXT, " +
                            "busNo INTEGER, " +
                            "travelDate TEXT, " +
                            "price REAL, " +
                            "routeName TEXT, " +
                            "intervalTime TEXT)"
            );

            // Seats table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS seats (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "routeName TEXT NOT NULL, " +
                            "travelDate TEXT NOT NULL, " +
                            "intervalTime TEXT NOT NULL, " +
                            "seatNumber INTEGER NOT NULL, " +
                            "gender TEXT, " +
                            "UNIQUE(routeName, travelDate, intervalTime, seatNumber))"
            );

            // City Cards table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS cityCards (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "userFin TEXT NOT NULL, " +
                            "cities TEXT NOT NULL, " +
                            "monthsLeft INTEGER)"
            );

            // City Intervals table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS cityIntervals (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "intervalTime TEXT NOT NULL)"
            );

            // Insert default city intervals if not exist
            stmt.execute(
                    "INSERT OR IGNORE INTO cityIntervals (id, intervalTime) VALUES " +
                            "(1, '06:00-07:00'), " +
                            "(2, '10:00-11:00'), " +
                            "(3, '14:00-15:00'), " +
                            "(4, '18:00-19:00'), " +
                            "(5, '22:00-23:00')"
            );

            System.out.println("✅ Database initialized successfully");

        } catch (SQLException e) {
            System.out.println("❌ Error initializing database: " + e.getMessage());
        }
    }
}