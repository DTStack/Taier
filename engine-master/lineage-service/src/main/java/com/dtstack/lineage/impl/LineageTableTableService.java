package com.dtstack.lineage.impl;

import com.dtstack.lineage.dao.LineageTableTableAppRefDao;
import com.dtstack.lineage.dao.LineageTableTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chener
 * @Classname LineageTableTableService
 * @Description TODO
 * @Date 2020/10/29 15:57
 * @Created chener@dtstack.com
 */
@Service
public class LineageTableTableService {
    @Autowired
    private LineageTableTableDao lineageTableTableDao;

    @Autowired
    private LineageTableTableAppRefDao lineageTableTableAppRefDao;

    /**
     * 保存表级血缘
     */
    public void saveTableTableLineage(){

    }

    /**
     * 根据表和应用类型查询表级血缘上游
     */
    public void queryTableInputLineageByAppType(Long tableId,Integer appType){
        
    }

    /**
     * 根据表和应用类型查询表级血缘下游
     * @param tableId
     * @param appType
     */
    public void queryTableResultLineageByAppType(Long tableId,Integer appType){

    }

    /**
     * 查询表血缘关系(全应用)
     */
    public void selectTableTableByTable(){

    }


}
