package src;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("쓰레드 실행 중! 이름: " + Thread.currentThread().getName());
            System.out.println("현재 쓰레드 우선순위: " + Thread.currentThread().getPriority());
        });

        thread.setName("new worker");
        thread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("이름: " + Thread.currentThread().getName() + " 새로운 쓰레드 시작 전");
        thread.start();
        System.out.println("이름: " + Thread.currentThread().getName() + " 새로운 쓰레드 시작 ");

        // 쓰레드를 재운다 -> 이 쓰레드는 그 시간동안 CPU를 쓰지 않는다
//        Thread.sleep(5000);

        // 운영체제에 의해 비동기적으로 작동함 -> 그래서 1.시작전 2.시작 3.실행 중 순으로 출력
//        이름: main 새로운 쓰레드 시작
//        이름: main 새로운 쓰레드 시작 전
//        쓰레드 실행 중! 이름: new worker
    }

}
