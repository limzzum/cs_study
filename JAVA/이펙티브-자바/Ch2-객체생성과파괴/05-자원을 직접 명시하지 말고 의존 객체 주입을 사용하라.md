## 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

```
자원을 직접 명시한다면, 변경이 필요할 떄 어떻게 될까
자원을 명시한 곳의 코드를 모두 고쳐야 한다.
  => 이것은 확장에는 열려있고 변경에는 닫혀야 한다는 OCP 원칙을 위반한다.
또, 테스트에도 매번 자원을 바꾸기 어려워 적합하지 않을 것이다.

이로 인해 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식이 나오게 되었다.
의존 객체 주입의 한 형태로, 유연성과 테스트 용이성을 높여준다.

public class ClientService {
    private final DependencyService dependency;

    public ClientService(DependencyService dependency) {
        this.dependency = dependency;
    }
}

이렇게 생성자로 받아 초기화함으로써 여러 자원을 지원할 수 있고, 불변을 보장한다.

```