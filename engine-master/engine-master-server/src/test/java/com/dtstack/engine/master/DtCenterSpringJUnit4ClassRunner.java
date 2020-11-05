package com.dtstack.engine.master;

import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.master.listener.RunnerListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class DtCenterSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {
    private final static String DICTIONARY_NAME = "DAGScheduleX";
    private RunnerListener runnerListener;
    /**
     * 设置 user.dir,使用项目根目录下的配置文件
     */
    public DtCenterSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        //获得项目文件的根目录
        setUserDirToTest();
    }


    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        if (test instanceof RunnerListener && runnerListener == null) {
            runnerListener = (RunnerListener)test;
            runnerListener.runsBeforeClass();
        }
        return test;
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        if (runnerListener != null) {
            runnerListener.runsAfterClass();
        }

    }

    private void setUserDirToTest() {
        String s_pre = System.getProperty("user.dir");
        int index = s_pre.indexOf(DICTIONARY_NAME);
        System.setProperty("user.dir.conf", s_pre.substring(0, index + DICTIONARY_NAME.length()) + "/test_conf/conf");
    }

}
