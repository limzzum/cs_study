## ordinal 인덱싱 대신 EnumMap을 사용하라

ordinal 메서드는 열거 타입 상수의 선언 순서를 반환해준다.
이것은 마치 인덱스를 표현해주는 느낌인데, 이 메서드를 상수들의 배열 인덱스로 사용하려고 한다면 많은 문제가 따른다.

```
- 배열은 각 인덱스의 의미를 모른다.
  - 출력 시 결과의 의미를 직접 레이블로 달아야 한다.

- 정수값이 정확한 값이라는 것을 보증해야 한다.
  - 정수는 열거 타입과 달리 타입 안전하지 않다. 잘못된 값을 사용해도 컴파일러는 알지 못한다.
```

이를 대신하여 열거 타입을 키로 사용하도록 설계한 Map의 구현체인 EnumMap이 있다.
```
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = 
    new EnumMap<>(Plant.LifeCycle.class);
    
for (Plant.LifeCycle lc : Plant.LifeCycle.values())
    plantsByLifeCycle.put(lc, new HashSet<>());
```

이렇게 사용할 수 있는데 짧고 명료하고 맵의 키인 열거 타입이 그 자체로 출력용 문자열을 제공해준다.
내부에서는 배열을 사용하지만 구현 방식을 숨겨 타입 안정성과 성능을 모두 얻어냈다.
배열 인덱스를 계산하는 과정에서 오류가 날 일도 없어졌다.

좀 더 복잡한 상황에서의 예시를 살펴보자.

```
public enum Phase {
    SOLID, LIQUID, GAS;
    
    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;
        
        private static final Transition[][] TRANSITIONS = {
            { null, MELT, SUBLIME },
            { FREEZE, null, BOIL },
            { DEPOSIT, CONDENSE, null }
        };
        
        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```
배열로 상태를 저장하고 ordinal 메서드를 배열의 인덱스로 이용하고 있다.
컴파일러는 ordinal과 배열 인덱스의 관계를 알지 못하고, 열거 타입을 수정하며 오류를 낼 가능성이 높다.
또 빈값은 null로 채워넣어야 한다.

이것을 EnumMap을 사용하여 표현하면
```
public enum Phase {
    SOLID, LIQUID, GAS;
    
    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);
        
        private final Phase from;
        private final Phase to;
        
        private static final Map<Phase, Map<Phase, Transition>>
          m = Stream.of(values()).collect(groupingBy(t -> t.from,
            () -> new EnumMap<>(Phase.class),
            toMap(t -> t.to, t -> t,
                (x, y) -> y, () -> new EnumMap<>(Phase.class))));
                
        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```
이렇게 표현할 수 있다.
새로운 상태 PLASMA를 하나 추가하려면 Phase에 상수를 하나 추가한 후,
Transition에 기체에서 플라스마로 변화는 이온화(IONIZE), 플라스마에서 기체를 변화는 탈이온화(DEIONIZE)만 추가하면 된다.

```
public enum Phase {
    SOLID, LIQUID, GAS, PLASMA;
    
    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
        IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
    
    ...
    }
}
```
이렇게 간단히 새로운 상수를 추가할 수 있는데 내부에서는 맵들의 맵이 배열들의 배열로 구현되고 있으니
낭비되는 공간과 시간도 거의 없이 명확하고 안전하고 유지보수 하기 좋다.


