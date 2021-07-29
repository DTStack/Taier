package com.dtstack.batch.dao.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:05 2020/12/26
 * @Description：任务以及项目用户
 */
@Data
public class TaskOwnerAndProjectPO implements Serializable {
    private Long ownerId;

    private Long projectId;
}
