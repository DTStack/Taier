package com.dtstack.engine.master.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


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
