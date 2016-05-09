import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by leonardoalbuquerque on 28/04/16.
 */
public interface SemaphoreMethods extends Remote {
    boolean addNumber(int number) throws RemoteException;
    int getNumber() throws RemoteException;
}
