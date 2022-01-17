package com.dtstack.engine.mapper;




import com.dtstack.engine.domain.DsVersion;

import java.util.List;

/**
 * @author tengzhen
 */
public interface DsVersionMapper {

    List<DsVersion> listDsVersion();


    List<DsVersion> queryDsVersionByType(String dataType);
}
