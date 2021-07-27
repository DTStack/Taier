package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.api.enums.TableOperateEnum;
import com.dtstack.engine.api.vo.lineage.AlterResult;

/**
 * @Author: ZYD
 * Date: 2021/4/2 10:24
 * Description: alter解析结果转换器
 * @since 1.0.0
 */
public class AlterResultAdapter {


    public static AlterResult sqlAlterResult2ApiResult(com.dtstack.sqlparser.common.client.domain.AlterResult alterResult){

        if(null == alterResult){
            return null;
        }
        AlterResult aResult = new AlterResult();
        aResult.setNewDB(alterResult.getNewDB());
        aResult.setOldDB(alterResult.getOldDB());
        aResult.setNewTableName(alterResult.getNewTableName());
        aResult.setOldTableName(alterResult.getOldTableName());
        aResult.setNewLocation(alterResult.getNewLocation());
        aResult.setSerdeProperties(alterResult.getSerdeProperties());
        aResult.setTableProperties(alterResult.getTableProperties());
        aResult.setAlterType(TableOperateEnum.valueOf(alterResult.getAlterType().name()));
        aResult.setNewLocationPart(alterResult.getNewLocationPart());
        return aResult;
    }

}
