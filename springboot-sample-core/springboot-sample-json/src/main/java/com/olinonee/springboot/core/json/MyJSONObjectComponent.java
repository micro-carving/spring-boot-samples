package com.olinonee.springboot.core.json;

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

/**
 * MyJSONObjectComponent
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
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
