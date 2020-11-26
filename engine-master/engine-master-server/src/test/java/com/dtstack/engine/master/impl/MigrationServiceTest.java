package com.dtstack.engine.master.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dtstack.engine.master.AbstractTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author basion
 * @Classname MigrationServiceTest
 * @Description unit test for MigrationService
 * @Date 2020-11-26 16:42:06
 * @Created basion
 */
@PrepareForTest()
public class MigrationServiceTest extends AbstractTest {

    @Autowired
    private MigrationService migrationService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    public void testMigrate() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("node","");
        migrationService.migrate(map);
    }
}
