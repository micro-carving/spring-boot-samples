# 日志（Logging）

Spring Boot 使用 [Commons Logging](https://commons.apache.org/logging)
进行所有内部日志记录，但底层日志实现保持打开状态。[Java Util Logging](https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html)
、[Log4J2](https://logging.apache.org/log4j/2.x/) 和 [Logback](https://logback.qos.ch/)
提供了默认配置。在每种情况下，记录器都预先配置为使用控制台输出，也可以使用可选的文件输出。

默认情况下，如果使用 "Starters"，则使用 Logback 进行日志记录。还包括适当的 Logback 路由，以确保使用 Java Util Logging、Commons Logging、Log4J 或 SLF4J
的依赖库都能正常工作。

**TIP**：

Java 有很多可用的日志框架。如果上面的列表看起来令人困惑，请不要担心。通常，你不需要更改日志依赖关系，Spring Boot 默认值工作正常。

当你将应用程序部署到 Servlet 容器或应用程序服务器时，使用 Java Util Logging API 执行的日志记录不会发送到应用程序的日志中。这将防止容器或已部署到容器的其他应用程序执行的日志记录出现在应用程序的日志中。

## 1.日志格式

Spring Boot 的默认日志输出类似于以下示例：

```text
2023-02-22 16:42:02.015  INFO 11512 --- [           main] c.o.s.core.logging.MyApplication         : Starting MyApplication using Java 11.0.9 on myhost with PID 11512
2023-02-22 16:42:02.017  INFO 11512 --- [           main] c.o.s.core.logging.MyApplication         : No active profile set, falling back to 1 default profile: "default"
2023-02-22 16:42:02.513  INFO 11512 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2023-02-22 16:42:02.514  INFO 11512 --- [           main] o.a.catalina.core.AprLifecycleListener   : Loaded Apache Tomcat Native library [1.2.33] using APR version [1.7.0].
2023-02-22 16:42:02.514  INFO 11512 --- [           main] o.a.catalina.core.AprLifecycleListener   : APR capabilities: IPv6 [true], sendfile [true], accept filters [false], random [true], UDS [true].
2023-02-22 16:42:02.514  INFO 11512 --- [           main] o.a.catalina.core.AprLifecycleListener   : APR/OpenSSL configuration: useAprConnector [false], useOpenSSL [true]
2023-02-22 16:42:02.516  INFO 11512 --- [           main] o.a.catalina.core.AprLifecycleListener   : OpenSSL successfully initialized [OpenSSL 1.1.1o  3 May 2022]
2023-02-22 16:42:02.522  INFO 11512 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-02-22 16:42:02.522  INFO 11512 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.71]
2023-02-22 16:42:02.573  INFO 11512 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-02-22 16:42:02.573  INFO 11512 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 522 ms
2023-02-22 16:42:02.766  INFO 11512 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-02-22 16:42:02.773  INFO 11512 --- [           main] c.o.s.core.logging.MyApplication         : Started MyApplication in 0.992 seconds (JVM running for 1.786)
```

将输出以下项目：

- 日期和时间：毫秒精度，易于排序；
- 日志级别：`ERROR`、`WARNING`、`INFO`、`DEBUG` 或 `TRACE`；
- 进程ID；
- `---` 分隔符，用于区分实际日志消息的开头；
- 线程名称：用方括号括起来（对于控制台输出，可以截断）；
- 日志记录器名称：这通常是源类名（通常缩写）；
- 日志信息。

**注意**：

Logback 没有 `FATAL` 级别。它被映射到 `ERROR`。

## 2.控制台输出

默认日志配置在写入消息时将消息回显到控制台。默认情况下，记录 `ERROR` -级别、`WARN` -级别和 `INFO` -级别消息。你还可以通过使用 `--debug` 标志启动应用程序来启用“调试”模式。

```shell
$ java -jar myapp.jar --debug
```

**注意**：

你还可以在 `application.properties` 中指定 `debug=true`。如下图所示：

![debug设置](../../assets/20230222-debug设置.png)

当启用调试模式时，一些核心日志记录器（嵌入式容器、Hibernate 和 Spring Boot）被配置为输出更多信息。启用调试模式不会将应用程序配置为以 `DEBUG` 级别记录所有消息。

或者，你可以通过使用 `--trace` 标志启动应用程序来启用 “跟踪” 模式（或在 `application.properties` 中使用 `trace=true`）。这样可以为选择的核心日志记录器（嵌入式容器、Hibernate
模式生成以及整个 Spring 组合）启用跟踪日志记录。

### 彩色编码输出

如果你的终端支持 ANSI，则使用颜色输出来提高可读性。你可以将 `spring.output.ansi.enabled`
设置为[支持的值](https://docs.spring.io/spring-boot/docs/2.7.8/api/org/springframework/boot/ansi/AnsiOutput.Enabled.html)
，以覆盖自动检测。

通过使用 `%clr` 转换字配置颜色编码。在最简单的形式中，转换器根据日志级别为输出着色，如下例所示：

```shell
%clr(%5p)
```

下表描述了日志级别到颜色的映射：

| 级别    | 颜色  |
|-------|-----|
| FATAL | 红色  |
| ERROR | 红色  |
| WARN  | 黄色  |
| INFO  | 绿色  |
| DEBUG | 绿色  |
| TRACE | 绿色  |

或者，可以通过将其作为转换选项来指定应使用的颜色或样式。例如，要使文本变为黄色，请使用以下设置：

```shell
%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){yellow}
```

支持以下颜色和样式：

- blue （蓝色的）
- cyan （青色的）
- faint （荧光的）
- green （绿色的）
- magenta （洋红色的）
- red （红色的）
- yellow （黄色的）

## 3.文件输出

默认情况下，Spring Boot 只记录到控制台，不写入日志文件。如果要在控制台输出之外写入日志文件，则需要设置 `logging.file.name` 或 `logging.files.path`
属性（例如，在 `application.properties` ）。

application.properties

```properties
# 自动在项目根目录下生成 /logs/myapp.log 文件
logging.file.name=./logs/myapp.log
# 自动在项目根目录下生成 /logs/spring.log 文件，默认生成的日志文件名为 ”spring.log“，不能与 logging.file.name 同时生效，logging.file.name 优先级比 logging.file.path 高
logging.file.path=./logs
```

下表显示了如何一起使用 `logging.*` 属性：

| `logging.file.name` | `logging.file.path` | 示例         | 描述                                        |
|---------------------|---------------------|------------|-------------------------------------------|
| (none)              | (none)              |            | 仅控制台日志记录                                  |
| 特定文件                | (none)              | `my.log`   | 写入指定的日志文件。名称可以是确切的位置或相对于当前目录。             |
| (none)              | 特定目录                | `/var/log` | 将 `spring.log` 写入指定目录。名称可以是确切的位置或相对于当前目录。 |

日志结果如下图所示：

![配置日志输出文件](../../assets/20230222-配置日志输出文件.png)

日志文件在达到 10 MB 时会循环，并且与控制台输出一样，默认情况下会记录错误级别、警告级别和信息级别的消息。

**TIP**：

日志记录属性独立于实际的日志记录基础结构。因此，特定的配置键（比如 Logback 的 `logback.configurationFile` ）不是由 Spring Boot 管理的。

## 4.文件轮换（循环）

如果使用 Logback，则可以使用 `application.properties` 或 `application.yaml` 文件微调日志轮换设置。对于所有其他日志记录系统，你需要自己直接配置轮换设置（例如，如果使用
Log4J2，则可以添加 `log4j2.xml` 或 `log4j2-spring.xml` 文件）。

支持以下循环策略属性：

| 名称                                                     | 描述                      |
|--------------------------------------------------------|-------------------------|
| `logging.logback.rollingpolicy.file-name-pattern`      | 用于创建日志存档的文件名模式          |
| `logging.logback.rollingpolicy.clean-history-on-start` | 是否应在应用程序启动时进行日志归档清理     |
| `logging.logback.rollingpolicy.max-file-size`          | 存档前日志文件的最大大小            |
| `logging.logback.rollingpolicy.total-size-cap`         | 删除之前可以占用的最大日志档案大小       |
| `logging.logback.rollingpolicy.max-history`            | 要保留的归档日志文件的最大数量(默认为 7)。 |

## 5.日志级别

所有支持的日志记录系统都可以通过使用 `logging.level.<logger-name>=<level>` 在 Spring 环境中设置日志记录程序级别（例如，在 `application.properties` 中），其中级别是
TRACE、DEBUG、INFO、WARN、ERROR、FATAL 或 OFF 之一。根日志记录程序可以通过使用 `logging.level.root` 进行配置。

以下示例显示 `application.properties` 中的潜在日志记录设置：

Properties

```properties
logging.level.root=warn
logging.level.org.springframework.web=debug
logging.level.org.hibernate=error
```

Yaml

```yaml
logging:
  level:
    root: "warn"
    org.springframework.web: "debug"
    org.hibernate: "error"
```

还可以使用环境变量设置日志记录级别。例如，`LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG` 将 `org.springframework.web` 设置为 `DEBUG`。

**注意**：

上述方法仅适用于包级日志记录。由于宽松绑定总是将环境变量转换为小写，因此不可能以这种方式为单个类配置日志记录。如果需要为类配置日志记录，可以使用 `SPRING_APPLICATION_JSON` 变量。

## 6.日志分组

能够将相关的日志记录器分组在一起，以便可以同时配置它们，这通常很有用。例如，你可能通常会更改所有与 Tomcat 相关的记录器的日志记录级别，但你很难记住顶级包。

为了帮助实现这一点，Spring Boot 允许你在 Spring `Environment` 中定义日志组。例如，下面是如何定义一个 “tomcat” 组，将它添加到你的 `application.properties`：

Properties

```properties
logging.group.tomcat=org.apache.catalina,org.apache.coyote,org.apache.tomcat
```

Yaml

```yaml
logging:
  group:
    tomcat: "org.apache.catalina,org.apache.coyote,org.apache.tomcat"
```

一旦定义，你可以用一行更改组中所有日志记录器的级别：

Properties

```properties
logging.level.tomcat=trace
```

Yaml

```yaml
logging:
  level:
    tomcat: "trace"
```

Spring Boot 包括以下预定义的日志组，可以开箱即用：

| 名称  | 日志记录器                                                                                                                                                                                                                 |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| web | org.springframework.core.codec,<br/>org.springframework.http,<br/>org.springframework.web,<br/>org.springframework.boot.actuate.endpoint.web,<br/>org.springframework.boot.web.servlet.ServletContextInitializerBeans |
| sql | org.springframework.jdbc.core,<br/>org.hibernate.SQL,<br/>org.jooq.tools.LoggerListener                                                                                                                               |

## 7.日志关闭钩子

为了在应用程序终止时释放日志记录资源，提供了一个 shutdown 钩子，该钩子将在 JVM 退出时触发日志系统清理。这个关机钩子是自动注册的，除非你的应用程序被部署为 war
文件。如果你的应用程序具有复杂的上下文层次结构，那么关闭钩子可能无法满足你的需要。如果没有，禁用 shutdown 钩子，并研究底层日志系统直接提供的选项。例如，Logback
提供[上下文选择器](https://logback.qos.ch/manual/loggingSeparation.html)，允许在自己的上下文中创建每个
Logger。你可以使用 `logging.register-shutdown-hook`
属性来禁用 shutdown 钩子。将其设置为 `false` 将禁用注册。你可以在 `application.properties` 或者 `application.yml` 文件中设置该属性：

Properties

```properties
logging.register-shutdown-hook=false
```

Yaml

```yaml
logging:
  register-shutdown-hook: false
```

### 扩展

关于这个日志钩子问题其实spring官方设计是存在缺陷的，可以参考如下两个链接查看详情：

- [官方issue](https://github.com/spring-projects/spring-boot/issues/26660)
- [springboot日志系统的设计缺陷](https://www.jianshu.com/p/2c25e283a514)

## 8.自定义日志配置

可以通过在类路径上包含适当的库来激活各种日志记录系统，还可以通过在类路径的根目录中或在由以下 Spring `Environment` 属性 `logging.config` 指定的位置提供合适的配置文件来进一步定制日志系统。

你可以使用 `org.springframework.boot.logging.LoggingSystem` 系统属性来强制 Spring Boot 使用特定的日志记录系统。该值应为 `LoggingSystem`
实现的完全限定类名。你还可以通过使用值 `none` 来完全禁用 Spring Boot 的日志记录配置。

**注意**：

由于日志记录是在创建 `ApplicationContext` 之前初始化的，因此无法从 Spring `@Configuration` 文件中的 `@PropertySources`
控制日志记录。更改日志记录系统或完全禁用日志记录系统的唯一方法是通过系统属性。

根据你的日志记录系统，将加载以下文件：

| 日志系统                    | 日志系统                                                                  |
|-------------------------|-----------------------------------------------------------------------|
| Logback                 | Logback-spring.xml、Logback-spring.groovy、logback.xml 或 logback.groovy |
| Log4j2                  | log4j2-spring.xml 或者 log4j2.xml                                       |
| JDK (Java Util Logging) | logging.properties                                                    |

**注意**：

如果可能，我们建议你在日志配置中使用 `-spring` 变量（例如，`logback-spring.xml` 而不是 `logback.xml`）。如果你使用标准配置的位置，spring 无法完全控制日志初始化。

**警告**：

Java Util Logging 存在已知的类加载问题，在从 “可执行jar” 运行时会导致问题。我们建议在从 “可执行jar” 运行时尽可能避免使用它。

为了帮助定制，还将一些其他属性从 Spring `Environment` 转移到系统属性，如下表所述：

| Spring 环境                         | 系统属性                          | 备注                                |
|-----------------------------------|-------------------------------|-----------------------------------|
| logging.exception-conversion-word | LOG_EXCEPTION_CONVERSION_WORD | 记录异常时使用的转换字                       |
| logging.file.name                 | LOG_FILE                      | 如果已定义，则在默认日志配置中使用                 |
| logging.file.path                 | LOG_PATH                      | 如果已定义，则在默认日志配置中使用                 |
| logging.pattern.console           | CONSOLE_LOG_PATTERN           | 要在控制台上使用的日志模式（标准输出）               |
| logging.pattern.dateformat        | LOG_DATEFORMAT_PATTERN        | 日志日期格式的追加模式                       |
| logging.charset.console           | CONSOLE_LOG_CHARSET           | 用于控制台日志记录的字符集                     |
| logging.pattern.file              | FILE_LOG_PATTERN              | 要在文件中使用的日志模式（如果启用了LOG_FILE）       |
| logging.charset.file              | FILE_LOG_CHARSET              | 用于文件日志记录的字符集（如果启用了LOG_FILE）       |
| logging.pattern.level             | LOG_LEVEL_PATTERN             | 呈现日志级别时使用的格式（默认值为%5p）             |
| PID                               | PID                           | 当前进程ID（如果可能，并且尚未定义为操作系统环境变量时被发现）。 |

如果使用 Logback，还会传输以下属性：

| Spring 环境                                            | 系统属性                                         | 备注                                              |
|------------------------------------------------------|----------------------------------------------|-------------------------------------------------|
| logging.logback.rollingpolicy.file-name-pattern      | LOGBACK_ROLLINGPOLICY_FILE_NAME_PATTERN      | 转存日志文件名的模式(默认为${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz) |
| logging.logback.rollingpolicy.clean-history-on-start | LOGBACK_ROLLINGPOLICY_CLEAN_HISTORY_ON_START | 是否在启动时清除存档日志文件                                  |
| logging.logback.rollingpolicy.max-file-size          | LOGBACK_ROLLINGPOLICY_MAX_FILE_SIZE          | 最大日志文件大小                                        |
| logging.logback.rollingpolicy.total-size-cap         | LOGBACK_ROLLINGPOLICY_TOTAL_SIZE_CAP         | 要保留的日志备份的总大小                                    |
| logging.logback.rollingpolicy.max-history            | LOGBACK_ROLLINGPOLICY_MAX_HISTORY            | 要保留的存档日志文件的最大数量                                 |

所有受支持的日志记录系统在解析其配置文件时都可以参考系统属性。有关示例，请参阅 `spring-boot.jar` 中的默认配置：

- [Logback](https://github.com/spring-projects/spring-boot/tree/v2.7.8/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/logback/defaults.xml)
- [Log4j 2](https://github.com/spring-projects/spring-boot/tree/v2.7.8/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/log4j2/log4j2.xml)
- [Java Util logging](https://github.com/spring-projects/spring-boot/tree/v2.7.8/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/java/logging-file.properties)

**TIP**：

如果要在日志属性中使用占位符，则应该使用 Spring Boot 的语法，而不是底层框架的语法。值得注意的是，如果使用 Logback，则应该使用 `:` 作为属性名及其缺省值之间的分隔符，而不是使用 `:-`。

可以通过仅覆盖 `LOG_LEVEL_PATTERN`（或使用 Logback 的 `logging.pattern.level`）将 MDC
和其他内容添加到日志行。例如，如果使用 `logging.pattern.level=user:%X{user} %5p`，则默认日志格式将包含 “user” 的MDC条目(如果存在)，如下例所示：

```text
2019-08-30 12:30:04.031 user:someone INFO 22174 --- [ nio-8080-exec-0] demo.Controller Handling authenticated request
```

## 9.Logback 扩展

Spring Boot 包括许多 Logback 扩展，可以帮助进行高级配置。你可以在 `logback-spring.xml` 配置文件中使用这些扩展名。

**注意**：

由于标准 `logback.xml` 配置文件加载得太早，因此不能在其中使用扩展名。你需要使用 `logback-spring.xml` 或定义 `logging.config` 属性

**警告**：

扩展不能用于 Logback 的[配置扫描](https://logback.qos.ch/manual/configuration.html#autoScan)。如果尝试这样做，则对配置文件进行更改会导致类似以下错误之一的错误被记录：

```text
ERROR in ch.qos.logback.core.joran.spi.Interpreter@4:71 - no applicable action for
[springProperty], current ElementPath is [[configuration][springProperty]]
ERROR in ch.qos.logback.core.joran.spi.Interpreter@4:71 - no applicable action for
[springProfile], current ElementPath is [[configuration][springProfile]]
```

### 特定于配置文件的配置

`<springProfile>` 标记允许您根据活动的 Spring 配置文件选择性地包括或排除配置部分。`<configuration>` 元素中的任何位置都支持配置文件部分。使用 `name`
属性指定哪个概要文件接受配置。这个 `<springProfile>` 标记可以包含配置文件名称（例如 `staging`
）或配置文件表达式。配置文件表达式允许表达更复杂的配置文件逻辑，例如 `production & (eu-central |
eu-west)`。以下列表显示了三个示例配置文件：

```xml

<springProfile name="staging">
    <!-- configuration to be enabled when the "staging" profile is active -->
</springProfile>

<springProfile name="dev | staging">
<!-- configuration to be enabled when the "dev" or "staging" profiles are active
-->
</springProfile>

<springProfile name="!production">
<!-- configuration to be enabled when the "production" profile is not active -->
</springProfile>
```

### 环境属性

`<springProperty>` 标记允许你从 Spring `Environment` 中暴露属性，以便在 Logback 中使用。如果你想访问 Logback 配置中 `application.properties`
文件中的值，那么这样做很有用。该标记的工作方式与 Logback 的标准 `<property>` 标记类似。但是，可以指定属性的 `source`（从环境中），而不是指定直接 `value`。如果需要将属性存储在 `local`
范围之外的其他位置，可以使用 `scope` 属性。如果需要回退值（如果未在环境中设置该属性），则可以使用 `defaultValue` 属性。以下示例显示如何暴露属性以在 Logback 中使用：

```xml

<springProperty scope="context" name="fluentHost" source="myapp.fluentd.host" defaultValue="localhost"/>
<appender name="FLUENT" class="ch.qos.logback.more.appenders.DataFluentAppender">
<remoteHost>${fluentHost}</remoteHost>
...
</appender>
```

**注意**：

必须以串行大小写指定 `source` （如 `my.property-name`）。 但是，可以使用放宽的规则将属性添加到 `Environment` 中。
