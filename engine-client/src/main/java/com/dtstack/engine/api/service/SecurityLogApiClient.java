package com.dtstack.engine.api.service;

import com.dtstack.engine.api.param.SecurityLogParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * Date: 2020/7/29
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface SecurityLogApiClient extends DtInsightServer {

    /**
     * 添加安全日志接口免登陆，需将参数加密传输,用于替换console: /api/console/service/securityAudit/addSecurityLog
     * @param securityLogParam 加密后的字符串
     */
    @RequestLine("POST /node/securityAudit/addSecurityLog")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> addSecurityLog(SecurityLogParam securityLogParam);

}
