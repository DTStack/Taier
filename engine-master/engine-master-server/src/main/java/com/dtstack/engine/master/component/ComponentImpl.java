package com.dtstack.engine.master.component;

public interface ComponentImpl {

    String getJsonString();

    void testConnection() throws Exception;

    void checkConfig();
}
