package com.dtstack.taier.common.util;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * file util
 *
 * @author ：wangchuan
 * date：Created in 17:06 2022/10/8
 * company: www.dtstack.com
 */
public class FileUtil {

    public static void mkdirsIfNotExist(String directoryPath) {
        if (StringUtils.isEmpty(directoryPath)) {
            throw new DtCenterDefException(ErrorCode.INVALID_PARAMETERS);
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            try {
                FileUtils.forceMkdir(directory);
            } catch (IOException e) {
                throw new DtCenterDefException(e.getMessage());
            }
        }
    }
}
