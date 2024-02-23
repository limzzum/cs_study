## @Override 애너테이션을 일관되게 사용하라

@Override 애너테이션은 우리가 가장 익숙한 애너테이션 중 하나다.
이 애너테이션은 상위 타입의 메서드를 재정의했음을 뜻한다.
하지만 상위타입을 재정의할 때 이 애너테이션이 필수는 아니다.

하지만 우리는 이런 실수를 범할 수 있다.
```
public class Bigram {
    private final char first;
    private final char second;
    
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }
}
```
여기서 우린 Object의 equals 메서드를 재정의할 것을 기대하고 코드를 작성하였다.
하지만 Object의 equals 메서드는 매개변수 타입이 Object이다. 그래서 이것은 재정의가 아닌 다중정의가 된다.

다음 그럼 @Override 애너테이션을 활용해보자.
```
public class Bigram {
    private final char first;
    private final char second;
    
    @Override
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }
}
```
이렇게 작성하면 컴파일러는 오류를 찾아줄 것이다.
그러니 상위 클래스의 메서드를 재정의하려면 @Override 애너테이션을 달아 실수를 예방하자.

인터페이스에서도 디폴트 메서드를 지원하기 때문에 @Override 메서드를 달아 일관성을 유지하고,
추상클래스나 인터페이스에서도 재정의하는 메서드를 알리고 추가된 메서드와 구분할 수 있도록 하자.
