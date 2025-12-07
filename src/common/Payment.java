package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String method;
    private double amount;
    private LocalDateTime date;

    public Payment() {}
    public Payment(String method, double amount) {
        this.method = method; this.amount = amount; this.date = LocalDateTime.now();
    }
    public String getMethod() { return method; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
}

