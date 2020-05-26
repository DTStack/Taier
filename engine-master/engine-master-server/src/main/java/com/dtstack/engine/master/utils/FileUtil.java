package com.dtstack.engine.master.utils;

import java.io.*;

/**
 * @author yuebai
 * @date 2020-05-25
 */
public class FileUtil {

    /**
     * 解析文件 每一行带换行符
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static String getContentFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()) {
            StringBuilder content = new StringBuilder();
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error read file content.", e);
            }
            return content.toString();
        }
        throw new FileNotFoundException("File " + filePath + " not exists.");
    }
}
