package com.dtstack.rdos.engine.execution.base.enumeration;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public enum ClientType {

    Flink,Spark;

    public static ClientType getClientType(String type){

        switch (type.toLowerCase()){

            case "flink":return ClientType.Flink;

            case "spark":return ClientType.Spark;
        }
         return null;
    }
}
