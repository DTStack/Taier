package com.dtstack.engine.api.service;

import java.util.Map;

public interface MigrationService {
    public void migrate(Map<String,Object> params) throws Exception;
}
