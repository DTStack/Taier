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

package com.dtstack.taier.develop.mapstruct.vo;

import com.dtstack.taier.dao.domain.BatchCatalogue;
import com.dtstack.taier.develop.dto.devlop.BatchCatalogueVO;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.vo.develop.query.BatchCatalogueAddVO;
import com.dtstack.taier.develop.vo.develop.query.BatchCatalogueUpdateVO;
import com.dtstack.taier.develop.vo.develop.result.BatchCatalogueResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchCatalogueMapstructTransfer {
    BatchCatalogueMapstructTransfer INSTANCE = Mappers.getMapper(BatchCatalogueMapstructTransfer.class);

    /**
     * IdeCatalogueAddVO -> BatchCatalogue
     *
     * @param vo
     * @return
     */
    BatchCatalogue newCatalogueAddVoToCatalogueVo(BatchCatalogueAddVO vo);

    /**
     * IdeCatalogueUpdateVO ->BatchCatalogueVO
     *
     * @param vo
     * @return
     */
    BatchCatalogueVO newCatalogueUpdateVoToCatalogueVo(BatchCatalogueUpdateVO vo);


    /**
     * CatalogueVO  ->  BatchCatalogueResultVO
     *
     * @param vo
     * @return
     */
    BatchCatalogueResultVO newCatalogueVoToCatalogueResultVo(CatalogueVO vo);
}
