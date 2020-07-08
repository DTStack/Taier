package com.dtstack.engine.sparkk8s.executor;

import com.dtstack.engine.common.pojo.JobResult;

public interface SparkSubmit {
    String LOG_LEVEL_KEY = "logLevel";

    JobResult submit();

    String buildJobParams();
}
