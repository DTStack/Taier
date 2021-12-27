//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dtstack.batch.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dt.insight.plat.lang.exception.sdk.SDKException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JSONs {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JSONs() {
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectNode object(String text) {
        Objects.requireNonNull(text);

        try {
            JsonNode node = objectMapper.readTree(text);
            if (node instanceof ObjectNode) {
                return (ObjectNode)node;
            } else {
                throw new SDKException("{} 非JSON Object", new Object[]{text});
            }
        } catch (IOException var2) {
            throw new SDKException(var2, "{} JSON解析异常", new Object[]{text});
        }
    }

    public static ArrayNode array(String text) {
        Objects.requireNonNull(text);

        try {
            JsonNode node = objectMapper.readTree(text);
            if (node instanceof ObjectNode) {
                return (ArrayNode)node;
            } else {
                throw new SDKException(Strings.format("{} 非JSON Array", new Object[]{text}), new Object[0]);
            }
        } catch (IOException var2) {
            throw new SDKException(Strings.format("{} JSON解析异常", new Object[]{text}), new Object[0]);
        }
    }

    public static String string(Object object) {
        if (Objects.isNull(object)) {
            return null;
        } else if (Strings.isCharSequence(object)) {
            return (String)object;
        } else {
            try {
                return getObjectMapper().writeValueAsString(object);
            } catch (JsonProcessingException var2) {
                throw new SDKException(var2, "JSON string Fail.", new Object[0]);
            }
        }
    }

    public static <T> T transform(Object object, Class<T> clazz) {
        if (!Objects.isNull(object) && !Objects.isNull(clazz)) {
            try {
                String stringText = object instanceof String ? String.valueOf(object) : string(object);
                return objectMapper.readValue(stringText, clazz);
            } catch (IOException var3) {
                throw new SDKException(var3, "JSON解析异常,source:{},target class:{}", new Object[]{string(object), clazz.getCanonicalName()});
            }
        } else {
            return null;
        }
    }

    public static <T> T to(String text, Class<T> clazz) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(clazz);

        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException var3) {
            throw new SDKException(var3, "JSON ({}) 反序列化为 @{}异常", new Object[]{text, clazz.getName()});
        }
    }

    public static <T> List<T> toList(String text, Class<T> clazz) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(clazz);

        try {
            return (List)objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception var3) {
            throw new SDKException(Strings.format("JSON ({}) 反序列化为 @List{}异常", new Object[]{text, clazz}), new Object[]{var3});
        }
    }

    public static <T> T to(String text, TypeReference<T> reference) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(reference);

        try {
            return objectMapper.readValue(text, reference);
        } catch (Exception var3) {
            throw new SDKException(Strings.format("JSON ({}) 反序列化为 @{}异常", new Object[]{text, reference.getType()}), new Object[]{var3});
        }
    }

    static {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(Include.ALWAYS);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(simpleModule);
    }
}
