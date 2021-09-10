package com.dtstack.engine.datasource.mapstruct;

import com.dtstack.engine.datasource.dao.po.datasource.DsAppList;
import com.dtstack.engine.datasource.vo.datasource.AuthProductListVO;
import com.dtstack.engine.datasource.vo.datasource.DsAppListVO;
import com.dtstack.engine.datasource.vo.datasource.ProductListVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/10
 */
@Mapper(componentModel = "spring")
public interface DsAppListStruct {

    AuthProductListVO toAuthProductListVO(DsAppListVO dsAppListVO);

    List<ProductListVO> toProductListVOs(List<DsAppList> dsAppLists);

    AuthProductListVO dsApp2AuthProductListVO(DsAppList t);
}
