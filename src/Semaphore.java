import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by leonardoalbuquerque on 08/04/16.
 */
public class Semaphore implements SemaphoreMethods {

    private static boolean freeToAdd = false;
    private static boolean freeToRead = false;
    private static boolean addAsProducer = true;
    private static boolean serverIsDown = false;
    private static int lastAddedNodeId = 0;
    private static int serverId = 0;

    private static ServerMethods server;
    private static Registry registry;

    public static void main(String args[]) {

        startListeningNodes();

        try {
            Thread.sleep(WAITING_TIME);
            addAsProducer = false;
            System.out.println("Agora so aceito consumidores");
            Thread.sleep(WAITING_TIME);
        } catch (Exception e) {
            System.out.println("Nao pode esperar pelos nos");
        }

        startServerComunication();

    }

    private static void startListeningNodes() {
        try {
            LocateRegistry.createRegistry(NODES_PORT);

            Semaphore obj = new Semaphore();
            SemaphoreMethods stub = (SemaphoreMethods) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(NODES_PORT);
            registry.bind("SemaphoreMethods", stub);

            System.out.println("Ovindo os nos");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void startServerComunication() {
        try {
            registry = LocateRegistry.getRegistry(ServerMethods.SERVER_PORT);
            server = (ServerMethods) registry.lookup("ServerMethods");
        } catch (Exception e) {
            System.out.println("Erro ao iniciar registro do servidor" + e.getMessage());
        }

        freeToAdd = true;
        freeToRead = true;
    }

    @Override
    public synchronized boolean addNumber(int number) {

        if (!freeToAdd || serverIsDown) {
            return false;
        }

        freeToAdd = false;

        try {
            server.addNumber(number);
        } catch (Exception e) {
            System.out.println("Servidor nao recebeu o numero " + e.getMessage());
            setServerIsDown();
        }

        System.out.println("Adicionado o numero: " + number);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Erro ao esperar");
        }

        freeToAdd = true;
        return true;
    }

    @Override
    public synchronized int getNumber() {
        if (serverIsDown) {
            return SERVER_IS_DOWN;
        }

        if (!freeToRead) {
            return SERVER_IN_USE;
        }

        try {
            return server.getNumber();
        } catch (Exception e) {
            System.out.println("Semaforo nao conseguiu receber o numero do servidor");
            setServerIsDown();
            return SERVER_IS_DOWN;
        }
    }

    @Override
    public synchronized int registerClient() {

        if (addAsProducer) {
            serverId = lastAddedNodeId;
        }

        return lastAddedNodeId++;
    }

    @Override
    public int getServerId() {

        if (serverId < 0) {
            System.out.println("Nenhum servidor pode ser eleito");
        }

        return serverId;
    }

    @Override
    public boolean amIAProducer(int id) {
        if (id < serverId) {
            return true;
        }

        return false;
    }

    private synchronized void setServerIsDown() {
        serverIsDown = true;
        serverId--;

        System.out.println("Servidor lockado");
    }

    public synchronized void setServerIsUp() {

        try {
            registry.unbind("SeverMethods");
            UnicastRemoteObject.unexportObject(server, true);
        } catch (Exception e) {
            System.out.println("Nao foi possivel matar o servidor " + e.getMessage());
        }

        startServerComunication();

        serverIsDown = false;
        System.out.println("Servidor deslockado");
    }
}