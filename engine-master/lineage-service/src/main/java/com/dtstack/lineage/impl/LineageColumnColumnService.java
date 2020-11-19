package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageColumnColumnUniqueKeyRef;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.lineage.dao.LineageColumnColumnUniqueKeyRefDao;
import com.dtstack.lineage.dao.LineageColumnColumnDao;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
    private LineageDataSetInfoService lineageDataSetInfoService;

    @Autowired
    private LineageColumnColumnDao lineageColumnColumnDao;

    @Autowired
    private LineageColumnColumnUniqueKeyRefDao lineageColumnColumnUniqueKeyRefDao;

    public void saveColumnLineage(List<LineageColumnColumn> columnColumns,String uniqueKey) {
        if (CollectionUtils.isEmpty(columnColumns)) {
            return;
        }
        //1.存入或者更新lineageColumnColumn表
        for (LineageColumnColumn columnColumn:columnColumns){
            columnColumn.setColumnLineageKey(generateColumnColumnKey(columnColumn));
        }
        lineageColumnColumnDao.batchInsertColumnColumn(columnColumns);
        Set<String> columnLineageKeys = columnColumns.stream().map(LineageColumnColumn::getColumnLineageKey).collect(Collectors.toSet());
        columnColumns = queryByColumnLineageKeys(columnColumns.get(0).getAppType(),columnLineageKeys);
        //2.删除uniqueKey对应批次的ref，插入新的ref
        lineageColumnColumnUniqueKeyRefDao.deleteByUniqueKey(uniqueKey);
        List<LineageColumnColumnUniqueKeyRef> refList = columnColumns.stream().map(cc -> {
            LineageColumnColumnUniqueKeyRef ref = new LineageColumnColumnUniqueKeyRef();
            ref.setAppType(cc.getAppType());
            ref.setLineageColumnColumnId(cc.getId());
            ref.setUniqueKey(uniqueKey);
            return ref;
        }).collect(Collectors.toList());
        lineageColumnColumnUniqueKeyRefDao.batchInsert(refList);
    }

    private List<LineageColumnColumn> queryByColumnLineageKeys(Integer appType, Set<String> columnLineageKeys) {
        return lineageColumnColumnDao.queryByLineageKeys(appType,columnLineageKeys);
    }

    public List<LineageColumnColumn> queryColumnInputLineageByAppType(Integer appType,Long tableId,String columnName,Set<String> columnSet) {
        List<LineageColumnColumn> res = Lists.newArrayList();
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnDao.queryColumnResultList(appType, tableId, columnName);
        lineageColumnColumns = lineageColumnColumns.stream().filter(tt->{
            if (columnSet.contains(generateColumnStr(tt.getInputTableId(),tt.getInputColumnName()))){
                return false;
            }
            if (columnSet.contains(generateColumnStr(tt.getResultTableId(),tt.getResultColumnName()))){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        for (LineageColumnColumn tt :lineageColumnColumns) {
            columnSet.add(generateColumnStr(tt.getResultTableId(),tt.getResultColumnName()));
            columnSet.add(generateColumnStr(tt.getInputTableId(),tt.getInputColumnName()));
        }
        res.addAll(lineageColumnColumns);
        if (CollectionUtils.isNotEmpty(lineageColumnColumns)){
            for (LineageColumnColumn columnColumn:lineageColumnColumns){
                List<LineageColumnColumn> parentColumnineages = queryColumnInputLineageByAppType(appType, columnColumn.getInputTableId(), columnColumn.getInputColumnName(),columnSet);
                res.addAll(parentColumnineages);
            }
        }
        return res;
    }

    private String generateColumnStr(Long tableId,String columnName){
        return String.format("%s.%s",tableId,columnName);
    }

    public List<LineageColumnColumn> queryColumnResultLineageByAppType(Integer appType,Long tableId,String columnName,Set<String> columnSet) {
        List<LineageColumnColumn> res = Lists.newArrayList();
        //查询时，如果血缘没有关联ref，则不能被查出
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnDao.queryColumnInputList(appType, tableId, columnName);
        lineageColumnColumns = lineageColumnColumns.stream().filter(tt->{
            if (columnSet.contains(generateColumnStr(tt.getInputTableId(),tt.getInputColumnName()))){
                return false;
            }
            if (columnSet.contains(generateColumnStr(tt.getResultTableId(),tt.getResultColumnName()))){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        for (LineageColumnColumn tt :lineageColumnColumns) {
            columnSet.add(generateColumnStr(tt.getResultTableId(),tt.getResultColumnName()));
            columnSet.add(generateColumnStr(tt.getInputTableId(),tt.getInputColumnName()));
        }
        res.addAll(lineageColumnColumns);
        if (CollectionUtils.isNotEmpty(lineageColumnColumns)){
            for (LineageColumnColumn columnColumn:lineageColumnColumns){
                List<LineageColumnColumn> parentColumnineages = queryColumnResultLineageByAppType(appType, columnColumn.getResultTableId(), columnColumn.getInputColumnName(),columnSet);
                res.addAll(parentColumnineages);
            }
        }
        return res;
    }

    public List<LineageColumnColumn> queryColumnLineages(Integer appType, Long tableId, String columnName){
        List<LineageColumnColumn> inputLineages = queryColumnInputLineageByAppType(appType,tableId,columnName,new HashSet<>());
        List<LineageColumnColumn> resultLineages = queryColumnResultLineageByAppType(appType,tableId,columnName,new HashSet<>());
        Set<LineageColumnColumn> lineageSet = Sets.newHashSet();
        lineageSet.addAll(inputLineages);
        lineageSet.addAll(resultLineages);
        return Lists.newArrayList(lineageSet);
    }

    public void manualAddColumnLineage(Integer appType, LineageColumnColumn lineageColumnColumn,String uniqueKey){
        //TODO 检查表信息lineageDataSetInfoService.getOne
        if (StringUtils.isEmpty(lineageColumnColumn.getColumnLineageKey())){
            lineageColumnColumn.setColumnLineageKey(generateColumnColumnKey(lineageColumnColumn));
        }
        lineageColumnColumnDao.batchInsertColumnColumn(Lists.newArrayList(lineageColumnColumn));
        if (StringUtils.isEmpty(uniqueKey)){
            uniqueKey = generateDefaultUniqueKey(appType);
        }
        LineageColumnColumnUniqueKeyRef ref = new LineageColumnColumnUniqueKeyRef();
        ref.setAppType(appType);
        ref.setUniqueKey(uniqueKey);
        ref.setLineageColumnColumnId(lineageColumnColumn.getId());
        lineageColumnColumnUniqueKeyRefDao.batchInsert(Lists.newArrayList(ref));
    }

    public void manualDeleteColumnLineage(Integer appType, LineageColumnColumn lineageColumnColumn,String uniqueKey ){
        //只删除ref表
        String columnLineageKey = generateColumnColumnKey(lineageColumnColumn);
        LineageColumnColumn columnColumn = lineageColumnColumnDao.queryByLineageKey(appType, columnLineageKey);
        if (Objects.isNull(columnColumn)){
            throw new RdosDefineException("血缘关系未查到");
        }
        if (StringUtils.isEmpty(uniqueKey)){
            uniqueKey = generateDefaultUniqueKey(appType);
        }
        lineageColumnColumnUniqueKeyRefDao.deleteByLineageIdAndUniqueKey(appType,uniqueKey,columnColumn.getId());
    }

    private String generateColumnColumnKey(LineageColumnColumn columnColumn) {
        String rawKey = String.format(COLUMN_COLUMN_KEY_TMP, columnColumn.getInputTableId(), columnColumn.getInputColumnName(), columnColumn.getResultTableId(), columnColumn.getResultColumnName());
        return MD5Util.getMd5String(rawKey);
    }

    public String generateDefaultUniqueKey(Integer appType){
        if (AppType.RDOS.getType() == appType){
            return AppType.RDOS.name();
        }
        if (AppType.DQ.getType() == appType){
            return AppType.DQ.name();
        }
        return UUID.randomUUID().toString();
    }
}
