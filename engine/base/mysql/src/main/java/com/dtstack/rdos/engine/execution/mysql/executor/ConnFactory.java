package com.dtstack.rdos.engine.execution.mysql.executor;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reason:
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ConnFactory {

    private static final String DBURL_KEY = "dbUrl";

    private static final String USER_NAME_KEY = "userName";

    private static final String PWD_KEY = "pwd";

    private String driverName = "com.mysql.jdbc.Driver";

    private AtomicBoolean isFirstLoaded = new AtomicBoolean(true);

    private static ConnFactory connFactory = new ConnFactory();

    private ConnFactory(){}

    public static ConnFactory getInstance(){
        return connFactory;
    }

    public Connection getConn(String pluginInfo) throws ClassNotFoundException, SQLException, IOException {
        if(isFirstLoaded.get()){
            Class.forName(driverName);
            isFirstLoaded.set(false);
        }

        Map<String, Object> params = PublicUtil.ObjectToMap(pluginInfo);

        String dbURL = MathUtil.getString(params.get(DBURL_KEY));
        String username = MathUtil.getString(params.get(USER_NAME_KEY));
        String pwd = MathUtil.getString(params.get(PWD_KEY));

        Preconditions.checkNotNull(dbURL, "db url can't be null");

        Connection conn;

        if (username == null) {
            conn = DriverManager.getConnection(dbURL);
        } else {
            conn = DriverManager.getConnection(dbURL, username, pwd);
        }

        return conn;
    }
}
