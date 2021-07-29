package com.dtstack.batch.dto;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.domain.BatchPackageItem;
import lombok.Data;

@Data
public class BatchPackageItemDTO extends BatchPackageItem {

    private JSONObject data;

    private JSONObject publishParamJson;

}
