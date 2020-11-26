package com.dtstack.engine.master;

import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * 继承此类写单元测试会调用测试库，但是使用之前不会删除测试库的数据。
 * 如果你想要编写功能测试类的代码，请先将自己想要进行功能测试的数据传入数据库，然后使用此父类。
 */
@Component
@RunWith(DtCommonSpringRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, CacheConfig.class, MybatisConfig.class})
@SpringBootTest
public abstract class AbstractCommonTest {


}
