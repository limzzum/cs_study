## try-finally보다는 try-with-resources를 사용하라

```
전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다.

하지만
  - 자원이 많아질 수록 중첩이 늘어나고 지저분해진다. 가독성이 떨어진다.
  - try문과 finally문 두 곳에서 예외가 나면 첫번째 예외인 try문의 예외 정보가 남지 않게 된다.

이를 대신해 try-with-resources를 쓴다면 이러한 문제를 해결할 수 있다.
우선 try-with-resources를 쓰기 위해서는 해당 자원이 AutoCloseable 인터페이스를 구현해야 한다.
자바 라이브러리에서 제공하는 대부분의 클래스와 인터페이스는 이미 구현이 되어있다.

try ( Car car = new Car(); Home home = new Home()){
  System.out.println("hi");
}

이렇게 자원이 두 개 이상이 되어도 try-finally를 중첩으로 쓰지 않고 깔끔하게 표현할 수 있다.
또 catch 절도 사용할 수 있다.
두번째로는 try문과 close 호출 양쪽에서 예외가 발생하면 try문의 예외를 기록함으로써
필요한 예외를 잘 확인할 수 있게 되었다.
```