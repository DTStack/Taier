package com.dtstack.batch.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public class FileUtils {
    public static String FILE_UPLOADS = "file-uploads";
    public static String uploadsDir = System.getProperty("user.dir") + File.separator + FILE_UPLOADS;

    public static void upload(List<MultipartFile> files, String targetPath) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        if (StringUtils.isEmpty(targetPath)) {
            throw new RdosDefineException("upload target filePath cannot be empty");
        }

        for (MultipartFile file : files) {
            String fileOriginalName = file.getOriginalFilename();
            String targetFilePath = targetPath + File.separator + fileOriginalName;
            File saveFile = new File(targetFilePath);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                file.transferTo(saveFile);
            } catch (Exception e) {
                throw new RdosDefineException("An error occurred while storing the file");
            }
        }
    }
}
