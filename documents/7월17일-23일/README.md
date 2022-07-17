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

## Posts, 게시글

### 등록

```java
@RequiredArgsConstructor
@RestController
public class PostsApiController {
    private final PostsService postsService;

    @PostMapping("api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto){
        return postsService.save(requestDto);
    }
}
```

**@RestContorller**

- 컨트롤러를 JSON을 반환하는 컨트롤러로 만들어 준다.
- @ResponseBody를 각 메소드마다 선언했던 것을 한번에 사용할 수 있게된 것과 같다.

```java
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto){
        return postsRepository.save(requestDto.toEntity()).getPostId();
    }
}
```

**@Transactional**

CRUD 중 C,U,D의 메소드 위에 붙여서 사용한다. 트랜잭션은 원자성, 일관성, 격리성, 영속성의 4가지 성질을 가진다.

※[https://velog.io/@kdhyo/JavaTransactional-Annotation-알고-쓰자-26her30h](https://velog.io/@kdhyo/JavaTransactional-Annotation-%EC%95%8C%EA%B3%A0-%EC%93%B0%EC%9E%90-26her30h)

```java
@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private int cost;
    private String content;

    @Builder
    public PostsSaveRequestDto(String title, Integer cost, String content) {
        this.title=title;
        this.cost=cost;
        this.content=content;
    }

    public Posts toEntity(){
        return Posts.builder()
                .title(title)
                .cost(cost)
                .content(content)
                .build();
    }
}
```

![Untitled]([https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6428b9e1-c8b5-4a31-85c0-fec6315e5f4b/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6428b9e1-c8b5-4a31-85c0-fec6315e5f4b/Untitled.png))

---

앞서 Spring의 동작 순서는 다음과 같다고 했다.

Spring의 동작순서는 다음과 같다.

1.클라이언트가 Request 요청을 하면 DispatcherServlet이 요청을 받는다.

2.DispatcherServlet에서 받은 요청을 HandlerMapping에게 보내 해당 요청을 처리할 수 있는 Controller를 찾는다.

3.실제 로직 처리(**Controller→Service→DAO→DB→Service→Controller**)

4.로직 처리 후 ViewResolver를 통해 view 화면을 찾는다.

5.View 화면을 최종 클라이언트에게 전송한다.

※[https://github.com/ChoSeoyoung/tour_project/tree/main/documents/7월10일-16일](https://github.com/ChoSeoyoung/tour_project/tree/main/documents/7%EC%9B%9410%EC%9D%BC-16%EC%9D%BC)

※[https://gmlwjd9405.github.io/2018/12/25/difference-dao-dto-entity.html](https://gmlwjd9405.github.io/2018/12/25/difference-dao-dto-entity.html)

![Untitled](spring%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%A5%E1%84%83%E1%85%B53%20419fe2da8f934e87866c9ee4da74dcdb/Untitled%201.png)

### DAO(Data Access Object)

- 실제로 DB에 접근하는 객체
- Service와 DB를 연결하는 역할
- SQL을 사용하여 DB에 접근한 후 적절한 API 제공

### DTO(Data Transfer Object)

- 계층간 데이터 교환을 위한 객체(Java Beans)이다.
- Request와 Response용 DTO는 View를 위한 클래스이다.
    - toEntity()메서드를 통해서 DTO에 필요한 부분을 이용하여  Entity로 만든다.

---

<테스트코드>

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void testDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {
        //given
        String title="title";
        int cost=999;
        String content="content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .cost(cost)
                .content(content)
                .build();

        String url = "http://localhost:"+port+"/api/v1/posts";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url,requestDto,Long.class);

        //then
assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(responseEntity.getBody()).isGreaterThan(0L);
        // 0L은 Long형 값을 비교할때 0보다 0L이라고 사용한다. (L이라고 뒤에 붙은 것은 명시적으로 Long형 값이란 의미)

        List<Posts> postsList = postsRepository.findAll();
        Posts posts = postsList.get(0);
assertThat(posts.getTitle()).isEqualTo(title);
assertThat(posts.getContent()).isEqualTo(content);
    }
}
```

@RunWith(SpringRunner.class)

- 테스트를 진행할 때 JUnit에 내장된 실행자 외에 다른 실행자를 실행시킨다.
- SpringRunner라는 실행자를 사용함으로써, 스프링 부트 테스트와 JUnit 사이에 연결자 역할을 한다.

### 수정

```java
@RequiredArgsConstructor
@RestController
public class PostsApiController {
    ...
    @PutMapping("api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto){
        return PostsService.update(id, requestDto);
    }
}
```

**@PathVariable Long id**

…/posts/{id}의 url을 요청받으면 기본적으로 String으로 받지만, @PathVariable Long id을 통해서 ****Long타입으로 자동 형변환된다.

```java
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

   ...

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto){
        Posts posts = postsRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id="+id));

        posts.update(requestDto.getTitle(),requestDto.getCost(),requestDto.getContent());

        return id;
    }
}
```

```
@Getter
@NoArgsConstructor
@Entity
public class Posts {
    ...
    public void update(String title, Integer cost, String content){
        this.title=title;
        this.cost=cost;
        this.content=content;
    }
}
```

update 기능에서 데이터베이스에 쿼리를 날리는 부분이 없다. 이는 **JPA 영속성 컨텍스트** 때문이다. **영속성 컨텍스트란, 엔티티를 영구 저장하는 환경**이다.

트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 이 데이터는 영속성 컨텍스트가 유지된 상태이다. 이 상태에서 해당 데이터의 값을 변경하면 **트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영**한다. 

즉, JPA에서는 트랜잭션이 끝나는 시점에 **변화가 있는 모든 엔티티 객체**를 데이터베이스에 자동으로 반영해준다.

※[https://jojoldu.tistory.com/415](https://jojoldu.tistory.com/415)

```java
@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {
    private String title;
    private int cost;
    private String content;

    @Builder
    public PostsUpdateRequestDto(String title, Integer cost, String content) {
        this.title = title;
        this.cost = cost;
        this.content = content;
    }
}
```

<테스트코드>

```
@Test
public void Posts_수정된다() throws Exception {
    //given
    String title="title1";
    int cost=999;
    String content="content1";

    Posts savedPosts = postsRepository.save(Posts.builder()
            .title(title)
            .cost(cost)
            .content(content)
            .build());

    Long updateId = savedPosts.getPostId();
    String updatetitle="title2";
    int updatecost=1000;
    String updatecontent="content2";

    PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
            .title(updatetitle)
            .cost(updatecost)
            .content(updatecontent)
            .build();

    String url = "http://localhost:"+port+"/api/v1/posts/"+updateId;

    HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

    //when
    ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,Long.class);

    //then
assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(responseEntity.getBody()).isGreaterThan(0L);

    List<Posts> postsList = postsRepository.findAll();
    Posts posts = postsList.get(0);
assertThat(posts.getTitle()).isEqualTo(updatetitle);
assertThat(posts.getContent()).isEqualTo(updatecontent);
}
```