package com.dtstack.rdos.engine.execution.exception;

public class SqlVerificationException extends Exception{

	private static final long serialVersionUID = -3483888916795493681L;
	
    public SqlVerificationException(String name){
    	super(String.format("{}:sql verification error, please check", name));
    }
}
