# Java 멀티스레딩, 병행성 및 병렬 실행 프로그래밍 배우기

## 멀티쓰레드가 필요한 이유
1. Responsiveness
- 병행성 : 멀티쓰레드로 빠른 멀티태스킹을 구현하면 동시에 실행하는 듯한 착각(illusion)을 줌
- 병행성을 구현하기 위해 반드시 멀티 코어일 필요는 없음
2. Performance
- 멀티쓰레드가 성능에 미치는 영향은 짧은 시간 안에 복잡한 작업을 완료하는 것인데, 당연히 싱글 스레드 보다 더 많은 작업을 수행할 수 있음

## 멀티쓰레드를 사용할 때 주의사항
- 멀티쓰레드 프로그래밍은 기존 단일쓰레드의 순차적인 프로그래밍과는 다름. 멀티쓰레드를 사용하는 순간 바로 OO가 깨짐.

## 쓰레드와 컨텍스트 스위칭에 대한 이해
- 많은 쓰레드는 많은 컨텍스트 스위칭을 야기함
- 쓰레드가 프로세스보다 컨텍스트 스위칭 비용이 적음 (쓰레드는 일부 자원을 공유하기 때문)
- 같은 프로세스에 속한 쓰레드 끼리 컨텍스트스위칭 하는 비용 < 각각 다른 프로세스의 쓰레드 컨텍스트 스위칭 비용

## 쓰레드 생성 방법
- 쓰레드 클래스 객체를 인스턴스화 하여 Runnable을 생성자에 전달
```java
Thread thread = new Thread(new Runnable() -> ~~)
```
- Thread를 상속하는 클래스를 만듬
```java
class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("쓰레드 이름: " + this.getName());
        }
    }
```

## 데몬 쓰레드
1. 백그라운드에서 실행 -> 메인 쓰레드가 종료되더라도 애플리케이션은 종료 안됨
2. 앱의 주 기능 보다는 백그라운드 작업을 실행
3. 텍스트 편집기를 쓸 때 일정 시간마다 자동 저장되는 경우가 이러한 경우임
4. 데몬 쓰레드는 애플리케이션 종료와 독립적이어야 함

## 쓰레드 간의 협력
- 스핀락 -> CPU 낭비 -> 효율성 저하 (적절하지 않는 방법)
- wait 방식 활용 -> Thread.join()

### 주의할 점
- 다른 쓰레드의 실행 순서에 의존하지 않음
- 믿을만한 결과를 도출하려면 쓰레드 coordination 필요
- 한 쓰레드가 완료하는데 지나치게 오래걸리는 상황을 항상 고려해야함
- 위와 같은 상황을 해결하기 위해 join을 통해 기다리는 시간을 정해 두어야 함
- 제시간에 작업을 맞추지 못한 쓰레드는 멈춰야 한다.

## 멀티쓰레드의 Performance 측정 척도
- 지연시간 : 시간 단위의 측정이며, 하나의 작업이 완료되는 시간
- 처리량 : 일정 시간 동안의 처리한 작업의 양

## 멀티쓰레드 애플리케이션의 Performance
- 쓰레드 풀링 사용 -> 쓰레드 생성 및 관리에 드는 불필요한 시간 제거, 작업을 하위 작업으로 쪼개는 작업이 불필요
- 자바에서는 낮은 오버헤드와 효율적은 대기열을 구현

## 쓰레드 간의 리소스 공유
- 리소스 : 컴퓨터 프로그램 영역에서의 리소스는 데이터나 어떤 상태를 의미
- 쓰레드가 공유 가능한 리소스 -> 힙 메모리 / 쓰레드가 공유 불가능한 것 -> 스택 메모리(스택메모리 일부를 공유할 수는 있긴 함)

## 자바가 제공하는 synchornized
- 여러개의 쓰레드가 코드 블록이나 전체 메서드에 액세스 할 수 없도록 설계된 락킹 매커니즘
- 전체 메서드를 동기화할 필요는 없음 -> 실행하는데  필요한 최소한만 임계영역으로 설정 -> 더 많은 코드를 여러 쓰레드가 접근 가능 -> 성능 향상 가능

## ReentrantLock
- 객체에 적용된 synchornized 키워드 처럼 작동
- 명확한 locking과 unlocking 필요 (unlocking을 구현해놓지 않으면 버그 발생, unlocking을 구현한다 하더라도 예외로 인해 메서드 자체가 실행되지 않을 수도 있음)
  - finally 활용해서 예외가 터져도 unlock 실행
```java
Lock lockObject = new ReentrantLock();
Resource resource = new Resource();
...
public void method() {
    lockObject.lock();
    ...
    useMethod(resouce);
    lockObject.unlock();
}
```

```java
Lock lockObject = new ReentrantLock();
Resource resource = new Resource();
...
public void method() throws AnyException{
    lockObject.lock();
    ...
    try {
        AnyOperation();
        useMethod(resouce);
    }
    finally {
        lockObject.unlock();
    }
}
```
- lockInterruptibly() 활용 -> 락을 흭득하다가 중단된 쓰레드는 외부 인터럽트를 실행
- trylock() 활용
  - lock 메서드로 쓰레드를 중단할 수 없음 -> 실시간으로 쓰레드가 block 되는 걸 피할 수 있음
  - lock을 true, false로 구분
  - 많은 기능이 연결되지만, race condition 막을 수 있음
```java
if(lockObject.lock()){
        try{
        useMethod(resouce);
        }
        finally{
        lockObject.unlock();   
        }
}
```
## ReentrantReadWriteLock
  - readLock -> 여러개의 read 쓰레드가 공유 자원에 접근 가능
  - writeLock -> 한 개의 쓰레드만 공유 자원에 접근 가능
  - ReentrantLock으로만 설정한 lock 보다 300% 빠름

## semaphore
- 사용자 수를 특정 리소스나 리소스 그룹을 제한하는데 사용
- 리소스당 하나의 쓰레드만 접근하게 하는 락과 다름
- 사용자 수가 많든 적든 사용자 수를 임계영역이나 리소스에 제한할 수 있음
- 락은 한개만 접근을 허가해주는 세마포어라고 볼 수도 있음
- 어떤 쓰레드이든지 세마포어를 릴리즈(=쓰레드간 통신 수단) ex) 생산자 소비자 시나리오 실행
