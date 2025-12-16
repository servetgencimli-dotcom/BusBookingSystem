package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import project.MainApp;
import project.User;
import project.UserDAO;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class UsersViewController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> genderCol;
    @FXML private TableColumn<User, Integer> ageCol;
    @FXML private TableColumn<User, String> finCol;
    @FXML private TableColumn<User, String> idSeriesCol;
    @FXML private TableColumn<User, Boolean> adminCol;

    @FXML private Label totalUsersLabel;
    @FXML private Label adminCountLabel;
    @FXML private Label regularUsersLabel;
    @FXML private TextField searchField;

    private ObservableList<User> allUsers;
    private FilteredList<User> filteredUsers;

    @FXML
    public void initialize() {
        // Sütun bağlantıları
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        finCol.setCellValueFactory(new PropertyValueFactory<>("fin"));
        idSeriesCol.setCellValueFactory(new PropertyValueFactory<>("idSeries"));
        adminCol.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        // Admin sütunu üçün xüsusi formatlama
        adminCol.setCellFactory(col -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean isAdmin, boolean empty) {
                super.updateItem(isAdmin, empty);
                if (empty || isAdmin == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(isAdmin ? "✅ Bəli" : "❌ Xeyr");
                    setStyle(isAdmin ? "-fx-text-fill: #10B981;" : "-fx-text-fill: #6B7280;");
                }
            }
        });

        // Məlumatları yüklə
        refreshTable();

        // Axtarış funksiyası
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers(newVal));
        }
    }

    private void refreshTable() {
        allUsers = FXCollections.observableArrayList(UserDAO.loadAllUsers());
        filteredUsers = new FilteredList<>(allUsers, p -> true);
        usersTable.setItems(filteredUsers);

        updateStatistics();

        System.out.println("✅ " + allUsers.size() + " istifadəçi yükləndi");
    }

    private void updateStatistics() {
        int total = allUsers.size();
        int adminCount = (int) allUsers.stream().filter(u -> u.getIsAdmin()).count();
        int regularCount = total - adminCount;

        if (totalUsersLabel != null) totalUsersLabel.setText(String.valueOf(total));
        if (adminCountLabel != null) adminCountLabel.setText(String.valueOf(adminCount));
        if (regularUsersLabel != null) regularUsersLabel.setText(String.valueOf(regularCount));
    }

    private void filterUsers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredUsers.setPredicate(p -> true);
        } else {
            String lowerCase = searchText.toLowerCase();
            filteredUsers.setPredicate(user ->
                    user.getName().toLowerCase().contains(lowerCase) ||
                            user.getFin().toLowerCase().contains(lowerCase)
            );
        }
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
        if (searchField != null) searchField.clear();
        showAlert("✅ Yeniləndi", "İstifadəçi siyahısı yeniləndi!");
    }

    @FXML
    private void handleExport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users_export.txt"))) {
            writer.write("=== İSTİFADƏÇİ HESABATI ===\n\n");
            writer.write("Ümumi istifadəçi sayı: " + allUsers.size() + "\n");
            writer.write("Admin sayı: " + allUsers.stream().filter(u -> u.getIsAdmin()).count() + "\n\n");
            writer.write("--------------------------------------------------------\n");

            for (User u : allUsers) {
                writer.write(String.format("Ad: %s | FIN: %s | Yaş: %d | Cins: %s | Admin: %s\n",
                        u.getName(), u.getFin(), u.getAge(), u.getGender(),
                        u.getIsAdmin() ? "Bəli" : "Xeyr"));
            }

            showAlert("✅ Export uğurlu", "Məlumatlar users_export.txt faylına yazıldı!");
        } catch (Exception e) {
            showAlert("❌ Xəta", "Export zamanı xəta: " + e.getMessage());
        }
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
}