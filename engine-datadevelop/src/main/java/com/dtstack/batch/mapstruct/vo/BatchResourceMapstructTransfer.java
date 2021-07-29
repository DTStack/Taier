package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.dto.BatchResourceAddDTO;
import com.dtstack.batch.dto.BatchResourceDTO;
import com.dtstack.batch.vo.BatchResourceVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.resource.vo.query.BatchResourceAddVO;
import com.dtstack.batch.web.resource.vo.query.BatchResourcePageQueryVO;
import com.dtstack.batch.web.resource.vo.result.BatchGetResourceByIdResultVO;
import com.dtstack.batch.web.resource.vo.result.BatchGetResourcesResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BatchResourceMapstructTransfer {

    BatchResourceMapstructTransfer INSTANCE = Mappers.getMapper(BatchResourceMapstructTransfer.class);

    /**
     * BatchResourcePageQueryVO --> BatchResourceDTO
     *
     * @param engineVO
     * @return
     */
    BatchResourceDTO resourceVOToResourceDTO(BatchResourcePageQueryVO engineVO);

    /**
     * BatchResourceAddVO --> BatchResourceAddDTO
     *
     * @param batchResourceAddVO
     * @return batchResourceAddDTO
     */
    BatchResourceAddDTO resourceVOToResourceAddDTO(BatchResourceAddVO batchResourceAddVO);

    /**
     * List<BatchResource> --> List<BatchGetResourcesResultVO>
     *
     * @param batchResource
     * @return
     */
    List<BatchGetResourcesResultVO> batchResourceListToBatchGetResourcesResultVOList(List<BatchResource> batchResource);

    /**
     * BatchResourceVO --> BatchGetResourceByIdResultVO
     *
     * @param batchResourceVO
     * @return
     */
    BatchGetResourceByIdResultVO batchResourceVOToBatchGetResourceByIdResultVO(BatchResourceVO batchResourceVO);

    /**
     * BatchResource --> BatchGetResourcesResultVO
     *
     * @param batchResource
     * @return
     */
    BatchGetResourcesResultVO batchResourceToBatchGetResourcesResultVO(BatchResource batchResource);

    /**
     * PageResult<List<BatchResourceVO>> --> ageResult<List<BatchGetResourceByIdResultVO>>
     *
     * @param pageResult
     * @return batchResourceAddDTO
     */
    PageResult<List<BatchGetResourceByIdResultVO>> pageResultToBatchGetResourceByIdResultVOPageResult(PageResult<List<BatchResourceVO>> pageResult);
}
