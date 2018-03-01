package com.dtstack.rdos.engine.execution.rdbs.executor;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.rdbs.constant.ConfigConstant;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reason:
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ConnFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConnFactory.class);

    private static final String driverName = "com.mysql.jdbc.Driver";

    private static final String testSql = "select 1 from dual";

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
        testConn();
    }

    private void testConn() {
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = getConn();
            stmt = conn.createStatement();
            stmt.execute(testSql);
        }catch (Exception e){
            throw new RdosException("get conn exception:" + e.toString());
        }finally {

            try{
                if(stmt != null){
                    stmt.close();
                }

                if(conn != null){
                    conn.close();
                }
            }catch (Exception e){
                LOG.error("", e);
            }
        }

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
