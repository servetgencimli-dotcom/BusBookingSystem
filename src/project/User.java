package project;

import java.util.ArrayList;

public class User {
    public String name, gender, fin, idSeries, password, cardNumber, cardExpiry, cvc;
    public int age;
    public boolean isAdmin;
    public ArrayList<CityCard> cityCards = new ArrayList<>();

    public User(String name, String gender, int age, String fin, String idSeries, String password,
                String cardNumber, String cardExpiry, String cvc, boolean isAdmin) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.fin = fin;
        this.idSeries = idSeries;
        this.password = password;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cvc = cvc;
        this.isAdmin = isAdmin;
    }

    public User(String admin, String gender, int age, String adminFin, String idSeries, String adminPass, String cardNumber, String cardExpiry) {
    }

    // ✅ JavaFX TableView üçün GETTER metodları (MÜTLƏQ LAZIMDIR!)
    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public String getFin() { return fin; }
    public String getIdSeries() { return idSeries; }
    public boolean getIsAdmin() { return isAdmin; }
    public String getCardNumber() { return cardNumber; }
    public String getCardExpiry() { return cardExpiry; }
    public String getCvc() { return cvc; }
}