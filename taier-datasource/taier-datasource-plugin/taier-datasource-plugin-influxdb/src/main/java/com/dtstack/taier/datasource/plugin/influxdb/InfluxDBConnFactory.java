package com.dtstack.taier.datasource.plugin.influxdb;

import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import com.dtstack.taier.datasource.plugin.common.service.ErrorAdapterImpl;
import com.dtstack.taier.datasource.plugin.common.service.IErrorAdapter;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InfluxDBSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;

/**
 * influxDB 连接工厂
 *
 * @author ：wangchuan
 * date：Created in 上午10:33 2021/6/7
 * company: www.dtstack.com
 */
@Slf4j
public class InfluxDBConnFactory {

    // 异常匹配器
    private static final IErrorPattern ERROR_PATTERN = new InfluxDBErrorPattern();

    // 异常适配器
    private static final IErrorAdapter ERROR_ADAPTER = new ErrorAdapterImpl();

    // http 前缀
    private static final String HTTP_PREFIX = "http://";

    // https 前缀
    private static final String HTTPS_PREFIX = "https://";

    /**
     * 获取 influxDB 连接客户端
     *
     * @param sourceDTO influxDB 数据源连接信息
     * @return influxDB 连接客户端
     */
    public static InfluxDB getClient(ISourceDTO sourceDTO) {
        InfluxDBSourceDTO influxDBSourceDTO = (InfluxDBSourceDTO) sourceDTO;
        if (StringUtils.isBlank(influxDBSourceDTO.getUrl())) {
            throw new SourceException("url cannot be null");
        }
        String originUrl = influxDBSourceDTO.getUrl().trim();
        // 默认 http 协议
        String url = (originUrl.startsWith(HTTP_PREFIX)||originUrl.startsWith(HTTPS_PREFIX)) ?
                originUrl : HTTP_PREFIX + originUrl;
        InfluxDB influxDB;
        if (StringUtils.isNotBlank(influxDBSourceDTO.getUsername())) {
            influxDB = InfluxDBFactory.connect(url, influxDBSourceDTO.getUsername(), influxDBSourceDTO.getPassword());
        } else {
            influxDB = InfluxDBFactory.connect(url);
        }
        // 设置 db
        if (StringUtils.isNotBlank(influxDBSourceDTO.getDatabase())) {
            influxDB.setDatabase(influxDBSourceDTO.getDatabase());
        }
        // 设置数据存储策略
        influxDB.setRetentionPolicy(influxDBSourceDTO.getRetentionPolicy());
        return influxDB;
    }

    /**
     * 测试 influxDB 连通性
     *
     * @param source 数据源连接信息
     * @return 是否连通
     */
    public static Boolean testCon(ISourceDTO source) {
        try (InfluxDB influxDB = InfluxDBConnFactory.getClient(source)) {
            Pong response = influxDB.ping();
            return response.isGood();
        } catch (Exception e) {
            throw new SourceException(ERROR_ADAPTER.connAdapter(e.getMessage(), ERROR_PATTERN), e);
        }
    }
}
