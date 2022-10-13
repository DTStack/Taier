package com.dtstack.taier.datasource.plugin.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:42 2020/8/27
 * @Description：Xml 转 JSON 工具
 */
public class Xml2JsonUtil {
    private final static String PROPERTY = "property";

    /**
     * xml转map
     */
    public static Map<String, String> xml2map(File xmlFile) {
        Map<String, String> map = new HashMap();
        // 转化为 JSON 再处理
        JSONObject json = xml2Json(xmlFile);
        if (json.containsKey(PROPERTY)) {
            Object propertyObject = json.get(PROPERTY);
            JSONArray jsonArray;
            if (propertyObject instanceof JSONObject) {
                jsonArray = new JSONArray();
                jsonArray.add(propertyObject);
            } else {
                jsonArray = (JSONArray) propertyObject;
            }
            jsonArray.forEach(o -> {
                JSONObject single = (JSONObject) o;
                map.put(single.getString("name"), single.getString("value"));
            });
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * 读取 xml 文件为 JSONObject
     *
     * @param xmlFile
     * @return
     * @throws Exception
     */
    public static JSONObject xml2Json(File xmlFile) {
        String xmlStr = readFile(xmlFile);
        try {
            Document doc = DocumentHelper.parseText(xmlStr);
            return dom4j2Json(doc.getRootElement());
        } catch (DocumentException e) {
            throw new SourceException(String.format("XML parse exception,%s", e.getMessage()), e);
        }

    }

    /**
     * 读取文件字节流
     *
     * @param file
     * @return
     */
    private static String readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);) {
            try (FileChannel fc = fis.getChannel()) {
                ByteBuffer bb = ByteBuffer.allocate((int) file.length());
                fc.read(bb);
                // 翻转子节流
                bb.flip();
                return new String(bb.array(), "UTF8");
            }
        } catch (FileNotFoundException e) {
            throw new SourceException(String.format("file is not exist,%s", e.getMessage()), e);
        } catch (IOException e) {
            throw new SourceException(String.format("File reading exception,%s", e.getMessage()), e);
        }
    }

    /**
     * xml 转 json
     *
     * @param element
     */
    private static JSONObject dom4j2Json(Element element) {
        JSONObject json = new JSONObject();
        // 如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!isEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> childElement = element.elements();
        // 如果没有子元素,只有一个值
        if (childElement.isEmpty() && !isEmpty(element.getText())) {
            json.put(element.getName(), element.getText());
        }

        // 有子元素
        for (Element singleChildElement : childElement) {
            // 子元素也有子元素
            if (!singleChildElement.elements().isEmpty()) {
                JSONObject childJson = dom4j2Json(singleChildElement);
                if (json.containsKey(singleChildElement.getName())) {
                    Object singleChildObject = json.get(singleChildElement.getName());
                    // 如果此元素已存在,则转为jsonArray
                    if (singleChildObject instanceof JSONObject) {
                        json.remove(singleChildElement.getName());
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.add(singleChildObject);
                        jsonArray.add(childJson);
                        json.put(singleChildElement.getName(), jsonArray);
                    }
                    if (singleChildObject instanceof JSONArray) {
                        ((JSONArray) singleChildObject).add(childJson);
                    }
                } else {
                    if (!childJson.isEmpty()) {
                        json.put(singleChildElement.getName(), childJson);
                    }
                }
                //子元素没有子元素
            } else {
                for (Object attribute : singleChildElement.attributes()) {
                    Attribute attr = (Attribute) attribute;
                    if (!isEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!singleChildElement.getText().isEmpty()) {
                    json.put(singleChildElement.getName(), singleChildElement.getText());
                }
            }
        }
        return json;
    }

    /**
     * 字段是否为空，且字段不能为 null
     *
     * @param str
     * @return
     */
    private static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }
}
