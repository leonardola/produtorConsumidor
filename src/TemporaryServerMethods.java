import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TemporaryServerMethods extends Remote {

    int PORT = 1234;
    int WAITING_TIME = 10000;

    void say() throws RemoteException;

    int registerClient() throws RemoteException;

    int getSemaphoreId() throws RemoteException;

    void turnIntoClient() throws RemoteException;

}
