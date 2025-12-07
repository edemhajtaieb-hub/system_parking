package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RMI pour recevoir des notifications du serveur.
 * Toute classe qui implémente cette interface peut être enregistrée
 * sur le serveur pour recevoir des événements ou messages en temps réel.
 */
public interface INotificationListener extends Remote {

    /**
     * Méthode appelée par le serveur pour notifier le client.
     * @param notification l'objet Notification contenant le titre et le message
     * @throws RemoteException en cas de problème de communication RMI
     */
    void onNotification(Notification notification) throws RemoteException;
}

