package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Database initialization
        BusBookingSystem.seedRoutes();
        BusBookingSystem.users.addAll(UserDAO.loadAllUsers());
        BusBookingSystem.loadBookingsFromDB();

        // Load initial scene
        loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
        primaryStage.show();
    }

    public static void loadScene(String fxmlPath, String title) {
        try {
            System.out.println("🔄 Attempting to load: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);

            System.out.println("✅ Successfully loaded: " + fxmlPath);

        } catch (Exception e) {
            System.out.println("❌ FXML yüklənə bilmədi: " + fxmlPath);
            System.out.println("❌ Error details:");
            e.printStackTrace();

            // Check if file exists
            if (MainApp.class.getResource(fxmlPath) == null) {
                System.out.println("❌ File not found at path: " + fxmlPath);
                System.out.println("💡 Check if file exists in: src/main/resources" + fxmlPath);
            }
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
}
}