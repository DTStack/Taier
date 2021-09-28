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

package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: newman
 * Date: 2020/12/31 4:23 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestKrb5FileUtil extends AbstractTest {

    @Test
    public void testResetMergeKrb5Content() throws Exception {

        String content = Krb5FileUtil.resetMergeKrb5Content("aaa", "bbb");
        Assert.assertNotNull(content);
    }


//    @Test
//    public void testMergeKrb5ContentByPath() throws Exception {
//
//        String mergeKrb5Path = getClass().getClassLoader().getResource("kerberos/krb5_new.conf").getFile();
//        String localKrb5Path = getClass().getClassLoader().getResource("kerberos/krb5_new.conf").getFile();
//
//        String content = Krb5FileUtil.mergeKrb5ContentByPath(mergeKrb5Path, localKrb5Path);
//        Assert.assertNotNull(content);
//    }


    @Test
    public void testCheckKrb5Content(){

        boolean flag = Krb5FileUtil.checkKrb5Content("[libdefaults]\n" +
                "default_realm = DTSTACK.COM\n" +
                "dns_lookup_kdc = false\n" +
                "dns_lookup_realm = false\n" +
                "ticket_lifetime = 600\n" +
                "renew_lifetime = 3600 \n" +
                "forwardable = true\n" +
                "default_tgs_enctypes = rc4-hmac aes256-cts\n" +
                "default_tkt_enctypes = rc4-hmac aes256-cts\n" +
                "permitted_enctypes = rc4-hmac aes256-cts\n" +
                "udp_preference_limit = 1\n" +
                "kdc_timeout = 3000\n" +
                "\n" +
                "[realms]\n" +
                "DTSTACK.COM = {\n" +
                "kdc = eng-cdh1\n" +
                "admin_server = eng-cdh1\n" +
                "default_domain = DTSTACK.COM\n" +
                "}\n" +
                "\n" +
                "[domain_realm]\n" +
                " .k.com = K.COM\n" +
                " k.com = K.COM\n" +
                " krb01.k.com = K.COM\n" +
                " eng-cdh1 = DTSTACK.COM\n" +
                " eng-cdh2 = DTSTACK.COM\n" +
                " eng-cdh3 = DTSTACK.COM\n" +
                " \n" +
                "\n");
        Assert.assertTrue(flag);
    }

}
