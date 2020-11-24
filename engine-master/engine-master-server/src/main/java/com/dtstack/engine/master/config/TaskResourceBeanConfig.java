package com.dtstack.engine.master.config;

import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.schedule.common.enums.EScheduleJobType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author tengzhen
 * @Description: 任务类型对应资源模板
 * @Date: Created in 8:36 下午 2020/10/14
 */
public class TaskResourceBeanConfig {

    public static List<TaskTypeResourceTemplateVO> templateList;

    static {
        templateList = new ArrayList<>();
        //1、设置spark类型任务
        //1.1 设置sparkSql类型任务
        TaskTypeResourceTemplateVO template1 = new TaskTypeResourceTemplateVO();
        template1.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        template1.setTaskTypeName(EScheduleJobType.SPARK_SQL.getName());
        Map<String,String> map1 = new HashMap<>(8);
        map1.put("driver.cores","Driver程序使用的CPU核数,默认为1");
        map1.put("driver.memory","Driver程序使用内存大小,默认512m");
        map1.put("executor.instances","启动的executor的数量，默认为1");
        map1.put("executor.cores","每个executor使用的CPU核数，默认为1");
        map1.put("executor.memory","每个executor内存大小,默认512m");
        template1.setParams(map1);
        templateList.add(template1);
        //1.2 设置spark类型任务
        TaskTypeResourceTemplateVO template2 = new TaskTypeResourceTemplateVO();
        template2.setTaskType(EScheduleJobType.SPARK.getType());
        template2.setTaskTypeName(EScheduleJobType.SPARK.getName());
        template2.setParams(map1);
        templateList.add(template2);
        //1.3 设置pySpark类型任务
        TaskTypeResourceTemplateVO template3 = new TaskTypeResourceTemplateVO();
        template3.setTaskType(EScheduleJobType.SPARK_PYTHON.getType());
        template3.setTaskTypeName(EScheduleJobType.SPARK_PYTHON.getName());
        template3.setParams(map1);
        templateList.add(template3);
        //2、设置flink类型,数据同步任务
        TaskTypeResourceTemplateVO template4 = new TaskTypeResourceTemplateVO();
        template4.setTaskType(EScheduleJobType.SYNC.getType());
        template4.setTaskTypeName(EScheduleJobType.SYNC.getName());
        Map<String,String> map2 = new HashMap<>(2);
        map2.put("jobmanager.memory.mb","jobManager配置的内存大小，默认1024（单位M)");
        map2.put("taskmanager.memory.mb","taskManager配置的内存大小，默认1024（单位M)");
        template4.setParams(map2);
        templateList.add(template4);
        //3、设置dtscript类型任务
        //3.1设置python类型任务
        TaskTypeResourceTemplateVO template5 = new TaskTypeResourceTemplateVO();
        template5.setTaskType(EScheduleJobType.PYTHON.getType());
        template5.setTaskTypeName(EScheduleJobType.PYTHON.name());
        Map<String,String> map3 = new HashMap<>(4);
        map3.put("worker.memory","每个worker所占内存，比如512m");
        map3.put("worker.cores","每个worker所占的cpu核的数量");
        map3.put("worker.num","worker数量");
        template5.setParams(map3);
        templateList.add(template5);
        //3.2 设置shell类型任务
        TaskTypeResourceTemplateVO template6 = new TaskTypeResourceTemplateVO();
        template6.setTaskType(EScheduleJobType.SHELL.getType());
        template6.setTaskTypeName(EScheduleJobType.SHELL.name());
        template6.setParams(map3);
        templateList.add(template6);
    }
}
