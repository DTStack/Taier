package com.dtstack.batch.engine.core.domain;

import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Date: 2019/5/29
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class MultiEngineFactory {

    public static EngineInfo createEngineInfo(int engineType) {
        if (engineType == MultiEngineType.HADOOP.getType()) {
            return new HadoopEngineInfo();
        } else if (engineType == MultiEngineType.LIBRA.getType()) {
            return new LibraEngineInfo();
        } else if (engineType == MultiEngineType.TIDB.getType()) {
            return new TiDBEngineInfo();
        } else if (engineType == MultiEngineType.ORACLE.getType()) {
            return new OracleEngineInfo();
        }
        return null;
    }

    /**
     * 根据engineType获取 所有的支持的组件
     *
     * @param engineTypeList
     * @return
     */
    public static List<EComponentType> getComponentTypeByEngineType(List<Integer> engineTypeList) {
        List<EComponentType> list = Lists.newArrayList();
        list.add(EComponentType.FLINK);
        list.add(EComponentType.DT_SCRIPT);

        if (CollectionUtils.isEmpty(engineTypeList)){
            return list;
        }
        if (engineTypeList.contains(MultiEngineType.HADOOP.getType())){
            list.add(EComponentType.YARN);
            list.add(EComponentType.HDFS);
            list.add(EComponentType.SPARK_THRIFT);
            list.add(EComponentType.SPARK);
            list.add(EComponentType.HIVE_SERVER);
            list.add(EComponentType.IMPALA_SQL);
            list.add(EComponentType.INCEPTOR_SQL);
        }
        if (engineTypeList.contains(MultiEngineType.LIBRA.getType())){
            list.add(EComponentType.LIBRA_SQL);
        }

        if (engineTypeList.contains(MultiEngineType.TIDB.getType())){
            list.add(EComponentType.TIDB_SQL);
        }
        if (engineTypeList.contains(MultiEngineType.ORACLE.getType())){
            list.add(EComponentType.ORACLE_SQL);
        }
        return list;
    }

}
