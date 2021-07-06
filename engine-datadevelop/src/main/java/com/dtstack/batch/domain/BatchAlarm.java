package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchAlarm extends Alarm {

    private String uncompleteTime;


    private String receivers;

}
