# JSON

Spring Boot 提供了与三个 JSON 映射库的集成：

- Gson
- Jackson
- JSON-B

Jackson 是首选和默认库。

## 1.Jackson

提供了 Jackson 的自动配置，Jackson 是 `spring-boot-start-json` 的一部分。当 Jackson 位于类路径上时，将自动配置一个 `ObjectMapper`
bean。为定制 `ObjectMapper`
的配置提供了几个配置属性。

### 自定义序列化器和反序列化器

如果使用 Jackson 序列化和反序列化 JSON 数据，可能需要编写自己的 `JsonSerializer` 和 `JsonDeserializer`
类。自定义序列化器通常[通过模块注册到 Jackson](https://github.com/FasterXML/jackson-docs/wiki/JacksonHowToCustomSerializers)，但 Spring Boot
提供了另一种 `@JsonComponent` 注解，可以更容易地直接注册 Spring bean。

可以直接在 `JsonSerializer`、`JsonDeserializer` 或 `KeyDeserializer` 实现上使用 `@JsonComponent`
注解。你还可以在包含序列化程序/反序列化程序作为内部类的类上使用它，如下例所示：

**MyJsonComponent.java**：

```java
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class MyJsonComponent {

    /**
     * 序列化
     */
    public static class Serializer extends JsonSerializer<MyObject> {

        @Override
        public void serialize(MyObject myObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", myObject.getName());
            jsonGenerator.writeNumberField("age", myObject.getAge());
            jsonGenerator.writeEndObject();
        }
    }

    /**
     * 反序列化
     */
    public static class Deserializer extends JsonDeserializer<MyObject> {

        @Override
        public MyObject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            final ObjectCodec codec = jsonParser.getCodec();
            final JsonNode treeNode = codec.readTree(jsonParser);
            final String name = treeNode.get("name").textValue();
            final int age = treeNode.get("age").intValue();
            return new MyObject(name, age);
        }
    }
}
```

**MyObject.java**：

```java
public class MyObject {
    private String name;
    private int age;

    public MyObject(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // getter, setter
}
```

`ApplicationContext` 中的所有 `@JsonComponent` bean 都会自动向 Jackson 注册。因为 `@JsonComponent` 是用 `@Component` 元注解的，所以通常的组件扫描规则适用。

Spring Boot
还提供了 [`JsonObjectSerializer`](https://docs.spring.io/spring-boot/docs/2.7.8/api/org/springframework/boot/jackson/JsonObjectSerializer.html)
和 [`JsonObjectDeserializer`](https://docs.spring.io/spring-boot/docs/2.7.8/api/org/springframework/boot/jackson/JsonObjectDeserializer.html)
基类，这些基类在序列化对象时为标准 Jackson 版本提供了有用的替代方案。有关详细信息，请参阅 Javadoc
中的 [`JsonObjectSerializer`](https://docs.spring.io/spring-boot/docs/2.7.8/api/org/springframework/boot/jackson/JsonObjectSerializer.html)
和 [`JsonObjectDeserializer`](https://docs.spring.io/spring-boot/docs/2.7.8/api/org/springframework/boot/jackson/JsonObjectDeserializer.html)
。

上面的例子可以使用 `JsonObjectSerializer`/`JsonObjectDeserializer` 重写，如下所示：

```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.io.IOException;

@JsonComponent
public class MyJSONObjectComponent {

    /**
     * 序列化
     */
    public static class Serializer extends JsonObjectSerializer<MyObject> {

        @Override
        protected void serializeObject(MyObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStringField("name", value.getName());
            jgen.writeNumberField("age", value.getAge());
        }
    }

    /**
     * 反序列化
     */
    public static class Deserializer extends JsonObjectDeserializer<MyObject> {

        @Override
        protected MyObject deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException {
            final String name = nullSafeValue(tree.get("name"), String.class);
            final int age = nullSafeValue(tree.get("age"), Integer.class);
            return new MyObject(name, age);
        }
    }
}
```

### 混合

Jackson 支持 mixin，它可以用来将额外的注解混合到目标类中已经声明的注解中。Spring Boot 的 Jackson 自动配置将扫描应用程序包中带有 `@JsonMixin`
注解的类，并将它们注册到自动配置的 `ObjectMapper` 中。注册是由 Spring Boot 的 `JsonMixinModule` 执行的。

### 常用配置和常用注解

#### 常用配置

```properties
# 日期格式字符串或标准日期格式类全限定名，只控制 java.util.Date 的序列化 format
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
# 指定 Joda date/time 的格式，比如 yyyy-MM-ddHH:mm:ss. 如果没有配置的话，dateformat 会作为 backup。
spring.jackson.joda-date-time-format=yyyy-MM-dd HH:mm:ss
# 全局设置 pojo 或被 @JsonInclude 注解的属性的序列化方式
spring.jackson.default-property-inclusion=NON_NULL
# 不为空的属性才会序列化,具体属性可看 JsonInclude.Include
# 是否开启 Jackson 的序列化
# 示例：spring.jackson.serialization.indent-output= true
spring.jackson.serialization.*=
# 是否开启 Jackson 的反序列化
spring.jackson.deserialization.*=
# 是否开启 json 的 generators
# 示例：spring.jackson.generator.auto-close-json-content=true
spring.jackson.generator.*=
# 指定 json 使用的 Locale
spring.jackson.locale=zh
# 是否开启Jackson通用的特性
spring.jackson.mapper.*=
# 是否开启 jackson 的 parser 特性
spring.jackson.parser.*=
# 指定 Json 策略模式
spring.jackson.property-naming-strategy=com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy
# 或
# spring.jackson.property-naming-strategy=UPPER_CAMEL_CASE
# 指定日期格式化时区，比如 Asia/Shanghai 或者 GMT+8
spring.jackson.time-zone=GMT+8
```

#### 常用注解

- `@JsonPropertyOrder(value={“value1”,“value2”,“value3”})`：将实体对应转换后默认 json 顺序，根据注解要求进行变换
- `@JsonIgnore`：将某字段排除在序列化和反序列化之外
- `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")`：按照指定日期格式进行转换
- `@JsonProperty("邮箱")`：给对应字段起别名
- `@JsonInclude(JsonInclude.Include.NON_NULL)`：如果字段为空则不做序列化和反序列化

#### 扩展

更多关于 Jackson 的使用，可以参考如下链接：

- [jackson-docs](https://github.com/FasterXML/jackson-docs)
- [Baeldung Jackson JSON Tutorial](https://www.baeldung.com/jackson)

## 2.Gson

提供了 `GSON` 的自动配置。当 gson 在类路径上时，会自动配置一个 gson Bean。提供了几个 `spring.gson.*`
配置属性用于定制配置。要获得更多控制，可以使用一个或多个 `GsonBuilderCustomizer`
Bean。

## 3.JSON-B

提供了 JSON-B 的自动配置。当 JSON-B API 和实现在类路径上时，将自动配置 `Jsonb` bean。首选的 JSON-B 实现是 Apache Johnzon，它提供了依赖管理。
