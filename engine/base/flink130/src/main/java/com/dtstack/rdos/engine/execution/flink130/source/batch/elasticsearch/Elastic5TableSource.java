package com.dtstack.rdos.engine.execution.flink130.source.batch.elasticsearch;

import com.dtstack.rdos.engine.execution.flink130.source.stream.elasticsearch.Elastic5InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.sources.BatchTableSource;

import java.util.List;

/**
 * 自定义flink 读取 elastic5做为数据的 batchTable
 * Date: 2017/7/26
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class Elastic5TableSource implements BatchTableSource {

    private String cluster;

    /**格式ip:port*/
    private List<String> addressList;

    private String index;

    private String query;

    private TypeInformation<?>[] fieldTypes;

    private String[] fieldNames;

    private int splitNum = 1;

    public Elastic5TableSource(String cluster, String index, List<String> addressList,
                               String query, TypeInformation<?>[] fieldTypes, String[] fieldNames){
        this.cluster = cluster;
        this.addressList = addressList;
        this.index = index;
        this.query = query;
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
    }

    @Override
    public TypeInformation getReturnType() {
        return new RowTypeInfo(fieldTypes, fieldNames);
    }

    @Override
    public String explainSource() {
        return "";
    }

    @Override
    public DataSet getDataSet(ExecutionEnvironment execEnv) {

        Elastic5InputFormat.Elastic5InputFormatBuilder builder = Elastic5InputFormat.buildElastic5InputFormat();
        builder.setCluster(cluster);
        builder.setIndex(index);
        builder.setQuery(query);
        builder.setSplitNum(splitNum);
        RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);
        builder.setRowTypeInfo(rowTypeInfo);
        builder.setRowFieldNames(fieldNames);
        builder.setHosts(addressList);

        Elastic5InputFormat elastic5InputFormat = builder.finish();
        return execEnv.createInput(elastic5InputFormat);
    }

    public void setSplitNum(int splitNum) {
        this.splitNum = splitNum;
    }
}
