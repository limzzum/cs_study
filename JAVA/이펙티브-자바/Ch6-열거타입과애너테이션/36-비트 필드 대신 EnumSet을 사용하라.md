## 비트 필드 대신 EnumSet을 사용하라

열거한 값들을 집합으로 표현하기 위해 정수 열거 패턴으로, 서로 다른 2의 거듭제곱 값을 할당하여 비트 필드로 만들어 사용해왔다.

```
public class Text {
    public static final int STYLE_BOLD = 1 << 0;
    public static final int STYLE_ITALIC = 1 << 1;
    public static final int STYLE_UNDERLINE = 1 << 2;
    public static final int STYLE_STRIKETHROUGH = 1 << 3;
    
    public void applyStyles(int styles) {...}
}

//사용예시
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```
비트별 OR 연산을 통해 여러 상수를 하나의 집합으로 모을 수 있다.

하지만 비트 필드는 앞서 살펴보았던 정수 열거 상수의 단점을 그대로 지니고 있다.
```
- 출력 시 그저 값만 출력이 되어 의미를 알 수 없다. 
    - 비트 필드 값이 그대로 출력되면 단순 정수 열거 상수를 출력할 때보다 해석이 더 어렵다.
- 비트 필드 하나에 녹아있는 모든 원소를 순회하기도 까다롭다.
- 수정이 필요할 경우 API를 수정해야 한다.
    - 비트 최댓값이 바뀌어 타입을 int에서 long으로 수정해야 할 수 있다.
```

비트 필드는 집합을 표현하기에 좋지만 이러한 단점들이 있는데 이를 위해 대신 EnumSet을 사용할 수 있다.

```
EnumSet의 내부는 비트 벡터로 구현되었고 원소가 64개 이하라면 사용할 수 있다.
비트를 직접 다룰 때 흔히 겪는 오류들에서 해방된다.
Set 인터페이스를 완벽히 구현하며, 타입 안전하다.

public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
    public void applyStyles(Set<Style> styles) {...}
}
text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```
앞서 구현했던 비트 필드 방식을 EnumSet을 활용하니 아주 간단하게 표현할 수 있다.

