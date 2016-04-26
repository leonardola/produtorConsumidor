import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by leonardoalbuquerque on 29/03/16.
 */
public interface ServerMethods extends Remote {

    void say() throws RemoteException;

    int registerClient() throws RemoteException;

    int getSemaphoreId() throws RemoteException;

    void turnIntoClient() throws RemoteException;
}
