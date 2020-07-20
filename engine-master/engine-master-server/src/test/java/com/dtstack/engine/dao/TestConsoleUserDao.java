package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2020-07-08
 */
public interface TestConsoleUserDao {

    @Insert({" INSERT INTO console_user (dtuic_user_id,user_name,email,status,phone_number)" +
            "        values(#{user.dtuicUserId},#{user.userName},#{user.email},#{user.status},#{user.phoneNumber})"})
    @Options(useGeneratedKeys=true, keyProperty = "user.id", keyColumn = "id")
    void insert(@Param("user") User user);
}
