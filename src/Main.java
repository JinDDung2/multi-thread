package src;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        NewThread thread = new NewThread();
        thread.start();
    }

    private static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("쓰레드 이름: " + this.getName());
        }
    }

}
