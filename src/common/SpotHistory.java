package common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant l'historique d'une place de parking.
 * Chaque événement sur la place (réservation, paiement, libération, annulation)
 * est enregistré avec la date, le type, les détails et le montant associé.
 */
public class SpotHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Date et heure de l'événement */
    private final LocalDateTime date;

    /** Type de l'événement : "RESERVATION", "PAYMENT", "FREE", "CANCEL" */
    private final String type;

    /** Détails supplémentaires sur l'événement */
    private final String details;

    /** Montant lié à l'événement (0 si non applicable) */
    private final double amount;

    /**
     * Constructeur principal
     * @param date date et heure de l'événement
     * @param type type d'événement
     * @param details informations supplémentaires
     * @param amount montant (0 si non applicable)
     */
    public SpotHistory(LocalDateTime date, String type, String details, double amount) {
        this.date = date;
        this.type = type;
        this.details = details;
        this.amount = amount;
    }

    // ======= Getters =======
    public LocalDateTime getDate() { return date; }

    public String getType() { return type; }

    public String getDetails() { return details; }

    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "[" + date + "] " + type + " - " + details + (amount > 0 ? (" (DT " + amount + ")") : "");
    }
}
