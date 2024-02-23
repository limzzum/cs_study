## int 상수 대신 열거 타입을 사용하라

자바에는 특수 목적의 참조 타입인 열거타입이 있다.
열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다.
```
열거 타입의 예
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
```

만약 열거 타입을 안쓴다면 정수 상수를 한 묶음으로 선언해 사용할 수 있다.
(열거 타입을 지원하기 전에는 이렇게 사용했다.)

```
public static final int CLASS_APPLE = 0;
public static final int CLASS_ORANGE = 1;
public static final int CLASS_BANANA = 2;
```

이러한 정수 열거 패턴의 단점은
```
- 타입 안전을 보장하지 못한다.
    - 이건 그냥 값일 뿐, 이 자체로 타입이 아니기 때문에 작성자의 의도와 다르게 실수로 작성하여도, 컴파일러는 알아채지 못한다.

- 별도의 이름 공간을 지원하지 않는다.
    - 이름 앞에 CLASS를 붙인 것처럼, 이름 충돌을 방지하기 위해 상수의 이름앞에 구분을 추가해 주어야 한다.

- 상수의 값이 바뀌면 클라이언트는 다시 컴파일 해주어야 한다.
    - 이것은 평범한 상수이기 때문에 컴파일할 경우 클라이언트 파일에 값이 그대로 새겨진다.

- 문자열로 출력하기 까다롭다.
    - 정수 상수는 그저 값일 뿐이기 때문에 출력하면 의미를 알 수 없는 그저 값이다.
    - 같은 열거 그룹에 속한 상수를 순회할 수도 없고, 그룹 안 상수가 몇 개인지도 알 수 없다.
   
```

이러한 정수 열거 패턴의 단점들을 없애주는 것이 열거 타입이다.
```
열거 타입의 예
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
```
클래스의 형태이고, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다.
열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 불변을 보장한다.
그렇기 때문에 열거타입은 싱글톤이고, 싱글톤은 원소가 하나뿐인 열거타입이라 할 수 있다.

열거 타입의 장점을 살펴보면
```
- 열거 타입은 컴파일타임 타입 안전성을 제공한다.
    - 열거 타입은 그 자체로 타입으로 활용할 수 있기 때문에 Apple 열거 타입을 매개변수로 받는 메서드를 작성했으면,
      다른 값을 전달하려 할 경우 컴파일 오류를 낸다.

- 열거 타입에는 각자의 이름공간이 있기 때문에 이름이 같은 상수도 평화롭게 공존한다.
    - public enum Apple { mango }
    - public enum Banana { mango }
    
- 상수롤 새로 추가하거나 순서를 바꿔도 다시 컴파일 하지 않아도 된다.
    - 공개되는 것은 오직 필드의 이름뿐이다. 값 자체가 클라이언트에 각인되지 않는다.
    
- 열거 타입의 toString 메서드는 의미있는 문자열을 출력한다.
```

열거 타입은 이런 간단한 형태 말고도 임의의 메서드나 필드를 추가할 수 있다.

```
public enum Day {
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday", 4),
    FRIDAY("Friday", 5),
    SATURDAY("Saturday", 6),
    SUNDAY("Sunday", 7);

    private final String displayName;
    private final int value;

    Day(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }

    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }
}

```
Day라는 열거타입에 월화수목금 정보를 저장하고 이름과, 1부터 7까지의 수를 필드로 저장한다.
isWeekend라는 메서드도 추가하여 주말인지 확인할 수도 있다.
이렇게 연관있는 상수들을 모아 열거 타입으로 만들 수 있고, 연관된 필드, 메서드들을 추가할 수 있다.
열거 타입은 근본적으로 불변이라 모든 필드는 final 이어야 한다.

열거 타입은 values를 제공하기 때문에 열거 타입 안에 정의된 상수들의 값을 배열로 받을 수 있다.
상수가 추가되거나 제거될 경우에는 클라이언트는 컴파일 시 컴파일 오류를 통해, 혹은 런타임에 확인할 수 있다.

그런데 만약 열거 타입에 정의된 상수마다 다른 동작을 하게 하려면 어떻게 해야할까?
```
- 메서드 안에서 현재 타입을 확인하고, 분기별로 동작하게 할 수 있지만 이것은 보기 좋지 않다.
  또, 상수가 추가될 경우 항상 분기를 추가해 주어야 한다. 깨지기 쉬운 코드이다.
```

열거 타입은 상수별로 다르게 동작하는 코드를 구현하는 수단을 제공한다.

```
public enum Operation {
    PLUS {public double apply(double x, double y){return x + y;}},
    MINUS {public double apply(double x, double y){return x - y;}},
    TIMES {public double apply(double x, double y){return x * y;}},
    DIVIDE {public double apply(double x, double y){return x / y;}};
    
    public abstract double apply(double x, double y);
}
```
이렇게 추상 메서드를 선언하고 각 상수별 클래스 몸체에서 재정의하면 된다.
추상 메서드를 재정의 하지 않으면 컴파일러가 재정의해야 한다고 알려줄 것이다.
toString 도 재정의 할 수 있는데 이 경우에는 toString이 반환하는 문자열을 열거타입 상수로 변환해주는 
fromString 메서드도 함께 제공해줄 수 있을 것이다.

```
fromString 메서드는 toString으로 표현되는 문자열을 통해 열거 타입에 정의된 상수 값을 반환해주는 것이다.

private static final Map<String, Operation> stringToEnum = 
        Stream.of(values()).collect(
            toMap(Object::toString, e -> e));
            
public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringtoEnum.get(symbol);
}
```
이렇게 제공해 줄 수 있다.
stringToEnum에서 values 메서드가 반환하는 배열 대신 스트림을 이용하였는데
```
- 새ㅇ성자가 실행되는 시점은 정적 필드가 초기화 되기 전이다.
    - 열거 타입의 생성자에서 접근할 수 있는 것은 상수 변수 뿐이다.
- 열거 타입의 각 상수들은 public static final 필드로 선언한 것과 같다.
      그렇기 때문에 생성자에서는 자기 자신도 참조할 수 없고, 같은 열거 타입인 다른 상수에도 접근할 수 없다.
```

상수별 메서드를 구현할 때는 코드를 공유하기 어렵다는 문제가 있는데(switch 문 사용하게 됨) 이를 전략을 선택하는 방법으로 해결할 수 있다.

```
//전략 열거 타입 패턴
enum TrafficLight {
    RED(Strategy.STOP),
    YELLOW(Strategy.STOP),
    GREEN(Strategy.GO);

    private final Strategy strategy;

    TrafficLight(Strategy strategy) {
        this.strategy = strategy;
    }

    public void executeStrategy() {
        strategy.performAction();
    }

    enum Strategy {
        STOP {
            @Override
            void performAction() {
                System.out.println("Stop");
            }
        },
        GO {
            @Override
            void performAction() {
                System.out.println("Go");
            }
        };

        abstract void performAction();
    }
}

```
중첩 열거 타입을 선언하여 새로운 상수를 추가할 때 전략을 선택해주도록 하였다.
새로운 상수를 추가하며 전략을 선택하는 것을 까먹을 수가 없고, switch 문을 통해 여러 분기로 나타내며 코드를 망치는 일도 없게됐다.

가끔은 그냥 swtich문을 사용하는 것이 더 나을 때도 있는데
```
//계산을 위한 PLUS, MINUS, TIMES, DIVIDE 상수가 있는 열거타입의 일부 예시이다
public static Operation inverse(Operation op) {
    switch(op) {
        case PLUS: return Operation.MINUS;
        case MINUS: return Operation.PLUS;
        case TIMES: return Operation.DIVIDE;
        case DIVIDE: return Operation.TIMES;
        
        default: throw new AssertionsError("알 수 없는 연산: "+ op);
    }
}   
```
이 메서드는 반대 연산을 반환하는 메서드이다. 
여기서는 굳이 중첩 열거 타입을 선언할 필요가 없다.

열거 타입 사용은
```
필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합일 때 사용한다.
열거 타입은 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계되었기 때문에 정의된 상수 개수가 영원히 불변일 필요는 없다.
```