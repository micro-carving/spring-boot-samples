# SpringApplication

`SpringApplication` 类提供了一种方便的方法来引导从 `main()` 方法启动的 Spring 应用程序。在很多情况下，你可以委托给静态的 `SpringApplication.run` 方法，如下例所示:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

程序启动可能会看到类似如下的输出信息：

```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.8)

2023-02-17 12:51:57.711  INFO 30352 --- [           main] c.o.s.quickstart.HelloWorldApplication   : Starting HelloWorldApplication using Java 11.0.9 on DESKTOP-2ILNKER with PID 30352 (E:\IdeaWorkspace\todo-projects\spring-boot-samples\springboot-sample-quickstart\target\classes started by 16602 in E:\IdeaWorkspace\todo-projects\spring-boot-samples)
2023-02-17 12:51:57.713  INFO 30352 --- [           main] c.o.s.quickstart.HelloWorldApplication   : No active profile set, falling back to 1 default profile: "default"
2023-02-17 12:51:58.145  INFO 30352 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8089 (http)
2023-02-17 12:51:58.146  INFO 30352 --- [           main] o.a.catalina.core.AprLifecycleListener   : Loaded Apache Tomcat Native library [1.2.33] using APR version [1.7.0].
2023-02-17 12:51:58.146  INFO 30352 --- [           main] o.a.catalina.core.AprLifecycleListener   : APR capabilities: IPv6 [true], sendfile [true], accept filters [false], random [true], UDS [true].
2023-02-17 12:51:58.146  INFO 30352 --- [           main] o.a.catalina.core.AprLifecycleListener   : APR/OpenSSL configuration: useAprConnector [false], useOpenSSL [true]
2023-02-17 12:51:58.148  INFO 30352 --- [           main] o.a.catalina.core.AprLifecycleListener   : OpenSSL successfully initialized [OpenSSL 1.1.1o  3 May 2022]
2023-02-17 12:51:58.153  INFO 30352 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-02-17 12:51:58.153  INFO 30352 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.71]
2023-02-17 12:51:58.205  INFO 30352 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-02-17 12:51:58.205  INFO 30352 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 469 ms
2023-02-17 12:51:58.396  INFO 30352 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-02-17 12:51:58.403  INFO 30352 --- [           main] c.o.s.quickstart.HelloWorldApplication   : Started HelloWorldApplication in 0.946 seconds (JVM running for 1.698)
```

默认情况下，将显示 `INFO` 日志消息，包括一些相关的启动细节，例如启动应用程序的用户。如果需要的日志级别不是 `INFO`，可以设置为[其他日志级别]()
。应用程序版本是使用主应用程序类包中的实现版本确定的。启动信息日志记录可以通过设置关闭 `spring.main.log-startup-info` 设为 `false`。这也将关闭应用程序活动配置文件的日志记录。

**application.yml**：

```yaml
# 服务器默认端口 8080
server:
  port: 8070

spring:
  main:
    # 是否开启启动日志（默认为 true），true：开启；false：关闭
    log-startup-info: false
```

关闭启动日志之后，日志少了 `SpringBoot` 启动相关的日志，控制台输出如下：

```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.8)

2023-02-17 12:59:46.547  INFO 27108 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8070 (http)
2023-02-17 12:59:46.548  INFO 27108 --- [           main] o.a.catalina.core.AprLifecycleListener   : Loaded Apache Tomcat Native library [1.2.33] using APR version [1.7.0].
2023-02-17 12:59:46.548  INFO 27108 --- [           main] o.a.catalina.core.AprLifecycleListener   : APR capabilities: IPv6 [true], sendfile [true], accept filters [false], random [true], UDS [true].
2023-02-17 12:59:46.549  INFO 27108 --- [           main] o.a.catalina.core.AprLifecycleListener   : APR/OpenSSL configuration: useAprConnector [false], useOpenSSL [true]
2023-02-17 12:59:46.550  INFO 27108 --- [           main] o.a.catalina.core.AprLifecycleListener   : OpenSSL successfully initialized [OpenSSL 1.1.1o  3 May 2022]
2023-02-17 12:59:46.556  INFO 27108 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-02-17 12:59:46.556  INFO 27108 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.71]
2023-02-17 12:59:46.608  INFO 27108 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-02-17 12:59:46.608  INFO 27108 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 501 ms
2023-02-17 12:59:46.803  INFO 27108 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8070 (http) with context path ''
```

**TIP**：

要在启动期间添加额外的日志记录，可以在 `SpringApplication` 的子类中重写 `logStartupInfo(boolean)`。

## 1.启动失败

如果应用程序无法启动，注册的 `FailureAnalyzers` 将有机会提供专用错误消息和解决问题的具体操作。例如，如果在端口 8080 上启动一个 web 应用程序，并且该端口已经在使用中，那么应该会看到类似以下消息的内容:

```text
***************************
APPLICATION FAILED TO START
***************************

Description:

Embedded servlet container failed to start. Port 8080 was already in use.

Action:

Identify and stop the process that is listening on port 8080 or configure this application to listen on another port.
```

**注意**：

`SpringBoot` 提供了许多 `FailureAnalyzer` 实现，也可以添加自己的实现。

如果没有故障分析器能够处理异常，仍然可以显示完整的条件报告，以更好地了解哪里出了问题。
为此，需要启用 `org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener` 的调试属性或启用调试日志记录。
例如，如果您正在使用 `java -jar` 运行应用程序，则可以按如下方式启用调试属性：

```shell
java -jar myproject-0.0.1-SNAPSHOT.jar --debug
```

## 2.延迟初始化

`SpringApplication` 允许应用程序延迟初始化。如果启用了延迟初始化，则会根据需要而不是在应用程序启动期间创建 bean。因此，启用延迟初始化可以减少应用程序启动所需的时间。在 web 应用程序中，启用延迟初始化将导致许多
web 相关 bean 在收到 HTTP 请求之前无法初始化。

延迟初始化的一个缺点是它会延迟应用程序问题的发现。如果延迟初始化错误配置的 Bean，则在启动过程中不会再出现故障，只有在初始化 Bean 时，问题才会变得明显。还必须注意确保 JVM 有足够的内存来容纳应用程序的所有
Bean，而不仅仅是那些在启动期间初始化的 Bean。由于这些原因，默认情况下不启用延迟初始化，建议在启用延迟初始化之前对 JVM 的堆大小进行微调。

可以使用 `SpringApplicationBuilder` 上的 `lazyInitialization` 方法或 `SpringApplication` 上的 `setLazyInitialize`
方法以编程方式启用延迟初始化。或者，可以使用 `spring.main.lazy-initialization` 属性启用它，如下例所示：

```yaml
spring:
  main:
    # 是否延迟初始化，默认为 false 
    lazy-initialization: true
```

**TIP**：

如果希望禁用某些 Bean 的延迟初始化，同时对应用程序的其余部分使用延迟初始化，则可以使用 `@Lazy(false)` 注释将它们的延迟属性显式设置为 false。

## 3.自定义 Banner

在启动时打印的 banner 可以通过在类路径中添加 `banner.txt` 文件或将 `spring.banner.location` 属性设置为这样一个文件的位置来更改。如果文件的编码不是
UTF-8，你可以设置 `spring.banner.charset`。除了文本文件，还可以在类路径中添加 `banner.gif`、`banner.jpg` 或 `banner.png`
图像文件，或者设置 `spring.banner.image.location` 属性。图像转换为 ASCII 艺术表示和打印在任何文本的 banner。

在 `banner.txt` 文件中，可以使用环境中可用的任何密钥以及以下占位符：

**Banner 变量**

| 变量                                                                            | 描述                                                                 |
|-------------------------------------------------------------------------------|--------------------------------------------------------------------|
| ${application.version}                                                        | 应用程序的版本号，如在 `MANIFEST.MF` 中声明。例如，`实现版本：1.0` 打印为 `1.0`。             |
| ${application.formatted-version}                                              | 应用程序的版本号，如在 `MANIFEST.MF` 中声明的，并格式化为显示（用括号括起来，前缀为 `v`）。例如（`v1.0`）。 |
| ${spring-boot.version}                                                        | 正在使用的 Spring Boot 版本。例如 `2.7.8`。                                   |
| ${spring-boot.formatted-version}                                              | 正在使用的Spring Boot版本，格式为显示（用括号括起来，前缀为 `v`）。例如（`v2.7.8`）。             |
| ${Ansi.NAME} (或者 ${AnsiColor.NAME},${AnsiBackground.NAME}, ${AnsiStyle.NAME}) | `NAME` 是 ANSI 转义代码的名称。有关详细信息，请参见 `AnsiPropertySource`。             |
| ${application.title}                                                          | 这个应用程序的标题被声明在 `MANIFEST.MF` 中。例如，`实现版本-Title: MyApp` 打印为 `MyApp`   |

**TIP**：

如果你想通过编程方式生成一个 banner，可以使用 `SpringApplication.setBanner(…)`方法。使用 `org.springframework.boot.Banner`
接口并实现自己的 `printBanner()` 方法。

还可以使用 `spring.main.banner-mode` 属性来确定 banner 是否必须在 `System.out(console)` 上打印、发送到配置的日志器(`log`) 或根本不生成(`off`)。

**application.yml**：

```yaml
spring:
  main:
    # banner 模式：console（打印到控制台）、log（打印到日志文件）、off（关闭）
    banner-mode: off
```

打印的 banner 被注册为一个单例 bean，名称如下: `springBootBanner`。

**注意**：

只有在使用 SpringBoot 启动程序时，`${application.version}` 和 `${appliation.formatted-version}` 属性才可用。如果正在运行一个未打包的 jar
并使用 `java-cp <classpath> <mainclass>` 启动它，则不会解析这些值。

这就是为什么我们建议始终使用 `java org.springframework.boot.loader.JarLauncher` 启动未打包的 jar
包。这将在构建类路径和启动应用程序之前初始化 `application.*banner` 变量。

配置的 banner.txt 内容如下：

```text
                _                    _                 _                                    _
               (_)                  | |               | |                                  | |
 ___ _ __  _ __ _ _ __   __ _ ______| |__   ___   ___ | |_ ______ ___  __ _ _ __ ___  _ __ | | ___  ___
/ __| '_ \| '__| | '_ \ / _` |______| '_ \ / _ \ / _ \| __|______/ __|/ _` | '_ ` _ \| '_ \| |/ _ \/ __|
\__ \ |_) | |  | | | | | (_| |      | |_) | (_) | (_) | |_       \__ \ (_| | | | | | | |_) | |  __/\__ \
|___/ .__/|_|  |_|_| |_|\__, |      |_.__/ \___/ \___/ \__|      |___/\__,_|_| |_| |_| .__/|_|\___||___/
    | |                  __/ |                                                       | |
    |_|                 |___/                                                        |_|
${AnsiColor.GREEN}
  :: Application Title   ::    ${application.title}
  :: Application Version ::    ${application.formatted-version}
  :: Spring Boot Version ::    ${spring-boot.formatted-version}
  :: App Server  Port    ::    ${server.port}
${AnsiColor.BLACK}

```

启动效果如下图所示：

![idea控制台输出的banner信息](../../assets/20230217-idea控制台输出的banner信息.png)

**扩展**：

banner 艺术字体生成网址链接如下：

> - [ASCII Generator](http://www.network-science.de/ascii/) </br>
> - [Ascii Text / Signature Generator](http://www.kammerl.de/ascii/AsciiSignature.php) </br>
> - [Text to ASCII Art Generator (TAAG)](http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20) </br>
> - [Spring Boot banner在线生成工具，制作下载banner.txt，修改替换banner.txt文字实现自定义，个性化启动banner](https://www.bootschool.net/ascii) </br>

