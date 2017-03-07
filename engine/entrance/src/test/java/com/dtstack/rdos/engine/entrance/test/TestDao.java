package com.dtstack.rdos.engine.entrance.test;

import com.dtstack.rdos.engine.entrance.db.dao.RdosNodeMachineDAO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/3/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class TestDao {

    private Logger logger = LoggerFactory.getLogger(TestDao.class);

    @Test
    public void testRdosNodeMachineMapping(){

        logger.info("111");
        RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();
        String ip = "127.0.0.1";
        int port = 6123;
        byte machineType = '1';
        rdosNodeMachineDAO.insert(ip, port, machineType);
        System.out.println("-----------over--------");
    }
}
