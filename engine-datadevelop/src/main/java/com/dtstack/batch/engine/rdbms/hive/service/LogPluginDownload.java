package com.dtstack.batch.engine.rdbms.hive.service;

import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.IDownloader;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chener
 * @Classname LogPluginDownloader
 * @Description TODO
 * @Date 2020/8/8 17:12
 * @Created chener@dtstack.com
 */
public class LogPluginDownload implements IDownload {

    private static Logger LOGGER = LoggerFactory.getLogger(LogPluginDownload.class);

    private IDownloader hdfsLogDownloader;

    private String applicationStr;

    private Map<String, Object> yarnConf;

    private Map<String, Object> hdfsConf;

    private String user;

    private Integer readLimit;

    public LogPluginDownload(String applicationStr, Map<String, Object> yarnConf, Map<String, Object> hdfsConf, String user, Integer readLimit) throws Exception {
        this.applicationStr = applicationStr;
        this.yarnConf = yarnConf;
        this.hdfsConf = hdfsConf;
        this.user = user;
        this.readLimit = readLimit;
        init();
    }

    private void init() throws Exception {
        Object kerberosConfig = hdfsConf.get("kerberosConfig");
        Map<String, Object> kerberosConfMap = null;
        if (Objects.nonNull(kerberosConfig)) {
            if (kerberosConfig instanceof String) {
                kerberosConfMap = PublicUtil.objectToMap(kerberosConfig);
            } else if (kerberosConfig instanceof Map) {
                kerberosConfMap = (Map<String, Object>) kerberosConfig;
            }
        }
        HdfsSourceDTO sourceDTO = HdfsSourceDTO.builder()
                .config(PublicUtil.objectToStr(hdfsConf))
                .defaultFS(hdfsConf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConfMap)
                .yarnConf(yarnConf)
                .appIdStr(applicationStr)
                .readLimit(readLimit)
                .user(user)
                .build();
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                .build();
        hdfsLogDownloader = hdfsClient.getLogDownloader(sourceDTO, sqlQueryDTO);
    }

    @Override
    public void configure() {
        try {
            hdfsLogDownloader.configure();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器configure失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public List<String> getMetaInfo() {
        try {
            return hdfsLogDownloader.getMetaInfo();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器getMetaInfo失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public Object readNext() {
        try {
            return hdfsLogDownloader.readNext();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器readNext失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public boolean reachedEnd() {
        try {
            return hdfsLogDownloader.reachedEnd();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器reachedEnd失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public void close() {
        try {
            hdfsLogDownloader.close();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器close失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public String getFileName() {
        try {
            return hdfsLogDownloader.getFileName();
        } catch (Exception e) {
            LOGGER.error(String.format("获取getFileName失败,原因是%s",e.getMessage()),e);
        }
        return "";
    }
}
