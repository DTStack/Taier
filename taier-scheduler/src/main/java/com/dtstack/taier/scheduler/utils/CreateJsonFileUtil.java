package com.dtstack.taier.scheduler.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @Author: 安陌
 * @CreateTime: 2022-10-11  20:46
 * @Description: 挤挤
 * @Version: 1.0
 */
public class CreateJsonFileUtil {

    /**
     * 生成.json格式文件
     */
    public static String createJsonFile(String jsonString, String filePath, String fileName) {

        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + ".json";

        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

//            // 格式化json字符串
//            jsonString = JsonFormatTool.formatJson(jsonString);

            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            fullPath = "";
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return fullPath;
    }

}
