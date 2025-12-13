package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingController {
    @FXML private ComboBox<String> travelTypeCombo, routeCombo, intervalCombo;
    @FXML private ComboBox<String> genderCombo, floorCombo, seatCombo;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> ticketSpinner;
    @FXML private TextField passengerNameField, passengerFinField, passengerAgeField, luggageField;
    @FXML private Label priceLabel, messageLabel;

    private User currentUser;

    // Oturacaq sistemi üçün
    private List<String> allSeats;
    private List<String> bookedSeats;
    private String selectedSeat = null;

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        travelTypeCombo.setItems(FXCollections.observableArrayList("Global", "Domestic"));
        genderCombo.setItems(FXCollections.observableArrayList("K", "Q", "U"));
        floorCombo.setItems(FXCollections.observableArrayList("1", "2"));

        // Vaxt intervallarını yüklə
        try {
            if (BusBookingSystem.cityIntervals != null && BusBookingSystem.cityIntervals.length > 0) {
                ObservableList<String> intervals = FXCollections.observableArrayList(
                        BusBookingSystem.cityIntervals
                );
                intervalCombo.setItems(intervals);
            } else {
                // Əgər cityIntervals boşdursa, default intervallar
                intervalCombo.setItems(FXCollections.observableArrayList(
                        "08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00",
                        "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00"
                ));
            }
        } catch (Exception e) {
            // Əgər xəta olarsa, default intervallar
            intervalCombo.setItems(FXCollections.observableArrayList(
                    "08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00",
                    "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00"
            ));
        }

        travelTypeCombo.setOnAction(e -> loadRoutes());

        // Mərtəbə dəyişəndə oturacaqları yenilə
        floorCombo.setOnAction(e -> updateAvailableSeats());

        // Oturacaq seçimi
        seatCombo.setOnAction(e -> {
            if (seatCombo.getValue() != null) {
                selectedSeat = seatCombo.getValue().split(" - ")[0];
            }
        });

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1);
        ticketSpinner.setValueFactory(valueFactory);

        // Oturacaqları hazırla
        initializeSeats();

        // Test üçün bəzi dolu oturacaqlar (real sistemdə database-dən gələcək)
        bookedSeats = new ArrayList<>();
        bookedSeats.add("1A");
        bookedSeats.add("1B");
        bookedSeats.add("2C");
        bookedSeats.add("5D");
    }

    // Bütün oturacaqları yarat
    private void initializeSeats() {
        allSeats = new ArrayList<>();

        // 10 cərgə, hər cərgədə 4 oturacaq (A, B, C, D)
        for (int row = 1; row <= 10; row++) {
            for (char col = 'A'; col <= 'D'; col++) {
                allSeats.add(row + String.valueOf(col));
            }
        }
    }

    // Müsait oturacaqları yenilə və ComboBox-a əlavə et
    private void updateAvailableSeats() {
        String selectedFloor = floorCombo.getValue();

        if (selectedFloor == null) {
            seatCombo.setItems(FXCollections.observableArrayList());
            seatCombo.setPromptText("Əvvəlcə mərtəbə seçin");
            return;
        }

        ObservableList<String> availableSeats = FXCollections.observableArrayList();
        int floorNum = Integer.parseInt(selectedFloor);

        // Mərtəbəyə görə oturacaqları filtr et
        if (floorNum == 1) {
            // Birinci mərtəbə: 1-5 cərgələr
            for (String seat : allSeats) {
                int row = Integer.parseInt(seat.replaceAll("[^0-9]", ""));
                if (row >= 1 && row <= 5 && !bookedSeats.contains(seat)) {
                    availableSeats.add(seat + " - Müsait 💺");
                }
            }
        } else if (floorNum == 2) {
            // İkinci mərtəbə: 6-10 cərgələr
            for (String seat : allSeats) {
                int row = Integer.parseInt(seat.replaceAll("[^0-9]", ""));
                if (row >= 6 && row <= 10 && !bookedSeats.contains(seat)) {
                    availableSeats.add(seat + " - Müsait 💺");
                }
            }
        }

        seatCombo.setItems(availableSeats);
        seatCombo.setPromptText("Oturacaq seçin...");

        // Əgər heç bir müsait oturacaq yoxdursa
        if (availableSeats.isEmpty()) {
            seatCombo.setPromptText("Bu mərtəbədə müsait oturacaq yoxdur ❌");
            messageLabel.setText("⚠️ Bu mərtəbədə müsait oturacaq yoxdur!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        } else {
            messageLabel.setText("✓ " + availableSeats.size() + " müsait oturacaq tapıldı");
            messageLabel.setStyle("-fx-text-fill: #10b981;");
        }
    }

    private void loadRoutes() {
        String type = travelTypeCombo.getValue();
        if (type == null) return;

        ObservableList<String> routes = FXCollections.observableArrayList();

        try {
            if (type.equalsIgnoreCase("global")) {
                if (BusBookingSystem.globalRoutes != null && !BusBookingSystem.globalRoutes.isEmpty()) {
                    routes.addAll(BusBookingSystem.globalRoutes.keySet());
                } else {
                    // Əgər globalRoutes boşdursa, default marşrutlar
                    routes.addAll(
                            "Bakı-İstanbul", "Bakı-Moskva", "Bakı-Dubay",
                            "Bakı-Ankara", "Bakı-Tbilisi"
                    );
                }
            } else {
                if (BusBookingSystem.domesticRoutes != null && !BusBookingSystem.domesticRoutes.isEmpty()) {
                    routes.addAll(BusBookingSystem.domesticRoutes.keySet());
                } else {
                    // Əgər domesticRoutes boşdursa, default marşrutlar
                    routes.addAll(
                            "Bakı-Gəncə", "Bakı-Şəki", "Bakı-Quba",
                            "Bakı-Lənkəran", "Bakı-Şamaxı", "Bakı-Qəbələ"
                    );
                }
            }

            routeCombo.setItems(routes);

            // Debug mesajı
            if (routes.isEmpty()) {
                messageLabel.setText("⚠️ Marşrut tapılmadı!");
                messageLabel.setStyle("-fx-text-fill: #f59e0b;");
            } else {
                messageLabel.setText("✓ " + routes.size() + " marşrut yükləndi");
                messageLabel.setStyle("-fx-text-fill: #10b981;");
            }

        } catch (Exception e) {
            messageLabel.setText("❌ Marşrut yükləmə xətası: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void showSeats() {
        String floor = floorCombo.getValue();

        if (floor == null) {
            messageLabel.setText("❌ Zəhmət olmasa mərtəbə seçin!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        int floorNum = Integer.parseInt(floor);

        // Oturacaq xəritəsini göstər
        StringBuilder seatMap = new StringBuilder();
        seatMap.append("═══════════════════════════════════════\n");
        seatMap.append("           OTURACAQ XƏRİTƏSİ\n");
        seatMap.append("═══════════════════════════════════════\n\n");

        if (floorNum == 1) {
            seatMap.append("📍 Birinci Mərtəbə (Cərgə 1-5)\n\n");
            seatMap.append("      A    B    C    D\n");
            seatMap.append("   ┌────────────────────┐\n");
            for (int row = 1; row <= 5; row++) {
                seatMap.append(String.format(" %d │", row));
                for (char col = 'A'; col <= 'D'; col++) {
                    String seat = row + String.valueOf(col);
                    if (bookedSeats.contains(seat)) {
                        seatMap.append("  ❌ ");
                    } else if (selectedSeat != null && selectedSeat.equals(seat)) {
                        seatMap.append("  ✓ ");
                    } else {
                        seatMap.append("  ◯ ");
                    }
                }
                seatMap.append("│\n");
            }
            seatMap.append("   └────────────────────┘\n");
        } else {
            seatMap.append("📍 İkinci Mərtəbə (Cərgə 6-10)\n\n");
            seatMap.append("      A    B    C    D\n");
            seatMap.append("   ┌────────────────────┐\n");
            for (int row = 6; row <= 10; row++) {
                seatMap.append(String.format("%d │", row));
                for (char col = 'A'; col <= 'D'; col++) {
                    String seat = row + String.valueOf(col);
                    if (bookedSeats.contains(seat)) {
                        seatMap.append("  ❌ ");
                    } else if (selectedSeat != null && selectedSeat.equals(seat)) {
                        seatMap.append("  ✓ ");
                    } else {
                        seatMap.append("  ◯ ");
                    }
                }
                seatMap.append("│\n");
            }
            seatMap.append("   └────────────────────┘\n");
        }

        seatMap.append("\n");
        seatMap.append("◯ Müsait    ❌ Dolu    ✓ Seçilmiş\n");
        seatMap.append("\n");

        // Statistika
        int totalSeats = (floorNum == 1) ? 20 : 20;
        int bookedCount = 0;
        int startRow = (floorNum == 1) ? 1 : 6;
        int endRow = (floorNum == 1) ? 5 : 10;

        for (int row = startRow; row <= endRow; row++) {
            for (char col = 'A'; col <= 'D'; col++) {
                if (bookedSeats.contains(row + String.valueOf(col))) {
                    bookedCount++;
                }
            }
        }

        seatMap.append(String.format("📊 Statistika:\n"));
        seatMap.append(String.format("   Ümumi: %d oturacaq\n", totalSeats));
        seatMap.append(String.format("   Dolu: %d oturacaq\n", bookedCount));
        seatMap.append(String.format("   Müsait: %d oturacaq\n", totalSeats - bookedCount));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🚌 Oturacaq Xəritəsi");
        alert.setHeaderText("Mərtəbə " + floorNum);
        alert.setContentText(seatMap.toString());

        // Dialog pəncərəsini böyüt
        alert.getDialogPane().setMinWidth(500);

        alert.showAndWait();
    }

    @FXML
    private void handleBooking() {
        try {
            String route = routeCombo.getValue();
            String type = travelTypeCombo.getValue();
            String pName = passengerNameField.getText().trim();
            String pFin = passengerFinField.getText().trim();
            int pAge = Integer.parseInt(passengerAgeField.getText().trim());
            String pGender = genderCombo.getValue();
            double luggage = Double.parseDouble(luggageField.getText().trim());
            String floor = floorCombo.getValue();

            // Validasiya
            if (route == null || pName.isEmpty() || pFin.isEmpty() || pGender == null) {
                messageLabel.setText("❌ Bütün məcburi xanaları doldurun!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (floor == null) {
                messageLabel.setText("❌ Mərtəbə seçin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            if (selectedSeat == null) {
                messageLabel.setText("❌ Oturacaq seçin!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            int distance = type.equalsIgnoreCase("global") ?
                    BusBookingSystem.globalRoutes.get(route) :
                    BusBookingSystem.domesticRoutes.get(route);

            double price = BusBookingSystem.payment.calculatePayment(distance, true, luggage);
            BusBookingSystem.payment.processPayment(new java.util.Scanner(System.in), price, currentUser);

            Passenger p = new Passenger(pName, pAge, pGender, luggage, pFin);
            Booking b = new Booking(p, 0, new java.util.Date(), price, route,
                    intervalCombo.getValue());
            BusBookingSystem.bookingManager.addBooking(b);

            // Oturacağı rezerv et
            bookedSeats.add(selectedSeat);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✅ Uğurlu Rezervasiya");
            alert.setHeaderText("Biletiniz hazırdır!");
            alert.setContentText(
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "📋 Rezervasiya Detalları:\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "🚌 Marşrut: " + route + "\n" +
                            "👤 Sərnişin: " + pName + "\n" +
                            "🪪 FIN: " + pFin + "\n" +
                            "💺 Oturacaq: " + selectedSeat + " (Mərtəbə " + floor + ")\n" +
                            "⏰ Vaxt: " + intervalCombo.getValue() + "\n" +
                            "💰 Qiymət: " + String.format("%.2f", price) + " AZN\n" +
                            "\n━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "Xoş səyahət! 🎫"
            );
            alert.showAndWait();

            handleCancel();
        } catch (NumberFormatException e) {
            messageLabel.setText("❌ Yaş və baqaj düzgün rəqəm olmalıdır!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        } catch (Exception e) {
            messageLabel.setText("❌ Xəta: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.loadScene("/fxml/UserMenu.fxml","Back");
        Stage stage = (Stage) passengerNameField.getScene().getWindow();
        stage.close();
    }
}