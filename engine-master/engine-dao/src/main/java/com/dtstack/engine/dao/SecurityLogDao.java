package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.SecurityLog;
import com.dtstack.engine.api.dto.SecurityLogDTO;

import java.util.List;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/6/4 21:50
 * @Description:
 */
public interface SecurityLogDao {

    Integer insert(SecurityLog securityLog);

    List<SecurityLog> list(SecurityLogDTO query);

    SecurityLog query(SecurityLog securityLog);
}
