## private 생성자나 열거 타입으로 싱글턴임을 보증하라

```aidl

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.
생성자를 private으로 지정함으로써 외부에서 접근을 못하게 막고 유일한 인스턴스를 다른 방식으로 제공할 수 있다.

첫 번째로 public static final 필드 방식의 싱글턴이 있다.
  
  public Car {
    public static final Car INSTANCE = new Car();
    private Car(){}
  }
  
  이렇게 static 필드가 초기화 될 때 생성자가 한 번 호출되고, final로 지정함으로써 유일한 인스턴스임이 보장된다.
  
두 번째로 정적 팩터리 방식의 싱글턴이 있다.

  public Car {
    private static final Car INSTANCE = new Car();
    private Car(){}
    public static Car getInstance(){
      return INSTANCE;
    } 
  }
  
  위와 비슷한데 이것은 메서드를 통해 인스턴스를 반환한다.
  
  public 필드 방식은 명백히 싱글턴임을 알 수 있다. 그리고 간결하다.
  정적 팩터리 방식은 반환값을 변경함으로써 싱글턴이 아니게 할 수도 있고 변경에 자유롭다.
  또 객체 생성시점을 컨트롤 할 수 있고, 제네릭 싱글턴 팩터리로 만들 수도 있다.
  
  그러나 이러한 두 방식은 직렬화 -> 역직렬화 과정에서 새로운 인스턴스가 만들어지는 문제갸 있다.
  직렬화 과정에서 객체의 상태가 바이트 스트림으로 바뀌고, 역직렬화 과정에서 이 정보를 바탕으로 Java는 
  새로운 객체를 생성한다.
  이를 막기 위해서는 
  private Object readResolve() {
    // 싱글턴 인스턴스 반환
    return INSTANCE;
  }
  이 메서드를 구현해 주면 된다.
  직렬화 과정에서 자동으로 인식하고 역직렬화 할 때 이 메서드를 호출하여 새로운 객체를 생성하지 않는다.
 
  이 외로 자바 언어 타입에서 싱글턴임을 보장하는 열거 타입 (Enum) 이 있다.
  원소가 하나인 열거 타입을 선언하여 싱글턴 패턴의 구현으로 활용할 수 있다.
  
  public enum Singleton {
    INSTANCE;
    public void doSomething() {}
  }

  열거 타입은 직렬화 상황이나 리플렉션 공격에서도 끄떡없이 싱글턴임이 보장된다.

*리플렉션 API는 접근할 수 없는 클래스 내부에 접근하고 수정할 수 있는 기능을 제공하는데,
 이것을 사용해 private 생성자를 호출할 수 있게됩니다.
 이러한 문제를 막기 위해 생성자에서 두 번 이상 호출되면 예외를 던지게 할 수 있습니다*
```