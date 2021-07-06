package com.dtstack.batch.engine.hdfs.service;

import com.dtstack.batch.datamask.domain.DataMaskRule;
import com.dtstack.batch.datamask.util.DataMaskUtil;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.datamask.impl.DataMaskColumnInfoService;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.IDownloader;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * hive查询数据下载器
 *
 * @author ：wangchuan
 * date：Created in 3:25 上午 2020/11/26
 * company: www.dtstack.com
 */
public class HiveSelectDownload implements IDownload {

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveSelectDownload.class);

    // common-loader中定义的download
    private IDownloader pluginDownloader;

    // hadoop相关配置
    private Map<String, Object> hadoopConfig;

    // jdbc连接信息
    private JdbcInfo jdbcInfo;

    // 查询字段集合
    private List<String> queryFieldNames;

    // 查询字段别名集合
    private List<String> fieldNamesShow;

    // 是否展示无权限字段 false：不展示， true：展示
    boolean permissionStyle;

    // 是否需要脱敏
    boolean needMask;

    // uic租户ID
    Long uicTenantId;

    // 数据源类型
    Integer dataSourceType;

    // 查询的库名
    private String db;

    // 查询的表名
    private String tableName;

    // 指定下载分区
    private String partition;

    // 脱敏规则和字段
    private Map<String, List<DataMaskRule>> columnRules;

    // 该表的所有字段列表
    private List<String> columnNames;

    // 不需要进行展示的列
    private List<String> excludeCol = new ArrayList<>();

    // 无权限字段展示
    private static final String NO_PERMISSION = "NO PERMISSION";

    // hive空字符串处理
    private static final String TEXT_STORE_NULL = "\\N";

    public HiveSelectDownload(Map<String, Object> hadoopConfig, JdbcInfo jdbcInfo, List<String> queryFieldNames,
                              List<String> fieldNamesShow, boolean permissionStyle, boolean needMask, Long uicTenantId,
                              String db, String tableName, String partition, Map<String, List<DataMaskRule>> columnRules,
                              Integer dataSourceType) throws Exception{
        this.hadoopConfig = hadoopConfig;
        this.jdbcInfo = jdbcInfo;
        this.queryFieldNames = queryFieldNames;
        this.fieldNamesShow = fieldNamesShow;
        this.permissionStyle = permissionStyle;
        this.needMask = needMask;
        this.uicTenantId = uicTenantId;
        this.db = db;
        this.tableName = tableName;
        this.partition = partition;
        this.dataSourceType = dataSourceType;
        if (!needMask) {
            this.columnRules = Maps.newHashMap();
        } else {
            this.columnRules = columnRules;
        }
        init();
    }

    // 初始downloader和相关参数
    private void init() throws Exception {
        Map<String, String> partitionMap = Maps.newHashMap();
        if (StringUtils.isNotEmpty(partition)) {
            List<Pair<String, String>> stringStringPair = splitPartition(partition);
            if (CollectionUtils.isNotEmpty(stringStringPair)) {
                for (int i = 0; i < stringStringPair.size(); i++) {
                    partitionMap.put(stringStringPair.get(i).getKey(), stringStringPair.get(i).getValue());
                }
            }
        }
        // 获取client
        ISourceDTO sourceDTO = Engine2DTOService.get(uicTenantId, null, dataSourceType, db, jdbcInfo);
        IClient client = ClientCache.getClient(dataSourceType);
        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
                .tableName(tableName)
                .columns(queryFieldNames)
                .partitionColumns(partitionMap)
                .build();
        pluginDownloader = client.getDownloader(sourceDTO, queryDTO);
        this.columnNames = pluginDownloader.getMetaInfo();

        if (CollectionUtils.isNotEmpty(queryFieldNames)) {
            // permissionStyle为true则展示无权限字段
            if (permissionStyle) {
                // 获取不展示列添加到fieldNameList后面
                excludeCol = columnNames.stream().filter(column -> {
                    Boolean flag = false;
                    for (String clumnName : queryFieldNames){
                        flag = clumnName.equalsIgnoreCase(column);
                        if (flag){
                            break;
                        }
                    }
                    return !flag;
                }).collect(Collectors.toList());
                fieldNamesShow.addAll(excludeCol);
            }
        } else {
            fieldNamesShow = new ArrayList<>(columnNames);
            queryFieldNames = columnNames;
        }
        // 脱敏后字段结合
        List<String> maskFields = new ArrayList<>(queryFieldNames);

        // 查询结果需要脱敏
        if (needMask) {
            // 字段添加脱敏标识
            maskFields = maskFields.stream().map(column -> {
                if (columnRules.containsKey(column.toLowerCase())) {
                    return column + DataMaskUtil.SIGN_FOR_COLUMNS_NEED_MASK;
                } else {
                    return column;
                }
            }).collect(Collectors.toList());
        }
        // queryFieldNames为空表示非查询全部字段操作，此时展示别名
        if (!CollectionUtils.isEmpty(queryFieldNames)) {
            for (int i = 0; i < maskFields.size(); i++) {
                // 脱敏处理的字段名使用别名进行替换
                if (maskFields.get(i).endsWith(DataMaskUtil.SIGN_FOR_COLUMNS_NEED_MASK)) {
                    String n = fieldNamesShow.get(i);
                    fieldNamesShow.set(i, n + DataMaskUtil.SIGN_FOR_COLUMNS_NEED_MASK);
                }
            }
        }
    }

    /**
     * 拆分分区
     *
     * @param partition
     * @return
     */
    private List<Pair<String, String>> splitPartition(String partition) {
        List<Pair<String, String>> pairs = Lists.newArrayList();
        if (StringUtils.isEmpty(partition)) {
            return Collections.EMPTY_LIST;
        }
        String[] keys = partition.split("\\/");
        for (String key : keys) {
            String[] split = key.split("=");
            if (split.length == 2) {
                pairs.add(new Pair<>(split[0], split[1]));
            } else {
                LOGGER.error("分区异常:{}", partition);
            }
        }
        return pairs;
    }

    @Override
    public void configure() {
        try {
            pluginDownloader.configure();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new DtCenterDefException("下载器configure失败");
        }
    }

    @Override
    public List<String> getMetaInfo() {
        // 直接返回查询字段列表
        return fieldNamesShow;
    }

    @Override
    public Object readNext() {
        try {
            List<String> row = (List<String>) pluginDownloader.readNext();
            List<String> data = new ArrayList<>();
            List<String> columns = CollectionUtils.isNotEmpty(queryFieldNames) ? queryFieldNames : columnNames;
            for (int index = 0; index < columns.size(); index++) {
                String source = row.get(index);
                if (columnRules.containsKey(columns.get(index).toLowerCase())) {
                    data.add(dealHiveTextNull(DataMaskUtil.mask(source, DataMaskColumnInfoService.generateMaskRule(columnRules.get(columns.get(index).toLowerCase()), source))));
                } else {
                    data.add(dealHiveTextNull(source));
                }
            }
            excludeCol.forEach(index -> data.add(NO_PERMISSION));
            return data;
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器readNext失败:%s", e.getMessage()), e);
        }
    }

    @Override
    public boolean reachedEnd() {
        try {
            return pluginDownloader.reachedEnd();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new DtCenterDefException("下载器reachedEnd失败");
        }
    }

    @Override
    public void close() {
        try {
            pluginDownloader.close();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new DtCenterDefException("下载器close失败");
        }
    }

    @Override
    public String getFileName() {
        return pluginDownloader.getFileName();
    }

    /**
     * @param source 单独字段值
     * 处理hive存储为null字段
     */
    private String dealHiveTextNull(String source) {
        return TEXT_STORE_NULL.equals(source) ? null : source;
    }
}
