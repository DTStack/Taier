package com.dtstack.engine.master;

import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class DtCommonSpringRunner extends SpringJUnit4ClassRunner {
    private final static String DICTIONARY_NAME = "DAGScheduleX";
    /**
     * 设置 user.dir,使用项目根目录下的配置文件
     */
    public DtCommonSpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        //获得项目文件的根目录
        CommonUtils.setUserDirToTest();
    }
}
