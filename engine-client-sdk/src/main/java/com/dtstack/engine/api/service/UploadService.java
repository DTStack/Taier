package com.dtstack.engine.api.service;

import java.util.List;

public interface UploadService {

    List<Object> upload(Integer componentType, Boolean autoDelete);

}
