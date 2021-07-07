package com.dtstack.batch.service.table;

import com.dtstack.batch.bo.ImportDataParam;

/**
 * 数据导入接口
 * Date: 2019/5/22
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IDataImportService {

    /**
     * 本地数据导入
     * 注意:经过文件上传接口处理的数据都是string类型
     * @param importDataParam
     * @return
     * @throws Exception
     */
    String importData(ImportDataParam importDataParam) throws Exception;

}
