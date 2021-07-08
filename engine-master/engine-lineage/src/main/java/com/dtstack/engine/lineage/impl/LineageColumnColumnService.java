package com.dtstack.engine.lineage.impl;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.vo.lineage.param.DeleteLineageParam;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.dao.LineageColumnColumnUniqueKeyRefDao;
import com.dtstack.engine.dao.LineageColumnColumnDao;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chener
 * @Classname LineageColumnColumnService
 * @Description TODO
 * @Date 2020/10/29 15:57
 * @Created chener@dtstack.com
 */
@Service
public class LineageColumnColumnService {

    private static final String COLUMN_COLUMN_KEY_TMP = "%s.%s_%s.%s";


    @Autowired
    private LineageColumnColumnDao lineageColumnColumnDao;

    @Autowired
    private LineageColumnColumnUniqueKeyRefDao lineageColumnColumnUniqueKeyRefDao;


    public void saveColumnLineage(Integer versionId,Integer type,List<LineageColumnColumn> columnColumns, String uniqueKey) {
        if (CollectionUtils.isEmpty(columnColumns)) {
            return;
        }
        for (LineageColumnColumn columnColumn : columnColumns) {
            columnColumn.setColumnLineageKey(generateColumnColumnKey(columnColumn));
        }
        if(type !=null && EScheduleType.TEMP_JOB.getType() == type){
            //临时运行,先查询,原来存在则不新增，否则新增
            for (LineageColumnColumn columnColumn : columnColumns) {
                LineageColumnColumn column = lineageColumnColumnDao.queryByLineageKey(columnColumn.getAppType(), columnColumn.getColumnLineageKey());
                if(null == column){
                    lineageColumnColumnDao.batchInsertColumnColumn(Collections.singletonList(columnColumn));
                }
                String finalUniqueKey = StringUtils.isEmpty(uniqueKey) ? generateDefaultUniqueKey(columnColumn.getAppType()) : uniqueKey;
                LineageColumnColumnUniqueKeyRef ref = new LineageColumnColumnUniqueKeyRef();
                ref.setAppType(columnColumn.getAppType());
                ref.setLineageColumnColumnId(columnColumn.getId());
                ref.setUniqueKey(finalUniqueKey);
                lineageColumnColumnUniqueKeyRefDao.batchInsert(Collections.singletonList(ref));
            }
        }else {
            //1.存入或者更新lineageColumnColumn表
            lineageColumnColumnDao.batchInsertColumnColumn(columnColumns);
            Set<String> columnLineageKeys = columnColumns.stream().map(LineageColumnColumn::getColumnLineageKey).collect(Collectors.toSet());
            columnColumns = queryByColumnLineageKeys(columnColumns.get(0).getAppType(), columnLineageKeys);
            //2.不是同一批scheduleJob,删除uniqueKey对应批次的ref，插入新的ref
            if (StringUtils.isEmpty(uniqueKey)) {
                uniqueKey = generateDefaultUniqueKey(columnColumns.get(0).getAppType());
            } else {
                //资产没有uniqueKey，不能删除。
                lineageColumnColumnUniqueKeyRefDao.deleteByUniqueKeyAndVersionId(uniqueKey,versionId);
            }
            String finalUniqueKey = uniqueKey;
            List<LineageColumnColumnUniqueKeyRef> refList = columnColumns.stream().map(cc -> {
                LineageColumnColumnUniqueKeyRef ref = new LineageColumnColumnUniqueKeyRef();
                ref.setAppType(cc.getAppType());
                ref.setLineageColumnColumnId(cc.getId());
                ref.setUniqueKey(finalUniqueKey);
                ref.setVersionId(versionId);
                return ref;
            }).collect(Collectors.toList());
            lineageColumnColumnUniqueKeyRefDao.batchInsert(refList);
        }
    }

    private List<LineageColumnColumn> queryByColumnLineageKeys(Integer appType, Set<String> columnLineageKeys) {
        return lineageColumnColumnDao.queryByLineageKeys(appType, columnLineageKeys);
    }

    public List<LineageColumnColumn> queryColumnInputLineageByAppType(Integer appType, Long tableId, String columnName, Set<Long> columnSet,Integer level) {
        List<LineageColumnColumn> res = Lists.newArrayList();
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnDao.queryColumnResultList(appType, tableId, columnName);
        lineageColumnColumns = lineageColumnColumns.stream().filter(tt -> !columnSet.contains(tt.getId())).collect(Collectors.toList());
        for (LineageColumnColumn tt : lineageColumnColumns) {
            columnSet.add(tt.getId());
        }
        res.addAll(lineageColumnColumns);
        if(level<=1){
            return res;
        }
        if (CollectionUtils.isNotEmpty(lineageColumnColumns)) {
            level --;
            for (LineageColumnColumn columnColumn : lineageColumnColumns) {
                List<LineageColumnColumn> parentColumnineages = queryColumnInputLineageByAppType(appType, columnColumn.getInputTableId(), columnColumn.getInputColumnName(), columnSet,level);
                res.addAll(parentColumnineages);
            }
        }
        return res;
    }


    public List<LineageColumnColumn> queryColumnResultLineageByAppType(Integer appType, Long tableId, String columnName, Set<Long> columnSet,Integer level) {
        List<LineageColumnColumn> res = Lists.newArrayList();
        //查询时，如果血缘没有关联ref，则不能被查出
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnDao.queryColumnInputList(appType, tableId, columnName);
        lineageColumnColumns = lineageColumnColumns.stream().filter(tt -> !columnSet.contains(tt.getId())).collect(Collectors.toList());
        for (LineageColumnColumn tt : lineageColumnColumns) {
            columnSet.add(tt.getId());
        }
        res.addAll(lineageColumnColumns);
        if(level<=1){
            return res;
        }
        if (CollectionUtils.isNotEmpty(lineageColumnColumns)) {
            level--;
            for (LineageColumnColumn columnColumn : lineageColumnColumns) {
                List<LineageColumnColumn> parentColumnineages = queryColumnResultLineageByAppType(appType, columnColumn.getResultTableId(), columnColumn.getResultColumnName(), columnSet,level);
                res.addAll(parentColumnineages);
            }
        }
        return res;
    }

    public List<LineageColumnColumn> queryColumnLineages(Integer appType, Long tableId, String columnName,Integer level) {
        List<LineageColumnColumn> inputLineages = queryColumnInputLineageByAppType(appType, tableId, columnName, new HashSet<>(),level);
        List<LineageColumnColumn> resultLineages = queryColumnResultLineageByAppType(appType, tableId, columnName, new HashSet<>(),level);
        Set<LineageColumnColumn> lineageSet = Sets.newHashSet();
        lineageSet.addAll(inputLineages);
        lineageSet.addAll(resultLineages);
        return Lists.newArrayList(lineageSet);
    }

    public void manualAddColumnLineage(Integer appType, LineageColumnColumn lineageColumnColumn, String uniqueKey, Integer lineageSource) {
        if (Objects.isNull(lineageSource)) {
            lineageColumnColumn.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        }
        if (StringUtils.isEmpty(lineageColumnColumn.getColumnLineageKey())) {
            lineageColumnColumn.setColumnLineageKey(generateColumnColumnKey(lineageColumnColumn));
        }
        lineageColumnColumnDao.batchInsertColumnColumn(Lists.newArrayList(lineageColumnColumn));
        if (StringUtils.isEmpty(uniqueKey)) {
            uniqueKey = generateDefaultUniqueKey(appType);
        }
        LineageColumnColumnUniqueKeyRef ref = new LineageColumnColumnUniqueKeyRef();
        ref.setAppType(appType);
        ref.setUniqueKey(uniqueKey);
        ref.setLineageColumnColumnId(lineageColumnColumn.getId());
        lineageColumnColumnUniqueKeyRefDao.batchInsert(Lists.newArrayList(ref));
    }

    public void manualDeleteColumnLineage(Integer appType, LineageColumnColumn lineageColumnColumn, String uniqueKey) {
        //只删除ref表
        String columnLineageKey = generateColumnColumnKey(lineageColumnColumn);
        LineageColumnColumn columnColumn = lineageColumnColumnDao.queryByLineageKey(appType, columnLineageKey);
        if (Objects.isNull(columnColumn)) {
            throw new RdosDefineException("血缘关系未查到");
        }
        if (StringUtils.isEmpty(uniqueKey)) {
            uniqueKey = generateDefaultUniqueKey(appType);
        }
        lineageColumnColumnUniqueKeyRefDao.deleteByLineageIdAndUniqueKey(appType, uniqueKey, columnColumn.getId());
    }

    private String generateColumnColumnKey(LineageColumnColumn columnColumn) {
        String rawKey = String.format(COLUMN_COLUMN_KEY_TMP, columnColumn.getInputTableId(), columnColumn.getInputColumnName(), columnColumn.getResultTableId(), columnColumn.getResultColumnName());
        return MD5Util.getMd5String(rawKey);
    }

    /**
     * 生成默认uniqueKey
     * @param appType
     * @return
     */
    public String generateDefaultUniqueKey(Integer appType){
        AppType value = AppType.getValue(appType);
        if (Objects.nonNull(value)){
            return value.name();
        }
        return "APP_TYPE_"+appType;
    }

    /**
     * 查询表的上游血缘字段
     * @return
     */
    public List<String> queryTableInputLineageColumns(Long tableId){
        return lineageColumnColumnDao.queryTableLineageInputColumns(tableId);
    }

    /**
     * 查询表的下游血缘字段
     * @return
     */
    public List<String> queryTableResultLineageColumns(Long tableId){
        return lineageColumnColumnDao.queryTableLineageResultColumns(tableId);
    }

    /**
     * 根据taskId和appType查询字段血缘
     * @param taskId
     * @param appType
     * @return
     */
    public List<LineageColumnColumn> queryColumnLineageByTaskIdAndAppType(Long taskId, Integer appType) {

        return lineageColumnColumnDao.queryColumnLineageByTaskIdAndAppType(taskId,appType);
    }

    public void deleteLineageByTaskIdAndAppType(DeleteLineageParam deleteLineageParam) {

        lineageColumnColumnUniqueKeyRefDao.deleteByUniqueKeyAndAppType(String.valueOf(deleteLineageParam.getTaskId()),deleteLineageParam.getAppType());
    }
}
