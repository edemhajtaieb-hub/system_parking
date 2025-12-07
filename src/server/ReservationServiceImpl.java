package server;

import common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReservationServiceImpl extends UnicastRemoteObject implements IReservationService {
    private final Map<Integer, ParkingSpot> spots = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final Map<String, INotificationListener> listeners = new ConcurrentHashMap<>();
    private double pricePerHour = 1.0;
    private final List<ParkingZone> zones = new ArrayList<>();

    protected ReservationServiceImpl() throws RemoteException {
        super();
        initZonesAndSpots();
    }

    private void initZonesAndSpots() {
        // start with empty or some defaults; but admin can add more later
        zones.add(new ParkingZone("Centre Ville", "Zone centre"));
        zones.add(new ParkingZone("Mall", "Centre commercial"));
        // create a few spots as example
        int id = 1;
        for (ParkingZone z : zones) {
            for (int i = 1; i <= 4; i++) {
                spots.put(id, new ParkingSpot(id, z.getName().substring(0,2).toUpperCase() + i, z.getName()));
                id++;
            }
        }
    }

    @Override
    public synchronized List<ParkingSpot> listAvailableSpots(String region) {
        List<ParkingSpot> list = new ArrayList<>();
        for (ParkingSpot p : spots.values()) {
            if (!p.isReserved() && (region==null || region.isEmpty() || p.getRegion().equals(region)))
                list.add(p);
        }
        return list;
    }

    @Override
    public synchronized Reservation reserveSpot(ClientInfo client, Vehicle vehicle, int spotId, int hours) throws RemoteException {
        ParkingSpot s = spots.get(spotId);
        if (s == null) throw new RemoteException("Spot introuvable");
        if (s.isReserved()) throw new RemoteException("Déjà prise");
        s.setReserved(true);
        double amount = hours * pricePerHour;
        Reservation r = new Reservation(client, vehicle, s, hours, amount);
        reservations.put(r.getId(), r);

        // add history per spot
        s.addHistory(new SpotHistory(LocalDateTime.now(), "RESERVATION", "Réf: " + r.getId() + " Client: " + client.getName(), amount));

        // notify client if listener registered
        notifyClient(client, new Notification("Réservation créée", "Réf: " + r.getId() + " Place: " + s.getLabel()));
        return r;
    }

    @Override
    public synchronized boolean payReservation(String reservationId, Payment payment) throws RemoteException {
        Reservation r = reservations.get(reservationId);
        if (r == null) throw new RemoteException("Reservation introuvable");
        if (Math.abs(r.getAmount() - payment.getAmount()) > 1e-6) throw new RemoteException("Montant incorrect");
        r.setPaid(true);

        // add history entry to spot
        ParkingSpot s = r.getSpot();
        s.addHistory(new SpotHistory(LocalDateTime.now(), "PAYMENT", "Paiement reservation " + r.getId() + " par " + r.getClient().getName(), payment.getAmount()));

        notifyClient(r.getClient(), new Notification("Paiement reçu", "Réf: " + reservationId + " Montant: " + payment.getAmount() + " DT"));
        return true;
    }

    @Override
    public Reservation getReservation(String reservationId) throws RemoteException {
        return reservations.get(reservationId);
    }

    @Override
    public void registerListener(String clientId, INotificationListener listener) throws RemoteException {
        listeners.put(clientId, listener);
    }

    @Override
    public void unregisterListener(String clientId) throws RemoteException {
        listeners.remove(clientId);
    }

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

    // ---- Admin helpers used by AdminServiceImpl ----
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
            r.getSpot().addHistory(new SpotHistory(LocalDateTime.now(), "CANCEL", "Annulation réservation " + id, 0));
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

    // zone management
    public synchronized boolean addZone(String name) {
        for (ParkingZone z : zones) if (z.getName().equalsIgnoreCase(name)) return false;
        zones.add(new ParkingZone(name, ""));
        return true;
    }

    public synchronized boolean removeZone(String name) {
        // remove zone only if no spots in it
        for (ParkingSpot p : spots.values()) {
            if (p.getRegion().equals(name)) return false;
        }
        return zones.removeIf(z -> z.getName().equals(name));
    }

    public synchronized List<ParkingSpot> listSpotsByZone(String zoneName) {
        List<ParkingSpot> list = new ArrayList<>();
        for (ParkingSpot p : spots.values()) if (p.getRegion().equals(zoneName)) list.add(p);
        return list;
    }
}
