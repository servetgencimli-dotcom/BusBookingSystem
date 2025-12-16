package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import project.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyBookingsViewController {

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> passengerCol;
    @FXML private TableColumn<Booking, String> routeCol;
    @FXML private TableColumn<Booking, String> dateCol;
    @FXML private TableColumn<Booking, String> intervalCol;
    @FXML private TableColumn<Booking, Double> priceCol;
    @FXML private Label totalTripsLabel;
    @FXML private Label totalSpentLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = findCurrentUser();
        setupTable();
        loadBookings();
    }

    private void setupTable() {
        passengerCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getPassenger().getName()
                )
        );

        routeCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getRouteName()
                )
        );

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        dateCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> sdf.format(cell.getValue().getTravelDate())
                )
        );

        intervalCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getInterval()
                )
        );

        priceCol.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cell.getValue().getPrice()
                )
        );

        priceCol.setCellFactory(col -> new TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f ₼", price));
                    setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void loadBookings() {
        if (currentUser == null) return;

        List<Booking> myBookings = BusBookingSystem.bookingManager.getBookingsByUser(currentUser.fin);
        bookingsTable.setItems(FXCollections.observableArrayList(myBookings));

        // Statistika
        totalTripsLabel.setText(String.valueOf(myBookings.size()));

        double totalSpent = 0;
        for (Booking b : myBookings) {
            totalSpent += b.getPrice();
        }
        totalSpentLabel.setText(String.format("%.2f AZN", totalSpent));
    }

    @FXML
    private void handleRefresh() {
        loadBookings();
    }

    @FXML
    private void handleBack() {
        MainApp.loadScene("/fxml/UserMenu.fxml", "İstifadəçi Menyu");
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
}