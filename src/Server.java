import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by leonardoalbuquerque on 29/03/16.
 */
public class Server implements ServerMethods {

    ArrayList<Integer> numbers = new ArrayList<Integer>();
    private int id;

    public Server(){}

    public Server(int id) {
        this.id = id;

        try{
            LocateRegistry.createRegistry(ServerMethods.SERVER_PORT);

            Server obj = new Server();
            TemporaryServerMethods stub = (TemporaryServerMethods) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(ServerMethods.SERVER_PORT);
            registry.bind("ServerMethods", stub);
        }catch (Exception e){
            System.out.println("Nao pode criar o servidor");
        }
    }

    @Override
    public void addNumber(int number) {
        numbers.add(number);
    }

    @Override
    public int getNumber() {
        int number = numbers.get(0);
        numbers.remove(0);

        return number;

    }
}
