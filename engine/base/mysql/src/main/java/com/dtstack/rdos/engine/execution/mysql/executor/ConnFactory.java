package com.dtstack.rdos.engine.execution.mysql.executor;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.mysql.constant.ConfigConstant;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    private AtomicBoolean isFirstLoaded = new AtomicBoolean(true);

    private String dbURL;

    private String userName;

    private String pwd;

    public void init(Properties properties) throws ClassNotFoundException {
        if(isFirstLoaded.get()){
            Class.forName(driverName);
            isFirstLoaded.set(false);
        }

        dbURL = MathUtil.getString(properties.get(ConfigConstant.DB_URL));
        userName = MathUtil.getString(properties.get(ConfigConstant.USER_NAME));
        pwd = MathUtil.getString(properties.get(ConfigConstant.PWD));

        Preconditions.checkNotNull(dbURL, "db url can't be null");
    }

    public Connection getConn() throws ClassNotFoundException, SQLException, IOException {

        Connection conn;

        if (userName == null) {
            conn = DriverManager.getConnection(dbURL);
        } else {
            conn = DriverManager.getConnection(dbURL, userName, pwd);
        }

        return conn;
    }
}
