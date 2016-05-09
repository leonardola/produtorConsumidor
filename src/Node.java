import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;

public class Node {

    public static void main(String args[]) {
        int id;

        try {
            Registry registry = LocateRegistry.getRegistry(SemaphoreMethods.NODES_PORT);
            SemaphoreMethods semaphore = (SemaphoreMethods) registry.lookup("SemaphoreMethods");

            id = semaphore.registerClient();
            System.out.println("Meu id eh " + id);

            Thread.sleep(SemaphoreMethods.WAITING_TIME);
            System.out.println("Sera que sou o server?");

            int serverId = semaphore.getServerId();

            System.out.println(serverId);

            if (serverId == id) {
                System.out.println("Sou o server agora");
                new Server(id);
            } else if (serverId < id) {
                System.out.println("Sou consumidor");
                turnIntoConsumer(semaphore, id);
            } else {
                System.out.println("Sou produtor");
                turnIntoProducer(semaphore, id);
            }


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void turnIntoProducer(SemaphoreMethods semaphore, int id) {
        Timer timer = new Timer();
        Producer producer = new Producer(semaphore, id, timer);

        timer.schedule(producer, 0, 1000);
    }

    private static void turnIntoConsumer(SemaphoreMethods semaphore, int id) {
        Consumer consumer = new Consumer(semaphore, id);

        Timer timer = new Timer();
        timer.schedule(consumer, 0, 1000);
    }

    /*public void turnIntoClient() throws RemoteException {
        try {
            registry.unbind("TemporaryServerMethods");
            UnicastRemoteObject.unexportObject(obj, true);
        } catch (Exception e) {
            System.out.println("Nao foi possivel matar o servidor " + e.getMessage());
        }

        Client client = new Client();
    }*/
}