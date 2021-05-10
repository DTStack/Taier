package com.dtstack.engine.lineage.adapter;

import com.dtstack.sqlparser.common.client.domain.AlterResult;
import com.dtstack.sqlparser.common.client.enums.TableOperateEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 15:11
 * Description: Alter结果转换器
 * @since 1.0.0
 */
public class AlterResultAdapterTest {



    @Test
    public void testSqlAlterResult2ApiResult(){

        AlterResult alterResult = new AlterResult();
        alterResult.setOldDB("dev");
        alterResult.setAlterType(TableOperateEnum.ALTER);
        com.dtstack.engine.api.vo.lineage.AlterResult result = AlterResultAdapter.sqlAlterResult2ApiResult(alterResult);
        Assert.assertEquals("dev",result.getOldDB());
    }


}
