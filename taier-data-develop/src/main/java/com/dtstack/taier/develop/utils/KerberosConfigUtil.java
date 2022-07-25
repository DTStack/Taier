package com.dtstack.taier.develop.utils;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.sftp.SFTPHandler;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

/**
 * @author zhiChen
 * @date 2022/3/4 16:51
 */
public class KerberosConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(KerberosConfigUtil.class);

    private static final String LOCK_SUFFIX = ".lock";

    private static final String SEPARATE = File.separator;

    protected static String localKerberosDir = String.format("%s/kerberosConf", System.getProperty("user.dir"));

    protected static String localSslDir = String.format("%s/sslConf", System.getProperty("user.dir"));

    protected static String localConfDir = String.format("%s/sourceConfDir", System.getProperty("user.dir"));

    /**
     * 从 SFTP 上下载 kerberos 配置文件到本地。
     * 先比较传入时间戳与 本地 lock 文件的时间戳，如果传入时间戳大于本地文件时间戳则重新下载
     * 如果传入时间戳为 null，则比较本地 kerberos 文件路径下的 lock 时间戳与 sftp lock 文件时间戳判断是否需要重新下载
     *
     * @param sftpDir       SFTP 上 kerberos 配置文件相对路径
     * @param localPath     本地 kerberos 目录
     * @param sftpMap       sftp 配置
     * @param fileTimestamp 本地时间戳
     */
    public static void downloadFileFromSftp(String sftpDir, String localPath, Map<String, String> sftpMap, Timestamp fileTimestamp) {
        //需要读取配置文件
        //本地kerberos文件
        Long localTimeLock = getLocalTimeLock(localPath);
        if (fileTimestamp != null && localTimeLock >= fileTimestamp.getTime()) {
            return;
        }
        String sftpPath = sftpMap.get("path") + SEPARATE + sftpDir;
        SFTPHandler handler = null;
        try {
            handler = SFTPHandler.getInstance(sftpMap);
            //sftp服务器文件
            Long timeLock = getSftpTimeLock(handler, sftpPath);
            // 如果 timeLock 为空，则说明不存在 .lock 文件，则
            if (timeLock == 0L || localTimeLock < timeLock) {
                // 对文件本地删除和sftp下载进行加锁
                synchronized (sftpDir.intern()) {
                    // 需要下载替换当时的配置
                    delFile(new File(localPath));
                    handler.downloadDir(sftpPath, localPath);
                    // 如果 SFTP 不存在 .lock 文件，则手动创建一个
                    createIfNotExistLockFile(localPath);
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException(String.format("从 SFTP 下载配置文件异常: %s", e.getMessage()), e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

    /**
     * 删除本地文件或文件夹
     *
     * @param file 本地文件/文件夹路径
     */
    public static void delFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            if (file.getName().endsWith("__MACOSX")) {
                return;
            }
            File[] files = file.listFiles();
            if (files == null || files.length < 1) {
                return;
            }
            for (File f : files) {
                delFile(f);
            }
        }
        logger.info("正在删除本地文件，文件路径：{}", file.getAbsolutePath());
        boolean delete = file.delete();
        if (!delete) {
            logger.warn("本地文件删除失败，文件路径：{}", file.getAbsolutePath());
        }
    }

    /**
     * 获取 SFTP 服务器上的 kerberos 配置文件时间戳，不存在则返回 0
     *
     * @param handler        SFTP 客户端
     * @param sourceSftpPath SFTP kerberos 配置文件目录
     * @return SFTP 时间戳
     * @throws SftpException sftp 异常
     */
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

    /**
     * 获取本地 kerberos 配置文件时间戳，不存在则返回 0
     *
     * @param localKerberosConf 本地 kerberos 配置路径
     * @return lock 文件时间戳
     */
    private static Long getLocalTimeLock(String localKerberosConf) {
        File localKerberosConfFile = new File(localKerberosConf);
        if (localKerberosConfFile.exists() && localKerberosConfFile.isDirectory()) {
            String[] list = localKerberosConfFile.list();
            if (ArrayUtils.isEmpty(list)) {
                return 0L;
            }
            // 过滤出 .lock 文件
            Optional<String> lockFileOptional = Arrays.stream(list).filter(str -> str.endsWith(LOCK_SUFFIX)).findFirst();
            // 如果存在文件则取时间戳，主要目的是历史数据处理
            if (lockFileOptional.isPresent()) {
                String filename = lockFileOptional.get();
                filename = filename.substring(0, filename.length() - LOCK_SUFFIX.length());
                return Long.valueOf(filename);
            }
        }
        return 0L;
    }

    /**
     * 如果 .lock 文件不存在，则新建
     *
     * @param srcDir 远程目录
     */
    private static void createIfNotExistLockFile(String srcDir) throws IOException {
        // 如果需要检验 .lock 文件，则说明如果存在，则不创建，返回 .lock 文件，不存在则创建
        Long localTimeLock = getLocalTimeLock(srcDir);
        if (localTimeLock != 0L) {
            return;
        }
        // 文件名称
        String filename = System.currentTimeMillis() + LOCK_SUFFIX;
        boolean createResult = new File(srcDir + SEPARATE + filename).createNewFile();
        if (!createResult) {
            logger.warn("kerberos lock file 文件创建失败");
        }
    }

    /**
     * 该数据源存放文件夹命名（sftp和本地）
     *
     * @param dtCenterSourceId 数据源中心id
     * @return sftp 数据源命名
     */
    public static String getLocalKerberosPath(Long dtCenterSourceId) {
        return localKerberosDir + SEPARATE + "_" + Optional.ofNullable(dtCenterSourceId).orElse(0L);
    }

    /**
     * 该数据源存放文件夹命名（sftp和本地）
     *
     * @param dtCenterSourceId 数据源中心id
     * @return sftp 数据源命名
     */
    public static String getLocalSslDir(Long dtCenterSourceId) {
        return localSslDir + SEPARATE + "_" + Optional.ofNullable(dtCenterSourceId).orElse(0L);
    }

    /**
     * 该数据源配置存放文件夹命名（sftp和本地）
     *
     * @param dtCenterSourceId 数据源中心id
     * @return sftp 数据源命名
     */
    public static String getLocalConfDir(Long dtCenterSourceId) {
        return localConfDir + SEPARATE + "_" + Optional.ofNullable(dtCenterSourceId).orElse(0L);
    }
}
