package lessons.lesson4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessonApp {

    private static final Object mon = new Object();
    private static volatile char firstLetter = 'A';
    private static final char[] letters = {'A', 'B', 'C'};
    private static final int numberRepetitions = 5;

    public static void printLittera(int position) {
        synchronized (mon) {

            for (int i = 0; i < numberRepetitions; i++) {
                try {
                    while (firstLetter != letters[position]) {
                        mon.wait();
                    }
                    System.out.print(letters[position]);

                    firstLetter = letters[(position + 1) % letters.length];
                    mon.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(() -> printLittera(0));
        executorService.execute(() -> printLittera(1));
        executorService.execute(() -> printLittera(2));
        executorService.shutdown();

    }
}
