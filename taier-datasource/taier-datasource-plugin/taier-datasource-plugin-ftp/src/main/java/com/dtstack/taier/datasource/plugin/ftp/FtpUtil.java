package com.dtstack.taier.datasource.plugin.ftp;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * ftp 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午4:11 2021/6/21
 * company: www.dtstack.com
 */
@Slf4j
public class FtpUtil {

    private static final Integer MAX_DFS = 1000;

    /**
     * FTP 默认端口
     */
    private static final Integer FTP_PORT_DEFAULT = 21;

    /**
     * SFTP 默认端口
     */
    private static final Integer SFTP_PORT_DEFAULT = 22;

    /**
     * 获取 ftp/sftp 端口
     *
     * @param protocol 协议类型
     * @param portStr  String 类型端口
     * @return ftp/sftp 端口
     */
    public static Integer getFtpPort(String protocol, String portStr) {
        if (StringUtils.equalsIgnoreCase(ProtocolEnum.SFTP.name(), protocol)) {
            return StringUtils.isNotBlank(portStr) ? Integer.valueOf(portStr) : SFTP_PORT_DEFAULT;
        } else {
            return StringUtils.isNotBlank(portStr) ? Integer.valueOf(portStr) : FTP_PORT_DEFAULT;
        }
    }

    /**
     * 获取 SFTP 上的文件集合
     *
     * @param handler    SFTP Client
     * @param path       地址
     * @param includeDir 是否包含文件夹
     * @param maxNum     最大条数
     * @param regexStr   正则匹配
     * @return 文件名集合
     */
    public static List<String> getSFTPFileNames(SFTPHandler handler, String path, Boolean includeDir, Boolean recursive, Integer maxNum, String regexStr) {
        List<String> fileNames = Lists.newArrayList();
        if (handler == null) {
            return fileNames;
        }
        // SFTP 文件夹队列
        LinkedList<String> dirQueue = Lists.newLinkedList();
        // 添加队列头信息
        dirQueue.add(path);
        try {
            int currentDfs = 0;
            while (!dirQueue.isEmpty()) {
                // 取出队列中的第一个元素
                String dirPath = dirQueue.removeFirst();
                Vector vector = handler.listFile(dirPath);
                for (Object single : vector) {
                    if (++currentDfs >= MAX_DFS) {
                        log.warn("Search more than {} files, stop looking for files", MAX_DFS);
                        if (fileNames.isEmpty()) {
                            throw new SourceException(String.format("The first %1$s files in the current folder do not match, please modify the matching rules", MAX_DFS));
                        }
                        return fileNames;
                    }
                    ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) single;
                    if (lsEntry.getAttrs().isDir() && !(lsEntry.getFilename().equals(".") || lsEntry.getFilename().equals(".."))) {
                        if (includeDir) {
                            if (fileNames.size() == maxNum) {
                                // 清空队列，退出循环
                                dirQueue.clear();
                                break;
                            }
                            listAddByRegex(fileNames, regexStr, lsEntry.getFilename(), dirPath);
                        }
                        if (recursive) {
                            // 如果循环则将文件路径添加到队列中
                            dirQueue.add(dirPath + "/" + lsEntry.getFilename());
                        }
                    } else if (!lsEntry.getAttrs().isDir()) {
                        if (fileNames.size() == maxNum) {
                            // 清空队列，退出循环
                            dirQueue.clear();
                            break;
                        }
                        listAddByRegex(fileNames, regexStr, lsEntry.getFilename(), dirPath);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileNames;
    }

    /**
     * 获取 FTP 上的文件集合
     *
     * @param ftpClient  FTP client
     * @param path       地址
     * @param includeDir 是否包含文件夹
     * @param maxNum     最大条数
     * @param regexStr   正则匹配
     * @return 文件名集合
     */
    public static List<String> getFTPFileNames(FTPClient ftpClient, String path, Boolean includeDir, Boolean recursive, Integer maxNum, String regexStr) {
        List<String> fileNames = Lists.newArrayList();
        if (ftpClient == null) {
            return fileNames;
        }
        // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码
        try {
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
                ftpClient.setControlEncoding("UTF-8");
            }
        } catch (IOException e) {
            log.error("set unicode error :{}", e.getMessage());
        }
        // SFTP 文件夹队列
        LinkedList<String> dirQueue = Lists.newLinkedList();
        // 添加队列头信息
        dirQueue.add(path);
        try {
            int currentDfs = 0;
            while (!dirQueue.isEmpty()) {
                // 取出队列中的第一个元素
                String dirPath = dirQueue.removeFirst();
                FTPFile[] ftpFiles = ftpClient.listFiles(dirPath);
                for (FTPFile file : ftpFiles) {
                    if (++currentDfs >= MAX_DFS) {
                        log.warn("Search more than {} files, stop looking for files", MAX_DFS);
                        if (fileNames.isEmpty()) {
                            throw new SourceException(String.format("The first %1$s files in the current folder do not match, please modify the matching rules", MAX_DFS));
                        }
                        return fileNames;
                    }
                    if (file.isDirectory() && !(file.getName().equals(".") || file.getName().equals(".."))) {
                        if (includeDir) {
                            if (fileNames.size() == maxNum) {
                                // 清空队列，退出循环
                                dirQueue.clear();
                                break;
                            }
                            listAddByRegex(fileNames, regexStr, file.getName(), dirPath);
                        }
                        if (recursive) {
                            // 如果循环则将文件路径添加到队列中
                            dirQueue.add(dirPath + "/" + file.getName());
                        }
                    } else if (!file.isDirectory()) {
                        if (fileNames.size() == maxNum) {
                            // 清空队列，退出循环
                            dirQueue.clear();
                            break;
                        }
                        listAddByRegex(fileNames, regexStr, file.getName(), dirPath);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileNames;
    }

    /**
     * 根据传入正则判断是否匹配，匹配则放入 list 中
     *
     * @param fileNames  文件名集合
     * @param regexStr   正则表达式
     * @param fileName   文件名
     * @param namePrefix 文件前缀（路径）
     */
    private static void listAddByRegex(List<String> fileNames, String regexStr, String fileName, String namePrefix) {
        if (StringUtils.isBlank(regexStr) || Pattern.compile(regexStr).matcher(fileName).matches()) {
            fileNames.add((namePrefix + "/" + fileName).replaceAll("//*", "/"));
        }
    }
}
