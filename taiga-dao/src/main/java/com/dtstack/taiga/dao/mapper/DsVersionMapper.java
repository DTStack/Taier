package com.dtstack.taiga.dao.mapper;




import com.dtstack.taiga.dao.domain.DsVersion;

import java.util.List;

/**
 * @author tengzhen
 */
public interface DsVersionMapper {

    List<DsVersion> listDsVersion();


    List<DsVersion> queryDsVersionByType(String dataType);
}
