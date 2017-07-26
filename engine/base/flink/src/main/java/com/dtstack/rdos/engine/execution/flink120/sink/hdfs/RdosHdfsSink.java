package com.dtstack.rdos.engine.execution.flink120.sink.hdfs;

import com.dtstack.rdos.engine.execution.base.operator.stream.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.IStreamSinkGener;
import org.apache.flink.util.Preconditions;

import java.util.Properties;

/**
 * Created by softfly on 17/7/3.
 */
public class RdosHdfsSink extends HdfsSink implements IStreamSinkGener<RdosHdfsSink> {

    @Override
    public RdosHdfsSink genStreamSink(BatchCreateResultOperator operator) {
        Properties properties = operator.getProperties();
        this.defaultFS = Preconditions.checkNotNull(properties.getProperty(HDFS_DEFAULT_FS),
                "Should speficy hdfs defaultFS");
        this.fileType = Preconditions.checkNotNull(properties.getProperty(HDFS_FILETYPE),
                "Should speficy hdfs filetype");
        this.path = Preconditions.checkNotNull(properties.getProperty(HDFS_PATH),
                "Should speficy hdfs path");
        this.tableName = Preconditions.checkNotNull(operator.getName(),
                "Should specify table name");

        this.fullFieldNames = Preconditions.checkNotNull(operator.getFields(),
                "Should specify full field names");

        this.fullFieldTypes = Preconditions.checkNotNull(operator.getFieldTypes(),
                "Should specify full field types");

        this.delimiter = properties.getProperty(HDFS_DELIMITER);

        this.compress = properties.getProperty(HDFS_COMPRESS);

        return this;
    }

}
