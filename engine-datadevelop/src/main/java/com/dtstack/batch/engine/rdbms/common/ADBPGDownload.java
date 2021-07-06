package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.engine.rdbms.impala.service.util.ImpalaDownload;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.IDownloader;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.exception.DtLoaderException;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ADB For PG
 * date: 2021/6/7 2:02 下午
 * author: zhaiyue
 */
public class ADBPGDownload implements IDownload {

    private static Logger logger = LoggerFactory.getLogger(ImpalaDownload.class);

    private IDownloader pluginDownloader;

    public ADBPGDownload(String sql, Long dtuicTenantId, String schema) throws Exception {
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, EJobType.ANALYTICDB_FOR_PG);
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, null, DataSourceType.ADB_FOR_PG.getVal(), schema, jdbcInfo);
        IClient client = ClientCache.getClient(DataSourceType.ADB_FOR_PG.getVal());
        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
                .sql(sql)
                .limit(jdbcInfo.getMaxRows())
                .queryTimeout(jdbcInfo.getQueryTimeout())
                .build();
        pluginDownloader = client.getDownloader(iSourceDTO, queryDTO);
    }

    @Override
    public void configure() {
        try {
            pluginDownloader.configure();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new DtCenterDefException(String.format("下载器configure失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public List<String> getMetaInfo() {
        try {
            return pluginDownloader.getMetaInfo();
        } catch (Exception e) {
            logger.error("", e);
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new DtCenterDefException(String.format("下载器getMetaInfo失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public Object readNext() {
        try {
            return pluginDownloader.readNext();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new DtCenterDefException(String.format("下载器readNext失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public boolean reachedEnd() {
        try {
            return pluginDownloader.reachedEnd();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new RdosDefineException(String.format("下载器reachedEnd失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public void close() {
        try {
            pluginDownloader.close();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            throw new RdosDefineException(String.format("下载器close失败，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public String getFileName() {
        try {
            return pluginDownloader.getFileName();
        } catch (Exception e) {
            if (e instanceof DtLoaderException) {
                throw (DtLoaderException) e;
            }
            logger.error(String.format("获取getFileName失败,原因是%s", e.getMessage()), e);
        }
        return "";
    }

}
