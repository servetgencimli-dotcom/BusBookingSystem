package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import project.*;

import java.util.Arrays;
import java.util.List;

public class LoyaltyPanelController {

    @FXML private Label pointsLabel;
    @FXML private Label tierLabel;

    @FXML private TableView<Discount> discountsTable;
    @FXML private TableColumn<Discount, Integer> discountPercentCol;
    @FXML private TableColumn<Discount, String> discountReasonCol;

    @FXML private ListView<String> historyListView;

    private User currentUser;

    @FXML
    public void initialize() {
        // Test istifadəçisi yarat (real halda session-dan gələcək)
        currentUser = findCurrentUser();

        if (currentUser != null) {
            setupDiscountsTable();
            refreshData();
        }
    }

    private void setupDiscountsTable() {
        discountPercentCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().percentage
                )
        );
        discountReasonCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().reason
                )
        );
    }

    private void refreshData() {
        if (currentUser == null) return;

        // Xalları göstər
        int points = BusBookingSystem.loyaltyProgram.getPoints(currentUser.fin);
        pointsLabel.setText(String.valueOf(points));

        // Tier göstər
        String tier = BusBookingSystem.loyaltyProgram.getTier(currentUser.fin);
        tierLabel.setText(tier);

        // Aktiv endirimlər
        List<Discount> discounts = BusBookingSystem.discountManager.getActiveDiscounts(currentUser.fin);
        discountsTable.setItems(FXCollections.observableArrayList(discounts));

        // Tarixçə
        List<String> history = Arrays.asList(
                "✅ +50 xal - Rezervasiya (Baku-Ganja)",
                "✅ +30 xal - Rezervasiya (City Travel)",
                "❌ -100 xal - 10% Endirim istifadə edildi",
                "✅ +75 xal - Rezervasiya (Baku-Turkey City 1)"
        );
        historyListView.setItems(FXCollections.observableArrayList(history));
    }

    @FXML
    private void redeemDiscount10() {
        redeemReward(100, 10, "10% Loyalty Discount");
    }

    @FXML
    private void redeemDiscount25() {
        redeemReward(300, 25, "25% Premium Discount");
    }

    @FXML
    private void redeemCityCard() {
        if (currentUser == null) return;

        int points = BusBookingSystem.loyaltyProgram.getPoints(currentUser.fin);
        if (points >= 200) {
            BusBookingSystem.loyaltyProgram.redeemPoints(currentUser.fin, 200);

            CityCard cc = new CityCard(Arrays.asList("GLOBAL"), 1);
            currentUser.cityCards.add(cc);
            CityCardDAO.saveCityCard(currentUser.fin, cc);

            showAlert("✅ Təbriklər!", "Pulsuz şəhər kartı əldə etdiniz!");
            refreshData();
        } else {
            showAlert("❌ Kifayət qədər xal yoxdur", "Bu mükafat üçün 200 xal lazımdır.");
        }
    }

    @FXML
    private void redeemVIP() {
        if (currentUser == null) return;

        int points = BusBookingSystem.loyaltyProgram.getPoints(currentUser.fin);
        if (points >= 500) {
            BusBookingSystem.loyaltyProgram.redeemPoints(currentUser.fin, 500);
            BusBookingSystem.discountManager.addDiscount(currentUser.fin, 15, "VIP Status - Permanent");

            showAlert("✅ VIP Status!", "Siz VIP istifadəçi oldunuz! Bütün səfərlərə 15% endirim!");
            refreshData();
        } else {
            showAlert("❌ Kifayət qədər xal yoxdur", "Bu mükafat üçün 500 xal lazımdır.");
        }
    }

    private void redeemReward(int requiredPoints, int discountPercent, String reason) {
        if (currentUser == null) return;

        int points = BusBookingSystem.loyaltyProgram.getPoints(currentUser.fin);
        if (points >= requiredPoints) {
            BusBookingSystem.loyaltyProgram.redeemPoints(currentUser.fin, requiredPoints);
            BusBookingSystem.discountManager.addDiscount(currentUser.fin, discountPercent, reason);

            showAlert("✅ Təbriklər!", discountPercent + "% endirim aktivləşdirildi!");
            refreshData();
        } else {
            showAlert("❌ Kifayət qədər xal yoxdur",
                    "Bu mükafat üçün " + requiredPoints + " xal lazımdır. Sizin: " + points);
        }
    }

    @FXML
    private void handleRefresh() {
        refreshData();
        showAlert("✅ Yeniləndi", "Məlumatlar yeniləndi!");
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("/fxml/UserMenu.fxml", "User Menu");
    }

    private User findCurrentUser() {
        // Real tətbiqdə session-dan istifadəçi gələcək
        // İndilik ilk istifadəçini götürürük
        List<User> users = UserDAO.loadAllUsers();
        for (User u : users) {
            if (!u.isAdmin) {
                return u;
            }
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}