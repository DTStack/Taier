package com.dtstack.engine.datasource.service.impl.datasource;

import com.dtstack.engine.datasource.common.enums.datasource.DsClassifyEnum;
import com.dtstack.engine.datasource.common.utils.CommonUtils;
import com.dtstack.engine.datasource.common.utils.Dozers;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsTypeMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsType;
import com.dtstack.engine.datasource.dao.po.datasource.DsVersion;
import com.dtstack.engine.datasource.param.datasource.DsTypeSearchParam;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.vo.datasource.DsTypeListVO;
import com.dtstack.engine.datasource.vo.datasource.DsTypeVO;
import dt.insight.plat.lang.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsTypeService extends BaseService<DsTypeMapper, DsType> {

    @Autowired
    private DsVersionService dsVersionService;

    /**
     * 获取数据类型下拉列表
     *
     * @return
     */
    public List<DsTypeListVO> dsTypeList() {
        List<DsType> dsTypeList = lambdaQuery().eq(DsType::getInvisible, 0).orderByDesc(DsType::getSorted).orderByAsc(DsType::getId).list();
        return Dozers.convertList(dsTypeList, DsTypeListVO.class);
    }

    /**
     * 根据分类获取数据源类型
     *
     * @param searchParam
     * @return
     */
   public List<DsTypeVO> queryDsTypeByClassify(DsTypeSearchParam searchParam) {
       Long classifyId = searchParam.getClassifyId();
       String search = searchParam.getSearch();
       if (Strings.isNotBlank(search)) {
            classifyId = null;
            search = search.trim();
        }
        List<DsType> dsTypes = lambdaQuery().like(Strings.isNotBlank(search), DsType::getDataType, search)
                .eq(Objects.nonNull(classifyId) && !CommonUtils.checkAnyObjectEquals(classifyId, DsClassifyEnum.TOTAL.getClassifyId(), DsClassifyEnum.MOST_USE.getClassifyId()), DsType::getDataClassifyId, classifyId)
                .eq(DsType::getInvisible, 0)
                .orderByDesc(DsClassifyEnum.MOST_USE.getClassifyId().equals(classifyId), DsType::getWeight)
                .orderByDesc(DsType::getSorted)
                .orderByAsc(DsType::getId)
                .last(DsClassifyEnum.MOST_USE.getClassifyId().equals(classifyId), "limit 8").list();
        List<String> versionList = dsVersionService.list().stream().map(DsVersion::getDataType)
                .collect(Collectors.toList());
        return dsTypes.stream().map(x -> Dozers.convert(x, DsTypeVO.class, (t, s, c) -> {
              t.setTypeId(s.getId());
              t.setHaveVersion(versionList.contains(s.getDataType()));
         })).collect(Collectors.toList());
   }

    /**
     * 添加数据源类型的权重值
     * 目前固定增加1
     * @param dataType
     * @return
     */
   public Boolean plusDataTypeWeight(String dataType, Integer plusWeight) {
       Objects.requireNonNull(plusWeight);
       Objects.requireNonNull(dataType);
       return this.baseMapper.plusDataTypeWeight(dataType, plusWeight) > 0;
   }
}
