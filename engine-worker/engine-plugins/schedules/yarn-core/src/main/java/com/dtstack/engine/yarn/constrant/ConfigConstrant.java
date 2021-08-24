package com.dtstack.engine.yarn.constrant;

import java.io.File;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/05/18
 */
public class ConfigConstrant {

    // ------------------------------------------------------------------------
    // General Configs
    // ------------------------------------------------------------------------

    public static final String SP = File.separator;
    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String FAIRSCHEDULER_TPYE = "FAIRSCHEDULER";
    public static final String CAPACITYSCHEDULER_TPYE = "CAPACITYSCHEDULER";
    public static final String FIFOSCHEDULER_TPYE = "FIFOSCHEDULER";

    public static final int HTTP_MAX_RETRY = 3;
    public static final String HTTP_AUTHENTICATION_TOKEN_KEY = "http.authentication.token";
    public static final String IS_FULL_PATH_KEY = "yarn.resourcemanager.scheduler.queue.is-full-path";

}
