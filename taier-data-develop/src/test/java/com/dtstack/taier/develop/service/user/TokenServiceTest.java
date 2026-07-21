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

package com.dtstack.taier.develop.service.user;

import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.develop.dto.user.DTToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class TokenServiceTest {

    private TokenService tokenService;

    @Before
    public void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "JWT_TOKEN", "test-secret");
        ReflectionTestUtils.setField(tokenService, "SESSION_TIMEOUT", 60);
    }

    @Test
    public void testDecryption() {
        String token = tokenService.encryption(1L, "admin", 2L);

        DTToken dtToken = tokenService.decryption(token);

        Assert.assertEquals(Long.valueOf(1L), dtToken.getUserId());
        Assert.assertEquals("admin", dtToken.getUserName());
        Assert.assertEquals(Long.valueOf(2L), dtToken.getTenantId());
    }

    @Test
    public void testDecryptionRejectInvalidToken() {
        try {
            tokenService.decryption("bypass");
            Assert.fail("Invalid token should be rejected");
        } catch (TaierDefineException e) {
            Assert.assertEquals(ErrorCode.TOKEN_IS_INVALID, e.getErrorCode());
        }
    }
}
