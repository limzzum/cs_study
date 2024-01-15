## clone 재정의는 주의해서 진행하라

Cloneable 은 복제해도 되는 클래스임을 명시하는 용도의 믹스인 인터페이스이다.
믹스인 인터페이스는 부가 기능을 더해주는 것이라고 생각하면 된다.

Cloneable은 사용방식이 조금 특이한데 Object의 protected 메서드인 clone의 동작 방식을 결정한다.
Cloneable을 구현하지 않은 클래스에서 clone을 호출하면 ClassNotSupportedException을 던진다.

clone 메서드는 값을 복사해 새로운 객체를 만들어준다.
```
그렇기 때문에 이와 같은 식이 성립한다. 
x.clone() != x  // true
x.clone().equals(x) // true
```

clone을 통한 복사는 원본객체와 독립적이다.
그런데 이것은 생성자 호출없이 객체를 생성하는 위험하고 모순적인 매커니즘이 탄생하는 것이다.

또 Cloneable을 구현하면 super.clone()을 통해 상위클래스인 Object의 객체를 반환하게 된다.
그렇게 되면 클라이언트에서 형변환이 필요하게 되므로 재정의 하여 현재 클래스를 반환하도록 하자.
자바는 공변 반환 타이핑을 지원하기 때문에 재정의가 가능하다.

그런데 구현 클래스가 가변 객체를 참조한다면 재정의는 필수이다.
clone 메서드는 생성자 효과를 내는데 생성자는 원본에 해를 끼치지 않고 불변식을 보장해야 하기 떄문이다.
재정의 없이 사용하게 된다면 가변 객체는 값만 복사되지 않기 때문에 원본 인스턴스와 공유를 하게 될 것이다.

이를 위해 내부 정보 복사가 필요하다.

```
public class Car {
    private int price;
    private Object[] elements; 
}

=> price는 값 복사가 되지만 elements는 내부 복사가 필요하다.

//다시 재정의를 해보자.
@Override public Car clone() {
    try {
        Car result = (Car) super.clone();
        result.elements = elements.clone();
        return result;
    } catch (CloneNotSupportedException e ) {
        throw new AssertionError();
    }
}

```
이런식으로 elements의 내부를 복사할 수 있다.
하지만 elements가 final 이었다면 이 방법은 사용할 수 없다. final을 제거해야 한다..
가변 객체를 참조하는 필드는 final로 선언하라는 일반 용법과도 충돌한다. 

또 이것보다 가변 객체 안 가변 객체를 가지고 있는 경우는 재귀호출이 필요할 수 있다.
해시 테이블의 예시이다.

```
public class HashTable implements Cloneable {
    private Entry [] buckets = ...;
    
    private static class Entry {
        final Object key;
        Object value;
        Entry next;
        
        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
```
해시테이블은 버킷들의 배열이 있고, 각 버킷 안에는 엔트리가 들어있다.
엔트리가 가리키는 연결 리스트까지 재귀적으로 복사해주어야 한다.

우선 해시테이블에 엔트리를 복사하는 메서드를 추가한다.
```
public class HashTable implements Cloneable {
    private Entry [] buckets = ...;
    
    private static class Entry {
        final Object key;
        Object value;
        Entry next;
        
        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    Entry deepCopy() {
        return new Entry(key, value,
            next == null ? null : next.deepCopy());
    }
    
    @Override public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for ( int i = 0; i < buckets.length; i++)
                if (buckets[i] != null)
                    result.buckets[i] = buckets[i].deepCopy();
            return result;
        } catch (CloneNotSupprotedException e) {
            throw new AssertionError();
        }
    }        
        
}
```

이렇게 재귀호출을 통해 원본 객체와는 독립되게 새로운 객체를 복사할 수 있다.
연결리스트의 크기가 크면 스택오버플로우 위험이 있기 때문에 반복 복사를 해줄 수 있다.

```
//반복 복사
Entry deepCopy() {
    Entry result = new Entry(key, value, next);
    for (Entry p = result; p.next != null; p = p.next)
        p.next = new Entry(p.next.key, p.next.value, p.next.next);
    return result;
```

* 마지막 정리 *
```
- clone은 생성자와 같은 역할을 하기 때문에 마찬가지로 재정의 가능 메서드는 호출하면 안된다.
- 상속용 클래스에서는 Cloneable을 구현하지 않고 하위클래스에서 사용하도록 해야한다. 혹은 사용하지 못하게 막을 수는 있다.
- Cloneable을 구현한다면 재정의는 필수이다. 동기화를 위한 부수 작업을 위해 or 깊은 복사를 위해
- 재정의 할 때 접근제한자는 public, 반환 타입은 클래스 자신으로 변경하자.
```

clone 메서드를 사용하는 것은 복잡하고, 생성자 없이 객체를 생성하는 위험한 매커니즘이다.
이미 Cloneable을 구현한 클래스를 확장하는 경우가 아니라면 복사 생성자, 복사 팩터리와 같은 방법을 사용할 수 있다.

```
// 복사 생성자
public Car(Car car){...}:

// 복사 팩터리
public static Car getInstance(Car car){...};
```

복사 생성자는 자신과 같은 클래스의 인스턴스를 인수로 받는 것이다.
이러한 방법은 앞서 문제되었던 생성자 없이 객체를 생성, 가변객체 final 필드 용법과 충돌, 형변환이
필요했던 문제를 모두 해결해준다. 또 해당 클래스가 구현한 인터페이스 타입의 인스턴스를 인수로 받을 수 있다.