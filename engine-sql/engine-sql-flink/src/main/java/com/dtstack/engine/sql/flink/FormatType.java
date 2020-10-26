package com.dtstack.engine.sql.flink;

public enum FormatType {
    //Indicates that the data is in nest json format(default)
    DT_NEST,
    //Indicates that the data is in json format
    JSON,
    //Indicates that the data is in avro format
    AVRO,
    //Indicates that the data is in csv format
    CSV
}
