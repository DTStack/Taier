package com.dtstack.rdos.engine.execution.odps.test;


import com.aliyun.odps.task.SQLTask;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class TestSql {

    //Map<String,Object> configMap;


    private static void loadConfig() {
        ObjectMapper objectMapper = new ObjectMapper();

    }

    public static void runSql() {

    }

    public static void runMR() {

    }

    public static void getOdps() {

    }


    public static void main(String[] args) throws Exception {
        //SQLTask sqlTask;

        FileInputStream fis = new FileInputStream("base/odps/hallo.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = br.readLine();
        System.out.println(line);
    }

}
