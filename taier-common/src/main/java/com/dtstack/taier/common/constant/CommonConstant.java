package com.dtstack.taier.common.constant;

import com.dtstack.taier.pluginapi.constrant.ConfigConstant;

/**
 * 常用常量
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-25 15:06
 */
public interface CommonConstant {

    String DOT = ".";
    String SYMBOL_COLON = ":";

    String XML_SUFFIX = ".xml";
    String ZIP_SUFFIX = ".zip";
    String JSON_SUFFIX = ".json";

    String RUN_JOB_NAME = "runJob";
    String RUN_DELIMITER = "_";

    String JOB_ID = "${jobId}";

    String LOGIN = "login";

    String DOWNLOAD_LOG = ConfigConstant.REQUEST_PREFIX + "/developDownload/downloadJobLog?jobId=%s&taskType=%s&tenantId=%s";

}
