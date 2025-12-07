package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final ClientInfo client;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final int hours;
    private final LocalDateTime createdAt;
    private final double amount;
    private boolean paid;

    public Reservation(ClientInfo client, Vehicle vehicle, ParkingSpot spot, int hours, double amount) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.vehicle = vehicle;
        this.spot = spot;
        this.hours = hours;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
        this.paid = false;
    }
    public String getId() { return id; }
    public ClientInfo getClient() { return client; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public int getHours() { return hours; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}

