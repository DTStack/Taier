/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
