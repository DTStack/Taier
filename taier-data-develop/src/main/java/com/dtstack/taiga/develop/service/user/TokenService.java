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

package com.dtstack.taiga.develop.service.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.dto.user.DTToken;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author yuebai
 * @date 2021-08-04
 */
@Service
@Configuration
public class TokenService{
    private Logger log = LoggerFactory.getLogger(TokenService.class);

    @Value("${dtstack.jwtSecret:SGVsbG8gV29ybGQK}")
    public String JWT_TOKEN;


    @Value("${server.session.timeout:29200}")
    public Integer SESSION_TIMEOUT;

    public DTToken decryption(String tokenText) {
        Assert.notNull(tokenText, "JWT Token Text can't blank.");
        try {
            /**
             * 验证
             */
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(JWT_TOKEN))
                    .build()
                    .verify(tokenText);
            DTToken token = new DTToken();

            token.setUserName(jwt.getClaim(DTToken.USER_NAME).asString());
            token.setUserId(Long.parseLong(jwt.getClaim(DTToken.USER_ID).asString()));
            if (!jwt.getClaim(DTToken.TENANT_ID).isNull()) {
                token.setTenantId(Long.parseLong(jwt.getClaim(DTToken.TENANT_ID).asString()));
            }
            token.setExpireAt(jwt.getExpiresAt());
            return token;
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                log.error("JWT Token decode Error.", e);
            }
            throw new RdosDefineException("DT Token解码异常.");
        } catch (TokenExpiredException e) {
            if (log.isErrorEnabled()) {
                log.error("JWT Token expire.", e);
            }
            throw new RdosDefineException("DT Token已过期");
        }
    }

    public DTToken decryptionWithOutExpire(String tokenText) {
        Assert.notNull(tokenText, "JWT Token Text can't blank.");
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(JWT_TOKEN))
                    .build()
                    .verify(tokenText);
            DTToken token = new DTToken();
            token.setUserName(jwt.getClaim(DTToken.USER_NAME).asString());
            token.setUserId(Long.parseLong(jwt.getClaim(DTToken.USER_ID).asString()));
            if (!jwt.getClaim(DTToken.TENANT_ID).isNull()) {
                token.setTenantId(Long.parseLong(jwt.getClaim(DTToken.TENANT_ID).asString()));
            }
            return token;
        } catch (UnsupportedEncodingException e) {
            throw new RdosDefineException("DT Token解码异常.");
        }
    }

    public String encryption(Long userId, String username, Long tenantId) {
        return encryption(userId, username, tenantId, new Date());
    }

    public String encryptionWithOutExpire(Long userId, String username, Long tenantId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(username);

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_TOKEN);

            JWTCreator.Builder builder = JWT.create();

            builder.withClaim(DTToken.USER_ID, String.valueOf(userId))
                    .withClaim(DTToken.USER_NAME, username);

            //若存在租户id,则增加租户id信息
            if (Objects.nonNull(tenantId)) {
                builder.withClaim(DTToken.TENANT_ID, String.valueOf(tenantId));
            }

            return builder.sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new RdosDefineException("dtToken生成异常.", e);
        }
    }

    public String encryption(Long userId, String username, Long tenantId, Date expireAt) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(username);

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_TOKEN);

            JWTCreator.Builder builder = JWT.create();

            if (SESSION_TIMEOUT >0) {
                //Token的过期时间和Session的过期时间保持一致
                Calendar c = Calendar.getInstance();
                c.setTime(expireAt);
                c.add(Calendar.MINUTE, SESSION_TIMEOUT);

                //create token builder

                //创建主体信息
                builder
                        .withExpiresAt(c.getTime())
                        .withIssuedAt(DateTime.now().toDate());
            }
            //增加user_id
            builder.withClaim(DTToken.USER_ID, String.valueOf(userId))
                    .withClaim(DTToken.USER_NAME, username);

            //若存在租户id,则增加租户id信息
            if (Objects.nonNull(tenantId)) {
                builder.withClaim(DTToken.TENANT_ID, String.valueOf(tenantId));
            }
            return builder.sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new RdosDefineException("dtToken生成异常.", e);
        }
    }
}
