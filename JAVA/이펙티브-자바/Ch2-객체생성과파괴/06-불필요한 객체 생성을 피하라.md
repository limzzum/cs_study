## 불필요한 객체 생성을 피하라

```
우리가 방심하면 똑같은 기능의 객체를 매번 생성하는 실수를 저지를 수 있다.
예를 들면 생성자는 매번 새로운 객체를 생성한다.
매번 새로운 객체가 필요없는 경우 정적 팩터리 메서드로 인스턴스를 통제할 수 있다.

또 생성 비용이 비싼 객체도 있다.
그 예로 정규표현식을 활용하여 유효성을 체크하는 코드이다.

static boolean isRomanNumeral(String a) {
  return s.matches("^(?=.)M*(C[MD]|D?C{0,3})" + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}

여기서 matches를 사용하면 내부적으로 정규표현식용 Pattern 인스턴스를 만든다.
그리고 이것은 한 번 쓰고 버려지게 된다.

public class RomanNumerals {
  private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})" + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
  
  static boolean isRomanNumeral(String a) {
    return ROMAN.matcher(s).matches();
  }
}

이 Patter 인스턴스를 정적 초기화 과정에서 미리 생성해 캐싱해두면 메서드가 호출될 때마다 재사용 하게 된다.

또 실수로 불필요한 객체를 만들어 낼 수 있는 오토박싱이 있다.
오토박싱은 기본 타입과 박싱된 기본 타입을 같이 쓸 때, 자동으로 형변환 해준다.

private static long sum() {
  Long sum = 0L;
  for (long i=0; i<=Integer.MAX_VALUE; i++){
    sum += i;
  }
  return sum;
}

여기서 문제는 sum 에 i 를 더하면서 int 자료형의 최대 크기만큼의 형변환이 일어난다.
i 를 long으로 선언했기 때문이다.

이렇게 불필요한 객체 생성, 낭비를 줄여야 한다.
하지만 방어적 복사가 필요한 상황에서 객체를 재사용 하는 것은 더 큰 피해가 따른다.
언제 터질지 모르는 버그와 보안 구멍으로 이어질 수 있으니 잘 파악하여 사용할 수 있어야 한다.


```