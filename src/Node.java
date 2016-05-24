import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;

public class Node implements TemporaryServerMethods {

    private static final int TYPE_SEMAPHORE = 1;
    private static final int TYPE_PRODUCER = 2;
    private static final int TYPE_CONSUMER = 3;
    private static final int TYPE_UNDEFINED = 4;

    private static int lastId = 0;
    private static int id;
    private static boolean electionTime = true;
    private static int semaphoreId;

    //private static int type;

    public static void main(String args[]) {
        lastId = 0;
        semaphoreId = 0;
        electionTime = true;

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

                if (args[0].equals("consumidor")) {
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

            temporaryServerRegistry.bind("TemporaryServerMethods", stub);

            System.out.println("Sou o servidor temporario");

            Thread.sleep(WAITING_TIME);

            electionTime = false;

            System.out.println("O no " + semaphoreId + " eh o semaforo");

            //espera os nós verificarem a eleição e iniciar o semaphoro
            Thread.sleep(3000);
            UnicastRemoteObject.unexportObject(temporaryServerRegistry, true);

            if(lastId == 0){
                try{
                    new Semaphore(lastId);
                    return TYPE_SEMAPHORE;
                }catch (Exception e){
                    System.out.println("Já existe um semaforo");
                }
            }

        }catch (ExportException e) {
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

            int semaphoreId = temporaryServer.getSemaphoreId();

            System.out.println("O id do semaphoro eh" + semaphoreId);

            if (semaphoreId == id) {
                System.out.println("Sou o semaphoro agora");
                new Semaphore(id);
                return TYPE_SEMAPHORE;
            }

        }catch (Exception e) {
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
        Timer timer = new Timer();

        Consumer consumer = new Consumer(semaphore, id, timer);
        timer.schedule(consumer, 0, 1000);
    }


    public void say() throws RemoteException {

    }

    public synchronized int registerClient() throws RemoteException {

        lastId++;

        if (electionTime) {
            semaphoreId = lastId;
            return lastId;
        }

        return lastId;
    }

    public synchronized void turnIntoClient() throws RemoteException {

    }

    public synchronized int getSemaphoreId() throws RemoteException {
        return semaphoreId;
    }
}