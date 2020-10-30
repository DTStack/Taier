package com.dtstack.lineage.impl;

import com.dtstack.lineage.dao.LineageColumnColumnAppRefDao;
import com.dtstack.lineage.dao.LineageColumnColumnDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chener
 * @Classname LineageColumnColumnService
 * @Description TODO
 * @Date 2020/10/29 15:57
 * @Created chener@dtstack.com
 */
@Service
public class LineageColumnColumnService {
    @Autowired
    private LineageColumnColumnDao lineageColumnColumnDao;

    @Autowired
    private LineageColumnColumnAppRefDao lineageColumnColumnAppRefDao;


}
