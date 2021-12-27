package com.dtstack.batch.controller.datasource;

import com.dtstack.batch.annotation.FileUpload;
import com.dtstack.batch.bo.datasource.AddDataSourceParam;
import com.dtstack.batch.bo.datasource.DsTypeSearchParam;
import com.dtstack.batch.bo.datasource.DsVersionSearchParam;
import com.dtstack.batch.common.convert.DataSourceParam2SourceVOConverter;
import com.dtstack.batch.common.exception.PubSvcDefineException;
import com.dtstack.batch.service.datasource.impl.DatasourceService;
import com.dtstack.batch.service.datasource.impl.DsClassifyService;
import com.dtstack.batch.service.datasource.impl.DsTypeService;
import com.dtstack.batch.service.datasource.impl.DsVersionService;
import com.dtstack.batch.utils.Asserts;
import com.dtstack.batch.utils.DataSourceUtils;
import com.dtstack.batch.utils.PublicUtil;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.batch.vo.datasource.DsClassifyVO;
import com.dtstack.batch.vo.datasource.DsTypeVO;
import com.dtstack.batch.vo.datasource.DsVersionVO;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 新增数据源相关功能控制器类
 * @description:
 * @author: liuxx
 * @date: 2021/3/8
 */
@Api(tags = {"数据源中心-新增数据源"})
@RestController
@RequestMapping(value ="/node/datasource/addDs")
public class AddDatasourceController {

    private final String RESOURCE = "resource";

    @Autowired
    private DsClassifyService dsClassifyService;

    @Autowired
    private DsTypeService dsTypeService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private DsVersionService dsVersionService;

    @ApiOperation("获取数据源分类类目列表")
    @PostMapping("/queryDsClassifyList")
    public R<List<DsClassifyVO>> queryDsClassifyList() {

        return R.ok(dsClassifyService.queryDsClassifyList());
    }


    @ApiOperation("根据分类获取数据源类型")
    @PostMapping("/queryDsTypeByClassify")
    public R<List<DsTypeVO>> queryDsTypeByClassify(@RequestBody DsTypeSearchParam searchParam) {

        return R.ok(dsTypeService.queryDsTypeByClassify(searchParam));
    }

    @ApiOperation("根据数据源类型获取版本列表")
    @PostMapping("/queryDsVersionByType")
    public R<List<DsVersionVO>> queryDsVersionByType(@RequestBody DsVersionSearchParam searchParam) {

        return R.ok(dsVersionService.queryDsVersionByType(searchParam));
    }



    @ApiOperation("测试联通性")
    @PostMapping("/testCon")
    public R<Boolean> testCon(@RequestBody AddDataSourceParam addDataSourceParam) {
        return new APITemplate<Boolean>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.hasText(addDataSourceParam.getDataType(), "数据源类型不能为空!");
            }
            @Override
            protected Boolean process() throws BizException {
                DataSourceVO dataSourceVO = new DataSourceParam2SourceVOConverter().convert(addDataSourceParam);
                return datasourceService.checkConnection(dataSourceVO);
            }
        }.execute();
    }


    @ApiOperation("上传Kerberos测试联通性")
    @PostMapping("/testConWithKerberos")
    @FileUpload
    public R<Boolean> testConWithKerberos(@ApiParam(name = "file", value = "上传文件") MultipartFile file, Map<String, Object> params) {
        return new APITemplate<Boolean>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                String dataJsonString = (String) params.get("dataJsonString");
                Map dataSourceJson = DataSourceUtils.getDataSourceJson(dataJsonString);
                String principal = (String) dataSourceJson.get("principal");
                Asserts.hasText(principal, "kerberos principle不能为空!");
            }
            @Override
            protected Boolean process() throws BizException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                params.put(RESOURCE, resource);
                return datasourceService.checkConnectionWithKerberos(dataSourceVo, resource, dataSourceVo.getProjectId(), dataSourceVo.getUserId());
            }
        }.execute();
    }



    @ApiOperation("添加和修改数据源")
    @PostMapping("/addOrUpdateSource")
    public R<Long> addOrUpdateSource(@RequestBody AddDataSourceParam addDataSourceParam) {
        return new APITemplate<Long>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                if (addDataSourceParam == null ||
                        StringUtils.isBlank(addDataSourceParam.getDataName())){
                    throw new PubSvcDefineException("dataSource name empty");
                }
            }
            @Override
            protected Long process() throws BizException {
                DataSourceVO dataSourceVO = new DataSourceParam2SourceVOConverter().convert(addDataSourceParam);
                return datasourceService.addOrUpdateSource(dataSourceVO, dataSourceVO.getUserId());
            }
        }.execute();
    }


    @ApiOperation("上传Kerberos添加和修改数据源")
    @PostMapping("/addOrUpdateSourceWithKerberos")
    @FileUpload
    public R<Long> addOrUpdateSourceWithKerberos(@ApiParam(name = "file", value = "上传文件", required = true) MultipartFile file, Map<String, Object> params) {
        return new APITemplate<Long>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                String dataJsonString = (String) params.get("dataJsonString");
                Map dataSourceJson = DataSourceUtils.getDataSourceJson(dataJsonString);
                String principal = (String) dataSourceJson.get("principal");
                Asserts.hasText(principal, "kerberos principle不能为空!");
            }
            @Override
            protected Long process() throws BizException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                if (dataSourceVo == null ||
                        StringUtils.isBlank(dataSourceVo.getDataName())){
                    throw new PubSvcDefineException("dataSource name empty");
                }
                params.put(RESOURCE, resource);
                return datasourceService.addOrUpdateSourceWithKerberos(dataSourceVo, resource, dataSourceVo.getProjectId(), dataSourceVo.getUserId(), dataSourceVo.getTenantId());
            }
        }.execute();
    }


    @ApiOperation(value = "解析kerberos文件获取principal列表")
    @PostMapping("/getPrincipalsWithConf")
    @FileUpload
    public R<List<String>> getPrincipalsWithConf(@RequestParam(value = "file", required = false) MultipartFile file, Map<String, Object> params) {
        Pair<String, String> resource = (Pair<String, String>) params.get("resource");
        params.remove(RESOURCE);
        DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
        params.put(RESOURCE, resource);
        return R.ok(datasourceService.getPrincipalsWithConf(dataSourceVo, resource, dataSourceVo.getTenantId(), dataSourceVo.getProjectId(), dataSourceVo.getUserId()));
    }

}
