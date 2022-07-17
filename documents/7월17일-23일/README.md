# Spring에서 JPA를 이용해 데이터베이스 다루기

## spring JPA란?

JPA란 Java Persistence API로, 자바의 ORM(자바 객체와 관계형 DB 맵핑)을 위한 표준기술이다.

JPA의 장점: SQL위주의 Mybatic 프로젝트와 비교하여 쿼리를 하나하나 작성할 필요가 없어 **코드량이 줄어든다.**

JPA 사용시 DB데이터에 작업할 경우 실제 쿼리를 날리기보다는 Entity 클래스의 수정을 통해 작업한다.

<……domain/posts/Posts>

```java
@Getter
@NoArgsConstructor
@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(length=100, nullable=false)
    private String title;

    private int cost;

    @Column(length=100, nullable=false)
    private String content;

    @Builder
    public Posts(String title, Integer cost, String content) {
        this.title = title;
        this.cost = cost;
        this.content = content;
    }
}
```

**@Entity**

- 테이블과 링크될 클래스임을 나타낸다.
- 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭한다.
- SalesManager.java→sales_manager table

**@Id**

- 해당 테이블의 PK필드를 나타낸다.

**@GeneratedValue**

- PK의 생성 규칙을 나타낸다.
- 스프링 부트 2.0에서는 **GenerationType.IDENTITY** 옵션을 추가해야만 **auto_increment**가 된다.
- Entity의 PK는 Long 타입의 Auto_increment를 추천한다.(MySQL 기준으로 bigint타입이 된다.) 주민등록번호와 같이 비즈니스상 유니크 키나, 여러 키를 조합한 복합키로 PK를 잡을 경우 난감한 상황이 발생할 수 있다.

**@Column**

- 테이블 칼럼을 나타내며 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 칼럼이 된다.
- 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용한다.
- 문자열의 경우 VARCHAR(255)가 기본인데, 사이즈를 500으로 늘리고 싶거나, 타입을 TEXT로 변경하고 싶거나 등의 경우에 사용한다.

**@NoArgsConstructor**

- 기본 생성자 자동 추가
- public Posts() {}와 같은 효과

**@Getter**

- 클래스 내 모든 필드의 Getter 메소드를 자동생성

**@Builder**

- 해당 클래스의 빌더 패턴 클래스를 생성
- 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함

****Builder 패턴***

생성자나 빌더나 생성 시점에 값을 채워주는 역할은 같으나, 생성자의 경우 지금 채워야할 필드가 무엇인지 명확히 지정할 수없다.

예를 들어 다음과 같은 생성자가 있다면 개발자가 new Example(b,a)처럼 a와 b의 위치를 변경해도 코드를 실행하기 전까지는 문제를 찾을 수가 없다.

```java
public Example(String a, String b){
	this.a=a;
	this.b=b;
}
```

하지만 빌더를 사용하게 되면 다음과 같이 어느 필드에 어떤 값을 채워야할지 명확하게 인지할 수 있다.

```java
Example.builder()
	.a(a)
	.b(b)
	.build();
```

<테스트 코드>

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {
    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup(){
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기(){
        //given
        String title="테스트 게시글1";
        String content="테스트 본문1";

        postsRepository.save(Posts.builder()
                .title(title)
                .cost(100)
                .content(content)
                .build());

        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);
        Assertions.assertThat(posts.getTitle()).isEqualTo(title);
        Assertions.assertThat(posts.getContent()).isEqualTo(content);
    }
}
```

**@After**

- Junit에서 단위 테스트가 끝날 때마다 수행되는 메소드를 저장한다.
- 보통은 배포 전 전체 테스트를 수행할 때 테스트간 데이터 침법을 막기 위해 사용한다.
- 여러 테스트가 동시에 수행되면 테스트용 데이터베이스인 H2에 데이터가 그대로 남아 있어 다음 테스트 실행 시 테스트가 실패할 수 있다.

**@postsRepository.save**

- 테이블 posts에 insert/update 쿼리를 실행한다.
- id 값이 있다면 update가, 없다면 insert 쿼리가 실행된다.

**@postsRepository.findAll**

- 테이블 posts에 있는 모든 데아터를 조회해오는 메소드이다.