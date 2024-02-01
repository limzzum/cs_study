## 한정적 와일드카드를 사용해 API 유연성을 높이말

매개변수화 타입은 불공변이다. 
List<String>은 List<Object>의 하위 타입이 아니라는 뜻이다.
왜 안될까? 라는 생각이 들 수도 있지만 List<String>은 List<Object>가 하는 일을 제대로 수행하지
못하기 때문에 하위 타입이 될 수 없다. (리스코프 치환 원칙)

그렇지만 유연한 코드를 위해 String이 Object의 하위타입으로 인정이 된다면 좋은 경우도 있을 것이다.

```
public void pushAll(Iterable<E> src){
    for (E e : src )
        push(e);
}
```
이 코드는 스택에 원소들을 넣는 메서드이다.
만약 Stack<Number>로 선언하고 Integer 타입의 원소들을 넣으려 한다면 불공변의 특성 때문에 컴파일 오류가 날 것이다.
하지만 Integer는 Number의 하위타입이니 잘 동작하길 기대하는데, 이를 위해 한정적 와일드카드 타입을 사용할 수 있다.

E의 Iterable 타입을 추가하는 것이 아닌, E의 하위 타입의 Iterable을 추가한다고 하면 되는 것이다.
```
    Iterable<? extends E>
```
바로 이렇게 원하던 대로 작성할 수 있다.

또 다른 예로 E의 하위타입이 아닌 상위 타입으로 한정할 수도 있다.
스택의 popAll 메서드를 살펴보자.

```
public void popAll(Collection<E> dst){
    while(!isEmpty())
        dst.add(pop());
}
```
이 메서드는 매개변수로 받은 dst로 원소들을 모두 옮긴다.
그런데 만약 Stack<Number>에서 Object용 컬렉션으로 옮기려고 한다면
```
    Stack<Number> numberStack = new Stack<>();
    Collection<Object> objects = ...;
    numberStack.popAll(objects);
```
이렇게 작성할 수 있다. 그리고 Collection<Object>는 Collection<Number>의 하위 타입이 아니므로 컴파일 오류가 난다.

이럴때 상위타입을 지정하는 한정적 와일드카드 타입을 사용할 수 있다.
```
    Collection<? super E dst>
```
이것은 E의 상위타입의 Collection을 말한다. (모든 타입은 자기 자신의 하위타입이며, 상위타입이다)

이렇게 와일드카드를 적용함으로써 유연해진 코드를 살펴보자.
```
    public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)
    
    Set<Integer> integers = Set.of(1,3,5);
    Set<Double> doubles = Set.of(2.0, 4.0, 6.0);
    Set<Number> numbers = union(integers, doubles);
```

Set<? extends E>로 와일드 카드를 적용함으로써 더욱 유연한 설계가 가능해졌다.
Set<Integer>에서는 Integer의 하위타입들을 허용하고, Set<Double>는 Doubles의 하위타입,
Set<Number>에서는 Number의 하위타입인 Integer와 Doubles를 모두 포함하므로 구조적인 설계를 할 수 있다.