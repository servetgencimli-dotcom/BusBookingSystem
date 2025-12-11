package controller;
import project.User;


public class SessionManager {

    public static User currentUser = null;

    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("✅ Session yaradıldı: " + (user != null ? user.name : "null"));
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin;
    }

    public static void clearSession() {
        if (currentUser != null) {
            System.out.println("🚪 Session bağlandı: " + currentUser.name);
        }
        currentUser = null;
    }

    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.name : "Qonaq";
    }

    public static String getCurrentUserFin() {
        return currentUser != null ? currentUser.fin : null;
    }
}