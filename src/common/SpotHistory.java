package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SpotHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime date;
    private final String type; // "RESERVATION", "PAYMENT", "FREE", "CANCEL"
    private final String details;
    private final double amount; // 0 if N/A

    public SpotHistory(LocalDateTime date, String type, String details, double amount) {
        this.date = date;
        this.type = type;
        this.details = details;
        this.amount = amount;
    }

    public LocalDateTime getDate() { return date; }
    public String getType() { return type; }
    public String getDetails() { return details; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "[" + date + "] " + type + " - " + details + (amount>0? (" (DT " + amount + ")") : "");
    }
}

