package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.plugin.common.convert.ColumnTypeConverter;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表操作工具
 *
 * @author ：wangchuan
 * date：Created in 11:56 上午 2021/9/2
 * company: www.dtstack.com
 */
public class TableUtil {

    /**
     * 获取分区字段
     *
     * @param columnMetaDTOS 全部字段
     * @return 分区字段信息
     */
    public static List<ColumnMetaDTO> getPartitionColumns(List<ColumnMetaDTO> columnMetaDTOS) {
        if (CollectionUtils.isEmpty(columnMetaDTOS)) {
            return Lists.newArrayList();
        }
        return columnMetaDTOS.stream().filter(ColumnMetaDTO::getPart).collect(Collectors.toList());
    }

    /**
     * 过滤分区字段
     *
     * @param columnMetaDTOS           全部字段
     * @param isFilterPartitionColumns 是否过滤分区字段
     * @return 过滤后的字段信息
     */
    public static List<ColumnMetaDTO> filterPartitionColumns(List<ColumnMetaDTO> columnMetaDTOS, boolean isFilterPartitionColumns) {
        if (CollectionUtils.isEmpty(columnMetaDTOS)) {
            return Lists.newArrayList();
        }
        if (!isFilterPartitionColumns) {
            return columnMetaDTOS;
        }
        return columnMetaDTOS.stream().filter(ColumnMetaDTO::getPart).collect(Collectors.toList());
    }

    /**
     * 处理字段类型
     *
     * @param columnMetaDTOS 字段、类型集合
     * @param converter      转化器
     * @return 转化后的类型
     */
    public static List<ColumnMetaDTO> dealColumnType(List<ColumnMetaDTO> columnMetaDTOS, ColumnTypeConverter converter) {
        for (ColumnMetaDTO columnMetaDTO : columnMetaDTOS) {
            columnMetaDTO.setType(converter.convert(columnMetaDTO.getType()));
        }
        return columnMetaDTOS;
    }
}
