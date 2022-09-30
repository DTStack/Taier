package com.dtstack.taier.script.util;

import com.dtstack.taier.script.ScriptConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Utilities {
    private static Log LOG = LogFactory.getLog(Utilities.class);

    private Utilities() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.warn("Sleeping are Interrupted ...", e);
        }
    }

    public static List<FileStatus> listStatusRecursively(Path path, FileSystem fs, List<FileStatus> fileStatuses)
            throws IOException {
        if (fileStatuses == null) {
            fileStatuses = new ArrayList<>(1000);
        }
        LOG.info("input path: " + path.toString());
        FileStatus[] fileStatus = fs.listStatus(path);
        if (fileStatus != null && fileStatus.length > 0) {
            LOG.info("input list size:" + fileStatus.length);
            for (FileStatus f : fileStatus) {
                if (fs.isDirectory(f.getPath())) {
                    listStatusRecursively(f.getPath(), fs, fileStatuses);
                } else {
                    fileStatuses.add(f);
                }
            }
        } else {
            LOG.info("fileStatus is null or size is 0");
        }
        return fileStatuses;
    }

    public static List<Path> convertStatusToPath(List<FileStatus> fileStatuses) {
        List<Path> paths = new ArrayList<>();
        if (fileStatuses != null) {
            for (FileStatus fileStatus : fileStatuses) {
                paths.add(fileStatus.getPath());
            }
        }
        return paths;
    }


    public static Path getRemotePath(YarnConfiguration conf, Configuration dtconf, ApplicationId appId, String fileName) {
        String pathSuffix = appId.toString() + "/" + fileName;
        // hdfs temp dir
        Path remotePath = new Path(dtconf.get(ScriptConfiguration.CONTAINER_STAGING_DIR, ScriptConfiguration.DEFAULT_CONTAINER_STAGING_DIR),
                pathSuffix);
        remotePath = new Path(conf.get("fs.defaultFS"), remotePath);
        LOG.debug("Got remote path of {} is {}" + fileName + " is " + remotePath.toString());
        return remotePath;
    }

    public static Path getRemotePath(YarnConfiguration conf, String fileName) {
        Path remotePath = new Path(conf.get("fs.defaultFS"), fileName);
        LOG.debug("Got remote path of " + fileName + " is " + remotePath.toString());
        return remotePath;
    }

    public static void setPathExecutableRecursively(String path) {
        File file = new File(path);
        if (!file.exists()) {
            LOG.warn("Path " + path + " does not exist!");
            return;
        }
        if (!file.setExecutable(true)) {
            LOG.error("Failed to set executable for " + path);
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                setPathExecutableRecursively(file.getAbsolutePath());
            }
        }
    }

    public static boolean mkdirs(String path) {
        return mkdirs(path, false);
    }

    public static boolean mkdirs(String path, boolean needDelete) {
        File file = new File(path);
        if (file.exists()) {
            if (needDelete) {
                file.delete();
            } else {
                return true;
            }
        }
        return file.mkdirs();
    }

    public static boolean mkParentDirs(String outFile) {
        File dir = new File(outFile);
        dir = dir.getParentFile();
        return dir.exists() || dir.mkdirs();
    }

    public static LocalResource createApplicationResource(FileSystem fs, Path path, LocalResourceType type)
            throws IOException {
        LocalResource localResource = Records.newRecord(LocalResource.class);
        FileStatus fileStatus = fs.getFileStatus(path);
        localResource.setResource(ConverterUtils.getYarnUrlFromPath(path));
        localResource.setSize(fileStatus.getLen());
        localResource.setTimestamp(fileStatus.getModificationTime());
        localResource.setType(type);
        localResource.setVisibility(LocalResourceVisibility.APPLICATION);
        return localResource;
    }


    public static Boolean cleanStagingRemotePath(YarnConfiguration conf, Configuration dtconf, ApplicationId appId) {
        if (conf == null || appId == null) {
            LOG.error("clean staging remote path failed because yarnConfiguration or applicationId is null.");
            return false;
        }
        try {
            Path remotePath = getRemotePath(conf, dtconf, appId, "");
            FileSystem fs = FileSystem.get(conf);
            fs.delete(remotePath, true);
            LOG.info("clean staging remote path " + remotePath.toString() + " success.");
            return true;
        } catch (IOException e) {
            LOG.error("clean staging remote path failed: " + e);
            return false;
        }
    }

    public static Map<String, String> getEnvironmentVariables(String envPrefix, YarnConfiguration conf) {
        Map<String, String> result = new HashMap();
        Iterator envIterator = conf.iterator();

        while (envIterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) envIterator.next();
            if ((entry.getKey()).startsWith(envPrefix) && (entry.getKey()).length() > envPrefix.length()) {
                String key = (entry.getKey()).substring(envPrefix.length());
                result.put(key, entry.getValue());
            }
        }

        return result;
    }

}