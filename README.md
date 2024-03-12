# ☕️YUNNI-BUCKS

세종대학교 컴퓨터공학과 19학번 2인 개발팀

개발 기간 : *2023-07 ~ 2023-09 (MVP 구현 완료)*

## 목차
- [프로젝트 소개](#프로젝트-소개)
- [맴버 구성](#맴버-구성)
- [개발 환경](#개발-환경)
- [프로젝트 설명](#프로젝트-설명)
- [구현기능 및 문서](#구현기능-및-문서)

## 프로젝트 소개
주문-결제-배달 온라인 카페 서비스

## 맴버 구성
|                  |                                                                                                            윤광오(팀장)                                                                                                           |                                                                              하윤(팀원)                                                                             |
|:----------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|     Position     |                                                                                                         Back-End Developer                                                                                                        |                                                                          Back-End Developer                                                                         |
|      Domain      |                                                                                                          회원, 주문, 배달                                                                                                         |                                                            결제, 카드, OCR(Optical Character Recognition)                                                           |
| Technology Stack |                                                             Spring Boot, Redis, Query-Dsl, Spring Data JPA, JPA, JUnit, H2 Database, MariaDB, Rest Docs, Mockito, JWT                                                             |                                     Spring Boot, Spring Data JPA, JPA, JUnit, H2 Database, MariaDB, Rest Docs, Mockito, Open API                                    |
|       설계       |                                                                                         ERD (DB), Domain Model, OOP, Layered Architecture                                                                                         |                                                              OOP, Layered Architecture(Pay, Card, OCR)                                                              |
|       구현       | Java Reflection 활용한 Record Class 전용 CustomMapper, Fake Repository, Redis(NoSql) Fake Repository 구현, Scheduler 활용한 배달 상태 변경 구현, 자체 비밀번호 암호화 구현, 썸네일 파일 업/다운로드, JWT 활용한 Login, Pagination | Clova OCR(https://clova.ai/ocr/), <br> Toss Payments(https://docs.tosspayments.com/guides/index) develop API 연동하여 신용/체크카드 이미지 인식 및 자동 결제 시스템 개발 |

## 개발 환경
- Java 17
- Oracle OpenJDK 17.0.4
- IDE : IntelliJ IDEA
- DATABASE : H2, MariaDB
- ORM : JPA
- Framework: Spring Boot 2.7.14

## 프로젝트 설명


### Architecture
<img width="807" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/c9dfabc9-9c5b-4b14-9640-67574b810891">

### Flow Chart

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/4a8f2a6d-4508-407b-a511-68374f6c3080">

### ERD

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/37452648-0c1b-4b6f-aa0a-42a53cbcc9ce">

### Domain Model
<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/3f782d5f-0a01-4336-86da-55e0b13f373a">

### 프로젝트 구조

[프로젝트 디렉토리](https://github.com/gkdbssla97/yunni-bucks/blob/master/DIRECTORY-STRUCTURE.md)

### 1.SRP
주문, 결제, 할인, 배달은 각각의 기능만 가지며 책임을 수행한다.
[도메인 모델](#Domain-Model)

### 2.OCP
기존 구성요소는 수정이 일어나지 말아야 하며, 쉽게 확장해서 재사용을 할 수 있어야 하므로 구현보다는 인터페이스에 의존하도록 설계한다.
모듈별 인터페이스를 두어 코드 재사용이 용이하다. Unit Test 소형 테스트 진행에 수월하다.

<img width="359" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/c333f588-7561-4b27-afdd-a453af0d6e74">
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
<img width="359" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/dd6c7f4a-172a-4a97-b2b2-e3811f117073">

### 3.ISP
인터페이스의 단일책임을 강조하여 Service, Repository layer 계층 별 서로 다른 성격의 인터페이스를 명백히 분리한다.

<img width="539" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/706880be-4877-4a8d-a3db-27092e5bbf17">

### 4.DIP
Transitive Dependency가 발생했을 때 상위 레벨의 레이어가 하위 레벨의 레이어를 바로 의존하게 하지 않고 둘 사이에 존재하는 추상레벨을 통해 의존한다. 상위 레벨의 모듈은 하위 레벨의 모듈의 의존성을 벗어나 재사용 및 확장성을 보장받는다.
> `interface`가 2개씩이나 필요한 이유?
> 1. JpaRepository는 DB와 강결합 되어있는 JPA 코드를 인스턴스화 하는 것이 어렵다.
> 2. 테스트 할때 Fake를 사용함으로써, Testability를 높일 수 있다.
>     - Service Layer는 영속성 계층과의 의존관계가 느슨해진다.
>     - H2 ➝ ‬MySQL로 DB를 교체해도 Service는 영향을 받지 않는다.
> 3. 절차 지향적인 코드의 문제점인 동시 작업의 문제점을 개선할 수 있다.

<img width="538" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/d7296b2a-496d-4487-b532-114976ecec9b">


### Unit / Integration Test 비교
- 단위테스트 구현 이유
    - 테스트 시간 단축으로 문제점 발견 가능성이 높아지고 안정성이 향상
    - 프로그램의 각 부분을 고립시켜서 정확하게 동작하는지 확인하는 것으로 어느 부분에서 에러가 발생하는지 재빠르게 확인이 가능하고, 디버깅 시간을 단축시킴으로써 개발의 생산성을 향상
    - 테스트 간 결합도가 낮으므로 간단해진 통합
- 프로덕션 코드의 repository, service 등 계층 간의 강한 의존성으로 인해 단위 테스트를 구현하는 것이 쉽지 않았다.
    1. Mock 라이브러리를 이용해 가짜 객체를 주입받아 미리 정의된 값을 반환 -> OpenAPI와 같은 외부 시스템과 상호작용이 필요한 경우 Mock객체가 API를 모방 (외부 시스템은 외부 서비스의 가용성, 데이터 보안, 비용 등 외부 시스템이 내부 시스템, 즉 개발 중인 프로덕션 코드와는 독립적으로 동작하기 때문에, 외부 시스템에서 발생하는 문제가 테스트 결과에 영향을 줄 수 있다.)

    2. 각 컴포넌트는 의존성 역전 원칙을 적용하여 추상화된 인터페이스에 의존하도록 구성(Fake Object를 만들어 실제 객체인 OrderRepository나 가짜 객체인 FakeOrderRepository를 인터페이스에 쉽게 교체할 수 있다. 이런 구조는 구체적인 구현에 의존하지 않고, 추상화에 의존함으로써 단위 테스트를 수행하는데 유리)

#### Fake Object(Repository)
1. atomicGeneratedId: AtomicLong을 사용하여 동시성 환경에서도 각 트랜잭션마다 고유한 ID 값을 생성 </br>
2. data: CardPayment DB 대신 사용하는 ArrayList로 객체들을 저장 In-Memory 방식으로 save 할 수 있다.</br>
  <img width="400" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/f938fadb-2003-4768-8302-4b4d6a3338cc">


3. 각 CRUD 메서드 구현, Stream & Lambda 식으로 작성하였고, Collections.toList() -> toList() Java17 기능 활용</br>
  <img width="399" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/67dcef06-6391-4844-9b3b-2c6ddeb99d98">

#### Service Result (Fake Object 사용)

- *Card (기존 Test 대비 약 5배 단축)*   
  <img width="339" alt="Untitled" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/fe0c4374-1b7d-4929-8780-138ec6de4b5f">
  <img width="329" alt="Untitled" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/cbd263db-478f-45ac-a373-496aefbbaece">

- *Payments (기존 Test 대비 약 12배 단축)*  
  <img width="364" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/d714ebeb-7c3a-482b-8c60-975b833077e6">
  <img width="394" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/68744969-81d8-444c-9957-66f48d71a0a6">

#### Controller Result (Fake Object, Test Container 사용)
- *Card (기존 Test 대비 약 5배 단축)*</br>
  <img width="364" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/3cd88a42-239d-47cc-a206-b5b39c689220">
  <img width="374" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/17e38b84-aa0d-4316-9103-d2a3709b1ce4">

- *Payments (기존 Test 대비 10배 단축)*</br>
  <img width="364" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/f5fb1dbc-3d29-4530-b4fe-fcb0d75bcfec">
  <img width="374" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/782e332b-19cd-4d1a-87c0-9ca6054fc2ed">

#### Test Container

TestPayContainer 클래스에 FakePayRepository, FakeCardRepository 는실제 DB 대신 메모리 내에서 데이터를 저장하고 제공하는 가짜 객체이다.
또한, FakeUuidHolder, FakeTossApiService, FakeOcrApiService 등은 각각 UUID 생성, Toss API 호출, OCR API 호출과 같은 외부 서비스와의 상호작용을 가짜로 대체하는 객체다.
이렇게 함으로써 DB 의존성 없이도 테스트를 수행하고, 외부 서비스와의 의존성 없이 원하는 결과를 반환하거나 동작을 검증해 독립적인 단위 테스트를 수행할 수 있다.

<img width="500" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/0e960804-2d9c-41ae-9853-03c66f67d1d9">

### 구현기능 및 문서
#### WebHook (Slack Notification)
- 특정 이벤트가 발생한 시점에서 즉시 알림을 제공하여 에러 추적이 용이하고 빠르게 대응 가능
- 동시에 여러 API 요청이 들어올 경우, 각 요청을 처리하고 그 결과를 클라이언트에게 알리는 작업을 동기 방식으로 처리하면 시스템의 처리 능력을 제한하고 응답 시간을 증가시키는 결과를 가져올 수 있다고 판단
#### API 요청 알림 및 에러 로그 알림
<img width="276" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/c0b8dc39-354a-4984-916b-bf0293751ab7">
<img width="289" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/99eb62cb-8e7c-48dc-bed8-388e4bbbe688">
<img width="438" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/fd2265c2-ea12-42c2-9ae0-d42c7240c2f6">

- 동기식 / 비동기식 속도 비교 (동기식 대비 비동기 최소 3배 단축)

현재 요청 정보(HttpServletRequest)를 가져오기 위해 RequestContextHolder와 ServletRequestAttributes를 사용
threadPoolExecutor에서 비동기적으로 sendSlackMessage() 메서드를 실행
proceedingJoinPoint.proceed()를 호출하여 원래의 메서드 실행을 계속하고 결과값을 반환

<img width="500" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/4d2a1711-ef3f-4d35-8702-62adccc03e13">
<img width="500" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/01f7eace-f3ac-4ce6-81c2-a329387c6e45">

#### Spring Rest Docs
- 코드를 작성하면서 동시에 API 문서를 자동 생성한다. 생성된 명세서는 코드 기반으로 구현되어 정확하고, 테스트 실패 시 문서가 생성되지 않는다.
- API의 사용 방법을 명확하게 설명해주어 협업뿐 아니라, 다른 팀이나 외부 개발자와의 협업에도 큰 도움이 된다고 판단하여 사용하였다.
#### Spring Rest Docs (Card, Payments Domain)
<img width="310" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/11f5dd23-20ba-4be5-840f-d95638fca8d1">
<img width="279" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/de35a375-b086-4a64-a505-89105a735892">
<img width="411" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/a0ff145a-c7d8-4907-92d4-e94f1a9ce285">

mockMvc.perform() 메서드가 반환하는 객체로, 이후의 검증과 문서화 작업을 위한 연산들을 체인 방식으로 호출
응답 결과를 기반으로 API 문서 스니펫을 생성 "card-create"는 생성될 스니펫 파일명
요청 헤더 중 'Authorization' 헤더에 대한 설명을 추가
요청/응답 본문의 필드들에 대한 설명을 추가
getCardRequests()/Responses() 메소드에서 FieldDescriptor 목록을 반환하도록 구현

### 플랜
1. 고민한 점: 아키텍처 설계 고민을 했다. 비즈니스 로직에 따른 테이블 구성 및 플로우 차트에 신경 썼다. 자바 OOP 패턴을 적용하는데에 초점을 두었다.
2. 개선할 점: 쿼리 성능 개선 및 예외처리의 사각지대가 있다. 확장성을 위해 단일 책임 원칙을 좀 더 세분화 해야한다.
3. 앞으로 계획:
- 트래픽 부하 테스트 *(~~JMeter~~, nGrinder)*
- 최단거리 배달 가게 알고리즘 구현
- Pay 충전 및 결제 수단 추가
- ~~동시성(DB Lock, Redisson), Redis Caching~~ *(yunni-bucks-traffic 개시)*
