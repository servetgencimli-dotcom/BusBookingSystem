package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import project.BusBookingSystem;
import project.MainApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdminPromoController {

    @FXML private TextField codeNameField;
    @FXML private Spinner<Integer> percentSpinner;
    @FXML private TextArea descriptionField;
    @FXML private ListView<String> promoListView;
    @FXML private Label totalCodesLabel;
    @FXML private Label usageCountLabel;

    private List<PromoCodeInfo> promoCodes = new ArrayList<>();

    @FXML
    public void initialize() {
        setupSpinner();
        loadPromoCodes();
        updateStatistics();
    }

    private void setupSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10, 5);
        percentSpinner.setValueFactory(valueFactory);
    }

    @FXML
    private void handleCreatePromo() {
        String code = codeNameField.getText().trim().toUpperCase();

        if (code.isEmpty()) {
            showAlert("❌ Xəta", "Promo kod adı daxil edin!");
            return;
        }

        int percent = percentSpinner.getValue();
        String description = descriptionField.getText().trim();

        // Promo kodu yarat
        BusBookingSystem.discountManager.createPromoCode(code, percent);

        // Listi yenilə
        PromoCodeInfo info = new PromoCodeInfo(code, percent, description);
        promoCodes.add(info);

        loadPromoCodes();
        updateStatistics();

        // Formanı təmizlə
        codeNameField.clear();
        percentSpinner.getValueFactory().setValue(10);
        descriptionField.clear();

        showAlert("✅ Uğurlu!",
                "Promo kod yaradıldı!\n\nKod: " + code + "\nEndirim: " + percent + "%");
    }

    @FXML
    private void handleQuickPromo1() {
        String code = "WELCOME" + new Random().nextInt(1000);
        BusBookingSystem.discountManager.createPromoCode(code, 15);
        promoCodes.add(new PromoCodeInfo(code, 15, "Yeni istifadəçi kampaniyası"));
        loadPromoCodes();
        updateStatistics();
        showAlert("✅ Yaradıldı", "Kod: " + code + " (15% endirim)");
    }

    @FXML
    private void handleQuickPromo2() {
        String code = "VIP" + new Random().nextInt(1000);
        BusBookingSystem.discountManager.createPromoCode(code, 20);
        promoCodes.add(new PromoCodeInfo(code, 20, "VIP üzvlər üçün"));
        loadPromoCodes();
        updateStatistics();
        showAlert("✅ Yaradıldı", "Kod: " + code + " (20% endirim)");
    }

    @FXML
    private void handleQuickPromo3() {
        String code = "FLASH" + new Random().nextInt(1000);
        BusBookingSystem.discountManager.createPromoCode(code, 25);
        promoCodes.add(new PromoCodeInfo(code, 25, "Flash sale kampaniyası"));
        loadPromoCodes();
        updateStatistics();
        showAlert("✅ Yaradıldı", "Kod: " + code + " (25% endirim)");
    }

    private void loadPromoCodes() {
        List<String> items = new ArrayList<>();

        for (PromoCodeInfo info : promoCodes) {
            items.add(String.format("🎟️ %s - %d%% OFF | %s",
                    info.code, info.percent,
                    info.description.isEmpty() ? "Təsvir yoxdur" : info.description));
        }

        promoListView.setItems(FXCollections.observableArrayList(items));
    }

    private void updateStatistics() {
        totalCodesLabel.setText(String.valueOf(promoCodes.size()));
        usageCountLabel.setText("0"); // TODO: Real istifadə sayı
    }

    @FXML
    private void handleRefresh() {
        loadPromoCodes();
        updateStatistics();
        showAlert("🔄 Yeniləndi", "Məlumatlar yeniləndi!");
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("/fxml/AdminMenu.fxml", "Admin Panel");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Köməkçi sinif
    private static class PromoCodeInfo {
        String code;
        int percent;
        String description;

        PromoCodeInfo(String code, int percent, String description) {
            this.code = code;
            this.percent = percent;
            this.description = description;
        }
    }
}