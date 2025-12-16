package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import project.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class PromoPanelController {

    @FXML private TextField promoCodeField;
    @FXML private Label promoResultLabel;
    @FXML private TableView<Discount> discountsTable;
    @FXML private TableColumn<Discount, Integer> percentCol;
    @FXML private TableColumn<Discount, String> reasonCol;
    @FXML private TableColumn<Discount, String> dateCol;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = findCurrentUser();

        setupTable();
        loadDiscounts();
    }

    private void setupTable() {
        percentCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().percentage
                )
        );

        reasonCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().reason
                )
        );

        dateCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cell.getValue().createdDate)
                )
        );
    }

    private void loadDiscounts() {
        if (currentUser == null) return;

        List<Discount> discounts = BusBookingSystem.discountManager.getActiveDiscounts(currentUser.fin);
        discountsTable.setItems(FXCollections.observableArrayList(discounts));
    }

    @FXML
    private void handleApplyPromo() {
        String code = promoCodeField.getText().trim();

        if (code.isEmpty()) {
            promoResultLabel.setText("❌ Promo kod daxil edin!");
            promoResultLabel.setStyle("-fx-text-fill: #FCA5A5;");
            return;
        }

        boolean success = BusBookingSystem.discountManager.applyPromoCode(code, currentUser.fin);

        if (success) {
            promoResultLabel.setText("✅ Promo kod aktivləşdirildi! Endirim tətbiq olundu.");
            promoResultLabel.setStyle("-fx-text-fill: #86EFAC;");
            promoCodeField.clear();
            loadDiscounts(); // Cədvəli yenilə

            // Alert göstər
            showAlert("🎉 Təbriklər!", "Promo kod uğurla aktivləşdirildi!\nGələn alışınızda endirim tətbiq olunacaq.");
        } else {
            promoResultLabel.setText("❌ Yanlış və ya istifadə olunmuş promo kod!");
            promoResultLabel.setStyle("-fx-text-fill: #FCA5A5;");
        }
    }

    @FXML
    private void handleCopyCode1() {
        copyToClipboard("WELCOME2025");
        showAlert("📋 Kopyalandı", "Promo kod: WELCOME2025");
    }

    @FXML
    private void handleCopyCode2() {
        copyToClipboard("VIP20");
        showAlert("📋 Kopyalandı", "Promo kod: VIP20");
    }

    @FXML
    private void handleCopyCode3() {
        copyToClipboard("FAST10");
        showAlert("📋 Kopyalandı", "Promo kod: FAST10");
    }

    @FXML
    private void handleRefresh() {
        loadDiscounts();
        promoResultLabel.setText("🔄 Yeniləndi!");
        promoResultLabel.setStyle("-fx-text-fill: #93C5FD;");
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("/fxml/UserMenu.fxml", "İstifadəçi Menyu");
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}