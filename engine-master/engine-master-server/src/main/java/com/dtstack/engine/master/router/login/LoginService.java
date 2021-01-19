package com.dtstack.engine.master.router.login;


import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author toutian
 */
@Service
public class LoginService {

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);


    public void login(DtUicUser dtUicUser, String token, Consumer<UserDTO> resultHandler) {
        try {
            if (dtUicUser == null) {
                resultHandler.accept(null);
                return;
            }
            boolean isRootUser = Optional.ofNullable(dtUicUser.getRootOnly()).orElse(false);
            logger.info("dtUic userId [{}] userName {} tenantId {} is Root {} login", dtUicUser.getUserId(), dtUicUser.getUserName(), dtUicUser.getTenantId(), isRootUser);
            UserDTO userDTO = new UserDTO();
            userDTO.setDtuicUserId(dtUicUser.getUserId());
            userDTO.setTenantId(dtUicUser.getTenantId());
            userDTO.setUserName(dtUicUser.getUserName());
            userDTO.setRootUser(isRootUser ? 1 : 0);
            resultHandler.accept(userDTO);
        } catch (Throwable e) {
            logger.error("login fail:", e);
            throw e;
        }
    }

}
