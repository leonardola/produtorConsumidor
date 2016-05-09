import java.util.TimerTask;

/**
 * Created by leonardoalbuquerque on 06/05/16.
 */
public class Consumer extends TimerTask {

    SemaphoreMethods semaphore;
    int id;

    Consumer(SemaphoreMethods semaphore, int id) {
        this.semaphore = semaphore;
        this.id = id;
    }

    public void run() {

        try {
            boolean got = false;

            do {
                int number = this.semaphore.getNumber();

                if (number > 0) {
                    System.out.println("Numero recebido: " + number);
                    got = true;
                }

                if(number == SemaphoreMethods.SERVER_IS_DOWN){
                    Thread.sleep(2000);
                }
            } while (!got);

        } catch (Exception e) {
            System.out.println("Produtor " + this.id + " nao conseguiu adicionar um numero " + e.getMessage());
        }
    }

}
