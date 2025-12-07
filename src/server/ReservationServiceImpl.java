package server;

import common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation côté serveur du service de réservation.
 * Gère les places, les réservations, les zones, et les notifications aux clients.
 */
public class ReservationServiceImpl extends UnicastRemoteObject implements IReservationService {

    /** Map des places : clé = id de la place, valeur = ParkingSpot */
    private final Map<Integer, ParkingSpot> spots = new ConcurrentHashMap<>();

    /** Map des réservations : clé = id réservation, valeur = Reservation */
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    /** Map des listeners RMI pour notifications clients */
    private final Map<String, INotificationListener> listeners = new ConcurrentHashMap<>();

    /** Tarif par heure */
    private double pricePerHour = 1.0;

    /** Liste des zones de parking */
    private final List<ParkingZone> zones = new ArrayList<>();

    /**
     * Constructeur
     * @throws RemoteException
     */
    protected ReservationServiceImpl() throws RemoteException {
        super(); // exporte automatiquement l'objet RMI
        initZonesAndSpots(); // initialisation des zones et des places
    }

    /** Initialisation par défaut de quelques zones et places */
    private void initZonesAndSpots() {
        zones.add(new ParkingZone("Centre Ville", "Zone centre"));
        zones.add(new ParkingZone("Mall", "Centre commercial"));

        // création de quelques places par zone
        int id = 1;
        for (ParkingZone z : zones) {
            for (int i = 1; i <= 4; i++) {
                spots.put(id, new ParkingSpot(
                        id,
                        z.getName().substring(0, 2).toUpperCase() + i,
                        z.getName()
                ));
                id++;
            }
        }
    }

    // ======= Méthodes client =======

    /**
     * Liste les places disponibles, optionnellement filtrées par zone
     */
    @Override
    public synchronized List<ParkingSpot> listAvailableSpots(String region) {
        List<ParkingSpot> list = new ArrayList<>();
        for (ParkingSpot p : spots.values()) {
            if (!p.isReserved() && (region == null || region.isEmpty() || p.getRegion().equals(region))) {
                list.add(p);
            }
        }
        return list;
    }

    /**
     * Réserve une place pour un client et véhicule donnés
     */
    @Override
    public synchronized Reservation reserveSpot(ClientInfo client, Vehicle vehicle, int spotId, int hours) throws RemoteException {
        ParkingSpot s = spots.get(spotId);
        if (s == null) throw new RemoteException("Spot introuvable");
        if (s.isReserved()) throw new RemoteException("Déjà prise");

        s.setReserved(true); // marque la place comme réservée
        double amount = hours * pricePerHour;

        Reservation r = new Reservation(client, vehicle, s, hours, amount);
        reservations.put(r.getId(), r);

        // ajoute un historique pour la place
        s.addHistory(new SpotHistory(
                LocalDateTime.now(),
                "RESERVATION",
                "Réf: " + r.getId() + " Client: " + client.getName(),
                amount
        ));

        // notifie le client si un listener est enregistré
        notifyClient(client, new Notification("Réservation créée", "Réf: " + r.getId() + " Place: " + s.getLabel()));

        return r;
    }

    /**
     * Paiement d'une réservation
     */
    @Override
    public synchronized boolean payReservation(String reservationId, Payment payment) throws RemoteException {
        Reservation r = reservations.get(reservationId);
        if (r == null) throw new RemoteException("Reservation introuvable");
        if (Math.abs(r.getAmount() - payment.getAmount()) > 1e-6) throw new RemoteException("Montant incorrect");

        r.setPaid(true); // marque comme payé

        // ajoute historique sur la place
        ParkingSpot s = r.getSpot();
        s.addHistory(new SpotHistory(
                LocalDateTime.now(),
                "PAYMENT",
                "Paiement reservation " + r.getId() + " par " + r.getClient().getName(),
                payment.getAmount()
        ));

        // notifie le client
        notifyClient(r.getClient(), new Notification(
                "Paiement reçu",
                "Réf: " + reservationId + " Montant: " + payment.getAmount() + " DT"
        ));

        return true;
    }

    @Override
    public Reservation getReservation(String reservationId) throws RemoteException {
        return reservations.get(reservationId);
    }

    /** Enregistre un listener RMI pour notifications */
    @Override
    public void registerListener(String clientId, INotificationListener listener) throws RemoteException {
        listeners.put(clientId, listener);
    }

    /** Supprime un listener */
    @Override
    public void unregisterListener(String clientId) throws RemoteException {
        listeners.remove(clientId);
    }

    /** Envoie une notification à un client */
    private void notifyClient(ClientInfo client, Notification n) {
        try {
            INotificationListener l = listeners.get(client.getPhone());
            if (l != null) {
                l.onNotification(n);
            }
        } catch (Exception ex) {
            System.err.println("Erreur notify: " + ex.getMessage());
        }
    }

    @Override
    public List<ParkingZone> listZones() throws RemoteException {
        return Collections.unmodifiableList(zones);
    }

    // ======= Méthodes utilisées par AdminServiceImpl =======

    public synchronized Map<Integer, ParkingSpot> getSpots() {
        return spots;
    }

    public synchronized Map<String, Reservation> getReservations() {
        return reservations;
    }

    public synchronized ParkingSpot getSpot(int id) {
        return spots.get(id);
    }

    public synchronized boolean removeReservation(String id) {
        Reservation r = reservations.remove(id);
        if (r != null) {
            r.getSpot().setReserved(false);
            r.getSpot().addHistory(new SpotHistory(
                    LocalDateTime.now(),
                    "CANCEL",
                    "Annulation réservation " + id,
                    0
            ));
            return true;
        }
        return false;
    }

    public synchronized ParkingSpot addSpot(String label, String region) {
        int id = spots.size() + 1;
        ParkingSpot p = new ParkingSpot(id, label, region);
        spots.put(id, p);
        return p;
    }

    public synchronized boolean removeSpot(int id) {
        ParkingSpot p = spots.remove(id);
        return p != null;
    }

    // ======= Gestion des zones =======

    public synchronized boolean addZone(String name) {
        for (ParkingZone z : zones) if (z.getName().equalsIgnoreCase(name)) return false;
        zones.add(new ParkingZone(name, ""));
        return true;
    }

    public synchronized boolean removeZone(String name) {
        // on supprime la zone seulement si aucune place n'y est associée
        for (ParkingSpot p : spots.values()) {
            if (p.getRegion().equals(name)) return false;
        }
        return zones.removeIf(z -> z.getName().equals(name));
    }

    public synchronized List<ParkingSpot> listSpotsByZone(String zoneName) {
        List<ParkingSpot> list = new ArrayList<>();
        for (ParkingSpot p : spots.values()) {
            if (p.getRegion().equals(zoneName)) list.add(p);
        }
        return list;
    }
}
