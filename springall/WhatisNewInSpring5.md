### 一、基础特性---相关版本
- Jdk最低支持为JDK1.7/JDK1.8
    - 因为Spring4的更新需要向后支持JDK 6，7，8，所以很多JDK8的一些特性比如流式编程都没能发挥很大的影响力
    - Spring 5 为了发挥明显特性和功能，最低支持JDK1.7/JDK1.8，兼容JDK1.9
- 其他相关的基准也有变化
  - JDK1.7中：
    - Servlet 3.1
    - JMS 2.0
    - JPA 2.1
    - JAX-RS 2.0
    - Bean Validation 1.1
  - 其他框架：
    - Hibernate 5
    - Jackson 2.6
    - EhCache 2.10
    - JUnit 5
    - Tiles 3
  - 服务器版本：
    - Tomcat 8.5+
    - Jetty 9.4+
    - WildFly 10+
    - Netty 4.1+
    - Undertow 1.4+
    


### 二、首要特性---响应式编程
- 响应式编程，接纳了JDK1.8流式编程模型的优点，在整个框架内部采用响应式编程，将数据的流转都转化为数据流，且异步非阻塞
- SpringFramework5 包含响应流（定义响应性API的语言中立尝试）和 Reactor（由Spring Pivotal团队提供的 Reactive Stream 的Java实现）， 以用于其自身的用途以及其许多核心API


### 三、全新WEB MVC子模块  ---Spring WebFlux 函数式WEB框架
- 基于注解的模型是 Spring WebMVC 的现代替代方案，该模型基于反应式基础而构建，而 Functional Web Framework 是基于 @Controller 注解的编程模型的替代方案
- 这些模型都通过同一种反应式基础来运行，后者调整非阻塞 HTTP 来适应反应式流 API
- 一种基于注解的模型和 Functional Web Framework
- 框架引入了两个基本组件：HandlerFunction 和 RouterFunction
- HandlerFunction 表示处理接收到的请求并生成响应的函数
- RouterFunction 替代了 @RequestMapping 注解
- 它用于将接收到的请求路由到处理函数
- 返回的对象（Mono 和 Flux）,Mono 对象处理一个仅含 1 个元素的流，而 Flux 表示一个包含 N 个元素的流
```text
@RestController
public class TestController {

    @GetMapping("/get/{id}")
    Mono<Person> list(@PathVariable String id) {
        return this.repository.findOne(id);
    }
```
```text
public class PersonHandler{
 public Mono<ServerResponse> getPerson(ServerRequest request) {

        return repository.getPerson(request.pathVariable("id"))

            .then(p -> ServerResponse.ok()

            .contentType(APPLICATION_JSON)

            .body(fromObject(p)))

            .otherwiseIfEmpty(ServerResponse.notFound().build());

    }
}
```
```text
PersonHandler handler = new PersonHandler();

RouterFunction<ServerResponse> route =

    route(
        GET("/get/{id}")

        .and(accept(APPLICATION_JSON)), handler::getPerson);
```
- RestAPI的调用：WebFlux 模块为 RestTemplate 提供了一种完全非阻塞、反应式的替代方案，名为WebClient
```text
Mono<Person> p = WebClient.create("http://localhost:8080")
      .get()
      .url("/get/{id}", 110)
      .accept(APPLICATION_JSON)
      .exchange(request)
      .then(response -> response.bodyToMono(Person.class));
```

### 四、Junit也可以使用函数式
- JUnit 5 全面接纳了 Java 8 流和 lambda 表达式
```text
@Test
void test1() {

    assertTrue(Stream.of(20, 40, 50)
      .stream()
      .mapToInt(i -> i)
      .sum() > 110, () -> "Test info");
}
```
- 开发人员可以使用 JUnit 5 的条件测试执行注解 @EnabledIf 和 @DisabledIf 来自动计算一个 SpEL (Spring Expression Language) 表达式，并适当地启用或禁用测试。借助这些注解，Spring 5 支持以前很难实现的复杂的条件测试方案。Spring TextContext Framework 现在能够并发执行测试
- 技术文章：https://www.ibm.com/developerworks/cn/java/j-introducing-junit5-part1-jupiter-api/index.html

### 五、Kotlin的支持
- Spring5.0 对 Kotlin 有很好的支持
- Kotlin 函数式编程风格与 Spring WebFlux 模块完美匹配，它的新路由 DSL 利用了函数式 Web 框架以及干净且符合语言习惯的代码

### 六、HTTP/2的支持
- HTTP/2 提高传输性能，减少延迟，并帮助提高应用程序吞吐量，从而提供经过改进的丰富 Web 体验
- Spring Framework 5.0 将提供专门的 HTTP/2 特性支持，还支持人们期望出现在 JDK 9 中的新 HTTP 客户端


### 七、包清理和弃用
- 取消对以下框架的支持：
    - Portlet
    - Velocity
    - JasperReports
    - XMLBeans
    - JDO
    - Guava

### 八、Spring 核心和容器的更新
- Spring Framework 5 改进了扫描和识别组件的方法.向 META-INF/spring.components 文件中的索引文件添加了组件坐标。该索引是通过一个为项目定义的特定于平台的应用程序构建任务来生成的。