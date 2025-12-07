package common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant un paiement effectué pour une réservation de parking.
 * Contient le mode de paiement, le montant et la date de paiement.
 */
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Méthode de paiement (ex: carte, cash, mobile) */
    private String method;

    /** Montant payé en DT */
    private double amount;

    /** Date et heure du paiement */
    private LocalDateTime date;

    /** Constructeur vide pour sérialisation/RMI */
    public Payment() {}

    /**
     * Constructeur principal
     * @param method méthode de paiement
     * @param amount montant payé
     */
    public Payment(String method, double amount) {
        this.method = method;
        this.amount = amount;
        this.date = LocalDateTime.now(); // capture la date actuelle
    }

    // ======= Getters =======
    public String getMethod() { return method; }

    public double getAmount() { return amount; }

    public LocalDateTime getDate() { return date; }
}
