package com.dtstack.batch.sync.util;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.batch.mockcontainer.HdfsOrcUtilMock;
import com.google.common.collect.Maps;
import org.junit.Test;

@MockWith(HdfsOrcUtilMock.class)
public class HdfsOrcUtilTest {

    @Test
    public void getColumnListTest() {
        HdfsOrcUtil.getColumnList("tableName1", "defaultFS", "{}", Maps.newHashMap());
        HdfsOrcUtil.getColumnList("tableName2", "defaultFS", "{}", Maps.newHashMap());
    }
}
