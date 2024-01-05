## finalizer와 cleaner 사용을 피하라

```
finalizer 와 cleaner는 자바에서 제공하는 객체 소멸자이다.
그런데 이것들은 기본적으로 쓰지 말아야 한다고 권고된다.
예측할 수 없고, 제때 실행됨이 보장되지 않는다. 수행 여부조차도 보장되지 않는다.

- finalizer는 가비지 컬렉터의 효율을 떨어뜨린다.
  이것을 달아두면 그 인스턴스의 자원 회수가 제멋대로 지연된다.
- finalizer 동작 중 발생한 예외는 무시된다.
  예외도 무시되고 그 순간 작업도 그대로 종료된다.
  
그래서 대신 AutoCloseable를 구현하고, 클라이언트가 이 close 메서드를 호출하게 한다.
그리고 이러한 finalizer 나 cleaner는 클라이언트가 이를 호출하지 않을 경우를 대비해
안전망으로 사용할 수 있다. 없는 것 보다는 나으니까

그리고 AutoColseable을 구현했다면
try-with-resources 블록을 사용할 수 있다.

try ( Car car = new Car()){
  System.out.println("hi");
}

이렇게 자동으로 close가 호출되어 다 쓴 참조 해제를 위한 구현된 코드가 실행된다. 

```
