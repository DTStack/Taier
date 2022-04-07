package com.dtstack.taier.develop.flink.sql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.TableType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.Base64Util;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.flink.sql.core.TableFactory;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

public class SqlGenerateFactory {

    /**
     * 生成 flinkSql 建表 sql，1.12 版本
     *
     * @param dataSource       数据源信息
     * @param paramJson        前端入参信息
     * @param componentVersion 组建版本
     * @param tableType        表类型
     * @return 建表 sql
     */
    public static String generateSql(DsInfo dataSource, JSONObject paramJson, String componentVersion, TableType tableType) {
        if (Objects.isNull(tableType)) {
            throw new DtCenterDefException("表类型不能为空");
        }
        JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(dataSource.getDataJson()));
        switch (tableType) {
            case SIDE:
                if (StringUtils.isNotBlank(componentVersion) && StringUtils.equalsIgnoreCase(componentVersion, FlinkVersion.FLINK_112.getType())) {
                    return TableFactory.getSideTable(dataSource.getDataTypeCode(), dataJson, paramJson, FlinkVersion.FLINK_112.getType()).getCreateSql();
                }
            case SINK:
                if (StringUtils.isNotBlank(componentVersion) && StringUtils.equalsIgnoreCase(componentVersion, FlinkVersion.FLINK_112.getType())) {
                    return TableFactory.getSinkTable(dataSource.getDataTypeCode(), dataJson, paramJson, FlinkVersion.FLINK_112.getType()).getCreateSql();
                }
            case SOURCE:
                if (StringUtils.isNotBlank(componentVersion) && StringUtils.equalsIgnoreCase(componentVersion, FlinkVersion.FLINK_112.getType())) {
                    return TableFactory.getSourceTable(dataSource.getDataTypeCode(), dataJson, paramJson, FlinkVersion.FLINK_112.getType()).getCreateSql();
                }
            default:
                throw new DtCenterDefException(String.format("不支持的表类型:%s", tableType.getTableType()));
        }
    }
}
