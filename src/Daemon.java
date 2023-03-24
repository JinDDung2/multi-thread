package src;

import java.math.BigInteger;

/**
 * 데몬 쓰레드
 * 1. 백그라운드에서 실행 -> 메인 쓰레드가 종료되더라도 애플리케이션은 종료 안됨
 * 2. 앱의 주 기능 보다는 백그라운드 작업을 실행
 * 3. 텍스트 편집기를 쓸 때 일정 시간마다 자동 저장되는 경우가 이러한 경우임
 * 4. 데몬 쓰레드는 애플리케이션 종료와 독립적이어야 함
 */
public class Daemon {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Interrupt.LongComputationThread(new BigInteger("10000000"), new BigInteger("1000000000")));

        thread.setDaemon(true);
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
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

            // 시간이 오래 걸릴 작업 -> 메인이 종료 되더라도 백그라운드에서 계속 작업하게 할 예정
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                rst = rst.multiply(base);
            }
            return rst;
        }
    }
}
