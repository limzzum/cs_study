## Comparable을 구현할지 고려하라

Comparator에 유일 메서드로 compareTo가 있다.
이것을 구현한 클래스의 인스턴스들에 자연적인 순서를 부여한다.
검색, 극단값 계산, 자동정렬, 컬렉션 관리 등에 사용할 수 있다.

compareTo 메서드에도 일반 규약이 존재하는데 equals의 규약과 비슷하다.

```
- 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다. (대칭성)
- 첫 번째가 두 번쨰보다 크고 두 번째가 세 번째보다 크면 첫 번쨰는 세 번째보다 커야 한다. (추이성)
- 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야 한다. (일관성)
- (필수는 아니지만 중요) compareTo로 비교한 동치의 결과가 equals와 같아야 한다.
```

이러한 규약을 지키지 않으면 이를 사용하는 컬렉션에서 제대로 동작하지 않는다.
compareTo는 equals와 달리 모든 객체에 대해 전역 동치관계를 부여하지 않는다.
그렇기 때문에 타입이 다른 객체라면 오류를 던져 간단히 해결할 수 있다. 공통 인터페이스를 매개로 한 비교는 가능하다.

equals 와 마찬가지로 기존 클래스에서 확장한 구체 클래스는 규약을 만족할 수 없다.
기존 클래스를 필드로 가지는 새로운 클래스를 생성하고 뷰 메서드로 제공하면 된다.

마지막 규약을 살펴보면 compareTo와 equals의 동치성 결과가 같아야 한다
정렬 필요 컬렉션에서는 동치성 비교를 위해 equals가 아닌 compareTo를 사용한다.
그렇기 때문에 HashSet과 정렬된 컬렉션인 TreeSet을 사용할 때 다른 결과를 내게될 수 있다.

```
HashSet 에서의 비교 => equals를 통한 비교.
ex) BigDecimal("1.0") 과 BigDecimal("1.00")은 다른 값으로 인식. 2개의 원소를 갖는다.

TreeSet 에서의 비교 => compareTo 사용.
ex) BigDecimal("1.0") 과 BigDecimal("1.00")의 크기가 같은 것으로 판단. 1개의 원소를 갖는다.
```

다음으로 Comparable을 구현하려면 비교는 핵심 필드 순으로 하면 된다.

```
public int compareTo(Car car){
    int result = Integer.compare(price, car.price);
    if(result == 0){
        result = Integer.compare(engine, car.engine);
        if(result == 0 ){
            result = Integer.compare(wheel, car.wheel);
        }
    }
}

```
compareTo에서는 첫 번째 값이 두 번째 값보다 작으면 음수를, 같으면 0, 크면 양수를 반환한다.
이 예시처럼 가장 중요한 핵심 필드를 비교하고, 같으면(결과가 0이면) 다음 필드를 비교하는 방식으로 진행한다.

여기 예시에서 사용한 것처럼 Integer.compare(a,b)를 통해 자동으로 비교한 결과를 나타내 줄 수 있다.
이 메서드를 사용하지 않는다면 a < b 로 비교해 음수값을 리턴하도록 해야 한다.

이보다 더 간단한 Comparator 인터페이스를 사용할 수도 있다.
성능은 조금 더 느려지지만 메서드 연쇄 방식으로 비교자를 생성할 수 있다.

```
private static final Comparator<Car> COMPARATOR = 
    comparingInt((Car car) -> car.price)
        .thenComparingInt(car -> car.engine)
        .thenComparingInt(car -> car.wheel);
        
public int compareTo(Car car) {
    return COMPARATOR.compare(this, car);
}
```

이렇게 Comparator을 생성해 사용할 수 있고, thenComparingInt 부터는 자바의 타입 추론 능력 
덕분에 타입을 명시하지 않고 사용할 수 있다.

비교자를 구현할 때 '<' 나 '>' 연산자를 사용해서 비교하는 방법은 복잡하고,
정수 오버플로우를 일으키거나 부동소수점 계산 시에 오류를 낼 수 있다.

그 대신 정적 compare 메서드를 활용하거나 비교자 생성 메서드를 활용할 수 있다.

```
//정적 compare 메서드
static Comparator<Object> hashCodeOrder = new Comparator<>(){
    public int compare(Object o1, Object o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
}
=> Integer 말고도 자바 기본 숫자들을 모두 활용할 수 있다.

//Comparator에서 제공하는 비교자 생성 메서드
static Comparator<Object> hashCodeOrder =
    Comparator.comparingInt(o -> o.hashCode());
```
