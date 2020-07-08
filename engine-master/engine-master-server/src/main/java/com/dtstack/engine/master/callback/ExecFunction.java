package com.dtstack.engine.master.callback;

@FunctionalInterface
public interface ExecFunction {
    Object execute() throws Exception;
}
