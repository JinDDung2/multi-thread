package src;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Join {
    public static void main(String[] args) throws InterruptedException {
        List<Long> numbers = Arrays.asList(0L, 1000L, 100000000L, 200L, 300L, 45L, 400L);

        List<FactorialThread> threads = new ArrayList<>();

        for (Long number : numbers) {
            threads.add(new FactorialThread(number));
        }

        for (FactorialThread thread : threads) {
            // 데몬을 해놓아서 애플리케이션이 종료 가능 -> 긴 쓰레드 1개 빼고 전부 완료
            thread.setDaemon(true);
            thread.start();
        }

        for (FactorialThread thread : threads) {
            // join할 때 제한 시간 꼭 넣기
            thread.join(2000);
        }

        for (int i = 0; i < threads.size(); i++) {
            FactorialThread factorialThread = threads.get(i);
            if (factorialThread.isDone()) {
                System.out.println("thread inputNum: " + numbers.get(i) + " result: " + factorialThread.getResult());
            } else {
                System.out.println("thread inputNUm: " + numbers.get(i) + " 은 숫자 계산 중!!");
            }
        }
    }

    static class FactorialThread extends Thread {
        private long inputNum;
        private BigInteger result = BigInteger.ZERO;
        private boolean isDone = false;

        public FactorialThread(long inputNum) {
            this.inputNum = inputNum;
        }

        @Override
        public void run() {
            this.result = factorial(inputNum);
            this.isDone = true;
        }

        public BigInteger factorial(long n) {
            BigInteger tempResult = BigInteger.ONE;
            for (long i = n; i > 0; i--) {
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }
            return tempResult;
        }

        public BigInteger getResult() {
            return result;
        }

        public boolean isDone() {
            return isDone;
        }
    }
}
