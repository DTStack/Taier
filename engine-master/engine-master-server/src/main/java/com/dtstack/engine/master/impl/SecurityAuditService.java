package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.SecurityLog;
import com.dtstack.engine.api.dto.SecurityLogDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.security.ApiOperateTypeVO;
import com.dtstack.engine.api.vo.security.SecurityLogVO;
import com.dtstack.engine.common.enums.ActionType;
import com.dtstack.engine.common.enums.DataInsightAppType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.AESUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.SecurityLogDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/6/4 22:01
 * @Description:
 */
@Service
public class SecurityAuditService {

    private static Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);

    private static final int corePoolSize = 10;

    private static final int maxPoolSize = 200;

    private static final int queueCapacity = 8;

    private static final int keepAlive = 60;

    private static ThreadPoolTaskExecutor executor;

    static {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(keepAlive);
        executor.initialize();
    }

    @Autowired
    private SecurityLogDao securityLogDao;

    /**
     * @param appTag
     * @param startTime
     * @param endTime
     * @param operator
     * @param currentPage
     * @param pageSize
     * @return
     */

    public PageResult<List<SecurityLogVO>> pageQuery(@Param("appTag") String appTag,
                                                     @Param("tenantId") long tenantId,
                                                     @Param("startTime") Long startTime,
                                                     @Param("endTime") Long endTime,
                                                     @Param("operator") String operator,
                                                     @Param("currentPage") Integer currentPage,
                                                     @Param("pageSize") Integer pageSize,
                                                     @Param("operation") String oprtation,
                                                     @Param("operationObject") String operationObject,
                                                     @Param("isRoot") Boolean isRoot) {

        //fixme 只有超管有权限
        if (!isRoot){
            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
        }
        SecurityLogDTO dto = new SecurityLogDTO();
        if (startTime != null) {
            dto.setStartTime(new Timestamp(startTime * 1000));
        }
        if (endTime != null) {
            dto.setEndTime(new Timestamp(endTime * 1000));
        }
        dto.setOperation(oprtation);
        dto.setOperationObject(operationObject);
        dto.setAppTag(appTag);
        dto.setOperator(operator);
        if (appTag.equals(DataInsightAppType.API.name())) {
            dto.setTenantId(tenantId);
        }
        Page<SecurityLog> page = PageHelper.startPage(currentPage, pageSize, "gmt_create desc")
                .doSelectPage(() -> securityLogDao.list(dto));

        List<SecurityLog> securityLogs = page.getResult();
        List<SecurityLogVO> collect = securityLogs.stream().map(log -> {
            String comment="";
            if (StringUtils.isNotBlank(log.getOperation())){
                comment=ActionType.getCommentByCode(Integer.parseInt(log.getOperation()));
            }
            SecurityLogVO vo = new SecurityLogVO();
            vo.setAction(log.getAction());
            vo.setAppTag(log.getAppTag());
            vo.setCreateTime(log.getGmtCreate());
            vo.setOperator(log.getOperator());
            vo.setOperation(comment);
            vo.setOperationObject(log.getOperationObject());
            return vo;
        }).collect(Collectors.toList());
        PageResult<List<SecurityLogVO>> result = new PageResult<>(page.getPageNum(), page.getPageSize(), (int) page.getTotal(), page.getPages(), collect);
        return result;
    }

    /**
     * 添加安全日志接口免登陆，需将参数加密传输
     * @param sign
     */
    public void addSecurityLog(@Param("sign") String sign) {
        SecurityLogVO securityLogVO = null;
        if (StringUtils.isNotEmpty(sign)){
            try {
                String decrypt = AESUtil.decrypt(sign);
                if (StringUtils.isEmpty(decrypt)){
                    return;
                }
                securityLogVO = PublicUtil.strToObject(decrypt, SecurityLogVO.class);
            } catch (Exception e) {
                logger.error("参数错误:{}",e);
            }
        }
        if (securityLogVO == null){
            return;
        }
        SecurityLogVO finalSecurityLogVO = securityLogVO;
        executor.execute(() -> {
            SecurityLog log = new SecurityLog();
            log.setAction(finalSecurityLogVO.getAction());
            log.setAppTag(finalSecurityLogVO.getAppTag());
            log.setOperator(finalSecurityLogVO.getOperator());
            log.setOperatorId(finalSecurityLogVO.getOperatorId());
            log.setTenantId(finalSecurityLogVO.getTenantId());
            log.setOperation(finalSecurityLogVO.getOperation()==null?"":finalSecurityLogVO.getOperation());
            log.setOperationObject(finalSecurityLogVO.getOperationObject()==null?"":finalSecurityLogVO.getOperationObject());
            securityLogDao.insert(log);
        });
    }

    public List<ApiOperateTypeVO> getOperationList(@Param("appTag") String appTag){
        List<ApiOperateTypeVO> result = new ArrayList<>();
        if (DataInsightAppType.valueOf(appTag).equals(DataInsightAppType.API)){

            Map<Integer,String>  map= ActionType.getApiMap();
            for (Map.Entry<Integer,String> entry : map.entrySet()){
                ApiOperateTypeVO api = new ApiOperateTypeVO();
                api.setCode(entry.getKey());
                api.setName(entry.getValue());
                result.add(api);
            }
        }
        return result;
    }


}
