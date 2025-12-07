package common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant une notification envoyée du serveur vers le client.
 * Elle contient un titre, un message et la date/heure de création.
 */
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Titre de la notification */
    private final String title;

    /** Message de la notification */
    private final String message;

    /** Date et heure de création de la notification */
    private final LocalDateTime at;

    /**
     * Constructeur principal.
     * @param title Titre de la notification
     * @param message Message de la notification
     */
    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
        this.at = LocalDateTime.now(); // on enregistre automatiquement le moment de création
    }

    /** Retourne le titre de la notification */
    public String getTitle() {
        return title;
    }

    /** Retourne le message de la notification */
    public String getMessage() {
        return message;
    }

    /** Retourne la date et l'heure de création de la notification */
    public LocalDateTime getAt() {
        return at;
    }

    /** Retourne une représentation texte de la notification pour affichage ou debug */
    @Override
    public String toString() {
        return "[" + at + "] " + title + " - " + message;
    }
}
