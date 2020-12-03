package com.dtstack.engine.master;

import com.dtstack.engine.master.utils.CommonUtils;
import com.dtstack.engine.master.utils.ValueUtils;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.atomic.AtomicBoolean;


public class DtCenterSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    private final static AtomicBoolean init = new AtomicBoolean(false);

    /**
     * 设置 user.dir,使用项目根目录下的配置文件
     */
    public DtCenterSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        //获得项目文件的根目录
        CommonUtils.setUserDirToTest();
    }


    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        synchronized (DtCenterSpringJUnit4ClassRunner.class) {
            if (init.compareAndSet(false, true)) {
                try {
                    ValueUtils.initData();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return test;

    }

}
