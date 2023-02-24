package com.olinonee.springboot.core.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * jackson 序列化和反序列
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
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
