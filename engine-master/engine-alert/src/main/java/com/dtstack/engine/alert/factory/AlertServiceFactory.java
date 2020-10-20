package com.dtstack.engine.alert.factory;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/5/22
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Service
public class AlertServiceFactory {

    private final Logger log = LoggerFactory.getLogger(AlertServiceFactory.class);

    @Autowired
    private List<AlertService> allAlertServiceList;

    private Map<String, AlertService> alertServiceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Assert.isTrue(CollectionUtils.isNotEmpty(allAlertServiceList), "no AlertService in spring");
        allAlertServiceList.forEach(alertService -> {
            alertServiceMap.put(alertService.alertGateCode().code(), alertService);
            log.info("register service to AlertServiceFactory   alertGateCode:{}  alertService:{}", alertService.alertGateCode().code(), alertService.getClass().getName());
        });
        log.info("init AlertServiceFactory success totalSize:{}", alertServiceMap.size());
    }

    public AlertService getAlertService(String alertGateCode) {
        AlertService alertService = alertServiceMap.get(alertGateCode);
        Assert.notNull(alertService, "can not get alertService by " + alertGateCode);
        return alertService;
    }

}
