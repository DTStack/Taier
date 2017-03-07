package com.dtstack.rdos.engine.entrance.test;

import com.dtstack.rdos.engine.entrance.db.dao.RdosActionLogDAO;
import com.dtstack.rdos.engine.entrance.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.entrance.db.dao.RdosTaskDAO;
import com.dtstack.rdos.engine.entrance.db.dataobject.RdosActionLog;
import com.dtstack.rdos.engine.entrance.db.mapper.RdosTaskMapper;
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
    public void testRdosNodeMachineInsert(){

        logger.info("111");
        RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();
        String ip = "127.0.0.1";
        int port = 6123;
        byte machineType = '1';
        rdosNodeMachineDAO.insert(ip, port, machineType);
        System.out.println("-----------over--------");
    }

    @Test
    public void testRdosActionLogUpdate(){
        RdosActionLogDAO rdosActionLogDAO = new RdosActionLogDAO();
        String actionLogId = "123";
        int status = 1;
        rdosActionLogDAO.updateActionStatus(actionLogId, status);
        System.out.println("-----------over--------");
    }

    @Test
    public void testUpdateTaskStatus(){
        RdosTaskDAO rdosTaskDAO = new RdosTaskDAO();
        String taskId = "123";
        int status = 1;
        rdosTaskDAO.updateTaskStatus(taskId, status);
        System.out.println("-----------over--------");
    }

    @Test
    public void testUpdateTaskEngine(){
        RdosTaskDAO rdosTaskDAO = new RdosTaskDAO();
        String taskId = "123";
        String engineId = "123";
        rdosTaskDAO.updateTaskEngineId(taskId, engineId);
    }

}
