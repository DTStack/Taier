package com.dtstack.taiga.common.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.exception.DtCenterDefException;
import com.dtstack.taiga.common.sftp.SFTPHandler;
import com.dtstack.taiga.common.util.Xml2JsonUtil;
import com.dtstack.taiga.common.util.ZipUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-15 22:42
 **/
public class KerberosConfigVerify {
    private static final Logger logger = LoggerFactory.getLogger(KerberosConfigVerify.class);

    private static final String LOCK_SUFFIX = ".lock";

    private static final String SEPARATE = File.separator;

    private static final String DOT = ".";

    private static final String XML_SUFFIX = ".xml";

    private static final String KERBEROS_CONFIG = "kerberosConfig";

    private static final String HOST = "_HOST";

    private static final List<String> SUFFIX_LIST = Arrays.asList("file", "keytab", "Path", "conf", "principalFile");

    /**
     * 从上传的zip文件中解析kerberos配置
     *
     * @param tmpFilePath       zip文件目录
     * @param localKerberosConf 解压的本地目录
     * @return
     * @throws Exception
     */
    @Deprecated
    public static Map<String, Map<String, String>> parseKerberosFromUpload(String tmpFilePath, String localKerberosConf) throws Exception {
        List<File> unzipFileList = getFilesFromZip(tmpFilePath, localKerberosConf);
        return parseConfMap(unzipFileList, localKerberosConf);
    }

    @Deprecated
    public static Map<String, Map<String, String>> parseConfMap(List<File> unzipFileList, String unZipLocation) throws Exception {
        Map<String, File> confFileMap = new HashMap<>();
        List<File> xmlFileList = new ArrayList<>();
        filterXml(unzipFileList, unZipLocation, xmlFileList, confFileMap);
        Map<String, Map<String, String>> confMap = null;
        if (CollectionUtils.isNotEmpty(xmlFileList)) {
            try {
                confMap = parseAndRead(xmlFileList);
            } catch (Exception e) {
                logger.error("{}", e);
                throw new DtCenterDefException("配置文件解析失败");
            }
        }
        return confMap;
    }

    /**
     * 解析并读取配置文件内容
     */
    @Deprecated
    private static Map<String, Map<String, String>> parseAndRead(List<File> xmlFiles) throws Exception {
        Map<String, Map<String, String>> xmlMaps = new HashMap<>();
        for (File file : xmlFiles) {
            Map<String, Object> xmlMap = Xml2JsonUtil.xml2map(file);
            xmlMaps.put(file.getName(), mapToString(xmlMap));
        }

        return xmlMaps;
    }

    @Deprecated
    private static Map<String, String> mapToString(Map<String, Object> xmlMaps) {
        //toString
        Map<String, String> result = new HashMap<>();
        for (String key : xmlMaps.keySet()) {
            result.put(key, MapUtils.getString(xmlMaps, key));
        }
        return result;
    }

    @Deprecated
    public static List<File> getFilesFromZip(String zipLocation, String unzipLocation) {
        try {
            List<File> xmlFiles = ZipUtil.upzipFile(zipLocation, unzipLocation);
            return xmlFiles;
        } catch (Exception e) {
            logger.error("{}", e);
            throw new DtCenterDefException("压缩包解压失败");
        }
    }

    @Deprecated
    private static void filterXml(List<File> unzipFileList, String pathSuf, List<File> xmlFiles, Map<String, File> confMap) {
        if (unzipFileList == null) {
            throw new DtCenterDefException("缺少必要配置文件");
        }
        for (File file : unzipFileList) {
            File parentFile = new File(pathSuf);
            if (file.getAbsolutePath().startsWith(parentFile.getAbsolutePath())) {
                if (!file.getName().startsWith(DOT)) {
                    if (file.getName().endsWith(XML_SUFFIX)) {
                        xmlFiles.add(file);
                    } else {
                        confMap.put(file.getName(), file);
                    }
                }
            }
        }
    }

    /**
     * @param sourceKey
     * @param localKerberosConf
     * @param sftpMap
     * @param kerberosFileTimestamp
     * @throws SftpException
     */
    public static void downloadKerberosFromSftp(String sourceKey, String localKerberosConf, Map<String, String> sftpMap, Timestamp kerberosFileTimestamp) throws SftpException {
        //需要读取配置文件
        //本地kerberos文件
        Long localTimeLock = getLocalTimeLock(localKerberosConf);
        if (kerberosFileTimestamp != null && localTimeLock > kerberosFileTimestamp.getTime()) {
            return;
        }
        SFTPHandler handler = null;
        try {
            handler = SFTPHandler.getInstance(sftpMap);
            String sourceSftpPath = sftpMap.get("path") + SEPARATE + sourceKey;
            //sftp服务器kerberos文件
            Long timeLock = getSftpTimeLock(handler, sourceSftpPath);
            // 如果 timeLock 为空，则说明不存在 .lock 文件，则
            if (timeLock == 0L || localTimeLock < timeLock) {
                // 对统一数据源的kerberos文件本地删除和sftp下载进行加锁
                synchronized (sourceKey.intern()) {
                    // 需要下载替换当时的配置
                    delFile(new File(localKerberosConf));
                    handler.downloadDir(sourceSftpPath, localKerberosConf);
                    // 如果 SFTP 不存在 .lock 文件，则手动创建一个
                    createIfNotExistLockFile(localKerberosConf, true);
                }
            }
        } catch (Exception e) {
            logger.warn("下载kerberos配置失败 {}", e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

    /**
     * 删除本地文件 及文件夹
     *
     * @param file
     * @return
     */
    public static boolean delFile(File file) {
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
        logger.info("正在删除本地文件，文件路径：{}", file.getAbsolutePath());
        return file.delete();
    }

    /**
     * 替换kerberosConfig中的 principalFile 参数为本地目录
     */
    public static Map<String, String> replaceFilePath(JSONObject kerberosConfig, String currentConfDir) {
        Map<String, String> finalConfMap = new HashMap<>();
        if (kerberosConfig != null && currentConfDir != null) {
            //替换 principalFile为本地
            String principalFile = (String) kerberosConfig.get("principalFile");
            if (StringUtils.isNotBlank(principalFile)) {
                String fileName = principalFile.contains(File.separator) ? principalFile.substring(principalFile.lastIndexOf(File.separator) + 1) : principalFile;
                kerberosConfig.put("principalFile", currentConfDir + File.separator + fileName);
            }
            //替换 krb5Conf为本地
            String krb5Conf = (String) kerberosConfig.get("java.security.krb5.conf");
            if (StringUtils.isNotBlank(krb5Conf)) {
                String fileName = krb5Conf.contains(File.separator) ? krb5Conf.substring(krb5Conf.lastIndexOf(File.separator) + 1) : krb5Conf;
                kerberosConfig.put("java.security.krb5.conf", currentConfDir + File.separator + fileName);
            }
            kerberosConfig.keySet().forEach(key -> finalConfMap.put(key, kerberosConfig.getString(key)));
        }
        return finalConfMap;
    }

    /**
     * 替换kerberosConfig中的 principalFile 格式为keytab文件名
     */
    public static JSONObject formatPrincipalFileName(JSONObject kerberosConfig) {
        if (kerberosConfig != null) {
            //替换 principalFile为本地
            String principalFile = (String) kerberosConfig.get("principalFile");
            if (StringUtils.isNotBlank(principalFile) && principalFile.contains(File.separator)) {
                String fileName = principalFile.substring(principalFile.lastIndexOf(File.separator) + 1);
                kerberosConfig.put("principalFile", fileName);
            }
            return kerberosConfig;
        }
        return kerberosConfig;
    }

    private static boolean suffixVerify(String key, List<String> list) {
        boolean flag = false;
        for (String str : list) {
            if (key.endsWith(str)) {
                flag = true;
            }
        }
        return flag;
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

    private static Long getLocalTimeLock(String localKerberosConf) {
        File localKerberosConfFile = new File(localKerberosConf);
        if (localKerberosConfFile.exists() && localKerberosConfFile.isDirectory()) {
            String[] list = localKerberosConfFile.list();
            // 过滤出 .lock 文件
            Optional<String> lockFileOptional = Arrays.stream(list).filter(str -> {
                if (str.endsWith(LOCK_SUFFIX)) {
                    return true;
                } else {
                    return false;
                }
            }).findFirst();

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
     * 上传lock临时文件
     *
     * @param srcDir
     * @param dstDir
     * @param handler
     * @throws IOException
     */
    public static void uploadLockFile(String srcDir, String dstDir, SFTPHandler handler) throws IOException {
        // 删除远程文件夹下的文件
        handler.deleteDir(dstDir);

        // 删除本地 .lock 文件
        deleteLockFile(srcDir);

        // 本地创建 .lcok 文件
        String filename = createIfNotExistLockFile(srcDir, false);

        // 上传本地文件夹
        handler.upload(dstDir, filename, srcDir);
    }

    /**
     * 如果不存在，则新建 .lock 文件
     *
     * @param srcDir 远程目录
     * @param isCheckExists 是否检验 .lock 文件
     * @return
     */
    private static String createIfNotExistLockFile(String srcDir, boolean isCheckExists) throws IOException {
        // 如果需要检验 .lock 文件，则说明如果存在，则不创建，返回 .lock 文件，不存在则创建
        if (isCheckExists) {
            Long localTimeLock = getLocalTimeLock(srcDir);
            if (localTimeLock != 0L) {
                return localTimeLock + LOCK_SUFFIX;
            }
        }

        // 文件名称
        String filename = System.currentTimeMillis() + LOCK_SUFFIX;
        String lockFile = srcDir + SEPARATE + filename;
        new File(lockFile).createNewFile();
        return filename;
    }

    /**
     * 删除本地的 .lock 文件
     *
     * @param srcDir
     */
    private static void deleteLockFile(String srcDir) {
        File srcDirFile = new File(srcDir);

        //删除本地lock文件
        if (srcDirFile.exists() && srcDirFile.isDirectory()) {
            File[] files = srcDirFile.listFiles();
            if (files == null || files.length < 1) {
                return;
            }
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".lock")) {
                    file.delete();
                }
            }
        }
    }

    @Deprecated
    public static Map<String, Object> replaceHost(Map<String, Object> confMap) {
        String canonicalHostName;
        try {
            canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            logger.error("", e);
            throw new DtCenterDefException("本地地址获取失败");
        }
        for (String key : confMap.keySet()) {
            String value = MapUtils.getString(confMap, key);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            if (value.contains(HOST)) {
                confMap.replace(key, value.replace(HOST, canonicalHostName));
            }
        }
        return confMap;
    }
}
