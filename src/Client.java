import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public Client() {
        int id;

        String host = null;
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ServerMethods server = (ServerMethods) registry.lookup("ServerMethods");

            id = server.registerClient();
            server.say();
            System.out.println("Meu id eh " + id);

            Thread.sleep(5000);
            System.out.println("Sera que sou o server?");

            int serverId = server.getSemaphoreId();

            System.out.println(serverId);

            if(serverId == id){
                server.turnIntoClient();
                System.out.println("Eu sou o server agora");
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}