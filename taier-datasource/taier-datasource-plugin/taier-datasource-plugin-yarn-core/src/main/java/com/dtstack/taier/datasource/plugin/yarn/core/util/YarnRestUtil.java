package com.dtstack.taier.datasource.plugin.yarn.core.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.client.IRestful;
import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.YarnSourceDTO;
import com.dtstack.taier.datasource.plugin.restful.core.RestfulSpecialClient;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * yarn rest util
 *
 * @author ：wangchuan
 * date：Created in 下午2:21 2022/3/17
 * company: www.dtstack.com
 */
public class YarnRestUtil {

    public static final String HTTP_AUTHENTICATION_TOKEN_KEY = "http.authentication.token";

    public static final String KNOX = "KNOX";

    public static final IRestful RESTFUL = new RestfulSpecialClient();

    public static String getDataFromYarnRest(ISourceDTO sourceDTO, Configuration yarnConfiguration, String url) {
        YarnSourceDTO yarnSourceDTO = (YarnSourceDTO) sourceDTO;
        String token = yarnConfiguration.get(HTTP_AUTHENTICATION_TOKEN_KEY);
        Map<String, String> headers = Maps.newHashMap();
        if (StringUtils.isNotEmpty(token)) {
            String authKey = "Authorization";
            String authValue = String.format("Bearer %s", token);
            headers.put(authKey, authValue);
        }

        // 开启knox代理情况，添加认证
        String proxy = yarnConfiguration.get("proxy");
        if (StringUtils.isNotBlank(proxy)) {
            JSONObject object = JSONObject.parseObject(proxy);
            String type = object.getString("type");
            if (KNOX.equals(type)) {
                JSONObject config = object.getJSONObject("config");
                url = config.getString("url");
                String username = config.getString("user");
                String password = config.getString("password");
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
                String authHeader = "Basic " + new String(encodedAuth);
                // 将验证信息放入到 Header
                headers.put(HttpHeaders.AUTHORIZATION, authHeader);
            }
        }

        Map<String, Object> sslConfigMap = Maps.newHashMap();
        sslConfigMap.put("skipSsl", true);
        // 跳过 ssl 认证
        SSLConfig sslConfig = SSLConfig.builder()
                .otherConfig(sslConfigMap).build();

        RestfulSourceDTO restfulSourceDTO = RestfulSourceDTO.builder()
                .sftpConf(yarnSourceDTO.getSftpConf())
                .kerberosConfig(yarnSourceDTO.getKerberosConfig())
                .sslConfig(sslConfig)
                .url(url).build();

        return RESTFUL.get(restfulSourceDTO, null, null, headers).getContent();
    }
}
