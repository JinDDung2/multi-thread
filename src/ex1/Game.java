package src.ex1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 금고 클래스 -> 비밀번호 맞는지 틀리는지 확인
// 경찰쓰레드 1개 : 10초 안에 해커가 안도망가면 해커 감옥으로
// 해커 쓰레드 -> 오름차순1개, 내림차순1개
public class Game {
    public static final int MAX_PASSWORD = 9999;
    public static void main(String[] args) {
        Random randomNum = new Random();

        Vault vault = new Vault(randomNum.nextInt(MAX_PASSWORD));

        List<Thread> threads = new ArrayList<>();
        threads.add(new AscendingHackerThread(vault));
        threads.add(new DescendingHackerThread(vault));
        threads.add(new PoliceThread());

        // 쓰레드 실행
        for (Thread thread : threads) {
            thread.start();
        }

    }

    static class Vault {
        private int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {}
            return this.password == guess;
        }
    }

    static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("쓰레드 시작! 이름 : " + this.getName());
            super.start();
        }
    }

    static class AscendingHackerThread extends HackerThread {

        public AscendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = 0; guess < MAX_PASSWORD; guess++) {
                if (vault.isCorrectPassword(guess)) {
                    System.out.println("금고가 열렸습니다. 비밀번호: " + guess + " 쓰레드 이름 : " + this.getName());
                    System.exit(0);
                }
            }
            super.run();
        }
    }

    static class DescendingHackerThread extends HackerThread {

        public DescendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = MAX_PASSWORD; guess >= 0; guess++) {
                if (vault.isCorrectPassword(guess)) {
                    System.out.println("금고가 열렸습니다. 비밀번호: " + guess + " 쓰레드 이름 : " + this.getName());
                    System.exit(0);
                }
            }
        }
    }

    static class PoliceThread extends Thread {
        // 10초에 1번씩 멈춤
        @Override
        public void run() {
            for (int i = 10; i >= 0; i--) {
                try {
                    Thread.sleep(1000 );
                } catch (InterruptedException e) {}
                System.out.println("rest time = " + i);
            }
            System.out.println("시간이 종료되었습니다. 남아 있는 해커는 감옥으로");
            System.exit(0);
        }
    }
}
