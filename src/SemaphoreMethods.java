import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by leonardoalbuquerque on 28/04/16.
 */
public interface SemaphoreMethods extends Remote {
    static final int WAITING_TIME = 5000;
    static final int NODES_PORT = 1234;
    static final int SERVER_IS_EMPTY = -1;
    static final int SERVER_IS_DOWN = -2;
    static final int SERVER_IN_USE = -3;


    int registerClient() throws RemoteException;

    int getServerId() throws RemoteException;

    boolean amIAProducer(int id) throws RemoteException;

    boolean addNumber(int number) throws RemoteException;

    int getNumber() throws RemoteException;

    void setServerIsUp() throws RemoteException;

}
