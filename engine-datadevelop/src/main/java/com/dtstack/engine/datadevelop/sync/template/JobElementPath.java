package com.dtstack.batch.sync.template;

public interface JobElementPath {

    String JOB = "$.job";
    String SETTING = "$.job.setting";
    String CONTENT_ARRAY = "$.job.content";
    String CONTENT_FIRST = "$.job.content[0]";
    String READER = "$.job.content[0].reader";
    String READER_NAME = "$.job.content[0].reader.name";
    String READER_PARAMETER = "$.job.content[0].reader.parameter";
    String WRITER = "$.job.content[0].writer";
    String WRITER_NAME = "$.job.content[0].writer.name";
    String WRITER_PARAMETER = "$.job.content[0].writer.parameter";
    String SPEED = "$.job.setting.speed.bytes";
    String CHANNEL = "$.job.setting.speed.channel";
    String RECORD = "$.job.setting.errorLimit.record";
    String PERCENTAGE = "$.job.setting.errorLimit.percentage";
    String DIRTY = "$.job.setting.dirty";
    String DIRTY_PATH = "$.job.setting.dirty.path";
    String DIRTY_HADOOP_CONFIG = "$.job.setting.dirty.hadoopConfig";
}
