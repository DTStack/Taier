package com.dtstack.engine.lineage.sourcekey;

import com.dtstack.schedule.common.enums.DataSourceType;

/**
 * @Author: ZYD
 * Date: 2021/2/3 18:00
 * Description: 获取sourceKey生成器
 * @since 1.0.0
 */
public class SourceKeyGeneratorHelper {


    public static AbstractSourceKeyGenerator getSourceKeyGenerator(Integer sourceType){

        if(DataSourceType.Phoenix.getVal() == sourceType || DataSourceType.PHOENIX5.getVal() == sourceType
            || DataSourceType.HBASE.getVal() == sourceType){
            return new ClusterSourceKeyGenerator();
        }else{
            return new NormalSourceKeyGenerator();
        }
    }

}
