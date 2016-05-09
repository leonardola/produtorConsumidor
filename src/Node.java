import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class Node implements TemporaryServerMethods {

    private static int lastClientId = 0;
    private static int selfId;
    private static Node obj;
    private static Registry registry;

    public static void main(String args[]) {

        String state = tryToCreateTemporaryServer();

        if (state == "client") {
            Client client = new Client();
        }
    }

    private static String tryToCreateTemporaryServer() {
        String state = null;

        try {
            obj = new Node();
            TemporaryServerMethods stub = (TemporaryServerMethods) UnicastRemoteObject.exportObject(obj, 0);

            registry = LocateRegistry.getRegistry();
            registry.bind("TemporaryServerMethods", stub);

            state = "temporaryServer";
            selfId = lastClientId;

            lastClientId++;

            System.out.println("Server ready");

        } catch (AlreadyBoundException e) {
            System.out.println("Ja existe um servidor, sou apenas um cliente");
            state = "client";
        } catch (RemoteException e) {
            System.out.println("Ouve um erro ao comunicar com o server " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Erro randomico " + e.getMessage());
        }

        if (state == null) {
            System.exit(1);
        }

        return state;
    }

    public void say() {
        System.out.println("Hello brotha");
    }

    public int registerClient() {
        return lastClientId++;
    }

    public int getSemaphoreId() throws RemoteException {
        return lastClientId - 1;
    }

    public void turnIntoClient() throws RemoteException {
        try {
            registry.unbind("TemporaryServerMethods");
            UnicastRemoteObject.unexportObject(obj, true);
        } catch (Exception e) {
            System.out.println("Nao foi possivel matar o servidor " + e.getMessage());
        }

        Client client = new Client();
    }
}