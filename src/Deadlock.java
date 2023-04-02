package src;

import java.util.Random;

public class Deadlock {
    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread aThread = new Thread(new TrainA(intersection));
        Thread bThread = new Thread(new TrainB(intersection));

        aThread.start();
        bThread.start();
    }

    public static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
                intersection.takeRoadA();
            }
        }
    }

    public static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random;

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
                intersection.takeRoadB();
            }
        }
    }

    public static class Intersection {
        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("roadA는 락 상태 " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("roadB 지나가는 중.");
                    try {
                        Thread.sleep(1);
                    }catch (InterruptedException e) {}
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("roadA는 락 상태 " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("roadB 지나가는 중.");
                    try {
                        Thread.sleep(1);
                    }catch (InterruptedException e) {}
                }
            }
        }
    }
}
