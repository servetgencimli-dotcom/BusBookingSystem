package controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import project.*;
import java.sql.*;

public class ViewUsersController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> nameColumn, finColumn, genderColumn;
    @FXML private TableColumn<User, Integer> ageColumn;
    @FXML private TableColumn<User, Boolean> adminColumn;
    @FXML private TableColumn<User, Integer> cardsColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().name));
        finColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().fin));
        genderColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().gender));
        ageColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().age).asObject());
        adminColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isAdmin).asObject());
        cardsColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().cityCards.size()).asObject());

        loadUsers();
    }

    @FXML
    private void loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        users.addAll(BusBookingSystem.users);
        users.addAll(UserDAO.loadAllUsers());
        usersTable.setItems(users);
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) usersTable.getScene().getWindow();
        stage.close();
    }
}
