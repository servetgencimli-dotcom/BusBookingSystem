package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import project.User;
import project.BusBookingSystem;
import project.MainApp;

/**
 * UserMenu.fxml üçün Controller
 */
public class UserMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button globalTravelButton;

    @FXML
    private Button domesticTravelButton;

    @FXML
    private Button cityTravelButton;

    @FXML
    private Button buyCityCardButton;

    @FXML
    private Button updateProfileButton;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Xoş gəldiniz, " + currentUser.name + "!");
        }
    }

    @FXML
    private void handleGlobalTravel(ActionEvent event) {
        loadSceneWithUser("/fxml/Booking.fxml", "Global Booking");
    }

    @FXML
    private void handleDomesticTravel(ActionEvent event) {
        loadSceneWithUser("/fxml/Booking.fxml", "Domestic Booking");
    }

    @FXML
    private void handleCityTravel(ActionEvent event) {
        loadSceneWithUser("/fxml/CityTravel.fxml", "City Travel");
    }

    @FXML
    private void handleBuyCityCard(ActionEvent event) {
        loadSceneWithUser("/fxml/CityCard.fxml", "Şəhər Kartı Alış Sistemi");
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        MainApp.loadScene("/fxml/UpdateProfile.fxml", "Profil Yenileme");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        MainApp.loadScene("/fxml/MainLogin.fxml", "Avtobus Rezervasiya Sistemi");
    }

    /**
     * User məlumatını controller-ə ötürən metod
     */
    private void loadSceneWithUser(String fxmlPath, String title) {
        try {
            User currentUser = SessionManager.getCurrentUser();

            if (currentUser == null) {
                showAlert("Xəta", "İstifadəçi məlumatı tapılmadı!");
                return;
            }

            // FXML yüklə
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Controller-i əldə et və user-i göndər
            Object controller = loader.getController();

            if (controller instanceof BookingController) {
                ((BookingController) controller).setUser(currentUser);
            } else if (controller instanceof CityTravelController) {
                ((CityTravelController) controller).setUser(currentUser);
            } else if (controller instanceof CityCardController) {
                ((CityCardController) controller).setUser(currentUser);
            }

            // Yeni Stage aç
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showAlert("Xəta", "Səhifə yüklənə bilmədi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}