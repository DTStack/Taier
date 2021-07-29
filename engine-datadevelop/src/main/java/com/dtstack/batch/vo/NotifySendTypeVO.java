package com.dtstack.batch.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifySendTypeVO {

    // 告警通道标识
    private String  alertGateSource;

    // 告警通道类型 1短信 2邮件 3钉钉 4自定义通道
    private Integer alertGateType;

    // 告警通道名称
    private String  alertGateName;

}
