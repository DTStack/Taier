package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.plugin.common.sftp.SFTPHandler;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

/**
 * sftp 工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:23 2022/3/4
 * company: www.dtstack.com
 */
@Slf4j
public class SftpUtil {

    private static final String LOCK_SUFFIX = ".lock";

    private static final String SEPARATE = File.separator;

    /**
     * 下载 confDir 到本地，返回本地文件夹路径
     *
     * @param sourceDTO 数据源配置信息
     * @param sftpPath  sftp 路径
     * @param localDir  本地文件夹
     * @return 本地文件夹路径
     */
    public static String downloadSftpDirFromSftp(ISourceDTO sourceDTO, String sftpPath, String localDir, Timestamp fileTimestamp) {
        AbstractSourceDTO abstractSourceDTO = (AbstractSourceDTO) sourceDTO;
        return downloadDirFromSftp(sftpPath, localDir, abstractSourceDTO.getSftpConf(), fileTimestamp);
    }

    /**
     * 下载 confDir 到本地，返回本地文件夹路径
     *
     * @param sourceDTO 数据源配置信息
     * @param sftpPath  sftp 路径
     * @param localDir  本地文件夹
     * @return 本地文件夹路径
     */
    public static String downloadSftpDirFromSftp(ISourceDTO sourceDTO, String sftpPath, String localDir) {
        AbstractSourceDTO abstractSourceDTO = (AbstractSourceDTO) sourceDTO;
        return downloadDirFromSftp(sftpPath, localDir, abstractSourceDTO.getSftpConf(), null);
    }

    /**
     * 下载 kerberos 配置到本地，返回本地文件夹路径
     *
     * @param sftpPath      sftp 路径
     * @param sftpMap       sftp 配置
     * @param fileTimestamp 认证文件时间戳
     * @return 本地文件夹路径
     */
    public static String downloadKerberosDirFromSftp(String sftpPath, Map<String, String> sftpMap, Timestamp fileTimestamp) {
        String kerberosConfDir = PathUtils.getKerberosConfDir();
        return downloadDirFromSftp(sftpPath, kerberosConfDir, sftpMap, fileTimestamp);
    }

    /**
     * sftp 下载方法总入口
     *
     * @param sftpPath      sftp 文件夹路径
     * @param localDir      本地文件路径
     * @param sftpMap       sftp 配置
     * @param fileTimestamp 文件时间戳
     * @return 下载后的路径
     */
    private static String downloadDirFromSftp(String sftpPath, String localDir, Map<String, String> sftpMap, Timestamp fileTimestamp) {
        if (StringUtils.isBlank(sftpPath)) {
            log.info("sftpPath is empty, return null !");
            return null;
        }
        // sftp 配置为空直接返回 sftpPath
        if (MapUtils.isEmpty(sftpMap)) {
            log.info("sftpMap is empty, sftpPath: {}", sftpPath);
            return sftpPath;
        }

        sftpPath = sftpPath.endsWith("/") ? sftpPath.substring(0, sftpPath.length() - 1) : sftpPath;

        // 兼容 path 中不传 sftp 路径的情况
        if (StringUtils.isNotEmpty(MapUtils.getString(sftpMap, "path"))) {
            String path = MapUtils.getString(sftpMap, "path").trim();
            // 判断是否已经拼接 sftp path
            if (!sftpPath.startsWith(path)) {
                sftpPath = path + File.separator + sftpPath;
            }
        }

        // 根据 sftp 的 host + sftp 上的文件绝对路径确定本地文件夹地址, 避免地址冲突
        String host = MapUtils.getString(sftpMap, SFTPHandler.KEY_HOST);
        AssertUtils.notBlank(host, "sftp host can't be null.");

        // md5 path
        String pathMd5 = MD5Util.getMd5String(host + sftpPath);
        // 文件夹名称
        String dirName = sftpPath.substring(sftpPath.lastIndexOf("/") + 1) + "_" + pathMd5;
        localDir = PathUtils.removeMultiSeparatorChar(localDir + File.separator + dirName);
        //需要读取配置文件
        //本地kerberos文件
        long localTimeLock = getLocalTimeLock(localDir);
        // 如果本地文件时间戳 >= 服务器文件时间戳则直接返回本地路径
        if (localTimeLock > 0 && Objects.nonNull(fileTimestamp) && localTimeLock >= fileTimestamp.getTime()) {
            return localDir;
        }
        SFTPHandler handler = null;
        try {
            handler = SFTPHandler.getInstance(sftpMap);
            //sftp服务器kerberos文件
            long timeLock = getSftpTimeLock(handler, sftpPath);
            // 如果 timeLock 为空，则说明不存在 .lock 文件，则
            if (timeLock == 0L || localTimeLock < timeLock) {
                // 对统一数据源的kerberos文件本地删除和sftp下载进行加锁
                synchronized (sftpPath.intern()) {
                    // 需要下载替换当时的配置
                    delFile(new File(localDir));
                    handler.downloadDir(sftpPath, localDir);
                    // 如果 SFTP 不存在 .lock 文件，则手动创建一个
                    createIfNotExistLockFile(localDir);
                }
            }
        } catch (Exception e) {
            log.warn("下载kerberos配置失败", e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
        return localDir;
    }

    /**
     * 删除本地文件 及文件夹
     *
     * @param file
     * @return
     */
    private static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            if (file.getName().endsWith("__MACOSX")) {
                return false;
            }
            File[] files = file.listFiles();
            if (files == null || files.length < 1) {
                return false;
            }
            for (File f : files) {
                delFile(f);
            }
        }
        log.info("正在删除本地文件，文件路径：{}", file.getAbsolutePath());
        return file.delete();
    }

    private static Long getSftpTimeLock(SFTPHandler handler, String sourceSftpPath) throws SftpException {
        Vector vector = handler.listFile(sourceSftpPath);
        for (Object obj : vector) {
            ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) obj;
            if (lsEntry.getFilename().endsWith(LOCK_SUFFIX)) {
                String filename = lsEntry.getFilename();
                filename = filename.substring(0, filename.length() - LOCK_SUFFIX.length());
                return Long.valueOf(filename);
            }
        }
        return 0L;
    }

    private static long getLocalTimeLock(String localKerberosConf) {
        File localKerberosConfFile = new File(localKerberosConf);
        if (localKerberosConfFile.exists() && localKerberosConfFile.isDirectory()) {
            String[] list = localKerberosConfFile.list();
            // 过滤出 .lock 文件
            Optional<String> lockFileOptional = Arrays.stream(list).filter(str -> str.endsWith(LOCK_SUFFIX)).findFirst();

            // 如果存在文件则取时间戳，主要目的是历史数据处理
            if (lockFileOptional.isPresent()) {
                String filename = lockFileOptional.get();
                filename = filename.substring(0, filename.length() - LOCK_SUFFIX.length());
                return Long.parseLong(filename);
            }
        }
        return 0L;
    }


    /**
     * 如果不存在，则新建 .lock 文件
     *
     * @param srcDir 远程目录
     */
    private static void createIfNotExistLockFile(String srcDir) throws IOException {
        // 如果需要检验 .lock 文件，则说明如果存在，则不创建，返回 .lock 文件，不存在则创建
        long localTimeLock = getLocalTimeLock(srcDir);
        if (localTimeLock != 0L) {
            return;
        }
        // 文件名称
        String filename = System.currentTimeMillis() + LOCK_SUFFIX;
        String lockFile = srcDir + SEPARATE + filename;
        new File(lockFile).createNewFile();
    }
}
