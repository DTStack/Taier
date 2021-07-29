package com.dtstack.batch.dao;

import com.dtstack.batch.domain.Permission;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author toutian
 */
public interface PermissionDao {

    Integer insert(Permission p);

    Permission getByCode(@Param("code") String code);

    List<Permission> listAll();

    Integer deleteByCodes(@Param("codes") List<String> codes, @Param("gmtModified") Timestamp timestamp);

    Permission getOne(@Param("id") Long permissionId);
}
