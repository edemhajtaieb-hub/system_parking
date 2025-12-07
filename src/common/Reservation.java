package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe représentant une réservation d'une place de parking.
 * Contient les informations du client, du véhicule, de la place, la durée,
 * le montant à payer, la date de création et l'état de paiement.
 */
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identifiant unique de la réservation (UUID) */
    private final String id;

    /** Informations du client ayant effectué la réservation */
    private final ClientInfo client;

    /** Informations sur le véhicule réservé */
    private final Vehicle vehicle;

    /** Place de parking réservée */
    private final ParkingSpot spot;

    /** Durée de réservation en heures */
    private final int hours;

    /** Date et heure de création de la réservation */
    private final LocalDateTime createdAt;

    /** Montant à payer pour cette réservation */
    private final double amount;

    /** Indique si la réservation a été payée */
    private boolean paid;

    /**
     * Constructeur principal
     * @param client informations du client
     * @param vehicle informations du véhicule
     * @param spot place de parking réservée
     * @param hours durée de réservation
     * @param amount montant à payer
     */
    public Reservation(ClientInfo client, Vehicle vehicle, ParkingSpot spot, int hours, double amount) {
        this.id = UUID.randomUUID().toString(); // génération d'un ID unique
        this.client = client;
        this.vehicle = vehicle;
        this.spot = spot;
        this.hours = hours;
        this.amount = amount;
        this.createdAt = LocalDateTime.now(); // date actuelle
        this.paid = false; // initialement non payé
    }

    // ======= Getters =======
    public String getId() { return id; }

    public ClientInfo getClient() { return client; }

    public Vehicle getVehicle() { return vehicle; }

    public ParkingSpot getSpot() { return spot; }

    public int getHours() { return hours; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public double getAmount() { return amount; }

    public boolean isPaid() { return paid; }

    // ======= Setter =======
    public void setPaid(boolean paid) { this.paid = paid; }
}
