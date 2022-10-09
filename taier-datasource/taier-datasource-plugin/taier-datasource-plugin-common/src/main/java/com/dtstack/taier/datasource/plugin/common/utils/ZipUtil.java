package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:37 2020/8/27
 * @Description：压缩工具
 */
@Slf4j
public class ZipUtil {
    /**
     * 字节数
     */
    private static byte[] byte_simple = new byte[1024];

    /**
     * 压缩文件或路径
     *
     * @param zipLocation    压缩的目的地址
     * @param sourceLocation 压缩的源文件
     */
    public static void zipFile(String zipLocation, String sourceLocation) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(zipLocation))) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream)) {
                if (zipLocation.endsWith(".zip") || zipLocation.endsWith(".ZIP")) {
                    zipOut.setEncoding("GBK");
                    handlerFile(zipLocation, zipOut, sourceLocation, "");
                } else {
                    throw new SourceException(String.format("The target file %s is not a file ending in ZIP", zipLocation));
                }
            }
        } catch (FileNotFoundException e) {
            throw new SourceException(String.format("file not found,%s", e.getMessage()), e);
        } catch (IOException e) {
            throw new SourceException(String.format("Target file compression abnormal:%s", e.getMessage()), e);
        }
    }

    /**
     * 对.zip文件进行解压缩
     *
     * @param zipLocation    解压缩文件地址
     * @param targetLocation 压缩的目标地址，如：D:\\测试 或 /mnt/d/测试
     * @return
     */
    public static List<File> unzipFile(String zipLocation, String targetLocation) {
        List<File> files = new ArrayList<>();
        try {
            // 构建 ZIP 文件并遍历
            ZipFile zipFile = new ZipFile(zipLocation, "GBK");
            for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                // 设置目标地址
                File singleFile = new File(targetLocation + File.separator + entry.getName());
                // 如果压缩文件是文件夹则创建
                if (entry.isDirectory()) {
                    singleFile.mkdirs();
                } else {
                    File parentFile = singleFile.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    try (InputStream inputStream = zipFile.getInputStream(entry);) {
                        try (OutputStream outputStream = new FileOutputStream(singleFile);) {
                            int len = 0;
                            while ((len = inputStream.read(byte_simple)) > 0) {
                                outputStream.write(byte_simple, 0, len);
                            }
                        }
                    }
                    files.add(singleFile);
                }
            }
        } catch (IOException e) {
            throw new SourceException(String.format("Unzip exception : %s", e.getMessage()), e);
        }
        return files;
    }

    /**
     * @param zipLocation    压缩的目的地址
     * @param zipOut         ZIP 输出流
     * @param sourceLocation 被压缩的文件信息
     * @param path           在zip中的相对路径
     * @throws IOException
     */
    private static void handlerFile(String zipLocation, ZipOutputStream zipOut, String sourceLocation, String path) throws IOException {
        log.info("开始压缩文件 : {}", sourceLocation);
        // 补充文件分隔符
        if (!"".equals(path) && !path.endsWith(File.separator)) {
            path += File.separator;
        }
        if (!sourceLocation.equals(zipLocation)) {
            return;
        }

        File sourceFile = new File(sourceLocation);
        if (sourceFile.isDirectory()) {
            // 判断是文件夹需要获取到子文件
            File[] sourceChildFiles = sourceFile.listFiles();
            // 如果子文件为空，则直接压缩空文件
            if (sourceChildFiles.length == 0) {
                zipOut.putNextEntry(new ZipEntry(path + sourceLocation + File.separator));
                zipOut.closeEntry();
            } else {
                // 否则再一个一个压缩
                for (File file : sourceChildFiles) {
                    handlerFile(zipLocation, zipOut, file.getAbsolutePath(), path + sourceFile.getName());
                }
            }
        } else {
            // 压缩单个文件
            try (InputStream inputStream = new FileInputStream(sourceFile);) {
                zipOut.putNextEntry(new ZipEntry(path + sourceFile.getName()));
                int len = 0;
                while ((len = inputStream.read(byte_simple)) > 0) {
                    zipOut.write(byte_simple, 0, len);
                }
            }

            zipOut.closeEntry();
        }
    }
}
