## 생성자에 매개변수가 많다면 빌더를 고려하라

```
먼저 기본 생성자 방식을 살펴보면
  => new Apple(1, 0, "사과"); 이런식으로 생성을 했을 것이다.
  
그런데 1의 의미가 뭔지, 0은 뭔지, "사과"는 무엇을 의미하는지 알기 어렵다.
또 생성에 필요한 인자값들을 확인하기 위해 Apple클래스를 확인하는 경험도 종종 있다.

여기서 매개변수가 더 늘어나거나, 경우에 따라 부분적으로 필요한 매개변수도 있을 것이다.
이런 경우 예전에는 점층적 생성자 패턴을 사용하기도 하였다.

```

```
// 점층적 생성자 패턴 사용 예시
public class Pizza {
    private int size;
    private boolean cheese;
    private boolean pepperoni;
    private boolean bacon;

    public Pizza(int size) {
        this.size = size;
    }

    public Pizza(int size, boolean cheese) {
        this(size);
        this.cheese = cheese;
    }

    public Pizza(int size, boolean cheese, boolean pepperoni) {
        this(size, cheese);
        this.pepperoni = pepperoni;
    }

    public Pizza(int size, boolean cheese, boolean pepperoni, boolean bacon) {
        this(size, cheese, pepperoni);
        this.bacon = bacon;
    }

}
이 방법은 코드가 길어지고 생성자가 더 많아지면 더욱 길어져 읽기 어려워진다.

```

```
그 다음으로 자바빈즈 패턴이 있다.

public class Pizza {
    private int size;       // 필수
    private boolean cheese; // 선택
    private boolean pepperoni; // 선택
    private boolean bacon; // 선택

    public Pizza(int size) {
        this.size = size;
    }

    public void setCheese(boolean cheese) {
        this.cheese = cheese;
    }

    public void setPepperoni(boolean pepperoni) {
        this.pepperoni = pepperoni;
    }

    public void setBacon(boolean bacon) {
        this.bacon = bacon;
    }

}
매개변수가 없는 기본 생성자를 만들거나, 필수인 매개변수의 생성자를 만들고 나머지는 set메서드를 통해
값을 초기화하는 것이다.

객체 생성 코드를 보면 
Pizza pizza = new Pizza(12);
pizza.setCheese(true);
pizza.setPepperoni(false);
pizza.setBacon(true);
이렇게 만들 수 있다.

이것의 심각한 문제는 원하는 객체를 만들기 전까지 일관성이 깨지게 된다.
클래스를 불변으로 만들 수 없으며, 여러 스레드에서 동시에 접근하게 되면 스레드 안정성도 떨어잔다.
또 디버깅도 어려워지고 오류의 원인을 찾기 어려워진다.
```

```
이를 해결할 수 있는 것이 빌더 패턴이다.
자바빈즈 패턴의 set 메서드와 같이 필요한 것들만 할당하면서 일관성도 유지할 수 있다.
생성자는 private으로 만들고 정적 멤버 클래스인 Builder를 통해 객체를 만들어 반환하게 할 수 있다.

public class Car {
    private final String make;
    private final String model;

    private int year = 0;
    private String color = "unknown";

    private Car(Builder builder) {
        this.make = builder.make;
        this.model = builder.model;
        this.year = builder.year;
        this.color = builder.color;
    }

    public static class Builder {
        // 필수 매개변수
        private final String make;
        private final String model;

        // 선택적 매개변수 - 기본값으로 초기화
        private int year = 0;
        private String color = "unknown";

        public Builder(String make, String model) {
            this.make = make;
            this.model = model;
        }

        public Builder year(int val) {
            year = val;
            return this;
        }

        public Builder color(String val) {
            color = val;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }

    // Car 객체 사용 예
    public static void main(String[] args) {
        Car car = new Car.Builder("Hyundai", "Sonata")
                        .year(2020)
                        .color("Blue")
                        .build();
    }
}

이렇게 Builder를 통해 객체를 생성함으로써 일관성을 유지할 수 있게 됐다.
setter 메서드들이 빌더 자신을 반환하기 때문에 연쇄적인 호출이 가능해진다.

빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다.
  - 빌더 패턴은 복잡한 객체를 단계별로 구축할 수 있게 해주는 디자인 패턴이기 때문에 계층적으로 설계된 클래스와 함께 쓰기에 좋다.
    부모 클래스의 특성을 상속받으면서도 자신만의 추가적인 특성을 가지고 있는 구조에서 빌더 패턴을 사용하면,
    객체의 생성 과정을 더 명확하게 파악하고 유연하게 관리할 수 있고, 코드의 가독성과 유지보수성을 크게 향상시킨다.

```