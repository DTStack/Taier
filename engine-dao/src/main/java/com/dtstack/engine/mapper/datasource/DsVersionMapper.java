package com.dtstack.engine.mapper.datasource;



import com.dtstack.engine.domain.datasource.DsVersion;

import java.util.List;

/**
 * @author tengzhen
 */
public interface DsVersionMapper {

    List<DsVersion> listDsVersion();


    List<DsVersion> queryDsVersionByType(String dataType);
}
