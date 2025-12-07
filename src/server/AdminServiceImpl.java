package server;

import common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Implémentation côté serveur du service d'administration pour Smart Parking.
 * Fournit des méthodes pour gérer les zones, les places, les réservations et l'historique.
 */
public class AdminServiceImpl extends UnicastRemoteObject implements IAdminService {

    /** Référence vers le service principal de réservation qui contient toute la logique métier */
    private final ReservationServiceImpl mainService;

    /**
     * Constructeur
     * @param mainService service principal de réservation
     * @throws RemoteException
     */
    public AdminServiceImpl(ReservationServiceImpl mainService) throws RemoteException {
        super(); // exporte automatiquement cet objet RMI
        this.mainService = mainService;
    }

    // ======= Gestion des places =======

    /** Retourne toutes les places de parking */
    @Override
    public List<ParkingSpot> getAllSpots() throws RemoteException {
        // récupère toutes les valeurs de la Map de spots et les transforme en liste
        return new java.util.ArrayList<>(mainService.getSpots().values());
    }

    /** Retourne toutes les réservations */
    @Override
    public List<Reservation> getAllReservations() throws RemoteException {
        return new java.util.ArrayList<>(mainService.getReservations().values());
    }

    /** Libère une place donnée par son ID */
    @Override
    public boolean freeSpot(int spotId) throws RemoteException {
        ParkingSpot s = mainService.getSpot(spotId);
        if (s == null) return false;
        s.setReserved(false);
        // ajoute un historique indiquant que l'admin a libéré la place
        s.addHistory(new SpotHistory(java.time.LocalDateTime.now(), "FREE", "Admin freed spot " + spotId, 0));
        return true;
    }

    /** Annule une réservation par son ID */
    @Override
    public boolean cancelReservation(String reservationId) throws RemoteException {
        return mainService.removeReservation(reservationId);
    }

    /** Ajoute une nouvelle place de parking dans une zone */
    @Override
    public ParkingSpot addSpot(String label, String region) throws RemoteException {
        return mainService.addSpot(label, region);
    }

    /** Supprime une place de parking par son ID */
    @Override
    public boolean removeSpot(int spotId) throws RemoteException {
        return mainService.removeSpot(spotId);
    }

    // ======= Gestion des zones =======

    /** Ajoute une nouvelle zone */
    @Override
    public boolean addZone(String zoneName) throws RemoteException {
        return mainService.addZone(zoneName);
    }

    /** Supprime une zone */
    @Override
    public boolean removeZone(String zoneName) throws RemoteException {
        return mainService.removeZone(zoneName);
    }

    /** Liste toutes les zones */
    @Override
    public List<ParkingZone> listZones() throws RemoteException {
        return mainService.listZones();
    }

    /** Liste toutes les places d'une zone donnée */
    @Override
    public List<ParkingSpot> listSpotsByZone(String zoneName) throws RemoteException {
        return mainService.listSpotsByZone(zoneName);
    }

    /** Récupère l'historique d'une place spécifique */
    @Override
    public List<SpotHistory> getSpotHistory(int spotId) throws RemoteException {
        ParkingSpot s = mainService.getSpot(spotId);
        if (s == null) return java.util.Collections.emptyList();
        return s.getHistory();
    }
}
