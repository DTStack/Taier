package com.dtstack.batch.engine.rdbms.service.impl;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 枚举类中不能autowire注入属性，用于初始化枚举类中的service
 *
 * @author ：wangchuan
 * date：Created in 下午3:33 2020/11/9
 * company: www.dtstack.com
 */
@Service
public class EnumServiceInit {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private EngineService engineService;

    @Autowired
    private ComponentService componentService;

    @PostConstruct
    public void init() {
        Engine2DTOService.init(componentService, engineService, clusterService, environmentContext);
    }
}
