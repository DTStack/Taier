package com.dtstack.rdos.engine.entrance.test;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.engine.db.dao.RdosStreamActionLogDAO;
import com.dtstack.rdos.engine.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.db.dao.RdosStreamServerLogDao;
import com.dtstack.rdos.engine.db.dao.RdosStreamTaskDAO;

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
        rdosNodeMachineDAO.insert(ip, port, machineType,null, "");
        System.out.println("-----------over--------");
    }

    @Test
    public void testRdosActionLogUpdate(){
        RdosStreamActionLogDAO rdosActionLogDAO = new RdosStreamActionLogDAO();
        Long actionLogId = 123l;
        int status = 1;
        rdosActionLogDAO.updateActionStatus(actionLogId, status);
        System.out.println("-----------over--------");
    }

    @Test
    public void testUpdateTaskStatus(){
        RdosStreamTaskDAO rdosTaskDAO = new RdosStreamTaskDAO();
        String taskId = "123";
        int status = 1;
        rdosTaskDAO.updateTaskStatus(taskId, status);
        System.out.println("-----------over--------");
    }

    @Test
    public void testUpdateTaskEngine(){
        RdosStreamTaskDAO rdosTaskDAO = new RdosStreamTaskDAO();
        String taskId = "123";
        String engineId = "123";
        rdosTaskDAO.updateTaskEngineId(taskId, engineId);
    }

    @Test
    public void insertSvrLog(){
        RdosStreamServerLogDao dao = new RdosStreamServerLogDao();
        String taskId = null;
        String engineTaskId = "";
        String logInfo = "--------this is a log -------";
        dao.insertLog(taskId, engineTaskId, 1234l, logInfo);
        System.out.println("-------over----------");

    }

}
