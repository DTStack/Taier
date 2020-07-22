package com.dtstack.engine.api.service;

import java.util.List;

public interface UploadService {
    public List<Object> upload(Integer componentType, Boolean autoDelete);
}
