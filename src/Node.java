import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;

public class Node implements TemporaryServerMethods {

    private static final int TYPE_SERVER = 0;
    private static final int TYPE_SEMAPHORE = 1;
    private static final int TYPE_PRODUCER = 2;
    private static final int TYPE_CONSUMER = 3;
    private static final int TYPE_UNDEFINED = 4;

    private static int lastId = 0;
    private static int id;
    private static boolean electionTime = true;

    //private static int type;

    public static void main(String args[]) {
        int id;

        int type = electServerAndSemaphore();

        if (type == TYPE_UNDEFINED) {

            //vira um nó comum
            try {
                Registry registry = LocateRegistry.getRegistry(SemaphoreMethods.PORT);
                SemaphoreMethods semaphore = (SemaphoreMethods) registry.lookup("SemaphoreMethods");

                id = semaphore.registerClient();
                System.out.println("Meu id eh " + id);

                Thread.sleep(SemaphoreMethods.WAITING_TIME);

                int serverId = semaphore.getServerId();

                if (serverId <= id) {
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

    }

    private static int electServerAndSemaphore() {
        try {
            Registry temporaryServerRegistry = LocateRegistry.createRegistry(PORT);

            Node obj = new Node();
            TemporaryServerMethods stub = (TemporaryServerMethods) UnicastRemoteObject.exportObject(obj, 0);

            //Registry registry = LocateRegistry.getRegistry(PORT);
            temporaryServerRegistry.bind("TemporaryServerMethods", stub);

            System.out.println("Sou o servidor temporario");

            Thread.sleep(WAITING_TIME);

            electionTime = false;

            System.out.println("O no " + lastId + " eh o servidor");

            int semaphore = lastId - 1;
            System.out.println("O no " + semaphore + " eh o semaphoro");

            //espera os nós verificarem a eleição e iniciar o semaphoro
            Thread.sleep(3000);
            UnicastRemoteObject.unexportObject(temporaryServerRegistry, true);

        } catch (ExportException e) {
            System.out.println("Já existe um servidor temporario");
            return waitElection();
        } catch (Exception e) {
            System.out.println("Erro ao se tornar servidor temporario " + e.getMessage());
            System.exit(1);
        }

        return TYPE_UNDEFINED;
    }

    private static int waitElection() {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            TemporaryServerMethods temporaryServer = (TemporaryServerMethods) registry.lookup("TemporaryServerMethods");

            id = temporaryServer.registerClient();

            System.out.println("Meu id temporario eh" + id);

            Thread.sleep(TemporaryServerMethods.WAITING_TIME);

            int serverId = temporaryServer.getServerId();

            System.out.println("O id do servidor eh" + serverId);

            if (serverId == id) {
                System.out.println("Sou o server agora");
                new Server(id);
                return TYPE_SERVER;
            }

            int semaphoreId = temporaryServer.getSemaphoreId();

            System.out.println("O id do semaphoro eh" + semaphoreId);

            if (semaphoreId == id) {
                System.out.println("Sou o semaphoro agora");
                new Semaphore(id);
                return TYPE_SEMAPHORE;
            }

        } catch (Exception e) {
            System.out.println("Impossivel se conectar ao servidor temporario " + e.getMessage());
            System.exit(1);
        }

        try {
            Thread.sleep(WAITING_TIME);
        } catch (Exception e) {
            System.out.println("Erro ao esperar a eleição" + e.getMessage());
        }

        return TYPE_UNDEFINED;
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


    public void say() throws RemoteException {

    }

    public synchronized int registerClient() throws RemoteException {

        if (electionTime) {
            lastId++;
            return lastId;
        } else {
            return -1;
        }

    }

    public synchronized int getSemaphoreId() throws RemoteException {
        return lastId - 1;
    }

    public synchronized void turnIntoClient() throws RemoteException {

    }

    public synchronized int getServerId() throws RemoteException {
        return lastId;
    }


    /*public void turnIntoClient() throws RemoteException {
        try {
            registry.unbind("TemporaryServerMethods");
            UnicastRemoteObject.unexportObject(obj, true);
        } catch (Exception e) {
            System.out.println("Nao foi possivel matar o servidor " + e.getMessage());
        }
    }*/
}