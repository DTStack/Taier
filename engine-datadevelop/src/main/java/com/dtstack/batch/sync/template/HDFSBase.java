package com.dtstack.batch.sync.template;

import com.dtstack.batch.engine.rdbms.common.enums.StoredType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
@Data
public abstract class HDFSBase extends BaseSource{

    protected String defaultFS;
    protected String path = "";
    protected String fileType = StoredType.ORC.getValue();
    protected String encoding = "utf-8";
    protected String fieldDelimiter = "\001";
    protected Map<String,Object> hadoopConfig;
    protected List column;
    protected String remoteDir;
    protected Map<String, Object> sftpConf;
}
