package com.dtstack.rdos.engine.execution.mysql.executor;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reason:
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ConnFactory {

    private String driverName = "com.mysql.jdbc.Driver";

    private static final String DBURL_KEY = "dbUrl";

    private static final String USER_NAME_KEY = "userName";

    private static final String PWD_KEY = "pwd";

    private AtomicBoolean isFirstLoaded = new AtomicBoolean(true);

    private String dbURL;

    private String userName;

    private String pwd;

    public void init(Properties properties) throws ClassNotFoundException {
        if(isFirstLoaded.get()){
            Class.forName(driverName);
            isFirstLoaded.set(false);
        }

        dbURL = MathUtil.getString(properties.get(DBURL_KEY));
        userName = MathUtil.getString(properties.get(USER_NAME_KEY));
        pwd = MathUtil.getString(properties.get(PWD_KEY));
    }

    public Connection getConn() throws ClassNotFoundException, SQLException, IOException {


        Preconditions.checkNotNull(dbURL, "db url can't be null");

        Connection conn;

        if (userName == null) {
            conn = DriverManager.getConnection(dbURL);
        } else {
            conn = DriverManager.getConnection(dbURL, userName, pwd);
        }

        return conn;
    }
}
