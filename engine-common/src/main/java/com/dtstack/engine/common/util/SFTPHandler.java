package com.dtstack.engine.common.util;

import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFactory;
import com.dtstack.engine.common.sftp.SftpPool;
import com.dtstack.engine.common.sftp.SftpPoolConfig;
import com.google.common.collect.Maps;
import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public class SFTPHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SFTPHandler.class);

    private static final String KEYWORD_FILE_NOT_EXISTS = "No such file";

    private static Map<String, SftpPool> sftpPoolMap = Maps.newConcurrentMap();
    private static Map<String, Long> fileLastModifyMap = Maps.newConcurrentMap();

    private ChannelSftp channelSftp;
    private SftpPool sftpPool;
    private SftpConfig sftpConfig;

    private SFTPHandler(ChannelSftp channelSftp, SftpPool sftpPool, SftpConfig sftpConfig) {
        this.channelSftp = channelSftp;
        this.sftpPool = sftpPool;
        this.sftpConfig = sftpConfig;
    }

    public static SFTPHandler getInstance(SftpConfig sftpConfig) {
        checkConfig(sftpConfig);

        boolean isUsePool = sftpConfig.getIsUsePool();

        ChannelSftp channelSftp = null;
        SftpPool sftpPool = null;
        if (isUsePool) {
            logger.info("get channelSftp from SftpPool!");
            sftpPool = getSftpPool(sftpConfig);
            channelSftp = sftpPool.borrowObject();
        } else {
            logger.info("get channelSftp from native!");
            SftpFactory sftpFactory = new SftpFactory(sftpConfig);
            channelSftp = sftpFactory.create();
        }

        setSessionTimeout(sftpConfig, channelSftp);
        return new SFTPHandler(channelSftp, sftpPool, sftpConfig);
    }

    private static SftpPool getSftpPool(SftpConfig sftpConfig) {
        String sftpPoolKey = getSftpPoolKey(sftpConfig);
        SftpPool sftpPool = sftpPoolMap.computeIfAbsent(sftpPoolKey, k -> {
            SftpPool sftpPool1 = null;
            //先检测sftp主机验证能否通过，再缓存
            SftpFactory sftpFactory = new SftpFactory(sftpConfig);
            ChannelSftp channelSftpTest = sftpFactory.create();
            if (channelSftpTest != null) {
                //释放资源，防止内存泄漏
                try {
                    channelSftpTest.disconnect();
                    channelSftpTest.getSession().disconnect();
                } catch (JSchException e) {
                    logger.error("channelSftpTest获取Session异常", e);
                }
                SftpPoolConfig sftpPoolConfig = new SftpPoolConfig(sftpConfig.getMaxTotal(), sftpConfig.getMaxIdle(), sftpConfig.getMinIdle());
                sftpPoolConfig.setMaxWaitMillis(sftpConfig.getMaxWaitMillis()); //从idle队列里面取对象时，阻塞时最大等待时长
                sftpPoolConfig.setMinEvictableIdleTimeMillis(sftpConfig.getMinEvictableIdleTimeMillis()); //处于idle状态超过此值时，会被destory
                sftpPoolConfig.setSoftMinEvictableIdleTimeMillis(sftpConfig.getSoftMinEvictableIdleTimeMillis()); //处于idle状态超过此值时，会被destory, 保留minIdle个空闲连接数。默认为-1
                sftpPoolConfig.setTimeBetweenEvictionRunsMillis(sftpConfig.getTimeBetweenEvictionRunsMillis()); //evict线程每次间隔时间
                sftpPool1 = new SftpPool(sftpFactory, sftpPoolConfig);
            } else {
                logger.info("SFTPHandler连接sftp失败, host:{} username:{} .", sftpConfig.getHost(), sftpConfig.getUsername());
            }
            return sftpPool1;
        });
        return sftpPool;

    }

    private static String getSftpPoolKey(SftpConfig sftpConfig) {
        return sftpConfig.getHost() + sftpConfig.getPort() + sftpConfig.getUsername() + sftpConfig.getPath();
    }

    private static void checkConfig(SftpConfig sftpConfig) {
        if (sftpConfig == null) {
            throw new IllegalArgumentException("The config of sftp is null");
        }
        if (StringUtils.isBlank(sftpConfig.getHost())) {
            throw new IllegalArgumentException("The host of sftp is null");
        }
        if (sftpConfig.getPort() == null) {
            throw new IllegalArgumentException("The port of sftp is null");
        }
        if (StringUtils.isBlank(sftpConfig.getUsername())) {
            throw new IllegalArgumentException("The username of sftp is null");
        }
        if (StringUtils.isBlank(sftpConfig.getPath())) {
            throw new IllegalArgumentException("The path of sftp is null");
        }
    }

    private static void setSessionTimeout(SftpConfig sftpConfig, ChannelSftp channelSftp) {
        Session sessionSftp;
        try {
            sessionSftp = channelSftp.getSession();
            sessionSftp.setTimeout(sftpConfig.getTimeout());
        } catch (JSchException e) {
            logger.error("获取sessionSftp异常", e);
            throw new RuntimeException("获取sessionSftp异常, 请检查sessionSftp是否正常", e);
        }
    }

    public void downloadFile(String ftpPath, String localPath) {
        if (!isFileExist(ftpPath)) {
            throw new RuntimeException("File not exist on sftp:" + ftpPath);
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(localPath));
            channelSftp.get(ftpPath, os);
        } catch (Exception e) {
            throw new RuntimeException("download file from sftp error", e);
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 下载目录
     * <p>
     * 覆盖本地路径
     *
     * @param ftpDir
     * @param localDir
     * @return
     */
    public int downloadDir(String ftpDir, String localDir) {
        int sum = 0;
        try {
            File localDirPath = new File(localDir);
            if (!localDirPath.exists()) {
                boolean mkdirs = localDirPath.mkdirs();
                logger.info("local file path {}  mkdir {} :", localDir, mkdirs);
            }
            try {
                Vector files = channelSftp.ls(ftpDir);
                if (files == null) {
                    return 0;
                }
                for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
                    ChannelSftp.LsEntry str = iterator.next();
                    String filename = str.getFilename();
                    if (".".equals(filename) || "..".equals(filename)) {
                        continue;
                    }
                    SftpATTRS attrs = str.getAttrs();
                    boolean isdir = attrs.isDir();
                    String localFilePath = localDir + "/" + filename;
                    String ftpFilePath;
                    if (channelSftp.stat(ftpDir).isDir()) {
                        ftpFilePath = ftpDir + "/" + filename;
                    } else {
                        ftpFilePath = ftpDir;
                    }
                    if (isdir) {
                        File dir2 = new File(localFilePath);
                        if (!dir2.exists()) {
                            logger.info("local file path mkdir :", localFilePath);
                            dir2.mkdir();
                        }
                        sum += downloadDir(ftpFilePath, localFilePath);
                    } else {
                        downloadFile(ftpFilePath, localFilePath);
                        sum++;
                    }
                }
            } catch (SftpException e) {
                logger.error("", e);
            }
            return sum;
        } catch (Exception e) {
            logger.error("sftp downloadDir error {}", e);
            return -1;
        }
    }

    public boolean isFileExist(String ftpPath) {
        try {
            channelSftp.lstat(ftpPath);
            return true;
        } catch (SftpException e) {
            if (e.getMessage().contains(KEYWORD_FILE_NOT_EXISTS)) {
                return false;
            } else {
                throw new RuntimeException("Check file exists error", e);
            }
        }
    }

    public void close() {
        if (sftpPool != null) {
            sftpPool.returnObject(channelSftp);
        } else {
            try {
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            } catch (Exception e) {
                logger.error("close channelSftp error: {}", e.getMessage());
            }
        }

    }

    public String loadFromSftp(String fileName, String remoteDir, String localDir) {
        String remoteFile = remoteDir + File.separator + fileName;
        String localFile = localDir + File.separator + fileName;
        try {
            if (new File(fileName).exists()) {
                return fileName;
            } else {
                downloadFile(remoteFile, localFile);
                return localFile;
            }
        } catch (Exception e) {
            logger.error("load file error: ", e);
            return fileName;
        } finally {
            close();
        }
    }


    /**
     * 强制下载最新文件
     *
     * @param fileName
     * @param remoteDir
     * @param localDir
     * @return
     */
    public String loadOverrideFromSftp(String fileName, String remoteDir, String localDir, boolean isEnd) {
        String remoteFile = remoteDir + File.separator + fileName;
        String localFile = localDir + File.separator + fileName;

        File localFileDir = new File(localFile);
        Long lastModifyTime;
        long fileTimeout;
        if ((fileTimeout = sftpConfig.getFileTimeout()) != 0L && (lastModifyTime = fileLastModifyMap.get(localFile)) != null) {
            if (System.currentTimeMillis() - lastModifyTime <= fileTimeout) {
                if (localFileDir.exists()) {
                    return localFile;
                }
            }
        }
        try {
            downloadFile(remoteFile, localFile);
            fileLastModifyMap.put(localFile, new File(localFile).lastModified());
            return localFile;
        } catch (Exception e) {
            logger.error("load file error: ", e);
            return fileName;
        } finally {
            if (isEnd) {
                close();
            }
        }
    }

    public String loadFromSftp(String fileName, String remoteDir, String localDir, boolean isEnd) {
        String remoteFile = remoteDir + File.separator + fileName;
        String localFile = localDir + File.separator + fileName;
        try {
            if (new File(localFile).exists()) {
                return localFile;
            } else {
                downloadFile(remoteFile, localFile);
                return localFile;
            }
        } catch (Exception e) {
            logger.error("load file error: ", e);
            return fileName;
        } finally {
            if (isEnd) {
                close();
            }
        }
    }

    public boolean uploadDir(String dstDir, String srcDir) {
        File file = new File(srcDir);
        if (file.isDirectory()) {
            dstDir += "/" + file.getName();
            if (!mkdir(dstDir)) {
                logger.error("创建sftp服务器路径失败:" + dstDir);
                return false;
            }
            File[] files = file.listFiles();
            for (File file1 : files) {
                uploadDir(dstDir, file1.getPath());
            }
        } else {
            return upload(dstDir, file.getName(), file.getParent(), true);
        }
        return false;
    }

    public boolean upload(String baseDir, String filePath, boolean multiplex) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("该路径不存在或不是文件路径");
        }
        return upload(baseDir, file.getName(), file.getParent(), multiplex);
    }

    public boolean upload(String baseDir, String filePath) {
        return upload(baseDir, filePath, false);
    }

    /**
     * sftp 上传文件 且会覆盖同名文件
     *
     * @param baseDir  目标路径
     * @param fileName 文件名
     * @param filePath 本地文件目录
     */
    public boolean upload(String baseDir, String fileName, String filePath, boolean multiplex) {
        logger.info("路径：baseDir=" + baseDir);
        try {
            //检查路径
            if (!mkdir(baseDir)) {
                logger.error("创建sftp服务器路径失败:" + baseDir);
                return false;
            }
            String dst = baseDir + "/" + fileName;
            String src = filePath + "/" + fileName;
            logger.info("开始上传，本地服务器路径：[" + src + "]目标服务器路径：[" + dst + "]");
            channelSftp.put(src, dst);
            logger.info("上传成功");
            return true;
        } catch (Exception e) {
            logger.error("上传失败", e);
            return false;
        } finally {
            if (!multiplex) {
                close();
            }
        }
    }

    /**
     * 支持创建多层路径
     *
     * @param path
     * @return
     */
    public boolean mkdir(String path) {
        String[] split = path.split("/");
        StringBuilder currPath = new StringBuilder();
        for (String dir : split) {
            currPath.append("/").append(dir).toString();
            try {
                channelSftp.cd(currPath.toString());
            } catch (SftpException sException) {
                if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
                    try {
                        channelSftp.mkdir(currPath.toString());
                    } catch (SftpException e) {
                        logger.error("sftp isExist error {}", e);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void deleteDir(String ftpPath) {
        try {
            channelSftp.cd(ftpPath);
        } catch (SftpException e) {
            logger.info("", e);
            return;
        }
        try {
            Vector files = channelSftp.ls(ftpPath);
            for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
                ChannelSftp.LsEntry str = iterator.next();
                String filename = str.getFilename();
                if (filename.equals(".") || filename.equals("..")) {
                    continue;
                }
                SftpATTRS attrs = str.getAttrs();
                if (attrs.isDir()) {
                    deleteDir(ftpPath + "/" + filename);
                } else {
                    channelSftp.rm(ftpPath + "/" + filename);
                }
            }
            if (channelSftp.ls(ftpPath).size() == 2) {
                channelSftp.rmdir(ftpPath);
            }
        } catch (SftpException e) {
            logger.error("", e);
            throw new RuntimeException("删除sftp路径失败，sftpPath=" + ftpPath);
        }
    }

    public Vector listFile(String ftpPath) throws SftpException {
        return channelSftp.ls(ftpPath);
    }

    public boolean renamePath(String oldPth, String newPath) {
        try {
            channelSftp.rename(oldPth, newPath);
        } catch (SftpException e) {
            logger.error("renamePath {} to {} error", oldPth, newPath, e);
            return false;
        }
        return true;
    }

    public void deleteFile(String path) {
        if (this.isFileExist(path)) {
            try {
                channelSftp.rm(path);
            } catch (SftpException e) {
                logger.error("", e);
                throw new RuntimeException("删除sftp路径失败，sftpPath=" + path);
            }
        }
    }
}