## 다 쓴 객체 참조를 해제하라

```
자바 언어에서는 메모리 관리를 가비지 컬렉터가 해준다.
그러나 메모리가 잘 회수되기 위해서는 신경써야 할 부분이 있다.

가비지 컬렉터에서는 참조가 없는 객체를 회수 대상으로 여긴다.
그런데 우리가 사용하지 않는 객체 == 다 쓴 참조를 해제하지 않으면 가비지 컬렉터는 여전히
참조되고 있는 객체로 여겨 회수하지 못하게 된다.

//Stack 클래스 메모리 누수 예시
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        // 메모리 누수가 발생하는 부분: 다 쓴 참조를 해제하지 않음
        return elements[--size];
    }

    // 메모리 누수를 방지하기 위해 참조를 null로 설정
    public Object popCorrect() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}

스택은 많이 알고 있는 자료구조이다.
LIFO 구조로 push 를 하면 값이 들어가고 pop하면 가장 나중에 들어간 값이 나오게 된다.

여기 코드를 보면 push 를 10까지 하고 pop을 해도 사이즈만 줄고 다 쓴 참조 해제를 안하고 있다.
그러면 가비지 컬렉터가 회수를 하지 못한다.

바로 밑에 popCorrect를 보면 해당 객체에 null을 할당하여 참조를 해제 하고 있다.
이렇게 다 쓴 참조 해제를 해주면 가비지 컬렉터가 알고 회수해 갈 수 있게 된다.

이렇게 메모리 누수를 일으키는 것 중 리스너, 콜백 문제도 대표적이다.
리스너와 콜백은 어떤 이벤트가 발생했을 때 다른 메서드를 실행하도록 지정해주는 것이다.

A객체에 B콜백을 지정해 줌으로써 A 객체는 B객체를 참조하게 된다.
그런데 이제 더이상 콜백 혹은 리스너가 필요하지 않아도 계속 참조하고 있으므로 가비지 컬렉터에
회수되지 않으며, 이는 메모리 누수로 이어진다.

이는 명시적 해제나 약한 참조를 통해 메모리에서 제거될 수 있다.
약한 참조는 WeakReference를 이용할 수 있고, WeakReference에 등록하면 가비지 컬렉터가 즉시 회수해간다. 

```