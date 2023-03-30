package src;

public class RaceCondition {
    public static void main(String[] args) throws InterruptedException {
        Inventory inventory = new Inventory();
        IncreaseThread increaseThread = new IncreaseThread(inventory);
        DecreaseThread decreaseThread = new DecreaseThread(inventory);

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

        public void increase() {
            items++;
        }

        public void decrease() {
            items--;
        }

        public int getItems() {
            return items;
        }
    }
}
