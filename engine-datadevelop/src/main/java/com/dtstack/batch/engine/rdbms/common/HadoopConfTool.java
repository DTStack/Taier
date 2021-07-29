package com.dtstack.batch.engine.rdbms.common;


/**
 * 解析配置获取Hadoop配置
 * Date: 2018/5/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class HadoopConfTool {

    public static final String FS_DEFAULTFS = "fs.defaultFS";
    public final static String KEY_JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public final static String KEYTAB_PATH = "keytabPath";
    public final static String PRINCIPAL_FILE = "principalFile";
    public static final String IS_HADOOP_AUTHORIZATION = "hadoop.security.authorization";
    public static final String HADOOP_AUTH_TYPE = "hadoop.security.authentication";

}
