import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by leonardoalbuquerque on 08/04/16.
 */
public class Semaphore implements SemaphoreMethods {

    private static boolean freeToAdd = true;
    private static boolean freeToRead = true;

    public static void main(String args[]) {

        startListeningNodes();
        startServerComunication();

    }

    private static void startListeningNodes() {
        try {
            LocateRegistry.createRegistry(ServerMethods.SERVER_PORT);

            Semaphore obj = new Semaphore();
            TemporaryServerMethods stub = (TemporaryServerMethods) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(ServerMethods.SERVER_PORT);
            registry.bind("TemporaryServerMethods", stub);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void startServerComunication(){
        try{
            Registry registry = LocateRegistry.getRegistry(ServerMethods.SERVER_PORT);
            TemporaryServerMethods server = (TemporaryServerMethods) registry.lookup("ServerMethods");
        }catch (Exception e){
            System.out.println("Erro ao iniciar registro do servidor" + e.getMessage());
        }
    }

    @Override
    public boolean addNumber(int number) {

        if (!freeToAdd) {
            return false;
        }

        freeToAdd = false;


        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Erro ao esperar");
        }

        freeToAdd = true;
        return true;
    }

    @Override
    public int getNumber() {
        if (!freeToRead) {
            return -1;
        }

        return 123;
    }
}
