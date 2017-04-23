package com.dtstack.rdos.engine.execution.base.exception;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class SqlVerificationException extends Exception{

	private static final long serialVersionUID = -3483888916795493681L;
	
    public SqlVerificationException(String name){
    	super(String.format("{}:sql verification error, please check", name));
    }
}
