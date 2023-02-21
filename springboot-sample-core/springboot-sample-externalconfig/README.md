# 外部化配置（Externalized Configuration）

Spring Boot 允许将配置外部化，方便在不同的环境中使用相同的应用程序代码。你可以使用各种外部配置源，包括 Java 配置文件、YAML 文件、环境变量和命令行参数。
属性值可以使用 `@Value` 注解直接注入到 bean 中，通过 Spring 的 `Environment` 抽象访问，或者通过 `@ConfigurationProperties` 绑定到结构化对象。

Spring Boot 使用了一个非常特殊的 `PropertySource` 顺序，旨在允许合理地重写值。以后的属性源可以替代以前的属性源中定义的值。信息来源按以下顺序考虑：

1. 默认属性(通过设置 `SpringApplication.setDefaultProperties` 指定)；
2. `@PropertySource` 注解注释在你的 `@Configuration` 配置类上。请注意，在刷新应用程序上下文之前，此类属性源不会添加到环境中。现在配置某些属性为时已晚，例如 `logging.*`
   和 `spring.main.*`，它们是在刷新开始之前读取的；
3. 配置数据(例如 `application.properties` 文件)；
4. `RandomValuePropertySource` 只在 `random.*` 中有属性；
5. 操作系统环境变量；
6. Java 系统属性(`System.getProperties()`)；
7. 来自 `java:comp/env` 的 JNDI 属性；
8. `ServletContext` 初始化参数；
9. `ServletConfig` 初始化参数；
10. `SPRING_APPLICATION_JSON` 的属性(内嵌在环境变量或系统属性中的内联 JSON)；
11. 命令行参数；
12. 在你的测试上的 `properties` 属性，在 `@SpringBootTest` 和测试注解中可用，用于测试应用程序的特定部分；
13. 用于你的测试类 `@TestPropertySource` 注解；
14. 当 DevTools 处于活动状态时，`$HOME/.config/spring-boot` 目录中的 DevTools 全局设置属性。

配置数据文件按以下顺序考虑：

1. 打包在 JAR 中的应用程序属性(application.properties 和 YAML 变体)；
2. 打包在 JAR 中的特定于配置文件的应用程序属性(application-{profile}.properties 和 YAML 变体)；
3. 打包在 JAR 之外的应用程序属性(application.properties 和 YAML 变体)；
4. 打包在 JAR 之外的特定于配置文件的应用程序属性(application-{profile}.properties 和 YAML 变体)；

**注意**：

对于整个应用程序，建议坚持使用一种格式。如果在同一位置具有同时具有 `.properties` 和 `.yml` 格式的配置文件，则 `.properties` 优先。

为了提供一个具体的示例，假设开发了一个使用 `name` 属性的 `@Component`，如下例所示：

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyBean {

    @Value("${name}")
    private String name;

    public String getName() {
        return name;
    }
}
```

在应用程序类路径上（例如，在 jar 中），可以有一个 `application.properties` 文件，该文件为 name 提供合理的默认属性值。在新环境中运行时，可以在 jar
外部提供 `application.properties` 文件来覆盖 `name` 值。对于一次性测试，可以使用特定的命令行开关启动（例如，`java -jar app.jar --name="Spring"`）。

**application.yml**:

```yaml
name: springboot
```

运行启动配置如下图所示：

![idea启动配置](../../assets/20230220-idea启动配置.png)

控制台输出内容如下：

```text
...
2023-02-20 11:53:37.123  INFO 14740 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-02-20 11:53:37.130  INFO 14740 --- [           main] c.o.s.core.externalconfig.MyApplication  : Started MyApplication in 0.959 seconds (JVM running for 1.611)
name：spring
```

**TIP**：

`env` 和 `configprops` 端点在确定属性为何具有特定值时非常有用。可以使用这两个端点诊断意外的属性值。

## 1.访问命令行属性

默认情况下，`SpringApplication` 将任何命令行选项参数（即以 `--` 开头的参数，例如 `--server.port=9000`）转换为 `property`，并将其添加到 Spring `Environment`
中。如前所述，命令行属性始终优先于基于文件的属性源。

如果不希望将命令行属性添加到 `Environment` 中，可以禁用它们。通过使用 `SpringApplication.setAddCommandLineProperties(false)`。如下例所示：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MyApplication {


    public static void main(String[] args) {
        final SpringApplication springApplication = new SpringApplication(MyApplication.class);
        // 禁用命令行参数
        springApplication.setAddCommandLineProperties(false);
        springApplication.run(args);
    }
}
```

## 2.JSON 应用程序属性

环境变量和系统属性通常有一些限制，这意味着某些属性名称不能使用。为了帮助实现这一点，Spring Boot 允许将属性块编码到单个 JSON 结构中。

当应用程序启动时，任何 `spring.application.json` 或 `SPRING_APPLICATION_JSON` 属性都将被解析并添加到 Environment 中。

例如，`SPRING_APPLICATION_JSON` 属性可以在 UN*X shell 的命令行中作为环境变量提供：

```shell
$ SPRING_APPLICATION_JSON='{"my":{"name":"test"}}' java -jar myapp.jar
```

在前面的例子中，在 Spring `Environment` 中得到 `my.name=test`。

同样的 JSON 也可以作为系统属性提供：

```shell
$ java -Dspring.application.json='{"my":{"name":"test"}}' -jar myapp.jar
```

或者你可以使用命令行参数来提供 JSON：

```shell
$ java -jar myapp.jar --spring.application.json='{"my":{"name":"test"}}'
```

如果要部署到经典的应用程序服务器，还可以使用名为 `java:comp/env/spring.application.json` 的 JNDI 变量。

**注意**：

尽管来自 JSON 的 null 值将被添加到结果属性源，但 `PropertySourcesPropertyResolver` 将 null 属性视为缺少的值。这意味着 JSON 不能用 null 值覆盖来自低级属性源的属性。

## 3.外部应用程序属性

当应用程序启动时，Spring Boot 将自动从以下位置查找并加载 `application.properties` 和 `application.yaml` 文件：

1. 类路径

   a. 类路径根

   b. 类路径 `/config` 包
2. 当前目录

   a. 当前目录

   b. 当前目录下的 `config/` 子目录

   c. `config/` 子目录的直接子目录

列表按优先级排序（来自较低项的值覆盖较早的项）。加载文件中的文档作为 `PropertySources` 添加到 Spring `Environment` 中。

如果你不喜欢将 `application` 作为配置文件名，可以通过指定 `spring.config.name` 环境属性来切换到其他文件名。例如，要查找 `myproject.properties` 和 `myproject.yaml`
文件，可以按如下方式运行应用程序：

```shell
$ java -jar myproject.jar --spring.config.name=myproject
```

还可以使用 `spring.config.location` 环境属性来引用显式位置。此属性接受一个或多个要检查的位置的逗号分隔列表。

以下示例显示了如何指定两个不同的文件：

```shell
$ java -jar myproject.jar --spring.config.location=\
  optional:classpath:/default.properties,\
  optional:classpath:/override.properties
```

**TIP**：

使用 `optional` 前缀：如果位置是可选的，并且不介意它们是否存在。

**警告**：

很早就使用 `spring.config.name`、`spring.config.location` 和 `spring.config.additional-location` 来确定必须加载哪些文件。它们必须定义为环境属性（通常是 OS
环境变量、系统属性或命令行参数）。

如果 `spring.config.location` 包含目录（而不是文件），它们应该以 `/` 结尾。在运行时，它们将在加载之前附加从 `spring.config.name`
生成的名称。直接导入 `spring.config.location` 中指定的文件。

**注意**：

目录和文件位置值也被展开，以检查特定于概要文件的文件。例如，如果你有一个 `classpath:myconfig.properties` 的 `spring.config.location`
，还将找到适当的 `classpath:myconfig-<profile>.properties` 加载文件。

在大多数情况下，你添加的每个 `spring.config.location` 项都将引用单个文件或目录。位置按照定义的顺序进行处理，后一个位置可以覆盖前一个位置的值。

如果有一个复杂的位置要设置，并且使用特定于配置的配置文件，可能需要提供进一步的提示，以便 Spring Boot
知道它们应该如何分组。位置组是一组位置，这些位置都被视为处于同一级别。例如，可能希望对所有类路径位置进行分组，然后对所有外部位置进行分组。位置组中的项目应以 ";" 分隔。

使用 `spring.config.location` 配置的位置将替换默认位置。例如，如果 `spring.config.location`
配置值为 `optional:classpath:/custom-config/,optional:file:./custom-config/`，则考虑的完整位置集为：

1. `optional:classpath:custom-config/`
2. `optional:file:./custom-config/`

如果希望添加其他位置而不是替换它们，可以使用 `spring.config.additional-location`
。从其他位置加载的属性可以覆盖默认位置中的属性。例如，如果 `spring.config.additional-location`
配置了 `optional:classpath:/custom-config/,optional:file:./custom-config/`，考虑的完整位置集是：

1. `optional:classpath:/;optional:classpath:/config/`
2. `optional:file:./;optional:file:./config/;optional:file:./config/*/`
3. `optional:classpath:custom-config/`
4. `optional:file:./custom-config/`

这种搜索顺序允许你在一个配置文件中指定默认值，然后选择性地覆盖另一个配置中的这些值。可以在其中一个默认位置的 `application.properties`（或使用 `spring.config.name`
选择的任何其他基本名称）中为应用程序提供默认值。然后，可以在运行时使用位于其中一个自定义位置的不同文件覆盖这些默认值。

**注意**：

如果你使用环境变量而不是系统属性，则大多数操作系统不允许使用句点分隔的键名，但可以使用下划线(例如，`SPRING_CONFIG_NAME` 而不是 `spring.config.name`)。

**注意**：

如果你的应用程序在 Servlet 容器或应用程序服务器中运行，则可以使用 JNDI 属性(在 `java:comp/env` 中)或 Servlet 上下文初始化参数来代替环境变量或系统属性。

### 可选位置

默认情况下，当指定的配置数据位置不存在时，Spring Boot 将抛出 `ConfigDataLocationNotFoundException` 异常并且应用程序将无法启动。

如果想指定一个位置，但不介意它是否总是存在，可以使用 `optional:` 前缀。可以在 `spring.config.location` 和 `spring.config.additional-location`
属性，以及 `spring.config.import` 声明。

例如，`spring.config.import` 值为 `optional:file:/myconfig.properties` 允许应用程序启动，即使 `myconfig.properties` 文件丢失也是如此。如下例所示：

**application.yml**:

```yaml
spring:
  config:
    import: optional:/myconfig.properties
```

如果要忽略所有 `ConfigDataLocationNotFoundExceptions` 并始终继续启动应用程序，可以使用 `spring.config.on-not-found`
属性。使用 `SpringApplication.setDefaultProperties(…)` 或使用系统/环境变量设置要忽略的值。

### 通配符位置

如果配置文件位置包含最后一个路径段的 `*` 字符，则将其视为通配符位置。加载配置时会展开通配符，以便也会检查直接子目录。当存在多个配置属性源时，通配符位置在Kubernetes等环境中特别有用。

例如，如果有一些 Redis 配置和一些 MySQL 配置，可能需要保持这两种配置的分离，同时要求 `application.properties` 文件。这可能会导致两个单独的 `application.properties`
文件。安装在不同的位置，如 `/config/redis/application.properties` 和 `/config/mysql/application.properties`。在这种情况下，通配符位置为 `config/*/`
，将导致两个文件都被处理。

默认情况下，Spring Boot 在默认搜索位置包含 `config/*/`。这意味着将搜索 jar 之外的 `/config` 目录的所有子目录。

可以将通配符位置与 `spring.config.location` 和 `spring.config.additional-location` 属性一起使用。

**注意**：

通配符位置只能包含一个 `*`，对于目录搜索位置，必须以 `*/` 结尾，对于文件搜索位置，则必须以 `*/<filename>` 结尾。带有通配符的位置根据文件名的绝对路径按字母顺序排序。

**TIP**：

通配符位置仅适用于外部目录。不能在 `classpath:` 位置中使用通配符。

### 配置特定文件

除了应用程序属性文件之外，Spring Boot 还将尝试使用命名约定 `application-{profile}` 加载特定配置文件。例如，如果应用程序激活名为 `prod` 的配置文件并使用 YAML
文件，那么将同时考虑 `application.yml` 和 `application-prod.yml`。

特定配置文件的属性从与标准 `application.properties` 相同的位置加载，特定配置文件总是覆盖非特定的文件。如果指定了多个配置文件，则采用最后获胜策略。例如，如果配置文件 `prod,live`
由 `spring.profiles.active` 属性指定
，`application-prod.properties` 中的值可以被 `application-live.properties` 中的值覆盖。

**注意**：

最后获胜策略应用于位置组级别。`classpath:/cfg/,classpath:/ext/` 的 `spring.config.location` 不会有与 `classpath:/cfg/;classpath:/ext/`
相同的重写规则。

例如，继续上面的 `prod,live` 示例，我们可能有以下文件：

```text
/cfg
    application-live.properties
/ext
    application-live.properties
    application-prod.properties
```

当我们有 `classpath:/cfg/,classpath/ext/we` 的 `spring.config.Location` 时。先处理所有 `/cfg` 文件，然后处理所有 `/ext` 文件：

1. `/cfg/application-live.properties`
2. `/ext/application-prod.properties`
3. `/ext/application-live.properties`

当我们有 `classpath:/cfg/;classpath:/ext/` 时（带有 ; 分隔符）我们在同一级别上处理 `/cfg` 和 `/ext`：

1. `/ext/application-prod.properties`
2. `/cfg/application-live.properties`
3. `/ext/application-live.properties`

`Environment` 具有一组默认配置文件（默认情况下为 `[default]`），如果未设置活动配置文件，则使用这些配置文件。换句话说，如果没有显式激活配置文件，那么将考虑 `application-default` 属性。

**注意**：

属性文件只加载一次。 如果已经直接导入了特定配置文件的属性文件，则不会再次导入。

### 导入附加数据

应用程序属性可以使用 `spring.config.import` 属性从其他位置导入更多配置数据。导入将在发现时进行处理，并被视为插入声明导入的文档下面的附加文档。

例如，类路径 `application.properties` 文件中可能包含以下内容：

Properties

```properties
spring.application.name=myapp
spring.config.import=optional:file:./dev.properties
```

Yaml

```yaml
spring:
  application:
    name: "myapp"
  config:
    import: "optional:file:./dev.properties"
```

这将触发当前目录中 `dev.properties` 文件的导入（如果存在这样的文件）。导入的 `dev.properties` 中的值将优先于触发导入的文件。在上面的示例中，`dev.properties`
可以将 `spring.application.name` 重新定义为不同的值。

无论声明多少次，导入都只会导入一次。在 properties/yaml 文件中的单个文档中定义导入的顺序并不重要。例如，下面的两个示例产生相同的结果：

Properties

```properties
spring.config.import=my.properties
my.property=value
```

Yaml

```yaml
spring:
  config:
    import: "my.properties"
my:
  property: "value"
```

Properties

```properties
my.property=value
spring.config.import=my.properties
```

Yaml

```yaml
my:
  property: "value"
spring:
  config:
    import: "my.properties"
```

在上述两个示例中，`my.properties` 文件中的值将优先于触发其导入的文件。

可以在单个 `spring.config.import` 键下指定多个位置。位置将按照定义的顺序进行处理，后面导入的将优先。

**注意**：

在适当的时候，也会考虑导入特定配置文件的变量。上面的示例将同时导入 `my.properties` 和任何 `my-<profile>.properties` 变量。

**TIP**：

Spring Boot 包括可插拔API，允许支持各种不同的位置地址。默认情况下，可以导入 Java 属性、YAML和 “配置树”。

第三方 jar 可以提供对其他技术的支持（不要求文件是本地的）。例如，你可以想象配置数据来自 Consul、Apache ZooKeeper 或 Netflix Archaius 等外部存储。

如果要支持自己的位置，请查看 `org.springframework.boot.context.config` 包中的 `ConfigDataLocationResolver` 和 `ConfigDataLoader` 类。

### 导入无扩展名文件

某些云平台无法向卷装载的文件添加文件扩展名。要导入这些无扩展名文件，您需要给 Spring Boot 一个提示，以便它知道如何加载它们。您可以通过在方括号中放置扩展提示来完成此操作。

例如，假设您有一个 `/etc/config/myconfig` 文件，希望将其作为 yaml 导入。你可以使用以下命令从 `application.properties` 导入它：

Properties

```properties
spring.config.import=file:/etc/config/myconfig[.yaml]
```

Yaml

```yaml
spring:
  config:
    import: "file:/etc/config/myconfig[.yaml]"
```

### 使用配置树

在云平台（如 Kubernetes）上运行应用程序时，通常需要读取平台提供的配置值。出于这种目的使用环境变量并不罕见，但这可能会有缺点，特别是如果值应该保密的话。

作为环境变量的替代方案，许多云平台现在允许您将配置映射到装载的数据卷中。例如，Kubernetes 可以卷装载 `ConfigMaps` 和 `Secrets`。

可以使用两种常见的卷装载模式：

1. 一个文件包含一组完整的属性(通常写为 YAML)。
2. 多个文件被写入目录树，其中文件名成为 “key”，内容成为 “value”。

对于第一种情况，可以像上面描述的那样直接使用 `spring.config.import` 导入 YAML 或 Properties 文件。对于第二种情况，需要使用 `configtree:` 前缀，以便 Spring Boot
知道它需要将所有文件公开为属性。

作为一个例子，让我们想象 Kubernetes 已经挂载了以下卷:

```text
etc/
    config/
        myapp/
            username
            password
```

`username` 文件的内容将是一个配置值，`password` 的内容将是一个秘钥。

要导入这些属性，你可以将以下内容添加到你的 `applation.properties` 或 `applation.yaml`文件中：

Properties

```properties
spring.config.import=optional:configtree:/etc/config/
```

Yaml

```yaml
spring:
  config:
    import: "optional:configtree:/etc/config/"
```

然后，可以按照通常的方式从 `Environment` 访问或注入 `myapp.username` 和 `myapp.password` 属性。

**TIP**：

配置树下的文件夹构成属性名称。在上述示例中，以 `username` 和 `password` 访问属性，可以将 `spring.config.import`
设置为 `optional:configtree:/etc/config/myapp`。

**注意**：

带点符号的文件名也被正确映射。例如，在上面的示例中，`/etc/config` 中名为 `myapp.username` 的文件将导致 `Environment` 中的 `myapp.username` 属性。

**TIP**：

配置树值可以绑定到字符串 `String` 和 `byte[]` 类型，具体取决于预期的内容。

如果要从同一父文件夹导入多个配置树，则可以使用通配符快捷方式。任何以 `/*/` 结尾的 `configtree:location` 都会将所有直接子级作为配置树导入。

例如，给定以下卷：

```text
etc/
    config/
        dbconfig/
            db/
                username
                password
    mqconfig/
        mq/
            username
            password
```

你可以使用 `configtree:/etc/config/*/` 作为导入位置：

Properties

```properties
spring.config.import=optional:configtree:/etc/config/*/
```

Yaml

```yaml
spring:
  config:
    import: "optional:configtree:/etc/config/*/"
```

这将添加 `db.username`, `db.password`, `mq.username` 和 `mq.password` 属性。

**注意**：

使用通配符加载的目录按字母顺序排序。如果你需要不同的顺序，则应将每个位置作为单独的导入列出。

配置树也可以用于 Docker 秘钥。授予 Docker 群服务时访问一个秘钥，该秘钥被装入容器中。例如，如果一个名为 `db.password` 安装在 `/run/secrets/` 位置，您可以使 `db.password` 对
Spring 可用环境，使用以下选项：

Properties

```properties
spring.config.import=optional:configtree:/run/secrets/
```

Yaml

```yaml
spring:
  config:
    import: "optional:configtree:/run/secrets/"
```

### 属性占位符

`application.properties` 和 `application.yml` 中的值在使用时会通过现有的 `Environment` 进行过滤，因此可以重新引用以前定义的值(例如，从系统属性或环境变量)
。标准的 `${name}` 属性占位符语法可以在值内的任何位置使用。属性占位符还可以使用 `:` 指定默认值，以将默认值与属性名称分开，例如 `${name:default}`。

以下示例显示了带默认值和不带默认值的占位符的使用：

Properties

```properties
app.name=MyApp
app.description=${app.name} is a Spring Boot application written by ${username:Unknown}
```

Yaml

```yaml
app:
  name: "MyApp"
  description: "${app.name} is a Spring Boot application written by ${username:Unknown}"
```

假设 `username` 属性没有在其他地方设置，`app.description` 的值将是 `MyApp is a Spring Boot application written by Unknown`。

**注意**：

你应该始终使用占位符中的规范形式（仅使用小写字母的串形写法）引用占位符中的属性名称。这将允许 Spring Boot 使用与放松绑定 `@ConfigurationProperties` 时相同的逻辑。

例如，`${demo.item-price}` 将从 `application.properties` 文件中获取 `demo.iterm-price` 和 `demo.itemPrice`
形式数据，并从系统环境中获取 `DEMO_ITEMPRICE`。如果改用 `${demo.itemPrice}`，则不会考虑 `demo.item-price` 和 `DEMO_ITEMPRICE`。

**TIP**：

你还可以使用此技术来创建现有 Spring Boot 属性的 “short” 变量。

### 使用多文档文件

Spring Boot 允许你将单个物理文件拆分为多个逻辑文档，每个逻辑文档都是独立添加的。文档按照从上到下的顺序进行处理。后续文档可以覆盖早期文档中定义的属性。

对于 `application.yml` 文件，使用标准的 YAML 多文档语法。三个连续的连字符表示一个文档的结尾和下一个文档开始。

例如，以下文件包含两个逻辑文档：

```yaml
spring:
  application:
  name: "MyApp"
---
spring:
  application:
    name: "MyCloudApp"
  config:
    activate:
      on-cloud-platform: "kubernetes"
```

对于 `application.properties` 文件，使用特殊的 `#---` 或 `!---` 注释用于标记文档拆分：

```properties
spring.application.name=MyApp
#---
spring.application.name=MyCloudApp
spring.config.activate.on-cloud-platform=kubernetes
```

**注意**：

属性文件分隔符不能有任何前导空格，并且必须正好有三个连字符。分隔符前后的行不能是相同的注释前缀。

**TIP**：

多文档属性文件通常与激活属性（如 `spring.config.activate.on-profile`）结合使用。

**警告**：

无法使用 `@PropertySource` 或 `@TestPropertySource` 批注加载多文档属性文件。

### 激活属性

有时，仅在满足某些条件时激活一组给定的属性非常有用。例如，你可能具有仅在特定配置文件处于活动状态时才相关的属性。

你可以使用 `spring.config.activate.*` 有条件地激活属性文档。

以下激活属性可用：

| 属性                | 说明                              |
|-------------------|---------------------------------|
| on-profile        | 必须匹配才能激活文档的配置文件表达式。             |
| on-cloud-platform | 要使文档处于活动状态，必须检测到的CloudPlatform。 |

例如，下面指定第二个文档仅在 Kubernetes 上运行时有效，并且仅在 “prod” 或 “staging” 配置文件处于活动状态时有效：

Properties

```properties
myprop=always-set
#---
spring.config.activate.on-cloud-platform=kubernetes
spring.config.activate.on-profile=prod | staging
myotherprop=sometimes-set
```

Yaml

```yaml
myprop:
  "always-set"
---
spring:
  config:
    activate:
      on-cloud-platform: "kubernetes"
      on-profile: "prod | staging"
myotherprop: "sometimes-set"
```

## 4.加密属性

Spring Boot 不提供任何内置的对加密属性值的支持，但是，它提供了修改 Spring 环境中包含的值所必需的钩子点。EnvironmentPostProcessor 接口允许你在应用程序启动之前操作环境。

如果你需要一种安全的方式来存储凭证和密码，请使用 [Spring Cloud Vault](https://cloud.spring.io/spring-cloud-vault/)
项目提供在 [HashiCorp Vault](https://www.vaultproject.io/) 中存储外部化配置的支持。

## 5.使用YAML

[YAML](https://yaml.org) 是 JSON 的超集，因此是指定分层配置数据的方便格式。只要类路径上有 [SnakeYAML](https://github.com/snakeyaml/snakeyaml)
库，`SpringApplication` 类就会自动支持 YAML 作为属性的替代。

**注意**：

如果你使用 “Starters”，则 `spring-boot-starter` 会自动提供 SnakeYAML。

### 将 YAML 映射到属性

YAML 文档需要从层次格式转换为可以与 Spring `Environment` 一起使用的平面结构。例如，考虑以下 YAML 文档:

```yaml
environments:
  dev:
    url: "https://dev.example.com"
    name: "Developer Setup"
  prod:
    url: "https://another.example.com"
    name: "My Cool App"
```

为了从 `Environment` 访问这些属性，它们将被展开，如下所示：

```properties
environments.dev.url=https://dev.example.com
environments.dev.name=Developer Setup
environments.prod.url=https://another.example.com
environments.prod.name=My Cool App
```

同样，YAML列表也需要扁平化。它们表示为带有 [index] 的属性键。例如，考虑以下YAML：

```yaml
my:
  servers:
    - "dev.example.com"
    - "another.example.com"
```

前面的示例将转换为以下属性：

```properties
my.servers[0]=dev.example.com
my.servers[1]=another.example.com
```

**TIP**：

使用 `[index]` 表示法的属性可以使用 Spring Boot 的 `Binder` 类绑定到 Java `List` 或 `Set` 对象。

**警告**：

YAML 文件不能通过使用 `@PropertySource` 或 `@TestPropertySource` 注解来加载。因此，在需要以这种方式加载值的情况下，需要使用 properties 文件。

### 直接加载 YAML

Spring Framework 提供了两个方便的类，可用于加载 YAML 文档。`YamlPropertiesFactoryBean` 将 YAML 作为 `Properties` 加载，`YamlMapFactoryBean` 将
YAML 作为 `Map` 加载。

如果要将 YAML 作为 Spring `PropertySource` 加载，也可以使用 `YamlPropertySourceLoader` 类。

## 6.配置随机值

`RandomValuePropertySource` 用于注入随机值（例如，注入秘钥或测试用例）。它可以生成整数、longs、uuids 或字符串，如下例所示：

Properties

```properties
my.secret=${random.value}
my.number=${random.int}
my.bignumber=${random.long}
my.uuid=${random.uuid}
my.number-less-than-ten=${random.int(10)}
my.number-in-range=${random.int[1024,65536]}
```

Yaml

```yaml
my:
  secret: "${random.value}"
  number: "${random.int}"
  bignumber: "${random.long}"
  uuid: "${random.uuid}"
  number-less-than-ten: "${random.int(10)}"
  number-in-range: "${random.int[1024,65536]}"
```

`random.int*` 语法是 `OPEN value (,max) CLOSE`，其中 `OPEN,CLOSE` 是任意字符，并且 `value,max` 是整数。如果提供了 `max`，则 `value` 为最小值，`max`
为最大值(不包括)。

## 7.配置系统环境属性

Spring Boot 支持为环境属性设置前缀。如果系统环境由多个具有不同配置要求的 Spring Boot 应用程序共享，这将非常有用。可以在 `SpringApplication` 上直接设置系统环境属性的前缀。

例如，如果将前缀设置为 `input`，则 `remote.timeout` 等属性也将解析为系统环境中的 `input.remote.timeout`。

## 8.类型安全配置属性

使用 `@Value("${property}")` 注解来注入配置属性有时会很麻烦，特别是如果你正在处理多个属性，或者你的数据本质上是分层的。Spring Boot 提供了另一种使用属性的方法，该方法允许强类型 bean
管理和验证应用程序的配置。

**TIP**：

可以查看 `@Value` 和类型安全配置属性之间的区别。

### JavaBean 属性绑定

可以绑定一个 bean 声明标准 JavaBean 属性，如下例所示:

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@ConfigurationProperties("my.service")
public class MyProperties {

    private boolean enabled;

    private InetAddress remoteAddress;

    private final Security security = new Security();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Security getSecurity() {
        return this.security;
    }


    public static class Security {
        private String username;

        private String password;

        private List<String> roles = new ArrayList<>(Collections.singleton("USER"));

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
```

前面的 POJO 定义了以下属性：

- `my.service.enabled`，默认值为 `false`；
- `my.service.remote-address`，使用可以从 `String` 强制转换的类型；
- `my.service.security.username`，使用嵌套的 “security” 对象，其名称由属性的名称确定。特别是，这里根本没有使用该类型，可能是 `SecurityProperties`；
- `my.service.security.password`；
- `my.service.security.roles`，具有默认为 `USER` 的 `String` 集合。

Yaml

```yaml
my:
  service:
    enabled: false
    remote-address: ""
    security:
      username: "admin"
      password: "admin"
      roles:
        - USER
```

**注意**：

通过 properties 文件、YAML 文件、环境变量和其他机制配置的映射到 Spring Boot 中可用的 `@ConfigurationProperties` 类的属性是公共 API，但类本身的访问器(
getters/setters)不能直接使用。

这种安排依赖于默认的空构造函数，getter 和 setter 通常是强制性的，因为绑定是通过标准的 JavaBeans 属性描述符进行的，就像在 SpringMVC 中一样。在下列情况下，可以省略设置器：

- Maps，只要它们被初始化，就需要一个 getter，但不一定需要一个 setter，因为它们可以被绑定器改变；
- 可以通过索引(通常使用 YAML)或使用单个逗号分隔值(属性)访问集合和数组。在后一种情况下，必须使用 setter。我们建议始终为这类类型添加 setter。如果初始化集合，请确保它不是不可变的(如上例所示)；
- 如果初始化了嵌套的 POJO 属性(如上例中的 `Security` 字段)，则不需要 setter。如果希望绑定器创建实例。通过使用它的默认构造函数，你需要一个 setter。

有些人使用 Project Lombok 自动添加 getter 和 setter。确保 Lombok 不会为这种类型生成任何特定的构造函数，因为容器会自动使用它来实例化对象。

最后，只考虑标准的 Java Bean 属性，不支持绑定静态属性。

### 构造函数绑定

上一节中的示例可以以不可变的方式重写，如下例所示：

```java
import java.net.InetAddress;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Component;

@Component
@ConstructorBinding
@ConfigurationProperties("my.service")
public class MyProperties {
    private final boolean enabled;
    private final InetAddress remoteAddress;
    private final Security security;

    public MyProperties(boolean enabled, InetAddress remoteAddress, Security security) {
        this.enabled = enabled;
        this.remoteAddress = remoteAddress;
        this.security = security;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public Security getSecurity() {
        return this.security;
    }

    public static class Security {

        private final String username;

        private final String password;

        private final List<String> roles;

        public Security(String username, String password, @DefaultValue("USER") List<String> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public List<String> getRoles() {
            return this.roles;
        }
    }
}
```

在此设置中，`@ConstructorBinding` 注解用于指示应使用构造函数绑定。这意味着绑定器将期望找到一个具有你希望绑定的参数的构造函数。如果你使用的是 Java16
或更高版本，则可以对记录使用构造函数绑定。在这种情况下，除非记录有多个构造函数，否则不需要使用 `@ConstructorBinding`。

`@ConstructorBinding` 类的嵌套成员（例如上面示例中的 Security）也将通过其构造函数绑定。

默认值可以在构造函数参数上使用 `@DefaultValue` 指定，或者在使用 Java 16 或更高版本时使用记录组件指定。转换服务将应用于将 `String` 值强制转换为缺失属性的目标类型。

参考前面的示例，如果没有属性绑定到 `Security`, `MyProperties` 实例将包含一个用于安全性的 `null` 值。为了使它包含一个非空的 `Security` 实例，即使没有属性绑定到它(在使用 Kotlin
时，这将要求 `Security` 的 `username` 和 `password` 参数被声明为空，因为它们没有默认值)，使用一个空的 `@DefaultValue` 注解:

```java
public MyProperties(boolean enabled,InetAddress remoteAddress,@DefaultValue Security security){
        this.enabled=enabled;
        this.remoteAddress=remoteAddress;
        this.security=security;
        }
```

**注意**：

要使用构造函数绑定，必须使用 `@EnableConfigurationProperties` 或配置属性扫描来启用类。不能对由常规 Spring 机制创建的 bean 使用构造函数绑定（例如 `@Component`
Bean、使用 `@Bean` 方法创建的 Bean 或使用 `@Import` 加载的 Bean）

**TIP**：

如果你的类有多个构造函数，你也可以在应该绑定的构造函数上直接使用 `@ConstructorBinding`。

**注意**：

不建议 `java.util.Optional` 与 `@ConfigurationProperties`
一起使用，因为它主要用于作为返回类型。因此，它不太适合配置属性注入。为了与其他类型的属性保持一致，如果你声明了一个 `Optional` 属性并且它没有值，那么将绑定 `null` 而不是空的 `Optional`。

### 启用 @ConfigurationProperties 注解类型

Spring Boot 提供了绑定 `@ConfigurationProperties` 类型并将它们注册为 Bean 的基础设施。你可以逐个类地启用配置属性，也可以启用与组件扫描类似的配置属性扫描。

有时，用 `@ConfigurationProperties` 注释的类可能不适合扫描，例如，如果你正在开发自己的自动配置或希望有条件地启用它们。在这些情况下，使用 `@EnableConfigurationProperties`
注解指定要处理的类型列表。这可以在任何 `@Configuration` 类上完成，如下例所示：

```java
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SomeProperties.class)
public class MyConfiguration {

}
```

要使用配置属性扫描，请将 `@ConfigurationPropertiesScan` 注解添加到应用程序中。通常，它被添加到用 `@SpringBootApplication` 注解的主应用程序类中，但也可以添加到任何
`@Configuration` 类中。默认情况下，扫描将从声明注解的类的包中进行。如果要定义特定的要扫描的包，可以按以下示例所示进行扫描：

```java
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan({"com.example.app", "com.example.another"})
public class MyApplication {

}
```

**注意**：

当使用配置属性扫描或通过 `@EnableConfigurationProperties` 注册 `@ConfigurationProperties` bean 时，bean 具有常规名称：`<prefix>-<fqn>`
，其中 `<prefix>` 是在 `@ConfigurationProperties` 注解和 `＜fqn＞` 是 bean 的完全限定名称。如果注释不提供任何前缀，则只使用 bean 的完全限定名称。

上面示例中的 bean 名称是 `com.example.app-com.example.app.SomeProperties`。

我们建议 `@ConfigurationProperties` 只处理环境，特别是不要从上下文注入其他 Bean。对于特殊情况，可以使用 setter 注入或框架提供的任何 `*Aware` 接口(
例如，如果你需要访问 `Environment`，则可以使用 `EnvironmentAware`)。如果你仍然希望使用构造函数注入其他 Bean，则配置属性 Bean 必须使用 `@Component` 进行注释，并使用基于
JavaBean 的属性绑定。

### 使用 @ConfigurationProperties 注解类型

这种类型的配置在 `SpringApplication` 外部 YAML 配置中尤其适用，如下例所示：

```yaml
my:
  service:
    remote-address: 192.168.1.1
    security:
      username: "admin"
      roles:
        - "USER"
        - "ADMIN"
```

要使用 `@ConfigurationProperties` bean，可以以与任何其他 bean 相同的方式注入它们，如下例所示：

```java
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private final MyProperties properties;

    public MyService(MyProperties properties) {
        this.properties = properties;
    }

    public void openConnection() {
        Server server = new Server(this.properties.getRemoteAddress());
        server.start();
        // ...
    }
    // ...
}
```

**TIP**：

使用 `@ConfigurationProperties` 还可以生成元数据文件，ide 可以使用该文件为自己的键提供自动补全功能。

### 第三方配置

除了使用 `@ConfigurationProperties` 注释类之外，还可以在公共 `@Bean` 方法上使用它。当你想要将属性绑定到你无法控制的第三方组件时，这样做可能特别有用。

要从 `Environment` 属性配置 Bean，请将 `@ConfigurationProperties` 添加到其 Bean 注册中，如下例所示：

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ThirdPartyConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "another")
    public AnotherComponent anotherComponent() {
        return new AnotherComponent();
    }
}
```

用 `another` 前缀定义的任何 JavaBean 属性都被映射到该 `AnotherComponent` Bean 上，其方式类似于前面的 `SomeProperties` 示例。

### 宽松的绑定

Spring Boot 使用一些宽松的规则将 `Environment` 属性绑定到 `@ConfigurationProperties` Bean，因此在 `Environment` 属性名称和 Bean
属性名称之间不需要完全匹配。这很有用的常见示例包括以破折号分隔的环境属性(例如，`context-path` 绑定到 `contextPath`)和大写环境属性。(例如，`PORT` 绑定到 `port`)。

例如，考虑以下 `@ConfigurationProperties` 类：

```java
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "my.main-project.person")
public class MyPersonProperties {

    private String firstName;

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
```

在前面的代码中，可以使用以下属性名称：

| 属性                                | 说明                                            |
|-----------------------------------|-----------------------------------------------|
| my.main-project.person.first-name | 串行大小写格式，建议在 `.properties` 和 `.yml` 文件中使用      |
| my.main-project.person.firstName  | 标准的驼峰大小写语法                                    |
| my.main-project.person.first_name | 下划线表示法，这是在 `.properties` 和 `.yml` 文件中使用的另一种格式 |
| MY_MAINPROJECT_PERSON_FIRSTNAME   | 大写格式，使用系统环境变量时建议使用                            |

**注意**：

注解的 `prefix` 必须是串行大小写（小写并用 `-` 分隔，例如 `my.main-project.person`）。

| 属性源           | 普通              | 列表                     |
|---------------|-----------------|------------------------|
| Properties 文件 | 骆驼式，串行式，或者下划线符号 | 使用 `[ ]` 或逗号分隔值的标准列表语法 |
| YAML 文件       | 骆驼式，串行式，或者下划线符号 | 标准 YAML 列表语法或逗号分隔值     |
| 环境变量          | 以下划线作为分隔符的大写格式  | 由下划线包围的数值              |
| 系统属性          | 骆驼式，串行式，或者下划线符号 | 使用 `[ ]` 或逗号分隔值的标准列表语法 |

**TIP**：

我们建议尽可能以小写的串行格式存储属性，例如 `my.person.first-name=Rod`。

#### 绑定 Map

绑定到 `Map` 属性时，可能需要使用特殊的括号表示法，以便保留原始键值。如果键没有用 `[]` 括起来，则为非字母数字、`-` 或 `.` 的任何字符都被移除了。

例如，考虑将以下属性绑定到 `Map<String,String>`：

Properties

```properties
my.map.[/key1]=value1
my.map.[/key2]=value2
my.map./key3=value3
```

Yaml

```yaml
my:
  map:
    "[/key1]": "value1"
    "[/key2]": "value2"
    "/key3": "value3"
```

**注意**：

对于 YAML 文件，括号需要用引号括起来，以便正确地解析键。

上面的属性将绑定到一个 `Map`，其中 `/key1`、`/key2` 和 `key3` 是映射中的键。已将斜杠从 `key3` 中删除，因为它没有用方括号括起来。

绑定到标量值时，其中携带 `.` 的键不需要被 `[]` 包围。标量值包括枚举和 `java.lang` 包中除 `Object` 之外的所有类型。将 `a.b=c` 绑定到 `Map<String，String>` 将保留在键中的 `.`
并返回带有条目 `{"a.b"="c"}` 的 Map。对于任何其他类型，如果键包含 `.` 。例如，将 `a.b=c` 绑定到 `Map<String，Object>` 将返回条目为 `{"a"＝{"b"＝"c"}}` 的
Map，而 `[a.b]=c` 将返回条目 `{"a.b"＝"c"}` 的 Map。

#### 从环境变量绑定

大多数操作系统对可用于环境变量的名称施加严格的规则。例如，Linux shell 变量只能包含字母（`a` 到 `z` 或 `A` 到 `Z`）、数字（`0` 到 `9`）或下划线字符（`_`）。按照惯例，Unix shell
变量的名称也将以大写字母表示。

Spring Boot 的宽松绑定规则尽可能与这些命名限制兼容。

要将规范形式的属性名称转换为环境变量名称，可以遵循以下规则：

- 将点（`.`）替换为下划线（`_`）;
- 删除所有破折号（`-`）;
- 转换为大写。

例如，配置属性 `spring.main.log-startup-info` 将是一个名为 `SPRING_MAIN_LOGSTARTUPINFO` 的环境变量。

当绑定到对象列表时，也可以使用环境变量。要绑定到 `List`，元素序号在变量名中应该用下划线括起来。

例如，配置属性 `my.service[0].other` 将使用名为 `MY_SERVICE_0_OTHER` 的环境变量。

### 合并复杂类型

当在多个地方配置列表时，重写将通过替换整个列表来工作。

例如，假设 `MyPojo` 对象的 `name` 和 `description` 属性默认为 `null`。下面的例子显示了 `MyProperties` 中的 `MyPojo` 对象列表：

```java
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("my")
public class MyProperties {

    private final List<MyPojo> list = new ArrayList<>();

    public List<MyPojo> getList() {
        return this.list;
    }
}
```

考虑以下配置：

Properties

```properties
my.list[0].name=my name
my.list[0].description=my description
#---
spring.config.activate.on-profile=dev
my.list[0].name=my another name
```

Yaml

```yaml
my:
  list:
    - name: "my name"
      description: "my description"
---
spring:
  config:
  activate:
  on-profile: "dev"
my:
  list:
    - name: "my another name"
```

如前所述，如果 `dev` 配置文件未处于活动状态，则 `MyProperties.list` 将包含一个 `MyPojo` 条目。但是，如果启用了 `dev` 配置文件，列表仍然只包含一个条目(`my another name`
的名称，`null` 的描述)。此配置不会将第二个 `MyPojo` 实例添加到列表中，也不会合并项目。

当在多个配置文件中指定 `List` 时，将使用具有最高优先级的配置文件(且仅使用该配置文件)。请考虑以下示例：

Properties

```properties
my.list[0].name=my name
my.list[0].description=my description
my.list[1].name=another name
my.list[1].description=another description
#---
spring.config.activate.on-profile=dev
my.list[0].name=my another name
```

Yaml

```yaml
my:
  list:
    - name: "my name"
      description: "my description"
    - name: "another name"
      description: "another description"
---
spring:
  config:
  activate:
  on-profile: "dev"
my:
  list:
    - name: "my another name"
```

在前面的示例中，如果 `dev` 配置文件处于活动状态，`MyProperties.list` 包含一个 `MyPojo` 条目（名称为 `my another name`，`null` 描述）。对于 YAML，逗号分隔列表和 YAML
列表都可以用于完全覆盖列表的内容。

对于 `Map` 属性，您可以绑定从多个源绘制的属性值。但是，对于多个源中的相同属性，将使用具有最高优先级的属性。下面的例子暴露了一个来自于 `MyProperties` 的 `Map<String, MyPojo>`：

```java
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("my")
public class MyProperties {

    private final Map<String, MyPojo> map = new LinkedHashMap<>();

    public Map<String, MyPojo> getMap() {
        return this.map;
    }
}
```

考虑以下配置：

Properties

```properties
my.map.key1.name=my name 1
my.map.key1.description=my description 1
#---
spring.config.activate.on-profile=dev
my.map.key1.name=dev name 1
my.map.key2.name=dev name 2
my.map.key2.description=dev description 2
```

Yaml

```yaml
my:
  map:
    key1:
      name: "my name 1"
      description: "my description 1"
---
spring:
  config:
    activate:
      on-profile: "dev"
my:
  map:
    key1:
      name: "dev name 1"
    key2:
      name: "dev name 2"
      description: "dev description 2"
```

如果 `dev` 配置文件未激活，则 `MyProperties.map` 包含一个键为 `key1` 的条目（名称为 `myname 1`，描述为 `my description 1`）。但是，如果启用了 `dev`
配置文件，则 `map` 包含两个条目，分别带有 `key1`（名称为 `dev name 1`，描述为 `my description 1`）和 `key2`（名称为 `dev name 2`
，描述为 `dev description 2`）。

**注意**：

上述合并规则适用于所有属性源中的属性，而不仅仅是文件。

### 特性转换

当 Spring Boot 绑定到 `@ConfigurationProperties` bean 时，它会尝试强制外部应用程序属性为正确的类型。如果需要自定义类型转换，可以提供 `ConversionService` bean(
具有名为 `conversionService` 的 bean)或自定义属性编辑器(通过 `CustomEditorConfigurer` bean)或自定义 `Converters`(
带有注解为 `@ConfigurationPropertiesBinding` 的 bean 定义)。

**注意**：

由于此 Bean 在应用程序生命周期的早期就被请求，因此请确保限制 `ConversionService`
正在使用的依赖项。通常，在创建时可能不会完全初始化所需的任何依赖项。如果配置键强制不需要自定义 `ConversionService`，并且仅依赖于使用 `@ConfigurationPropertiesBinding`
限定的自定义转换器，则可能需要重命名该自定义 `ConversionService`。

#### 转换时间

Spring Boot 对表示持续时间提供了专门的支持。如果你暴露 `java.time.Duration` 属性时，应用程序属性中的以下格式可用：

- 常规的 `long` 表示法(使用毫秒作为默认单位，除非已指定 `@DurationUnit`)
- `java.time.Duration` 使用的标准 ISO-8601 格式
- 一种更易读的格式，其中值和单位是耦合的( `10s` 表示 10 秒)

请考虑以下示例：

```java
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties("my")
public class MyProperties {

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration sessionTimeout = Duration.ofSeconds(30);
    private Duration readTimeout = Duration.ofMillis(1000);

    public Duration getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(Duration sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Duration getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }
}
```

要将会话超时指定为 30 秒，`30`、`PT30S` 和 `30S` 都是等效的。`500ms` 的读取超时可指定为以下任何形式：`500`、`PT0.5S` 和 `500ms`。

你也可以使用任何受支持的单位。它们是：

- `ns` 纳秒
- `us` 微秒
- `ms` 毫秒
- `s` 秒
- `m` 分钟
- `h` 小时
- `d` 天

默认单位是毫秒，可以使用 @DurationUnit 覆盖，如上面的示例所示。

如果你更喜欢使用构造函数绑定，可以暴露相同的属性，如下例所示：

```java
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties("my")
@ConstructorBinding
public class MyProperties {

    private final Duration sessionTimeout;
    private final Duration readTimeout;

    public MyProperties(@DurationUnit(ChronoUnit.SECONDS) @DefaultValue("30s")
                        Duration sessionTimeout,
                        @DefaultValue("1000ms") Duration readTimeout) {
        this.sessionTimeout = sessionTimeout;
        this.readTimeout = readTimeout;
    }

    public Duration getSessionTimeout() {
        return this.sessionTimeout;
    }

    public Duration getReadTimeout() {
        return this.readTimeout;
    }
}
```

**TIP**：

如果要升级 `Long` 属性，如果单位不是毫秒，请确保定义单位(使用 `@DurationUnit`)。这样做提供了一条透明的升级路径，同时支持更丰富的格式。

#### 转换时间

除了持续时间，Spring Boot 还可以使用 `java.time.Period` 类型。以下格式可以在应用程序属性中使用:

- 常规 `int` 表示(使用天数作为默认单位，除非指定了 `@PeriodUnit`)
- `java.time.Period` 使用的标准 ISO-8601 格式
- 更简单的格式，其中值和单位对是耦合的( `1y3d` 表示 1 年和 3 天)

简单格式支持以下单位：

- y 年份
- m 月份
- w 周
- d 天

**注意**：

`java.time.Period` 类型实际上从未存储周数，它是一个表示 “7天” 的快捷方式。

#### 转换数据大小

Spring Framework 有一个 `DataSize` 值类型，它以字节为单位表示大小。如果暴露 `DataSize` 属性，则应用程序属性中的以下格式可用：

- 常规 `long` 表示(使用字节作为默认单位，除非指定了 `@DataSizeUnit`)
- 一种更可读的格式，其中值和单位是耦合的（`10MB` 表示 10 兆字节）

考虑以下示例：

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@ConfigurationProperties("my")
public class MyProperties {

    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize bufferSize = DataSize.ofMegabytes(2);
    private DataSize sizeThreshold = DataSize.ofBytes(512);

    public DataSize getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(DataSize bufferSize) {
        this.bufferSize = bufferSize;
    }

    public DataSize getSizeThreshold() {
        return this.sizeThreshold;
    }

    public void setSizeThreshold(DataSize sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
    }
}
```

要指定 10 兆字节的缓冲区大小，`10` 和 `10MB` 是等价的。256 字节的大小阈值可以指定为 `256` 或 `256B`。

你也可以使用任何受支持的单位。它们是：

- B 字节
- KB 千字节
- MB 兆字节
- GB 千兆字节
- TB 兆兆字节

默认单位是字节，可以使用 `@DataSizeUnit` 重写，如上面的示例所示。

如果你更喜欢使用构造函数绑定，可以暴露相同的属性，如下例所示：

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@ConfigurationProperties("my")
@ConstructorBinding
public class MyProperties {

    private final DataSize bufferSize;
    private final DataSize sizeThreshold;

    public MyProperties(@DataSizeUnit(DataUnit.MEGABYTES) @DefaultValue("2MB")
                        DataSize bufferSize,
                        @DefaultValue("512B") DataSize sizeThreshold) {
        this.bufferSize = bufferSize;
        this.sizeThreshold = sizeThreshold;
    }

    public DataSize getBufferSize() {
        return this.bufferSize;
    }

    public DataSize getSizeThreshold() {
        return this.sizeThreshold;
    }
}
```

**TIP**：

如果要升级 `Long` 属性，如果不是字节，请确保定义单位（使用 `@DataSizeUnit`）。这样做可以提供透明的升级路径，同时支持更丰富的格式。

### @ConfigurationProperties 验证

当 `@ConfigurationProperties` 类被 Spring 的 `@Validated` 注解注释时，Spring Boot 会尝试验证它们。你可以直接在配置类上使用 JSR-303 `javax.validation`
约束注解。要做到这一点，请确保类路径上有一个兼容的 JSR-303 实现，然后向字段添加约束注释，如下例所示：

```java
import java.net.InetAddress;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("my.service")
@Validated
public class MyProperties {

    @NotNull
    private InetAddress remoteAddress;

    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
```

**TIP**：

还可以通过用 `@Validated` 注解创建配置属性的 `@Bean` 方法来触发验证。

为了确保总是触发嵌套属性的验证，即使没有找到属性，关联的字段也必须用 `@Valid` 标注。下面的示例建立在前面的 `MyProperties` 示例之上：

```java
import java.net.InetAddress;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("my.service")
@Validated
public class MyProperties {

    @NotNull
    private InetAddress remoteAddress;
    @Valid
    private final Security security = new Security();

    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Security getSecurity() {
        return this.security;
    }

    public static class Security {
        @NotEmpty
        private String username;

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
```

你还可以通过创建一个名为 `ConfigurationPropertiesValidator` 的 Bean 定义来添加一个定制的 Spring `Validator`。`@Bean`
方法应该声明为静态的。配置属性验证器是在应用程序生命周期的早期创建的，如果将 `@Bean` 方法声明为静态方法，则无需实例化 `@Configuration` 类即可创建 Bean。这样做可以避免早期实例化可能导致的任何问题。

TIP：

`spring-boot-actuator` 模块包括一个端点，它暴露所有 `@ConfigurationProperties` bean。将 web 浏览器指向 `/actuator/configprops` 或使用等效的 JMX 端点。

### @ConfigurationProperties 和 @Value

`@Value` 注解是核心容器功能，它不提供与类型安全配置属性相同的功能。下表总结了 `@ConfigurationProperties` 和 `@Value` 支持的功能：

| 特性    | @ConfigurationProperties | @Value |
|-------|--------------------------|--------|
| 宽松绑定  | √                        | 限制     |
| 元数据支持 | √                        | ×      |
| SpEL  | ×                        | √      |

**注意**：

如果你确实想使用 `@Value`，我们建议你使用规范形式引用属性名称（串行大小写仅使用小写字母）。这将允许 Spring Boot 使用与放松绑定 `@ConfigurationProperties` 时相同的逻辑。

例如，`@Value("${demo.Item-Price}")` 将从 `application.properties` 文件以及从系统环境 `DEMO_ITEMPRICE` 中获取 `demo.item-price`
和 `demo.itemPrice` 数据。如果你使用 `@Value("${demo.itemPrice}")`，则不会考虑 `demo.item-price` 和 `DEMO_ITEMPRICE`。

如果为自己的组件定义了一组配置键，我们建议将它们分组到带有 `@ConfigurationProperties` 注释的 POJO 中。这样做将为你提供结构化的类型安全对象，你可以将其注入到自己的 bean 中。

在解析这些文件并填充环境时，不会处理应用程序属性文件中的 `SpEL` 表达式。但是，可以在 `@Value` 中编写 `SpEL` 表达式。如果应用程序属性文件中的属性值是 `SpEL` 表达式，则在通过 `@Value`
使用时将对其求值。
