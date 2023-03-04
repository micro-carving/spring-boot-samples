# 测试（Testing）

Spring Boot 提供了许多实用工具和注解来帮助测试应用程序。测试支持由两个模块提供：`spring-boot-test` 包含核心项，`spring-boot-test-autoconfigure` 支持测试的自动配置。

大多数开发人员使用 `spring-boot-starter-test` “启动器”，它导入了两个 Spring Boot 测试模块以及 JUnitJupiter、AssertJ、Hamcrest 和许多其他有用的库。

**TIP**：

如果你有使用 JUnit4 的测试，则可以使用 JUnit5 的老式引擎来运行它们。要使用 Vintage Engine，请添加对 `junit-Vintage-Engine` 的依赖项，如下例所示：

```xml

<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

`hamcrest-core` 被排除在外，`org.hamcrest:hamcrest` 是 `spring-boot-starter-test` 的一部分。

## 1.测试范围依赖关系

`spring-boot-starter-test` “启动器” （在测试范围内）包含以下提供的库：

- [JUnit 5](https://junit.org/junit5/)：Java 应用程序单元测试的实际标准；
- [Spring Test](https://docs.spring.io/spring-framework/docs/5.3.25/reference/html/testing.html#integration-testing) &
  Spring Boot Test：Spring Boot 应用程序的实用工具和集成测试支持；
- [AssertJ](https://assertj.github.io/doc/)：一个流畅的断言库；
- [Hamcrest](https://github.com/hamcrest/JavaHamcrest)：匹配器对象库（也称为约束或谓词）；
- [Mockito](https://site.mockito.org/)：Java mock 框架；
- [JSONassert](https://github.com/skyscreamer/JSONassert)：JSON 的断言库；
- [JsonPath](https://github.com/jayway/JsonPath)：JSON 的 XPath。

我们通常发现这些公共库在编写测试时很有用。如果这些库不符合你的需要，你可以添加自己的附加测试依赖项。

## 2.测试 Spring 应用程序

依赖注入的主要优点之一是它应该使你的代码更容易进行单元测试。可以使用 `new` 操作符实例化对象，甚至不需要涉及 Spring。你还可以使用模拟对象而不是真正的依赖项。

通常，你需要超越单元测试，开始集成测试（使用 Spring `ApplicationContext`）。能够在不需要部署应用程序或连接到其他基础设施的情况下执行集成测试是非常有用的。

Spring 框架包括用于此类集成测试的专用测试模块。可以直接向 `org.springframework:spring-test` 声明一个依赖项，或者使用 `spring-boot-starter-test` “启动器”
以过渡方式将其引入。

如果你以前没有使用过 `spring-test` 模块，那么应该首先阅读 Spring
框架参考文档的[相关部分](https://docs.spring.io/spring-framework/docs/5.3.25/reference/html/testing.html#testing)。

## 3.测试Spring Boot应用程序

Spring Boot 应用程序是一个 Spring `ApplicationContext`，因此除了通常使用普通的 Spring 上下文进行测试外，无需进行任何特殊的测试。

**注意**：

只有在使用 `SpringApplication` 创建的情况下，Spring Boot 的外部属性、日志记录和其他特性才会默认安装在上下文中。

SpringBoot 提供了一个 `@SpringBootTest` 注解，当你需要 SpringBoot 特性时，它可以作为标准 `spring-test @ContextConfiguration`
注解的替代。注解的工作原理是通过 `SpringApplication` 创建测试中使用的 `ApplicationContext`。除了 `@SpringBootTest` 之外，还提供了许多其他注解，用于测试应用程序中更具体的部分。

**TIP**：

如果你正在使用 JUnit4，请不要忘记将 `@RunWith(SpringRunner.class)` 添加到测试中，否则注解将被忽略。如果你使用 JUnit
5，需要将等效的 `@ExtendWith(SpringExtension.class)` 添加为 `@SpringBootTest`，其他 `@…Test` 注解已经用它进行了注解。

默认情况下，`@SpringBootTest` 不会启动服务器。你可以使用 `@SpringBootTest` 的 `webEnvironment` 属性来进一步优化测试的运行方式：

- MOCK(默认)：加载 Web `ApplicationContext` 并提供模拟 web 环境。使用此注解时，嵌入式服务器不会启动。如果你的类路径上没有可用的 web 环境，则此模式将透明地退回到创建常规的非
  web `ApplicationContext`。它可以与 `@AutoConfigureMockMvc` 或 `@AutoConfigureWebTestClient` 结合使用，以进行基于模拟的 web 应用程序测试。
- RANDOM_PORT：加载 `WebServerApplicationContext` 并提供一个真实的 web 环境。嵌入式服务器启动并监听一个随机端口。
- DEFINED_PORT：加载 `WebServerApplicationContext` 并提供一个真实的 web 环境。嵌入式服务器启动并监听已定义的端口（来自 `application.properties`
  ）或默认端口 `8080`。
- NONE：使用 `SpringApplication` 加载 `ApplicationContext`，但不提供任何 web 环境（mock 或其他）。

**注意**：

如果你的测试是 `@Transactional`，默认情况下，它会在每个测试方法结束时回滚事务。然而，由于将这种安排与 `RANDOM_PORT` 或 `DEFINED_PORT` 一起使用，隐式地提供了一个真正的 servlet
环境，HTTP 客户机和服务器在不同的线程中运行，因此在不同的事务中运行。在这种情况下，服务器上发起的任何事务都不会回滚。

如果你的应用程序使用不同的管理服务器端口，带有 `webEnvironment=WebEnvironment.RANDOM_PORT` 属性的 `@SpringBootTest` 也将在单独的随机端口上启动管理服务器。

### 检测 Web 应用程序类型

如果 SpringMVC 可用，则配置基于 MVC 的常规应用程序上下文。如果你只有 SpringWebFlux，我们将检测到并配置基于WebFlux的应用程序上下文。

如果两者都存在，则 Spring MVC 优先。如果要在此场景中测试反应式 web 应用程序，必须设置 `spring.main.web-application-type` 属性：

```java
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.main.web-application-type=reactive")
class MyWebFluxTests {
    // ...
}
```

### 检测查测试配置

如果你熟悉 Spring 测试框架，可能会习惯使用 `@ContextConfiguration(classes=…)` 来指定要加载的 Spring `@Configuration`
。或者，可能经常在测试中使用嵌套的 `@Configuration` 类。

在测试 Spring Boot 应用程序时，通常不需要这样做。只要你没有明确定义主配置，Spring Boot 的 `@*Test` 注解就会自动搜索主配置。

搜索算法从包含测试的包开始工作，直到找到用 `@SpringBootApplication` 或 `@SpringBootConfiguration` 注释的类。只要你以合理的方式构造代码，通常就会找到你的主要配置。

**注意**：

> 如果使用测试注解测试应用程序的更特定部分，则应避免添加特定于主方法应用程序类上特定区域的配置设置。
>
> `@SpringBootApplication` 的底层组件扫描配置定义了排除用于确保切片按预期工作的过滤器。如果你在带 `@SpringBootApplication` 注解的类上使用显式的 `@ComponentScan`
> 指令，请注意这些过滤器将被禁用。如果你正在使用切片，你应该重新定义它们。

如果要自定义主配置，可以使用嵌套的 `@TestConfiguration` 类。与嵌套的 `@Configuration` 类不同，嵌套的 `@TestConfiguration` 类是在应用程序的主配置之外使用的。

**注意**：

> Spring 的测试框架在测试之间缓存应用程序上下文。因此，只要你的测试共享相同的配置（无论如何发现），加载上下文这一可能耗时的过程只会发生一次。

### 排除测试配置

如果你的应用程序使用组件扫描（例如，如果你使用 `@SpringBootApplication` 或 `@ComponentScan`），你可能会发现仅为特定测试创建的顶级配置类在任何地方都会被意外选中。

正如我们前面所看到的，`@TestConfiguration` 可以用于测试的内部类来定制主配置。当放置在顶级类上时，`@TestConfiguration` 表示 `src/test/java`
中的类不应该通过扫描来获取。然后，可以在需要的地方显式导入该类，如下例所示：

```java
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MyTestsConfiguration.class)
class MyTests {

    @Test
    void exampleTest() {
        // ...
    }
}
```

注意：

> 如果你直接使用 `@ComponentScan`（也就是说，不是通过 `@SpringBootApplication`），则需要向其注册 `TypeExcludeFilter`
>
。有关详细信息，请参阅 [Javadoc](https://docs.spring.io/spring-boot/docs/2.7.8/api/org/springframework/boot/context/TypeExcludeFilter.html)
> 。

### 使用应用程序参数

如果应用程序需要参数，可以让 `@SpringBootTest` 使用 `args` 属性注入它们。

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(args = "--app.test=one")
class MyApplicationArgumentTests {
    @Test
    void applicationArgumentsPopulated(@Autowired ApplicationArguments args) {
        assertThat(args.getOptionNames()).containsOnly("app.test");
        assertThat(args.getOptionValues("app.test")).containsOnly("one");
    }
}
```

### 使用模拟环境进行测试

默认情况下，`@SpringBootTest` 不会启动服务器，而是为测试 web 端点设置一个模拟环境。

使用 Spring MVC，我们可以使用 `MockMvc` 或 `WebTestClient` 查询 web 端点，如下例所示：

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class MyMockMvcTests {

    @Test
    void testWithMockMvc(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("HelloWorld"));
    }

    @Test
    void testWithWebTestClient(@Autowired WebTestClient webClient) {
        webClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("HelloWorld");
    }
}
```

**TIP**：

> 如果你希望只关注 web 层而不启动完整的 `ApplicationContext`，请考虑改用 `@WebMvcTest`。

对于 Spring WebFlux 端点，你可以使用 `WebTestClient`，如下例所示：

```java

@SpringBootTest
@AutoConfigureWebTestClient
public class MyMockWebTestClientTests {

    @Test
    void exampleTest(@Autowired WebTestClient webClient) {
        webClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Hello World");
    }
}
```

**TIP**：

> 在模拟环境中进行测试通常比使用完整的 servlet 容器运行更快。然而，由于模仿发生在 SpringMVC 层，依赖于较低级别 servlet 容器行为的代码不能直接使用 MockMvc 进行测试。
>
> 例如，Spring Boot 的错误处理基于 servlet 容器提供的“错误页”支持。这意味着，虽然可以按预期测试 MVC
> 层抛出和处理异常，但不能直接测试是否呈现了特定的自定义错误页面。如果需要测试这些较低级别的问题，可以启动一个完全运行的服务器，如下一节所述。

### 使用运行中的服务器进行测试

如果需要启动完全运行的服务器，我们建议你使用随机端口。如果使用 `@SpringBootTest(webEnvironment=webEnvironment.RANDOM_PORT)`，则每次运行测试时都会随机选择一个可用端口。

`@LocalServerPort` 注解可用于将实际使用的端口注入测试。为了方便起见，需要对启动的服务器进行 REST 调用的测试可以另外 `@Autowire` 一个 `WebTestClient`
，它解析到正在运行的服务器的相对链接，并附带一个用于验证响应的专用 API，如下例所示：

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyRandomPortWebTestClientTests {

    @Test
    void exampleTest(@Autowired WebTestClient webTestClient) {
        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World");
    }
}
```

**TIP**：

> `WebTestClient` 可用于实时服务器和模拟环境。

此设置需要类路径上的 `spring-webflux`。如果你不能或不会添加 webflux，Spring Boot 还提供了 `TestRestTemplate` 工具：

```java
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyRandomPortWebTestClientTests {
    @Test
    void exampleTest(@Autowired TestRestTemplate restTemplate) {
        String body = restTemplate.getForObject("/", String.class);
        Assertions.assertThat(body).isEqualTo("Hello World");
    }
}
```

### 自定义 WebTestClient

要自定义 `WebTestClient` bean，请配置 `WebTestClientBuilderCustomizer` bean。使用用于创建 `WebTestClient` 的 `WebTestClient.Builder`
调用任何此类 bean。

### 使用 JMX

由于测试上下文框架缓存上下文，默认情况下 JMX 被禁用，以防止相同的组件在同一域上注册。如果此类测试需要访问 MBeanServer，请考虑将其标记为脏：

```java
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.management.MBeanServer;

@SpringBootTest(properties = "spring.jmx.enabled=true")
@DirtiesContext
public class MyJmxTests {

    @Autowired
    private MBeanServer mBeanServer;

    @Test
    void exampleTest() {
        Assertions.assertThat(this.mBeanServer.getDomains()).contains("java.lang");
        // ...
    }
}
```
