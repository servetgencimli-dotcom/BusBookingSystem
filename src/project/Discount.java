package project;

import java.util.Date;

public class Discount {
    public int percentage;
    public String reason;
    public Date createdDate;

    Discount(int percentage, String reason, Date createdDate) {
        this.percentage = percentage;
        this.reason = reason;
        this.createdDate = createdDate;
    }
}