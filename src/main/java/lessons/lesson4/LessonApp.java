package lessons.lesson4;

public class LessonApp {

    private final Object mon = new Object();
    private volatile char firstLetter = 'A';
    private final char[] letters = {'A', 'B', 'C'};
    private final int numberRepetitions = 5;

    public void printLittera(int position) {
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
        LessonApp app = new LessonApp();

        Thread t1 = new Thread(() -> app.printLittera(0));
        Thread t2 = new Thread(() -> app.printLittera(1));
        Thread t3 = new Thread(() -> app.printLittera(2));

        t1.start();
        t2.start();
        t3.start();

    }
}
