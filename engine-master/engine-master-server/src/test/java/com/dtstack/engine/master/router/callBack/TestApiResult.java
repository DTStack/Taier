package com.dtstack.engine.master.router.callBack;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.router.callback.ApiResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: newman
 * Date: 2020/12/31 2:11 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestApiResult extends AbstractTest {


    @Test
    public void testStructureFunc(){

        ApiResult<Object> result1 = new ApiResult<>();
        ApiResult<Object> api1 = new ApiResult<>(200, "api1");
        Assert.assertEquals(200,api1.getCode());
        ApiResult<Long> result2= new ApiResult<>(500, "cuow错误", 1L);
        Assert.assertEquals(500,result2.getCode());

    }

    @Test
    public void testSetGet(){

        ApiResult<Object> result1 = new ApiResult<>();
        result1.setCode(200);
        int code = result1.getCode();
        Assert.assertEquals(200,code);
        result1.setData(1L);
        Object data = result1.getData();
        Assert.assertEquals(1L,(long)data);
        result1.setMessage("hello");
        String message = result1.getMessage();
        Assert.assertEquals("hello",message);
        result1.setSpace(1L);
        long space = result1.getSpace();
        Assert.assertEquals(1L,space);
    }
}
