package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.domain.LineageTableTableUniqueKeyRef;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.lineage.dao.LineageTableTableUniqueKeyRefDao;
import com.dtstack.lineage.dao.LineageTableTableDao;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
    public void saveTableLineage(List<LineageTableTable> tableTables,String uniqueKey) {
        if (CollectionUtils.isEmpty(tableTables)){
            return;
        }
        tableTables.forEach(tt->{tt.setTableLineageKey(generateTableTableKey(tt));});
        //数据插入后，id会更新
        lineageTableTableDao.batchInsertTableTable(tableTables);
        List<String> keys = tableTables.stream().map(tt -> tt.getTableLineageKey()).collect(Collectors.toList());
        tableTables = getTableTablesByTableLineageKeys(tableTables.get(0).getAppType(), keys);
        //如果uniqueKey不为空，需要删除ref表中相同uniqueKey的数据，再插入该批数据。
        if (StringUtils.isNotEmpty(uniqueKey)){
            lineageTableTableUniqueKeyRefDao.deleteByUniqueKey(tableTables.get(0).getAppType(),uniqueKey);
        }
        //插入新的ref
        List<LineageTableTableUniqueKeyRef> refList = tableTables.stream().map(tt -> {
            LineageTableTableUniqueKeyRef ref = new LineageTableTableUniqueKeyRef();
            ref.setAppType(tt.getAppType());
            ref.setUniqueKey(uniqueKey);
            ref.setLineageTableTableId(tt.getId());
            return ref;
        }).collect(Collectors.toList());
        lineageTableTableUniqueKeyRefDao.batchInsert(refList);
    }

    /**
     * 根据表和应用类型查询表级血缘上游
     */
    public List<LineageTableTable> queryTableInputLineageByAppType(Long tableId, Integer appType) {
        List<LineageTableTable> res = Lists.newArrayList();
        List<LineageTableTable> lineageTableTables = lineageTableTableDao.queryTableResultList(appType, tableId);
        res.addAll(lineageTableTables);
        if (CollectionUtils.isNotEmpty(lineageTableTables)){
            for (LineageTableTable tt:lineageTableTables){
                //TODO 未处理死循环
                List<LineageTableTable> parentList = queryTableInputLineageByAppType(tt.getInputTableId(), appType);
                res.addAll(parentList);
            }
        }
        return res;
    }

    /**
     * 根据表和应用类型查询表级血缘下游
     *
     * @param tableId
     * @param appType
     */
    public List<LineageTableTable> queryTableResultLineageByAppType(Long tableId, Integer appType) {
        List<LineageTableTable> res = Lists.newArrayList();
        List<LineageTableTable> lineageTableTables = lineageTableTableDao.queryTableInputList(appType, tableId);
        res.addAll(lineageTableTables);
        if (CollectionUtils.isNotEmpty(lineageTableTables)){
            for (LineageTableTable tt:lineageTableTables){
                //TODO 未处理死循环
                List<LineageTableTable> parentList = queryTableResultLineageByAppType(tt.getResultTableId(), appType);
                res.addAll(parentList);
            }
        }
        return res;
    }

    /**
     * 查询表血缘关系(全应用)
     */
    public List<LineageTableTable> queryTableTableByTableAndAppId(Integer appType, Long tableId) {
        List<LineageTableTable> inputLineages = queryTableInputLineageByAppType(tableId, appType);
        List<LineageTableTable> resultLineages = queryTableResultLineageByAppType(tableId, appType);
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
    public void manualAddTableLineage(Integer appType, LineageTableTable lineageTableTable,String uniqueKey){
        //需要确保表存在
        //添加血缘
        //添加血缘ref
        lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        if (StringUtils.isEmpty(uniqueKey)){
            lineageTableTable.setTableLineageKey(generateTableTableKey(lineageTableTable));
        }
        //TODO 处理好tableKey
        lineageTableTableDao.batchInsertTableTable(Lists.newArrayList(lineageTableTable));
        Long lineageTableTableId = lineageTableTable.getId();
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
        lineageTableTableUniqueKeyRefDao.deleteByLineageTableIdAndUniqueKey(appType,uniqueKey,lineageTableTable.getId());
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
        if (AppType.RDOS.getType() == appType){
            return AppType.RDOS.name();
        }
        if (AppType.DQ.getType() == appType){
            return AppType.DQ.name();
        }
        return UUID.randomUUID().toString();
    }

    private List<LineageTableTable> getTableTablesByTableLineageKeys(Integer appType,List<String> tableLineageKeys){
        return lineageTableTableDao.queryByTableLineageKeys(appType, tableLineageKeys);
    }
}
