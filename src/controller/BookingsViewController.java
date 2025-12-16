package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import project.Booking;
import project.BusBookingSystem;
import project.MainApp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class BookingsViewController {

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> passengerCol;
    @FXML private TableColumn<Booking, String> routeCol;
    @FXML private TableColumn<Booking, String> intervalCol;
    @FXML private TableColumn<Booking, String> dateCol;
    @FXML private TableColumn<Booking, Double> priceCol;

    @FXML private Label totalBookingsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label popularRouteLabel;
    @FXML private Label selectedBookingInfo;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;

    private ObservableList<Booking> allBookings;
    private FilteredList<Booking> filteredBookings;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @FXML
    public void initialize() {
        // Sütun bağlantıları
        passengerCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getPassenger().getName()
                )
        );

        routeCol.setCellValueFactory(new PropertyValueFactory<>("routeName"));
        intervalCol.setCellValueFactory(new PropertyValueFactory<>("interval"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        dateCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> sdf.format(cell.getValue().getTravelDate())
                )
        );

        // Qiymət sütununun formatı
        priceCol.setCellFactory(col -> new TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f ₼", price));
                    setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                }
            }
        });

        // Məlumatları yüklə
        refreshTable();

        // Filtr seçimləri
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList(
                    "Hamısı", "Bu gün", "Bu həftə", "Bu ay", "Qlobal marşrutlar", "Daxili marşrutlar"
            ));
            filterComboBox.setValue("Hamısı");
            filterComboBox.setOnAction(e -> applyFilter());
        }

        // Axtarış funksiyası
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBookings(newVal));
        }

        // Seçilən sətir məlumatı
        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && selectedBookingInfo != null) {
                selectedBookingInfo.setText(String.format(
                        "Seçilmiş: %s | %s | %.2f AZN",
                        newVal.getPassenger().getName(),
                        newVal.getRouteName(),
                        newVal.getPrice()
                ));
            } else if (selectedBookingInfo != null) {
                selectedBookingInfo.setText("Seçilmiş: yoxdur");
            }
        });
    }

    private void refreshTable() {
        allBookings = FXCollections.observableArrayList(BusBookingSystem.bookingManager.bookings);
        filteredBookings = new FilteredList<>(allBookings, p -> true);
        bookingsTable.setItems(filteredBookings);

        updateStatistics();

        System.out.println("✅ " + allBookings.size() + " rezervasiya yükləndi");
    }

    private void updateStatistics() {
        int total = allBookings.size();
        double revenue = allBookings.stream().mapToDouble(Booking::getPrice).sum();

        // Ən məşhur marşrut
        String popular = "N/A";
        if (!allBookings.isEmpty()) {
            Map<String, Long> routeCounts = allBookings.stream()
                    .collect(Collectors.groupingBy(Booking::getRouteName, Collectors.counting()));
            popular = routeCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
        }

        if (totalBookingsLabel != null) totalBookingsLabel.setText(String.valueOf(total));
        if (totalRevenueLabel != null) totalRevenueLabel.setText(String.format("%.2f AZN", revenue));
        if (popularRouteLabel != null) popularRouteLabel.setText(popular);
    }

    private void filterBookings(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredBookings.setPredicate(p -> true);
        } else {
            String lowerCase = searchText.toLowerCase();
            filteredBookings.setPredicate(booking ->
                    booking.getPassenger().getName().toLowerCase().contains(lowerCase) ||
                            booking.getRouteName().toLowerCase().contains(lowerCase)
            );
        }
    }

    private void applyFilter() {
        if (filterComboBox == null) return;

        String filter = filterComboBox.getValue();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();

        filteredBookings.setPredicate(booking -> {
            switch (filter) {
                case "Bu gün":
                    return isSameDay(booking.getTravelDate(), now);
                case "Bu həftə":
                    cal.setTime(now);
                    cal.add(Calendar.DAY_OF_YEAR, 7);
                    return booking.getTravelDate().after(now) && booking.getTravelDate().before(cal.getTime());
                case "Bu ay":
                    cal.setTime(now);
                    cal.add(Calendar.MONTH, 1);
                    return booking.getTravelDate().after(now) && booking.getTravelDate().before(cal.getTime());
                case "Qlobal marşrutlar":
                    return booking.getRouteName().contains("Turkey") ||
                            booking.getRouteName().contains("Germany") ||
                            booking.getRouteName().contains("France") ||
                            booking.getRouteName().contains("Italy") ||
                            booking.getRouteName().contains("Spain");
                case "Daxili marşrutlar":
                    return booking.getRouteName().contains("Ganja") ||
                            booking.getRouteName().contains("Qabala") ||
                            booking.getRouteName().contains("Sumgait") ||
                            booking.getRouteName().contains("Shaki");
                default:
                    return true;
            }
        });
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
        if (searchField != null) searchField.clear();
        if (filterComboBox != null) filterComboBox.setValue("Hamısı");
        showAlert("✅ Yeniləndi", "Rezervasiya siyahısı yeniləndi!");
    }

    @FXML
    private void handleExportExcel() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bookings_export.csv"))) {
            writer.write("Sərnişin,Marşrut,Interval,Tarix,Qiymət (AZN)\n");

            for (Booking b : allBookings) {
                writer.write(String.format("%s,%s,%s,%s,%.2f\n",
                        b.getPassenger().getName(),
                        b.getRouteName(),
                        b.getInterval(),
                        sdf.format(b.getTravelDate()),
                        b.getPrice()
                ));
            }

            showAlert("✅ Export uğurlu", "Məlumatlar bookings_export.csv faylına yazıldı!");
        } catch (Exception e) {
            showAlert("❌ Xəta", "Export zamanı xəta: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSelected() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Xəbərdarlıq", "Silmək üçün rezervasiya seçin!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Təsdiq");
        confirm.setHeaderText("Rezervasiyanı silmək istədiyinizdən əminsiniz?");
        confirm.setContentText(String.format(
                "Sərnişin: %s | Marşrut: %s",
                selected.getPassenger().getName(),
                selected.getRouteName()
        ));

        if (confirm.showAndWait().get() == ButtonType.OK) {
            BusBookingSystem.bookingManager.bookings.remove(selected);
            refreshTable();
            showAlert("✅ Silindi", "Rezervasiya uğurla silindi!");
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