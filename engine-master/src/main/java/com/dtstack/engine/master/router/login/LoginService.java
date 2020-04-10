package com.dtstack.engine.master.router.login;


import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author toutian
 */
@Service
public class LoginService {

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    private static final String IS_ROOT_USER = "%s/api/user/isRootUser?userId=%s";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private EnvironmentContext env;

    @Transactional(rollbackFor = Exception.class)
    public void login(DtUicUser dtUicUser, String token, Consumer<UserDTO> resultHandler) {
        try {
            if (dtUicUser == null) {
                resultHandler.accept(null);
                return;
            }

            Map<String, Object> cookies = new HashMap<>(1);
            cookies.put("dt_token", token);
            Long userId = dtUicUser.getUserId();
            String result = PoolHttpClient.get(String.format(IS_ROOT_USER, env.getDtUicUrl(), userId), cookies);
            if (StringUtils.isBlank(result)) {
                resultHandler.accept(null);
                return;
            }
            boolean isRootUser = false;
            Map<String, Object> mResult = (Map) OBJECT_MAPPER.readValue(result, Map.class);
            if (((Boolean) mResult.get("success")).booleanValue()) {
                isRootUser = BooleanUtils.toBoolean(mResult.get("data").toString());
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setDtuicUserId(dtUicUser.getUserId());
            userDTO.setTenantId(dtUicUser.getTenantId());
            userDTO.setUserName(dtUicUser.getUserName());
            userDTO.setRootUser(isRootUser ? 1 : 0);
            resultHandler.accept(userDTO);
        }catch (IOException ioe){
            throw new RdosDefineException(ioe.getMessage());
        }catch (Throwable e) {
            logger.error("login fail:", e);
            throw e;
        }
    }

}
