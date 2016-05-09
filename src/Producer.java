import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by leonardoalbuquerque on 06/05/16.
 */
public class Producer extends TimerTask {

    SemaphoreMethods semaphore;
    int id;
    Timer timer;

    Producer(SemaphoreMethods semaphore, int id, Timer timer) {
        this.semaphore = semaphore;
        this.id = id;
        this.timer = timer;
    }

    public void run() {

        try{
            int serverId = semaphore.getServerId();

            if(serverId == this.id){
                System.out.println("Eu sou o novo servidor");
                System.out.println("Eu sou o novo servidor");
                System.out.println("Eu sou o novo servidor");
                System.out.println("Eu sou o novo servidor");
                System.out.println("Eu sou o novo servidor");
                System.out.println("Eu sou o novo servidor");

                timer.cancel();
                timer.purge();

                new Server(id);

                Thread.sleep(10000);
                semaphore.setServerIsUp();

                return;
            }
        }catch (Exception e){
            System.out.println("Erro ao verificar id do servidor" + e.getMessage());
        }

        int number = (int) (Math.random() * 100);

        try {

            boolean added = false;
            do {
                System.out.println("Enviando numero: " + number);
                added = this.semaphore.addNumber(number);
            } while (!added);

            System.out.println("Aceito");

        } catch (Exception e) {
            System.out.println("Produtor " + this.id + " nao conseguiu adicionar um numero " + e.getMessage());
        }
    }
}
