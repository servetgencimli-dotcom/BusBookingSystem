package project;


import java.util.Date;

public class Rating {
    String userFin;
    int stars;
    String comment;
    Date date;

    Rating(String userFin, int stars, String comment, Date date) {
        this.userFin = userFin;
        this.stars = Math.max(1, Math.min(5, stars)); // 1-5 arası
        this.comment = comment;
        this.date = date;
    }
}