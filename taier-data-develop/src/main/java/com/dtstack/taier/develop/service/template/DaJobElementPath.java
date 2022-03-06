package com.dtstack.taier.develop.service.template;

/**
 * @author sanyue
 * @date 2018/9/19
 */
public interface DaJobElementPath {
    String JOB = "$.job";
    String SETTING = "$.job.setting";
    String CONTENT_ARRAY = "$.job.content";
    String CONTENT_FIRST = "$.job.content[0]";
    String READER = "$.job.content[0].reader";
    String READER_TYPE = "$.job.content[0].reader.type";
    String READER_NAME = "$.job.content[0].reader.name";
    String READER_PARAMETER = "$.job.content[0].reader.parameter";
    String WRITER = "$.job.content[0].writer";
    String WRITER_NAME = "$.job.content[0].writer.name";
    String WRITER_TYPE = "$.job.content[0].writer.name";
    String WRITER_PARAMETER = "$.job.content[0].writer.parameter";
    String SPEED = "$.job.setting.speed.bytes";
    String CHANNEL = "$.job.setting.speed.channel";
}
