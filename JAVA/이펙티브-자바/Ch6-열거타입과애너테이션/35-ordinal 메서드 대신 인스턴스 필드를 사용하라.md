## ordinal 메서드 대신 인스턴스 필드를 사용하라

열거 타입에서는 상수가 해당 열거 타입에서 몇 번째 위치인지(선언되었는지) 반환하는 ordinal 메서드를 제공한다.
하지만 이 메서드를 사용하면 유지보수가 힘들어지니 사용하지 말라고 권고된다.

선언된 순서에 의지하는 ordinal 메서드를 사용할 경우 선언 순서를 바꾸거나, 중간에 상수를 추가하거나, 혹은 
중간값을 비워두기가 어려워진다.

```
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET,
    OCTET, NONET, DECTET;
    
    public int numberOfMusicians() { return ordinal() + 1; }
}
```
이 열거타입에서는 연주자가 1명인 솔로부터 10명인 디렉트를 순서대로 정의했다.
중간에 3명의 연주자가 있는 다른 종류를 추가하게 되면 ordinal을 통해 연주자의 수를 받기를 기대했던 코드는 꺠지게 될 것이다.
또 중간에 몇 개를 삭제하거나 몇 개를 건너뛰고 추가하고 싶으면 그 자리를 비워둘 수 없기 때문에 더미 상수를 추가해야 할 것이다.

이렇게 상수와 연결되는 의미있는 값은 필드를 추가하여 저장하도록 하자.
```
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET,
    OCTET, NONET, DECTET;
    
    private final int numberOfMusicians;
    Ensemble(int size) { this.numberOfMusicians = size; }
    public int getNumberOfMusicians() { return numberOfMusicians; }
}
```