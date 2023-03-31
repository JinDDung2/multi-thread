package src;

import java.util.Random;

public class Metric {

    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        BusinessLogicThread businessLogicThread1 = new BusinessLogicThread(metrics);
        BusinessLogicThread businessLogicThread2 = new BusinessLogicThread(metrics);
        Printer printer = new Printer(metrics);

        businessLogicThread1.start();
        businessLogicThread2.start();
        printer.start();

    }

    public static class Printer extends Thread {
        private Metrics metrics;

        public Printer(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}

                double currentAverage = metrics.getAverage();
                System.out.println("currentAverage = " + currentAverage);
            }
        }
    }

    public static class BusinessLogicThread extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogicThread(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {

            while (true) {
                long start = System.currentTimeMillis();
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {}

                long end = System.currentTimeMillis();

                metrics.addSample(end - start);
            }
        }
    }

    public static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;

        // 개수가 추가되면 다시 평균 구하기
        private void addSample(long sample) {
            double currentSum =  average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        private double getAverage() {
            return average;
        }
    }
}
