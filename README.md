# Snowflake ID Generator (Java)

분산 시스템에서 64비트 Long 타입의 유일한 ID 를 생성해낼 수 있는 JAVA로 개발된 ID 생성기이다.

트위터에서 2010년에 발표한 [Snowflake](https://blog.twitter.com/engineering/en_us/a/2010/announcing-snowflake) 알고리즘 기반으로 개발하였다.

스노우플레이크는 64비트로 이루어진 ID로, 각각의 비트는 특정 의미를 갖고 있다.

<img width="886" alt="image" src="https://user-images.githubusercontent.com/67363545/232283753-aef4ea69-e5b2-4fb6-a743-af02254c5361.png">

첫 번째 비트는 부호 비트이다. 0으로 채워서 ID 값을 양수로 만들어준다.
다음 41비트는 타임스탬프 비트이다. ID 생성 요청일의 timestamp 에서 기준시간 timestamp 를 뺀값을 저장한다. 기준시간으로부터 69.73년 동안 고유한 ID 생성을 보장한다.
나머지 10비트는 서버ID 를 나타낸다. 최대 1024개의 분산 서버에서 사용해도 고유한 ID 생성을 보장한다.
나머지 12비트는 시퀀스를 나타낸다. 이를 통해 동일한 millisecond의 요청에도 고유한 ID 생성을 보장한다.

Snowflake ID의 장점과 단점은 다음과 같다.

## 장점
* 고유한 ID 생성: 분산 시스템에서 고유한 ID를 생성하기 위한 가장 일반적인 방법 중 하나이다. 이를 통해 중복된 ID가 발생하는 것을 방지할 수 있다.
* 높은 확장성: 분산 시스템에서 사용하기에 적합한 알고리즘으로, 대규모 시스템에서도 효과적으로 작동한다.
* 간단한 사용: 사용하기에 간단하다. 서버 실행 시 하나의 인스턴스만 생성해두고 ID 생성이 필요한 곳에서 사용하면 된다.

## 단점
* 시스템 시계와의 의존성: 시스템의 시계와 밀접한 연관이 있다. 특정 서버의 시스템 시계가 다른 서버보다 빠르다면, 해당 서버에서 생성하는 ID의 타임스탬프는 항상 빠를 것이다.
* 타임스탬프 오버플로우: 41비트의 타임스탬프를 사용하여 ID를 생성한다. 이러한 이유로, 시스템 시작 이후 69년이 지나면 오버플로우가 발생할 수 있다. 이 문제를 해결하기 위해서는 서버ID 비트를 줄이고, 타임스탬프 비트를 늘리는 방식으로 보완 가능하다.
* 이기종 통신시 주의 : Javascript 와 같이 부동소수점 숫자체계를 사용하여 정수를 저장하는 경우, 2의 53승 이상의 정수를 근사값으로 저장하기 때문에 주의가 필요하다. 문자열로 주고 받는 등의 처리로 해결 가능하다.

# Usage
SnowflakeIdGenerator 는 사용하는 application 에서 singleton instance 로 생성해야 한다.
```java
SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0);
idGenerator.nextId();
```
