package com.dtstack.engine.master;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


/**
 * 继承此类写单元测试会调用测试库，使用之前会删除测试库的数据，并自动拉取DataCollection中的数据写入数据库。
 * 如果你想要编写评估覆盖率的单测用例，请使用该父类，并将所需支持的数据写在DataCollection中。
 */
@RunWith(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, CacheConfig.class, MybatisConfig.class})
@SpringBootTest
public abstract class AbstractTest {

}
