import java.rmi.ConnectException;
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

        int number = (int) (Math.random() * 100);

        try {

            boolean added;
            do {
                System.out.println("Enviando numero: " + number);
                added = this.semaphore.addNumber(number);
            } while (!added);

            System.out.println("Aceito");

        }catch (ConnectException e){
            this.timer.cancel();
            this.timer.purge();

            restartElection();
        }
        catch (Exception e) {
            System.out.println("Produtor " + this.id + " nao conseguiu adicionar um numero " + e.getMessage());
        }
    }

    private void restartElection(){
        String[] type = {"produtor"};

        Node.main(type);
    }
}
