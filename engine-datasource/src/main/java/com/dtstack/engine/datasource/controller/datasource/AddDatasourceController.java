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

package com.dtstack.engine.datasource.controller.datasource;

import com.dtstack.engine.common.exception.BizException;
import dt.insight.plat.lang.coc.template.APITemplate;
import com.dtstack.engine.datasource.common.annotation.FileUpload;
import com.dtstack.engine.datasource.common.utils.PublicUtil;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.converter.datasource.DataSourceParam2SourceVOConverter;
import com.dtstack.engine.datasource.facade.datasource.DatasourceFacade;
import com.dtstack.engine.datasource.param.datasource.AddDataSourceParam;
import com.dtstack.engine.datasource.param.datasource.DsTypeSearchParam;
import com.dtstack.engine.datasource.param.datasource.DsTypeVersionParam;
import com.dtstack.engine.datasource.param.datasource.DsVersionSearchParam;
import com.dtstack.engine.datasource.service.impl.datasource.DsClassifyService;
import com.dtstack.engine.datasource.service.impl.datasource.DsTypeService;
import com.dtstack.engine.datasource.service.impl.datasource.DsVersionService;
import com.dtstack.engine.datasource.vo.datasource.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Api(tags = {"数据源中心-新增数据源"})
@RestController
@RequestMapping(value = "/api/publicService/addDs")
public class AddDatasourceController {

    private final String RESOURCE = "resource";

    @Autowired
    private DsClassifyService dsClassifyService;

    @Autowired
    private DsTypeService dsTypeService;

    @Autowired
    private DatasourceFacade datasourceFacade;

    @Autowired
    private DsVersionService dsVersionService;

    @ApiOperation("获取数据源分类类目列表")
    @PostMapping("/queryDsClassifyList")
    public R<List<DsClassifyVO>> queryDsClassifyList() {
        return new APITemplate<List<DsClassifyVO>>() {

            @Override
            protected List<DsClassifyVO> process() throws BizException {
                return dsClassifyService.queryDsClassifyList();
            }
        }.execute();
    }


    @ApiOperation("根据分类获取数据源类型")
    @PostMapping("/queryDsTypeByClassify")
    public R<List<DsTypeVO>> queryDsTypeByClassify(@RequestBody DsTypeSearchParam searchParam) {
        return new APITemplate<List<DsTypeVO>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                super.checkParams();
            }

            @Override
            protected List<DsTypeVO> process() throws BizException {
                return dsTypeService.queryDsTypeByClassify(searchParam);
            }
        }.execute();
    }

    @ApiOperation("根据数据源类型获取版本列表")
    @PostMapping("/queryDsVersionByType")
    public R<List<DsVersionVO>> queryDsVersionByType(@RequestBody DsVersionSearchParam searchParam) {
        return new APITemplate<List<DsVersionVO>>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                super.checkParams();
            }

            @Override
            protected List<DsVersionVO> process() throws BizException {
                return dsVersionService.queryDsVersionByType(searchParam);
            }
        }.execute();
    }

    @ApiOperation("获取数据源与租户交集的产品列表")
    @PostMapping("/queryAppList")
    public R<List<DsAppListVO>> queryAppList(@RequestBody DsTypeVersionParam param) {
        return new APITemplate<List<DsAppListVO>>() {
            //TODO 租户购买的产品和数据源对应的产品交集列表

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.hasText(param.getDtToken(), "用户token不能为空!");
                Asserts.notNull(param.getTenantId(), "用户Dtuic租户Id不能为空!");
                Asserts.hasText(param.getDataType(), "数据源类型不能为空!");
            }

            @Override
            protected List<DsAppListVO> process() throws BizException {
                return datasourceFacade.queryAppList(param);
            }
        }.execute();
    }


    @ApiOperation("测试联通性")
    @PostMapping("/testCon")
    public R<Boolean> testCon(@RequestBody AddDataSourceParam addDataSourceParam) {
        return new APITemplate<Boolean>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.hasText(addDataSourceParam.getDataType(), "数据源类型不能为空!");
//                Asserts.hasText(addDataSourceParam.getDataJsonString(), "表单填写内容不能为空");
//                Asserts.notNull(addDataSourceParam.getDataJson(), "表单填写内容不能为空!");
            }

            @Override
            protected Boolean process() throws BizException {
                DataSourceVO dataSourceVO = new DataSourceParam2SourceVOConverter().convert(addDataSourceParam);
                return datasourceFacade.checkConnection(dataSourceVO, dataSourceVO.getDtuicTenantId(), dataSourceVO.getUserId(), dataSourceVO.getProjectId());
            }
        }.execute();
    }


    @ApiOperation("上传Kerberos测试联通性")
    @PostMapping("/testConWithKerberos")
    @FileUpload
    public R<Boolean> testConWithKerberos(@ApiParam(name = "file", value = "上传文件") MultipartFile file, Map<String, Object> params) {
        return new APITemplate<Boolean>() {

            @Override
            protected Boolean process() throws BizException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                params.put(RESOURCE, resource);
                return datasourceFacade.checkConnectionWithKerberos(dataSourceVo, resource, dataSourceVo.getDtuicTenantId(), dataSourceVo.getProjectId(), dataSourceVo.getUserId());
            }
        }.execute();
    }


    @ApiOperation("添加和修改数据源")
    @PostMapping("/addOrUpdateSource")
    public R<Long> addOrUpdateSource(@RequestBody AddDataSourceParam addDataSourceParam) {
        return new APITemplate<Long>() {

            @Override
            protected Long process() throws BizException {
                DataSourceVO dataSourceVO = new DataSourceParam2SourceVOConverter().convert(addDataSourceParam);
                return datasourceFacade.addOrUpdateSource(dataSourceVO, dataSourceVO.getProjectId(), dataSourceVO.getUserId(), dataSourceVO.getDtuicTenantId());
            }
        }.execute();
    }


    @ApiOperation("上传Kerberos添加和修改数据源")
    @PostMapping("/addOrUpdateSourceWithKerberos")
    @FileUpload
    public R<Long> addOrUpdateSourceWithKerberos(@ApiParam(name = "file", value = "上传文件", required = true) MultipartFile file, Map<String, Object> params) {
        return new APITemplate<Long>() {

            @Override
            protected Long process() throws BizException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                params.put(RESOURCE, resource);
                return datasourceFacade.addOrUpdateSourceWithKerberos(dataSourceVo, resource, dataSourceVo.getProjectId(), dataSourceVo.getUserId(), dataSourceVo.getDtuicTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "解析kerberos文件获取principal列表")
    @PostMapping("/getPrincipalsWithConf")
    @FileUpload
    public R<List<String>> getPrincipalsWithConf(@RequestParam(value = "file", required = false) MultipartFile file, Map<String, Object> params) {
        return new APITemplate<List<String>>() {

            @Override
            protected List<String> process() throws BizException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                params.put(RESOURCE, resource);
                return datasourceFacade.getPrincipalsWithConf(dataSourceVo, resource, dataSourceVo.getDtuicTenantId(), dataSourceVo.getProjectId(), dataSourceVo.getUserId());
            }
        }.execute();
    }


    public static void main(String[] args) throws IOException {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("appTypeList", "[1,2]");
        DataSourceVO vo = new DataSourceVO();
        vo.setAppTypeList(Arrays.asList(1, 2));
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(Arrays.asList(1, 2)));
        List list = objectMapper.readValue(objectMapper.writeValueAsString(Arrays.asList(1, 2)), List.class);
        DataSourceVO dataSourceVO = PublicUtil.mapToObject(map, DataSourceVO.class);
        System.out.println(dataSourceVO);
    }

}
