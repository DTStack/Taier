package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.dto.BatchPackageCreateDTO;
import com.dtstack.batch.dto.BatchPackageQueryDTO;
import com.dtstack.batch.vo.BatchPackageItemStatusVO;
import com.dtstack.batch.vo.BatchPackageItemVO;
import com.dtstack.batch.vo.BatchPackageVO;
import com.dtstack.batch.vo.PackageCheckResultVO;
import com.dtstack.batch.web.testproduct.vo.query.BatchPackageCreatePackageVO;
import com.dtstack.batch.web.testproduct.vo.query.BatchPackagePageQueryVO;
import com.dtstack.batch.web.testproduct.vo.result.BatchPackageItemResultVO;
import com.dtstack.batch.web.testproduct.vo.result.BatchPackageItemStatusResultVO;
import com.dtstack.batch.web.testproduct.vo.result.BatchPackageResultVO;
import com.dtstack.batch.web.testproduct.vo.result.BatchPackageUploadPackageResultVO;
import com.dtstack.engine.api.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BatchPackageMapstructTransfer {

    BatchPackageMapstructTransfer INSTANCE = Mappers.getMapper(BatchPackageMapstructTransfer.class);

    /**
     * BatchPackageCreatePackageVO -> BatchPackageCreateDTO
      * @param batchPackageCreatePackageVO
     * @return
     */
    BatchPackageCreateDTO BatchPackageCreatePackageVOToBatchPackageCreateDTO(BatchPackageCreatePackageVO batchPackageCreatePackageVO);


    /**
     * BatchPackagePageQueryVO -> BatchPackageQueryDTO
     * @param queryVO
     * @return
     */
    BatchPackageQueryDTO  BatchPackagePageQueryVOToBatchPackageQueryDTO(BatchPackagePageQueryVO queryVO);

    /**
     * PageResult<List<BatchPackageItemVO>> -> PageResult<List<BatchPackageItemResultVO>>
     * @param listPageResult
     * @return
     */
    com.dtstack.batch.web.pager.PageResult<List<BatchPackageItemResultVO>> BatchPackageItemPageListVOToResultVO(PageResult<List<BatchPackageItemVO>> listPageResult);

    /**
     * BatchPackageItemStatusVO -> BatchPackageItemStatusResultVO
     * @param vo
     * @return
     */
    BatchPackageItemStatusResultVO BatchPackageItemStatusVOToResultVO(BatchPackageItemStatusVO vo);

    /**
     * PageResult<List<BatchPackageVO>>  -> PageResult<List<BatchPackageResultVO>>
     * @param pageResult
     * @return
     */
    com.dtstack.batch.web.pager.PageResult<List<BatchPackageResultVO>> BatchPackagePageListVOToResultPageListVO(com.dtstack.batch.web.pager.PageResult<List<BatchPackageVO>> pageResult);

    /**
     * BatchPackageVO -> BatchPackageResultVO
     * @param vo
     * @return
     */
    BatchPackageResultVO BatchPackageVOToResultVO(BatchPackageVO vo);

    /**
     * PackageCheckResultVO -> BatchPackageUploadPackageResultVO
     * @param resultVO
     * @return
     */
    BatchPackageUploadPackageResultVO PackageCheckResultVOToBatchPackageUploadPackageResultVO(PackageCheckResultVO resultVO);
}
