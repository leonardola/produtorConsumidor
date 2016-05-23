import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by leonardoalbuquerque on 28/04/16.
 */
public interface SemaphoreMethods extends Remote {
    static final int WAITING_TIME = 13000;
    static final int PORT = 4321;
    static final int SERVER_IS_EMPTY = -1;
    static final int SERVER_IS_DOWN = -2;
    static final int SERVER_IN_USE = -3;


    int registerClient() throws RemoteException;

    boolean addNumber(int number) throws RemoteException;

    int getNumber() throws RemoteException;

}
