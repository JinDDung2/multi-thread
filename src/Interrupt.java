package src;

import java.math.BigInteger;

public class Interrupt {
    public static void main(String[] args) {
        Thread blockingThread = new Thread(new BlockingThread());

        blockingThread.start();

        blockingThread.interrupt();

        Thread comThread = new Thread(new LongComputationThread(new BigInteger("10000000"), new BigInteger("1000000000")));
        comThread.start();
        comThread.interrupt();
    }

    static class BlockingThread implements Runnable {
        @Override
        public void run() {
            // InterruptedException 을 만나면 종료
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("블로킹 쓰레드 종료");
            }
        }
    }

    static class LongComputationThread implements Runnable {
        private BigInteger base;
        private BigInteger power;

        public LongComputationThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + "= " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger rst = BigInteger.ONE;

            // 시간이 오래 걸릴 작업 -> 인터럽트
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("작업이 오래걸릴거라 인터럽트 할거야!");
                    return BigInteger.ZERO;
                }
                rst = rst.multiply(base);
            }
            return rst;
        }
    }
}
