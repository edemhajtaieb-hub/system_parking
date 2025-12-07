package server;

import common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class AdminServiceImpl extends UnicastRemoteObject implements IAdminService {

    private final ReservationServiceImpl mainService;

    public AdminServiceImpl(ReservationServiceImpl mainService) throws RemoteException {
        super();
        this.mainService = mainService;
    }

    @Override
    public List<ParkingSpot> getAllSpots() throws RemoteException {
        return new java.util.ArrayList<>(mainService.getSpots().values());
    }

    @Override
    public List<Reservation> getAllReservations() throws RemoteException {
        return new java.util.ArrayList<>(mainService.getReservations().values());
    }

    @Override
    public boolean freeSpot(int spotId) throws RemoteException {
        ParkingSpot s = mainService.getSpot(spotId);
        if (s == null) return false;
        s.setReserved(false);
        s.addHistory(new SpotHistory(java.time.LocalDateTime.now(), "FREE", "Admin freed spot " + spotId, 0));
        return true;
    }

    @Override
    public boolean cancelReservation(String reservationId) throws RemoteException {
        return mainService.removeReservation(reservationId);
    }

    @Override
    public ParkingSpot addSpot(String label, String region) throws RemoteException {
        return mainService.addSpot(label, region);
    }

    @Override
    public boolean removeSpot(int spotId) throws RemoteException {
        return mainService.removeSpot(spotId);
    }

    // zone management
    @Override
    public boolean addZone(String zoneName) throws RemoteException {
        return mainService.addZone(zoneName);
    }

    @Override
    public boolean removeZone(String zoneName) throws RemoteException {
        return mainService.removeZone(zoneName);
    }

    @Override
    public List<ParkingZone> listZones() throws RemoteException {
        return mainService.listZones();
    }

    @Override
    public List<ParkingSpot> listSpotsByZone(String zoneName) throws RemoteException {
        return mainService.listSpotsByZone(zoneName);
    }

    @Override
    public List<SpotHistory> getSpotHistory(int spotId) throws RemoteException {
        ParkingSpot s = mainService.getSpot(spotId);
        if (s == null) return java.util.Collections.emptyList();
        return s.getHistory();
    }
}
