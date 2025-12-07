package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAdminService extends Remote {

    // Spots & reservations (existing)
    List<ParkingSpot> getAllSpots() throws RemoteException;
    List<Reservation> getAllReservations() throws RemoteException;
    boolean freeSpot(int spotId) throws RemoteException;
    boolean cancelReservation(String reservationId) throws RemoteException;
    ParkingSpot addSpot(String label, String region) throws RemoteException;
    boolean removeSpot(int spotId) throws RemoteException;

    // New: zone management and per-spot history
    boolean addZone(String zoneName) throws RemoteException;
    boolean removeZone(String zoneName) throws RemoteException;
    List<ParkingZone> listZones() throws RemoteException; // same as reservation service but exposed
    List<ParkingSpot> listSpotsByZone(String zoneName) throws RemoteException;
    List<SpotHistory> getSpotHistory(int spotId) throws RemoteException;
}
