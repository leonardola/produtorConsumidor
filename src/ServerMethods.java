import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerMethods extends Remote{

    int SERVER_PORT = 5678;

    void addNumber(int number) throws RemoteException;
    int getNumber() throws RemoteException;

}
