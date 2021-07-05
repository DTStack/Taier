package com.dtstack.batch.sync.util;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.batch.mockcontainer.ImpalaUtilsMock;
import org.junit.Test;

@MockWith(ImpalaUtilsMock.class)
public class ImpalaUtilsTest {

    @Test
    public void getTableFileTypeTest() {
        ImpalaUtils.getTableFileType(null, "tableName");
    }

    @Test
    public void getImpalaKuduTableParamsTest() {
        ImpalaUtils.getImpalaKuduTableParams(null, "tableName");
    }

    @Test
    public void getImpalaHiveTableDetailInfoTest() {
        ImpalaUtils.getImpalaHiveTableDetailInfo(null, "tableName");
    }
}
