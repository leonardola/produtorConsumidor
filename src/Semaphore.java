import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Semaphore implements SemaphoreMethods {

    private static boolean freeToAdd = false;
    private static boolean freeToRead = false;
    private static boolean serverIsDown = false;
    private static int lastAddedNodeId = 0;
    private static int serverId;
    private static boolean addAsProducer = true;

    private static int id;

    private static ServerMethods server;
    private static Registry registry;

    public Semaphore() {

    }

    public Semaphore(int id) {

        System.out.println("Semaphoro iniciando...");

        this.id = id;

        startListeningNodes();

        try {
            System.out.println("Esperando nos se conectarem");
            Thread.sleep(WAITING_TIME);
            addAsProducer = false;
        } catch (Exception e) {
            System.out.println("Nao pode esperar pelos nos");
        }

        System.out.println("Iniciando comunicacao com o servidor na porta "+ Server.SERVER_PORT);
        startServerComunication();

    }

    private static void startListeningNodes() {
        try {
            LocateRegistry.createRegistry(PORT);

            Semaphore obj = new Semaphore();
            SemaphoreMethods stub = (SemaphoreMethods) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(PORT);
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
        }

        System.out.println("Semaforo adicionou o numero: " + number);
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
            return SERVER_IS_DOWN;
        }
    }

    @Override
    public synchronized int registerClient() {

        if(addAsProducer){
            serverId++;
        }

        System.out.println("Cliente "+lastAddedNodeId+ " adicionado");

        return lastAddedNodeId++;
    }
}