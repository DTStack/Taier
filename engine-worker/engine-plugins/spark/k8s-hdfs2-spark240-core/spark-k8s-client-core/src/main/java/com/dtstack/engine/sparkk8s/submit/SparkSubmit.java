package com.dtstack.engine.sparkk8s.submit;

import com.dtstack.engine.common.pojo.JobResult;

/**
 * Date: 2020/7/9
 * Company: www.dtstack.com
 * @author maqi
 */
public interface SparkSubmit {
    String LOG_LEVEL_KEY = "logLevel";

    /**
     * 任务提交
     * @return
     */
    JobResult submit();

    /**
     *  job运行参数
     * @return
     */
    String buildJobParams();
}
