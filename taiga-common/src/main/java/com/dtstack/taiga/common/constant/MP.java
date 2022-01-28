package com.dtstack.taiga.common.constant;

import com.google.common.base.CaseFormat;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/15
 */
public interface MP {
    String COLUMN_ID = "id";
    String COLUMN_DELETED = "is_deleted";
    String COLUMN_CREATE_AT = "gmt_create";
    String COLUMN_UPDATE_AT = "gmt_modified";
    String COLUMN_CREATE_BY = "create_user_id";
    String COLUMN_UPDATE_BY = "modify_user_id";
    String COLUMN_TENANT_ID = "tenant_id";
    String COLUMN_PROJECT_ID = "project_id";
    String FIELD_CREATE_AT = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,COLUMN_CREATE_AT);
    String FIELD_CREATE_BY = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,COLUMN_CREATE_BY);
    String FIELD_UPDATE_AT = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,COLUMN_UPDATE_AT);
    String FIELD_UPDATE_BY = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,COLUMN_UPDATE_BY);
    String FIELD_TENANT_ID = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,COLUMN_TENANT_ID);
    String FIELD_PROJECT_ID = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,COLUMN_PROJECT_ID);
}
