package src;

public class Automic {

    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.increase();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.checkDataRace();
            }
        });

        t1.start();
        t2.start();
    }

    public static class SharedClass{
        private volatile int x = 0;
        private volatile int y = 0;

        public void increase() {
            x++;
            y++;
        }

        public void checkDataRace() {
            if (y > x) {
                System.out.println("y > x -> DataRace is detected!!");
            };
        }
    }
}
