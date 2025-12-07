package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IReservationService extends Remote {
    List<ParkingSpot> listAvailableSpots(String region) throws RemoteException;
    Reservation reserveSpot(ClientInfo client, Vehicle vehicle, int spotId, int hours) throws RemoteException;
    boolean payReservation(String reservationId, Payment payment) throws RemoteException;
    Reservation getReservation(String reservationId) throws RemoteException;

    // Notification (callback) methods
    void registerListener(String clientId, INotificationListener listener) throws RemoteException;
    void unregisterListener(String clientId) throws RemoteException;

    // admin helpers
    List<ParkingZone> listZones() throws RemoteException;
}
