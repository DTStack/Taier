package com.dtstack.engine.remote.constant;

/**
 * @Auther: dazhi
 * @Date: 2020/9/2 5:56 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ServerConstant {

    public static final String BASE_PATH = "serverClient";

    public static final String SERVER_PATH = "dagschedulex";

    public static final String SERVER_CUTTING_PATH = "@";

    public static final String BLOCKING_DISPATCHER = "blocking-dispatcher";

    /**
     *  cpus
     */
    public static final int CPUS = Runtime.getRuntime().availableProcessors();

    /**
     * OS Name
     */
    public static final String OS_NAME = System.getProperty("os.name");

}
