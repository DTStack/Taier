package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 文件操作工具类
 *
 * @author luming
 * @date 2022/3/7
 */
@Slf4j
public class FileUtil {
    /**
     * 校验本地文件是否存在,存在则返回文件
     *
     * @param localPath
     * @return
     */
    public static File checkFileExists(String localPath) {
        File file = new File(localPath);
        if (!file.isFile() || !file.exists()) {
            log.error(" 文件 {} 不存在", localPath);
            throw new SourceException(String.format("文件 %s 不存在", localPath));
        }
        return file;
    }
}
