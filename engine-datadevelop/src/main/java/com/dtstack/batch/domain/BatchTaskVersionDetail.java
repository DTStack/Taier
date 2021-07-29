package com.dtstack.batch.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: xuchao
 * create: 2019/04/23
 */
@Data
public class BatchTaskVersionDetail extends BatchTaskVersion {

    private String userName;
    private List<String> dependencyTaskNames;
    private JSONObject dependencyTasks;
}
