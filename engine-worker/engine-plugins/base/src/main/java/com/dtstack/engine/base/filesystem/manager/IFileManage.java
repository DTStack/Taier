package com.dtstack.engine.base.filesystem.manager;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Vector;

/**
 *  文件管理接口
 *
 * Date: 2020/7/20
 * Company: www.dtstack.com
 * @author maqi
 */
public interface IFileManage {
    org.slf4j.Logger LOG = LoggerFactory.getLogger(IFileManage.class);

    /**
     * 获取文件处理器能处理的文件前缀：hdfs://, sftp://, local://, http://
     * @return
     */
    String getPrefix();
    /**
     * 文件管理器能处理的文件路径
     * @param remotePath
     * @return
     */
    boolean canHandle(String remotePath);

    /**
     * 是否过滤url文件前缀标识
     * @return
     */
    default boolean filterPrefix() {
        return true;
    }

    /**
     *  下载文件
     * @param remotePath
     * @param localPath
     */
    boolean downloadFile(String remotePath, String localPath, boolean isEnd);

    /**
     *  下载目录
     * @param remotePath
     * @param localPath
     */
    boolean downloadDir(String remotePath, String localPath);

    /**
     * 上传文件
     * @param remotePath
     * @param localPath
     * @return
     */
    boolean uploadFile(String remotePath, String localPath);

    /**
     * 上传文件到指定目录
     * @param remotePath  远程文件夹
     * @param localPath   本地文件
     * @param fileName    文件名称
     * @param isEnd
     * @return
     */
    boolean uploadFile(String remotePath, String localPath, String fileName, boolean isEnd);

    /**
     * 上传文件夹
     * @param remotePath
     * @param localPath
     * @return
     */
    boolean uploadDir(String remotePath, String localPath);

    /**
     *  删除文件
     * @param remotePath
     */
    boolean deleteFile(String remotePath);

    /**
     *  删除文件夹
     * @param remotePath
     */
    boolean deleteDir(String remotePath);

    /**
     * 创建文件夹
     * @param path
     * @return
     */
    boolean mkdir(String path);

    /**
     * 重命名
     * @param oldPth
     * @param newPath
     * @return
     */
    boolean renamePath(String oldPth, String newPath);

    /**
     *  查看路径文件
     * @param remotePath
     * @return
     */
    Vector listFile(String remotePath);

    /**
     * 关闭连接
     */
    void close();


    /**
     * 文件下载失败，清理已下载文件
     * @param delPath
     */
    default void clearDownloadFile(String delPath) {
        try {
            File file = new File(delPath);
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delPath + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        clearDownloadFile(delPath + File.separator + filelist[i]);
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
            LOG.error("clearDownloadFile error ", e);
        }
    }

}

