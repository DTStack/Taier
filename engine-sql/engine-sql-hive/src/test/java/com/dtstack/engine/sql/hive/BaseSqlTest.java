package com.dtstack.engine.sql.hive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.engine.sql.Column;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname SqlTestBase
 * @Description
 * @Date 2020/9/16 14:41
 * @Created chener@dtstack.com
 */
public class BaseSqlTest {

    public Map<String, List<Column>> readColumnMapFromResource(String resourceName){
        String json = readStringFromResource(resourceName);
        TypeReference typeReference = new TypeReference<Map<String,List<Column>>>(){};
        return (Map<String, List<Column>>) JSON.parseObject(json, typeReference);
    }

    public Map<String, List<Column>> readColumnMapFromFile(String path){
        String json = getStringFromFile(path);
        TypeReference typeReference = new TypeReference<Map<String,List<Column>>>(){};
        return (Map<String, List<Column>>) JSON.parseObject(json, typeReference);
    }

    public String readStringFromResource(String resourceName){
        String sqlFile = getClass().getClassLoader().getResource(resourceName).getFile();
        return getStringFromFile(sqlFile);
    }

    public String getStringFromFile(String path){
        String sql = readFile(path);
        return sql;
    }

    public static String readFile(String path){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
            reader = new BufferedReader(inputStreamReader);
            String tmpStr = null;
            while ((tmpStr = reader.readLine()) != null){
                builder.append(tmpStr);
                builder.append("\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
