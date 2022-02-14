package com.dtstack.taier.dao.mapper;




import com.dtstack.taier.dao.domain.DsVersion;

import java.util.List;

/**
 * @author tengzhen
 */
public interface DsVersionMapper {

    List<DsVersion> listDsVersion();


    List<DsVersion> queryDsVersionByType(String dataType);
}
