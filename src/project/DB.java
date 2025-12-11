package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static final String URL = "jdbc:sqlite:C:/sss/busbooking.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            System.out.println("SQLite driver not found: " + e.getMessage());
        }
        createTables();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void createTables() {
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "gender TEXT," +
                    "age INTEGER," +
                    "fin TEXT," +
                    "series TEXT," +
                    "password TEXT," +
                    "card TEXT," +
                    "expiry TEXT," +
                    "cvc TEXT," +
                    "isAdmin INTEGER" +
                    ")");


            st.execute("CREATE TABLE IF NOT EXISTS bookings(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "passengerName TEXT," +
                    "passengerGender TEXT," +
                    "passengerAge INTEGER," +
                    "luggage REAL," +
                    "busNo INTEGER," +
                    "routeName TEXT," +
                    "intervalTime TEXT," +
                    "travelDate TEXT," +
                    "price REAL," +
                    "userFin TEXT" +
                    ")");


            st.execute("CREATE TABLE IF NOT EXISTS citycards(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userFin TEXT," +
                    "cities TEXT," +
                    "monthsLeft INTEGER" +
                    ")");


            st.execute("CREATE TABLE IF NOT EXISTS seats(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "route TEXT," +
                    "date TEXT," +
                    "intervalTime TEXT," +
                    "seatNo INTEGER," +
                    "gender TEXT" +
                    ")");


            st.execute("CREATE TABLE IF NOT EXISTS routes(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "routeName TEXT UNIQUE," +
                    "distance INTEGER" +
                    ")");

        } catch (Exception e) {
            System.out.println("createTables error: " + e.getMessage());
        }
    }
}
