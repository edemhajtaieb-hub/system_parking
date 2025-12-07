package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INotificationListener extends Remote {
    void onNotification(Notification notification) throws RemoteException;
}
