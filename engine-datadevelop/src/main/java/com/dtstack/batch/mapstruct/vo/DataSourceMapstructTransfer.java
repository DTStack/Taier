/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchTableMetaInfoDTO;
import com.dtstack.batch.vo.DataSourceTypeVO;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.batch.vo.FtpRegexVO;
import com.dtstack.batch.web.datasource.vo.query.BatchDataSourceAddVO;
import com.dtstack.batch.web.datasource.vo.query.BatchDataSourceBaseVO;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceResultVO;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceTableInfoResultVO;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceTypeResultVO;
import com.dtstack.batch.web.datasource.vo.result.BatchFtpPreResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DataSourceMapstructTransfer {
    DataSourceMapstructTransfer INSTANCE = Mappers.getMapper(DataSourceMapstructTransfer.class);

    /**
     * BatchDataSourceAddVO  ->  DataSourceVO
     *
     * @param vo
     * @return
     */
    DataSourceVO newDataSourceAddVoToDataSourceVo(BatchDataSourceAddVO vo);

    /**
     * BatchDataSourceBaseVO  ->  DataSourceVO
     *
     * @param vo
     * @return
     */
    DataSourceVO newDataSourceBaseVOToDataSourceVo(BatchDataSourceBaseVO vo);

    /**
     * DataSourceVO  ->  BatchDataSourceResultVO
     *
     * @param vo
     * @return
     */
    BatchDataSourceResultVO newDataSourceVoToDataSourceResultVo(DataSourceVO vo);

    /**
     * List<DataSourceVO>  ->  List<BatchDataSourceResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDataSourceResultVO> newListDataSourceVoToDataSourceResultVo(List<DataSourceVO> list);

    /**
     * PageResult<List<DataSourceVO>>  ->  PageResult<List<BatchDataSourceResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDataSourceResultVO>> newPageDataSourceVoToDataSourceResultVo(PageResult<List<DataSourceVO>> result);

    /**
     * List<DataSourceTypeVO>  ->  List<BatchDataSourceTypeResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDataSourceTypeResultVO> newDataSourceTypeVoToDataSourceTypeResultVo(List<DataSourceTypeVO> list);


    /**
     * batchTableMetaInfoDTO -> BatchDataSourceTableInfoResultVO
     *
     * @param batchTableMetaInfoDTO
     * @return
     */
    BatchDataSourceTableInfoResultVO tableInfoToBatchDataSourceTableInfoResultVO(BatchTableMetaInfoDTO batchTableMetaInfoDTO);

    /**
     * ftpRegexVO -> BatchFtpPreResultVO
     *
     * @param ftpRegexVO
     * @return
     */
    BatchFtpPreResultVO ftpRegexVOToBatchFtpPreResultVO(FtpRegexVO ftpRegexVO);

}
