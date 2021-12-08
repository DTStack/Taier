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

package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.dtscript.api.DtYarnConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class KrbUtilsTest {

    @Test
    public void testHasKrb5() {
        Map<String, String> env = new HashMap<>();
        Assert.assertFalse(KrbUtils.hasKrb(env));
        env.put(DtYarnConstants.ENV_PRINCIPAL, "foo@foo.com");
        Assert.assertTrue(KrbUtils.hasKrb(env));
    }
}