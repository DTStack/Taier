package com.dtstack.engine.master.router.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import com.dtstack.engine.master.router.login.domain.UserTenant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/18
 */
public class DtUicUserConnect {

    private static Logger LOGGER = LoggerFactory.getLogger(DtUicUserConnect.class);

    private static final String LONGIN_TEMPLATE = "%s/api/user/get-info?dtToken=%s";

    private static final String LONGIN_OUT_TEMPLATE = "%s/uic/api/v2/logout";

    private static final String GET_IS_ROOT_UIC_USER_TEMPLATE = "%s/api/user/isRootUser?userId=%s&dtToken=%s";

    private static final String GET_FULL_TENANTS = "%s/uic/api/v2/account/user/get-full-tenants-by-name?tenantName=%s&productCode=%s";

    private static final String GET_TENANT_INFO = "%s/uic/api/v2/tenant/detail/%s";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void getInfo(String token, String url, Consumer<DtUicUser> resultHandler) {
        try {
            String result = PoolHttpClient.get(String.format(LONGIN_TEMPLATE, url, token),null);
            if (StringUtils.isBlank(result)) {
                LOGGER.error("uic access exception,please check...");
                resultHandler.accept(null);
                return;
            }

            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                Map<String, Object> data = (Map<String, Object>) mResult.get("data");
                DtUicUser dtUicUser = PublicUtil.mapToObject(data, DtUicUser.class);
                String isRootRes = PoolHttpClient.get(String.format(GET_IS_ROOT_UIC_USER_TEMPLATE, url, dtUicUser.getUserId(), token));
                dtUicUser.setOwnerOnly(dtUicUser.getTenantOwner());
                if (StringUtils.isNotBlank(isRootRes)) {
                    Boolean isRoot = JSONObject.parseObject(isRootRes).getBoolean("data");
                    if (isRoot) {
                        dtUicUser.setTenantOwner(isRoot);
                    }
                    dtUicUser.setRootOnly(isRoot);
                }
                resultHandler.accept(dtUicUser);
            }
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable tr) {
            LOGGER.error("", tr);
            resultHandler.accept(null);
        }

    }

    public static boolean removeUicInfo(String token, String url) {
        Map<String, Object> cookies = Maps.newHashMap();
        cookies.put("dt_token", token);
        String result = PoolHttpClient.post(String.format(LONGIN_OUT_TEMPLATE, url), null, cookies);
        if (StringUtils.isBlank(result)) {
            LOGGER.error("uic loginout exception,please check...");
            return false;
        }
        try {
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("uic loginout exception,please check...", e);
        }
        return false;
    }

    public static List<UserTenant> getUserTenants(String url, String token, String tenantName) {
        Map<String, Object> cookies = Maps.newHashMap();
        cookies.put("dt_token", token);
        List<UserTenant> userTenantList = Lists.newArrayList();
        try {
            String result = PoolHttpClient.get(String.format(GET_FULL_TENANTS, url, tenantName,"RDOS"), cookies);
            if (StringUtils.isBlank(result)) {
                LOGGER.warn("uic api returns null.");
                return Lists.newArrayList();
            }
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                List<UserTenant> dataList = JSON.parseArray(JSON.toJSONString(mResult.get("data")), UserTenant.class);
                if (!CollectionUtils.isEmpty(dataList)) {
                    userTenantList.addAll(dataList);
                }
            }
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable tr) {
            LOGGER.error("{}", tr);
        }
        return userTenantList;
    }

    /**
     * 获取uic租户信息
     * @param url
     * @param tenantId
     * @return
     */
    public static Map<String, Object> getUicTenantInfo(String url, Long tenantId,String token) {
        Map<String, Object> cookies = Maps.newHashMap();
        cookies.put("dt_token", token);
        try {
            String result = PoolHttpClient.get(String.format(GET_TENANT_INFO, new Object[]{url, tenantId}), cookies);
            if (StringUtils.isBlank(result)) {
                LOGGER.warn("uic api returns null.");
                return Maps.newHashMap();
            }
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                return (Map<String, Object>) mResult.get("data");
            }
        } catch (IOException e) {
            LOGGER.error("{}", e);
        }
        return Maps.newHashMap();
    }

}
