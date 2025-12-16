package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import project.MainApp;

public class AdminMenuController {

    @FXML
    private void handleGlobalRoutes(MouseEvent event) {
        MainApp.loadScene("/fxml/ManageRoutes.fxml", "Global Marsurutlarini idare et");
    }

    @FXML
    private void handleDomesticRoutes(MouseEvent event) {
        MainApp.loadScene("/fxml/ManageRoutes.fxml", "Lokal Marsurutlarini idare et");
    }

    @FXML
    private void handleCityRoutes(MouseEvent event) {
        showComingSoon("Şəhər Marşrutları");
        MainApp.loadScene("/fxml/ManageRoutes.fxml", "Seher Marsurutlarini idare et");
    }

    @FXML
    private void handleBookings(MouseEvent event) {
        MainApp.loadScene("/fxml/BookingsView.fxml", "Rezervasiyalar");
    }

    @FXML
    private void handleUsers(MouseEvent event) {
        MainApp.loadScene("/fxml/UsersView.fxml", "İstifadəçilər");
    }

    @FXML
    private void handleAnalytics(MouseEvent event) {
        MainApp.loadScene("/fxml/AnalyticsDashboard.fxml", "Analitika");
    }

    @FXML
    private void handleDiscounts(MouseEvent event) {
        MainApp.loadScene("/fxml/AdminPromoView.fxml", "Promo İdarəsi");
    }

    @FXML
    private void handleRatings(MouseEvent event) {
        showRatingsInfo();
    }

    @FXML
    private void handleNotifications(MouseEvent event) {
        showNotificationDialog();
    }

    @FXML
    private void handleRefreshStats() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✅ Yeniləndi");
        alert.setHeaderText(null);
        alert.setContentText("Statistikalar yeniləndi!");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Çıxış");
        confirm.setHeaderText("Çıxmaq istədiyinizdən əminsiniz?");
        confirm.setContentText("Admin paneldən çıxacaqsınız.");

        if (confirm.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            MainApp.loadScene("/fxml/UserLogin.fxml", "Ana Səhifə");
        }
    }

    private void showDiscountDialog() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Endirim Yaradın");
        dialog.setHeaderText("Yeni Promo Kod");
        dialog.setContentText("Kod adı:");

        dialog.showAndWait().ifPresent(code -> {
            javafx.scene.control.TextInputDialog percentDialog = new javafx.scene.control.TextInputDialog();
            percentDialog.setTitle("Endirim Faizi");
            percentDialog.setHeaderText("Endirim miqdarı");
            percentDialog.setContentText("Faiz (%):");

            percentDialog.showAndWait().ifPresent(percent -> {
                try {
                    int p = Integer.parseInt(percent);
                    project.BusBookingSystem.discountManager.createPromoCode(code, p);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("✅ Uğurlu");
                    success.setHeaderText(null);
                    success.setContentText("Promo kod '" + code + "' yaradıldı! (" + p + "% endirim)");
                    success.showAndWait();
                } catch (NumberFormatException e) {
                    showAlert("❌ Xəta", "Yanlış faiz dəyəri!");
                }
            });
        });
    }

    private void showRatingsInfo() {
        project.BusBookingSystem.ratingSystem.displayAllRatings();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("⭐ Reytinqlər");
        alert.setHeaderText("Reytinq Sistemi");
        alert.setContentText("Konsola yoxlayın - bütün reytinqlər göstərilir.");
        alert.showAndWait();
    }

    private void showNotificationDialog() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Toplu Bildiriş");
        dialog.setHeaderText("Bütün istifadəçilərə mesaj göndərin");
        dialog.setContentText("Mesaj:");

        dialog.showAndWait().ifPresent(message -> {
            for (project.User u : project.BusBookingSystem.users) {
                if (!u.isAdmin) {
                    project.BusBookingSystem.notificationSystem.sendNotification(u.fin, message);
                }
            }

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("✅ Göndərildi");
            success.setHeaderText(null);
            success.setContentText("Bildiriş " + project.BusBookingSystem.users.size() + " istifadəçiyə göndərildi!");
            success.showAndWait();
        });
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🚧 Tezliklə");
        alert.setHeaderText(feature);
        alert.setContentText("Bu funksiya hazırlanır...");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}