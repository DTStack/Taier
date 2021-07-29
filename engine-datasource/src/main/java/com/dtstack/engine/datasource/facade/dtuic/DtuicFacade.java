package com.dtstack.engine.datasource.facade.dtuic;

import com.dtstack.dtcenter.common.http.PoolHttpClient;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Component
@Slf4j
public class DtuicFacade {
    private static final String GET_RDOS_PRODUCTS = "%s/uic/api/v2/license/menu/RDOS";
    @Autowired
    private EnvironmentContext environmentContext;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 根据租户ID获取本租户被赋予权限的产品列表
     *
     * @param dtToken 用户登录Token
     * @return 产品id集合
     */
    public Collection<String> listGrantProducts(String dtToken) {
        Objects.requireNonNull(dtToken);

        List<String> productCodeList = new LinkedList<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put("dt_token", dtToken);
            String result = PoolHttpClient.get(String.format(GET_RDOS_PRODUCTS, environmentContext.getDtUicUrl()), param);
            if (StringUtils.isBlank(result)) {
                log.error("uic access exception,please check...");
            }

            JsonNode node = OBJECT_MAPPER.readTree(result);
            if (node.get("success").asBoolean()) {
                if (node.get("data") != null) {
                    Iterator<JsonNode> ltr = node.get("data").iterator();
                    while (ltr.hasNext()) {
                        JsonNode product = ltr.next();
                        if (product.get("isShow").asBoolean(false)) {
                            productCodeList.add(product.get("id").asText());
                        }
                    }
                }
            } else {
                throw new PubSvcDefineException("Get product code from uic encounter error.");
            }
        } catch (IOException e) {
            throw new PubSvcDefineException("Get product code from uic Fail", e);
        }
        return productCodeList;
    }
}
