package com.dtstack.engine.lineage.impl;


import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.domain.LineageTableTableUniqueKeyRef;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.LevelAndCount;
import com.dtstack.engine.api.vo.lineage.param.DeleteLineageParam;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.LineageTableTableUniqueKeyRefDao;
import com.dtstack.engine.dao.LineageTableTableDao;
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
 * @Classname LineageTableTableService
 * @Description TODO
 * @Date 2020/10/29 15:57
 * @Created chener@dtstack.com
 */
@Service
public class LineageTableTableService {

    /**
     * 表级血缘唯一码规则inputTableId_resultTable_id
     */
    private static final String TABLE_TABLE_KEY_TMP = "%s_%s";

    @Autowired
    private LineageDataSetInfoService dataSetInfoService;

    @Autowired
    private LineageTableTableDao lineageTableTableDao;

    @Autowired
    private LineageTableTableUniqueKeyRefDao lineageTableTableUniqueKeyRefDao;


    /**
     * 保存表级血缘,不需要事务
     */
    public void saveTableLineage(Integer versionId,Integer type,List<LineageTableTable> tableTables,String uniqueKey) {
        if (CollectionUtils.isEmpty(tableTables)){
            return;
        }
        tableTables.forEach(tt -> {
            tt.setTableLineageKey(generateTableTableKey(tt));
        });
        if(type!=null && type.equals(EScheduleType.TEMP_JOB.getType())){
            //临时运行,先查询,原来存在则不新增，否则新增
            for (LineageTableTable tableTable : tableTables) {
                LineageTableTable lineageTableTable = lineageTableTableDao.queryBTableLineageKey(tableTable.getAppType(), tableTable.getTableLineageKey());
                if(null == lineageTableTable){
                    lineageTableTableDao.batchInsertTableTable(Collections.singletonList(tableTable));
                    String finalUniqueKey = StringUtils.isEmpty(uniqueKey) ? generateDefaultUniqueKey(tableTable.getAppType()) : uniqueKey;
                    LineageTableTableUniqueKeyRef ref = new LineageTableTableUniqueKeyRef();
                    ref.setAppType(tableTable.getAppType());
                    ref.setUniqueKey(finalUniqueKey);
                    ref.setLineageTableTableId(tableTable.getId());
                    lineageTableTableUniqueKeyRefDao.batchInsert(Collections.singletonList(ref));
                }
            }
        }else {
            //数据插入后，id会更新
            lineageTableTableDao.batchInsertTableTable(tableTables);
            List<String> keys = tableTables.stream().map(LineageTableTable::getTableLineageKey).collect(Collectors.toList());
            tableTables = getTableTablesByTableLineageKeys(tableTables.get(0).getAppType(), keys);
            //如果uniqueKey不为空,且不是同一个scheduleJob，需要删除ref表中相同uniqueKey的数据，再插入该批数据。
            if (StringUtils.isNotEmpty(uniqueKey)) {
                lineageTableTableUniqueKeyRefDao.deleteByUniqueKeyAndVersionId(tableTables.get(0).getAppType(), uniqueKey,versionId);
            }
            //插入新的ref
            String finalUniqueKey = StringUtils.isEmpty(uniqueKey) ? generateDefaultUniqueKey(tableTables.get(0).getAppType()) : uniqueKey;
            List<LineageTableTableUniqueKeyRef> refList = tableTables.stream().map(tt -> {
                LineageTableTableUniqueKeyRef ref = new LineageTableTableUniqueKeyRef();
                ref.setAppType(tt.getAppType());
                ref.setUniqueKey(finalUniqueKey);
                ref.setLineageTableTableId(tt.getId());
                return ref;
            }).collect(Collectors.toList());
            lineageTableTableUniqueKeyRefDao.batchInsert(refList);
        }
    }

    /**
     * 查询表血缘直接上游数量
     * @param tableId
     * @param appType
     * @return
     */
    public Integer queryTableInputLineageDirectCount(Long tableId, Integer appType){

        return lineageTableTableDao.queryTableResultCount(appType, tableId);
    }

    /**
     * 根据表和应用类型查询表级血缘上游
     */
    public List<LineageTableTable> queryTableInputLineageByAppType(Long tableId, Integer appType, final Set<String> tableIdSet, LevelAndCount lc) {
        Integer level = lc.getLevelCount();
        List<LineageTableTable> res = Lists.newArrayList();
        List<LineageTableTable> lineageTableTables = lineageTableTableDao.queryTableResultList(appType, tableId);

        lineageTableTables = lineageTableTables.stream().filter(tt-> !tableIdSet.contains(tt.getInputTableId() + "-" + tt.getResultTableId())).collect(Collectors.toList());
        for (LineageTableTable tt :lineageTableTables) {
            tableIdSet.add(tt.getInputTableId()+"-"+tt.getResultTableId());
        }
        res.addAll(lineageTableTables);
        if(level<=1){
            return res;
        }
        if (CollectionUtils.isNotEmpty(lineageTableTables)){
            level -- ;
            lc.setLevelCount(level);
            for (LineageTableTable tt:lineageTableTables){
                List<LineageTableTable> parentList = queryTableInputLineageByAppType(tt.getInputTableId(), appType,tableIdSet,lc);
                res.addAll(parentList);
            }
        }
        return res;
    }


    /**
     * 查询表血缘直接下游数量
     * @param tableId
     * @param appType
     * @return
     */
    public Integer queryTableResultLineageDirectCount(Long tableId, Integer appType){

        return lineageTableTableDao.queryTableInputCount(appType, tableId);
    }

    /**
     * 根据表和应用类型查询表级血缘下游
     * @param tableId
     * @param appType
     */
    public List<LineageTableTable> queryTableResultLineageByAppType(Long tableId, Integer appType,final Set<String> tableIdSet,LevelAndCount lv) {
        List<LineageTableTable> res = Lists.newArrayList();
        Integer level = lv.getLevelCount();
        List<LineageTableTable> lineageTableTables = lineageTableTableDao.queryTableInputList(appType, tableId);
        lineageTableTables = lineageTableTables.stream().filter(tt-> !tableIdSet.contains(tt.getInputTableId() + "-" + tt.getResultTableId())).collect(Collectors.toList());
        for (LineageTableTable tt :lineageTableTables) {
            tableIdSet.add(tt.getInputTableId()+"-"+tt.getResultTableId());
        }
        res.addAll(lineageTableTables);
        if(level<=1){
            return res;
        }
        if (CollectionUtils.isNotEmpty(lineageTableTables)){
            level --;
            lv.setLevelCount(level);
            for (LineageTableTable tt:lineageTableTables){
                List<LineageTableTable> parentList = queryTableResultLineageByAppType(tt.getResultTableId(), appType,tableIdSet,lv);
                res.addAll(parentList);
            }
        }
        return res;
    }

    /**
     * 查询表血缘关系
     */
    public List<LineageTableTable> queryTableTableByTableAndAppId(Integer appType, Long tableId,Integer level) {
        LevelAndCount levelAndCount = new LevelAndCount();
        levelAndCount.setLevelCount(level);
        List<LineageTableTable> inputLineages = queryTableInputLineageByAppType(tableId, appType,new HashSet<>(),levelAndCount);
        List<LineageTableTable> resultLineages = queryTableResultLineageByAppType(tableId, appType,new HashSet<>(),levelAndCount);
        Set<LineageTableTable> lineageSet = Sets.newHashSet();
        lineageSet.addAll(inputLineages);
        lineageSet.addAll(resultLineages);
        return Lists.newArrayList(lineageSet);
    }

    /**
     * 手动添加血缘关系
     * @param appType
     * @param lineageTableTable
     */
    public void manualAddTableLineage(Integer appType, LineageTableTable lineageTableTable,String uniqueKey,Integer lineageSource){
        //需要确保表存在
        //添加血缘
        //添加血缘ref
        if (Objects.isNull(lineageSource)){
            lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        }
        if (StringUtils.isEmpty(lineageTableTable.getTableLineageKey())){
            lineageTableTable.setTableLineageKey(generateTableTableKey(lineageTableTable));
        }
        //TODO 处理好tableKey
        lineageTableTableDao.batchInsertTableTable(Lists.newArrayList(lineageTableTable));
        Long lineageTableTableId = lineageTableTable.getId();
        if (StringUtils.isEmpty(uniqueKey)){
            uniqueKey = generateDefaultUniqueKey(appType);
        }
        LineageTableTableUniqueKeyRef ref = new LineageTableTableUniqueKeyRef();
        ref.setAppType(appType);
        ref.setLineageTableTableId(lineageTableTableId);
        ref.setUniqueKey(uniqueKey);
        lineageTableTableUniqueKeyRefDao.batchInsert(Lists.newArrayList(ref));
    }

    /**
     * 手动删除血缘关系
     * @param appType
     * @param lineageTableTable
     */
    public void manualDeleteTableLineage(Integer appType, LineageTableTable lineageTableTable,String uniqueKey){
        String tableLineageKey = generateTableTableKey(lineageTableTable);
        LineageTableTable tableTable = lineageTableTableDao.queryBTableLineageKey(appType, tableLineageKey);
        if (Objects.isNull(tableTable)){
            throw new RdosDefineException("未找到血缘关系");
        }
        if (Objects.isNull(uniqueKey)){
            uniqueKey = generateDefaultUniqueKey(appType);
        }
        //只需要删除关联关系即可
        lineageTableTableUniqueKeyRefDao.deleteByLineageTableIdAndUniqueKey(appType,uniqueKey,tableTable.getId());
    }

    /**
     * 计算tableTableKey。逻辑表唯一。
     * 计算规则：
     *
     * @param tableTable
     * @return
     */
    public String generateTableTableKey(LineageTableTable tableTable) {
        return String.format(TABLE_TABLE_KEY_TMP, tableTable.getInputTableId(), tableTable.getResultTableId());
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

    private List<LineageTableTable> getTableTablesByTableLineageKeys(Integer appType,List<String> tableLineageKeys){
        return lineageTableTableDao.queryByTableLineageKeys(appType, tableLineageKeys);
    }


    public List<LineageTableTable> queryTableLineageByTaskIdAndAppType(Long taskId, Integer appType) {

        return lineageTableTableDao.queryTableLineageByTaskIdAndAppType(taskId, appType);
    }


    /**
     * 根据任务id和appType删除血缘
     * @param deleteLineageParam
     */
    public void deleteLineageByTaskIdAndAppType(DeleteLineageParam deleteLineageParam) {

        lineageTableTableUniqueKeyRefDao.deleteByUniqueKey(deleteLineageParam.getAppType(),String.valueOf(deleteLineageParam.getTaskId()));
    }
}
