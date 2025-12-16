package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import project.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsDashboardController {

    @FXML private Label totalRevenueLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label avgRatingLabel;

    @FXML private BarChart<String, Number> routeChart;
    @FXML private LineChart<String, Number> revenueChart;

    @FXML private TableView<Booking> recentBookingsTable;
    @FXML private TableColumn<Booking, String> bookingUserCol;
    @FXML private TableColumn<Booking, String> bookingRouteCol;
    @FXML private TableColumn<Booking, Double> bookingPriceCol;

    @FXML private TableView<UserStats> topUsersTable;
    @FXML private TableColumn<UserStats, String> userNameCol;
    @FXML private TableColumn<UserStats, Integer> userBookingsCol;
    @FXML private TableColumn<UserStats, Double> userSpentCol;

    @FXML
    public void initialize() {
        setupTables();
        refreshData();
    }

    private void setupTables() {
        // Son rezervasiyalar cədvəli
        bookingUserCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getPassenger().getName()
                )
        );
        bookingRouteCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getRouteName()
                )
        );
        bookingPriceCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getPrice()
                )
        );

        // Top istifadəçilər cədvəli
        userNameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        userBookingsCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("bookingCount"));
        userSpentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalSpent"));
    }

    private void refreshData() {
        // Əsas statistikalar
        List<Booking> allBookings = BusBookingSystem.bookingManager.bookings;
        List<User> allUsers = BusBookingSystem.users;

        double totalRevenue = allBookings.stream()
                .mapToDouble(Booking::getPrice)
                .sum();

        totalRevenueLabel.setText(String.format("%.2f AZN", totalRevenue));
        totalBookingsLabel.setText(String.valueOf(allBookings.size()));
        totalUsersLabel.setText(String.valueOf(allUsers.size()));
        avgRatingLabel.setText("4.5 ⭐"); // Demo məlumat

        // Marşrut qrafiki
        updateRouteChart(allBookings);

        // Gəlir trendi qrafiki
        updateRevenueChart(allBookings);

        // Son rezervasiyalar
        List<Booking> recentBookings = allBookings.stream()
                .sorted((a, b) -> b.getTravelDate().compareTo(a.getTravelDate()))
                .limit(10)
                .collect(Collectors.toList());
        recentBookingsTable.setItems(FXCollections.observableArrayList(recentBookings));

        // Top istifadəçilər
        updateTopUsers(allBookings);
    }

    private void updateRouteChart(List<Booking> bookings) {
        Map<String, Long> routeCounts = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getRouteName, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rezervasiyalar");

        routeCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(entry ->
                        series.getData().add(new XYChart.Data<>(
                                entry.getKey().length() > 15 ?
                                        entry.getKey().substring(0, 15) + "..." :
                                        entry.getKey(),
                                entry.getValue()
                        ))
                );

        routeChart.getData().clear();
        routeChart.getData().add(series);
    }

    private void updateRevenueChart(List<Booking> bookings) {
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

        // Son 6 ay üçün məlumat
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            cal.add(Calendar.MONTH, -1);
            monthlyRevenue.put(monthFormat.format(cal.getTime()), 0.0);
        }

        // Rezervasiyaları aylara görə qrupla
        for (Booking b : bookings) {
            String month = monthFormat.format(b.getTravelDate());
            if (monthlyRevenue.containsKey(month)) {
                monthlyRevenue.put(month, monthlyRevenue.get(month) + b.getPrice());
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Gəlir");

        for (Map.Entry<String, Double> entry : monthlyRevenue.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    private void updateTopUsers(List<Booking> bookings) {
        Map<String, UserStats> userStatsMap = new HashMap<>();

        for (Booking b : bookings) {
            String userFin = b.getPassenger().getFin();
            String userName = b.getPassenger().getName();

            UserStats stats = userStatsMap.get(userFin);
            if (stats == null) {
                stats = new UserStats(userName);
                userStatsMap.put(userFin, stats);
            }
            stats.bookingCount++;
            stats.totalSpent += b.getPrice();
        }

        // Sort və limit (Java 8 uyğun)
        List<UserStats> allStats = new ArrayList<>(userStatsMap.values());
        Collections.sort(allStats, new Comparator<UserStats>() {
            @Override
            public int compare(UserStats a, UserStats b) {
                return Double.compare(b.totalSpent, a.totalSpent);
            }
        });

        List<UserStats> topUsers = new ArrayList<>();
        for (int i = 0; i < Math.min(10, allStats.size()); i++) {
            topUsers.add(allStats.get(i));
        }

        topUsersTable.setItems(FXCollections.observableArrayList(topUsers));
    }

    @FXML
    private void handleRefresh() {
        refreshData();
        showAlert("✅ Yeniləndi", "Məlumatlar uğurla yeniləndi!");
    }

    @FXML
    private void handleExport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("analytics_report.txt"))) {
            writer.write("=== ANALYTICS REPORT ===\n\n");
            writer.write("Tarix: " + new Date() + "\n\n");

            writer.write("ƏSAS STATİSTİKALAR:\n");
            writer.write("Ümumi Gəlir: " + totalRevenueLabel.getText() + "\n");
            writer.write("Ümumi Rezervasiya: " + totalBookingsLabel.getText() + "\n");
            writer.write("Ümumi İstifadəçi: " + totalUsersLabel.getText() + "\n");
            writer.write("Orta Reytinq: " + avgRatingLabel.getText() + "\n\n");

            writer.write("TOP 5 MARŞRUT:\n");
            List<Booking> bookings = BusBookingSystem.bookingManager.bookings;
            Map<String, Long> routeCounts = bookings.stream()
                    .collect(Collectors.groupingBy(Booking::getRouteName, Collectors.counting()));

            routeCounts.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(5)
                    .forEach(entry -> {
                        try {
                            writer.write(entry.getKey() + ": " + entry.getValue() + " rezervasiya\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            showAlert("✅ Export uğurlu", "Hesabat analytics_report.txt faylına yazıldı!");
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

    // Köməkçi sinif
    public static class UserStats {
        private String name;
        private int bookingCount;
        private double totalSpent;

        public UserStats(String name) {
            this.name = name;
            this.bookingCount = 0;
            this.totalSpent = 0.0;
        }

        public String getName() { return name; }
        public int getBookingCount() { return bookingCount; }
        public double getTotalSpent() { return totalSpent; }
    }
}