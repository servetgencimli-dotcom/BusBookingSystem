package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import project.*;

import java.util.*;

public class UserMenuController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;
    @FXML private Label pointsLabel;
    @FXML private Label tierLabel;
    @FXML private Label tripsLabel;
    @FXML private Label notifBadge;

    private User currentUser;

    @FXML
    public void initialize() {
        // Test istifadəçisi yüklə (real halda session-dan gələcək)
        currentUser = findCurrentUser();

        if (currentUser != null) {
            updateUserInfo();
        }
    }

    private void updateUserInfo() {
        welcomeLabel.setText("Xoş Gəldiniz, " + currentUser.name + "!");
        userInfoLabel.setText("FIN: " + currentUser.fin + " | " + currentUser.age + " yaş");

        // Loyalty xalları
        int points = BusBookingSystem.loyaltyProgram.getPoints(currentUser.fin);
        pointsLabel.setText(String.valueOf(points));

        // Tier
        String tier = BusBookingSystem.loyaltyProgram.getTier(currentUser.fin);
        tierLabel.setText(tier);

        // Səfərlər
        List<Booking> trips = BusBookingSystem.bookingManager.getBookingsByUser(currentUser.fin);
        tripsLabel.setText(String.valueOf(trips.size()));

        // Bildirişlər
        List<String> notifs = BusBookingSystem.notificationSystem.getNotifications(currentUser.fin);
        notifBadge.setText(notifs.size() + " yeni");
    }

    @FXML
    private void handleGlobalTravel(MouseEvent event) {
        MainApp.loadScene("/fxml/Booking.fxml", "Qlobal Səfər");
    }

    @FXML
    private void handleDomesticTravel(MouseEvent event) {
        MainApp.loadScene("/fxml/Booking.fxml", "Daxili Səfər");
    }

    @FXML
    private void handleCityTravel(MouseEvent event) {
        MainApp.loadScene("/fxml/CityTravel.fxml", "Şəhər Səfəri");
    }

    @FXML
    private void handleBuyCityCard(MouseEvent event) {
        MainApp.loadScene("/fxml/CityCard.fxml", "Şəhər Kartı Al");
    }

    @FXML
    private void handleMyBookings(MouseEvent event) {
        MainApp.loadScene("/fxml/MyBookingsView.fxml", "Rezervasiyalarım");
    }

    @FXML
    private void handleProfile(MouseEvent event) {
        MainApp.loadScene("/fxml/ProfileWiew.fxml", "Profil Tənzimləmələri");
    }

    @FXML
    private void handleLoyaltyRewards(MouseEvent event) {
        MainApp.loadScene("/fxml/LoyaltyPanel.fxml", "Mükafatlar");
    }

    @FXML
    private void handlePromoCode(MouseEvent event) {
        MainApp.loadScene("/fxml/PromoPanelView.fxml", "Promo Kodlar");
    }

    @FXML
    private void handleNotifications(MouseEvent event) {
        List<String> notifs = BusBookingSystem.notificationSystem.getNotifications(currentUser.fin);

        if (notifs.isEmpty()) {
            showAlert("🔔 Bildirişlər", "Yeni bildiriş yoxdur.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < notifs.size(); i++) {
            sb.append((i + 1)).append(". ").append(notifs.get(i)).append("\n\n");
        }

        showAlert("🔔 Bildirişlər (" + notifs.size() + ")", sb.toString());
        updateUserInfo(); // Badge-i yenilə
    }

    @FXML
    private void handleRateTrip() {
        List<Booking> myBookings = BusBookingSystem.bookingManager.getBookingsByUser(currentUser.fin);

        if (myBookings.isEmpty()) {
            showAlert("⭐ Qiymətləndirmə", "Qiymətləndirmək üçün səfəriniz yoxdur.");
            return;
        }

        // Marşrut seçimi - Java 8 uyğun
        List<String> routes = new ArrayList<>();
        for (Booking b : myBookings) {
            if (!routes.contains(b.getRouteName())) {
                routes.add(b.getRouteName());
            }
        }

        ChoiceDialog<String> routeDialog = new ChoiceDialog<>(routes.get(0), routes);
        routeDialog.setTitle("Marşrut Seç");
        routeDialog.setHeaderText("Hansı səfəri qiymətləndirmək istəyirsiniz?");
        routeDialog.setContentText("Marşrut:");

        Optional<String> routeResult = routeDialog.showAndWait();
        routeResult.ifPresent(route -> {
            // Reytinq seçimi
            List<Integer> ratings = Arrays.asList(1, 2, 3, 4, 5);
            ChoiceDialog<Integer> ratingDialog = new ChoiceDialog<>(5, ratings);
            ratingDialog.setTitle("Reytinq Ver");
            ratingDialog.setHeaderText("Səfəri qiymətləndirin");
            ratingDialog.setContentText("Ulduz (1-5):");

            Optional<Integer> ratingResult = ratingDialog.showAndWait();
            ratingResult.ifPresent(rating -> {
                // Şərh
                TextInputDialog commentDialog = new TextInputDialog();
                commentDialog.setTitle("Şərh");
                commentDialog.setHeaderText("Rəyinizi yazın (ixtiyari)");
                commentDialog.setContentText("Şərh:");

                String comment = commentDialog.showAndWait().orElse("");

                BusBookingSystem.ratingSystem.addRating(route, currentUser.fin, rating, comment);

                // Bonus xal əlavə et
                BusBookingSystem.loyaltyProgram.addPoints(currentUser.fin, 10);

                showAlert("✅ Təşəkkürlər!",
                        "Rəyiniz qəbul edildi!\n+10 bonus xal qazandınız!");
                updateUserInfo();
            });
        });
    }

    @FXML
    private void handleRefresh() {
        updateUserInfo();
        showAlert("🔄 Yeniləndi", "Məlumatlar yeniləndi!");
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Çıxış");
        confirm.setHeaderText("Çıxmaq istədiyinizdən əminsiniz?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            MainApp.loadScene("/fxml/MainLogin.fxml", "Ana Səhifə");
        }
    }

    private User findCurrentUser() {
        List<User> users = UserDAO.loadAllUsers();
        for (User u : users) {
            if (!u.isAdmin) {
                return u;
            }
        }
        return null;
    }

    private void showComingSoon(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🚧 " + title);
        alert.setHeaderText("Tezliklə...");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}