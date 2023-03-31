package src;

public class RaceCondition {
    public static void main(String[] args) throws InterruptedException {
        Inventory inventory = new Inventory();
        IncreaseThread increaseThread = new IncreaseThread(inventory);
        DecreaseThread decreaseThread = new DecreaseThread(inventory);

        // Inventory 객체이기에 힙에 저장 -> 공유됨
        increaseThread.start();
        decreaseThread.start();

        increaseThread.join();
        decreaseThread.join();

        System.out.println("items = " + inventory.getItems());
    }

    public static class DecreaseThread extends Thread{
        private Inventory inventory;

        public DecreaseThread(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventory.decrease();
            }
        }
    }

    public static class IncreaseThread extends Thread{
        private Inventory inventory;

        public IncreaseThread(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventory.increase();
            }
        }
    }

    public static class Inventory {
        private int items = 0;

        Object lock = new Object();

        /**
         * 이 작업은 단일 작업인가? -> NO -> 3개의 작업임
         * 1. 메모리에 저장된 items 의 현재 값을 불러옴
         * 2. items++ 실행
         * 3. 변경된 결과 itmes 를 메모리에 저장
         */
        public void increase() {
            synchronized (this.lock) {
                items++;
            }
        }

        public synchronized void decrease() {
            synchronized (this.lock) {
                items--;
            }
        }

        public int getItems() {
            synchronized (this.lock) {
                return items;
            }
        }
    }
}
