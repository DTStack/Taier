package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Role;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @author: toutian
 * @create: 2017/10/25
 */
@Slf4j
@Data
public class RoleVO extends Role {

    private List<Long> permissionIds;

    private String modifyUserName;

    public static RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        try {
            BeanUtils.copyProperties(role, vo);
        } catch (Exception e) {
            log.error("", e);
        }
        return vo;
    }

}
