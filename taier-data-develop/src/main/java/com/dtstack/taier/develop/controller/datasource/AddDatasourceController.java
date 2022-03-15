package com.dtstack.taier.develop.controller.datasource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.PubSvcDefineException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.develop.annotation.FileUpload;
import com.dtstack.taier.develop.bo.datasource.AddDataSourceParam;
import com.dtstack.taier.develop.bo.datasource.DsTypeSearchParam;
import com.dtstack.taier.develop.bo.datasource.DsVersionSearchParam;
import com.dtstack.taier.develop.common.convert.DataSourceParam2SourceVOConverter;
import com.dtstack.taier.develop.dto.devlop.DataSourceVO;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsClassifyService;
import com.dtstack.taier.develop.service.datasource.impl.DsTypeService;
import com.dtstack.taier.develop.service.datasource.impl.DsVersionService;
import com.dtstack.taier.develop.utils.Asserts;
import com.dtstack.taier.develop.vo.datasource.DsClassifyVO;
import com.dtstack.taier.develop.vo.datasource.DsTypeVO;
import com.dtstack.taier.develop.vo.datasource.DsVersionVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDataSourceColumnForSyncopateVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDataSourceGetVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDataSourcePreviewVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDataSourceTableColumnVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDataSourceTableListVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDataSourceTableLocationVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDatasourceTableCreateSQLVO;
import com.dtstack.taier.develop.vo.develop.query.BatchDatasourceTableCreateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 新增数据源相关功能控制器类
 * @description:
 * @author: liuxx
 * @date: 2021/3/8
 */
@Api(tags = {"数据源中心-新增数据源"})
@RestController
@RequestMapping(value ="/dataSource/addDs")
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
            protected Boolean process() throws RdosDefineException {
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
            protected Boolean process() throws RdosDefineException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                params.put(RESOURCE, resource);
                return datasourceService.checkConnectionWithKerberos(dataSourceVo, resource, dataSourceVo.getUserId());
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
            protected Long process() throws RdosDefineException {
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
            protected Long process() throws RdosDefineException {
                Pair<String, String> resource = (Pair<String, String>) params.get("resource");
                params.remove(RESOURCE);
                DataSourceVO dataSourceVo = PublicUtil.mapToObject(params, DataSourceVO.class);
                if (dataSourceVo == null ||
                        StringUtils.isBlank(dataSourceVo.getDataName())){
                    throw new PubSvcDefineException("dataSource name empty");
                }
                params.put(RESOURCE, resource);
                return datasourceService.addOrUpdateSourceWithKerberos(dataSourceVo, resource, dataSourceVo.getUserId(), dataSourceVo.getTenantId());
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
        return R.ok(datasourceService.getPrincipalsWithConf(dataSourceVo, resource, dataSourceVo.getUserId()));
    }

    @PostMapping(value = "tablelist")
    @ApiOperation(value = "获取表列表")
    public R<List<String>> tablelist(@RequestBody(required = false) BatchDataSourceTableListVO sourceVO) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return datasourceService.tablelist(sourceVO.getSourceId(), sourceVO.getSchema(), sourceVO.getName());
            }
        }.execute();
    }

    @PostMapping(value = "tablecolumn")
    @ApiOperation(value = "获取表字段信息")
    public R<List<JSONObject>> tablecolumn(@RequestBody BatchDataSourceTableColumnVO vo) {
        return new APITemplate<List<JSONObject>>() {
            @Override
            protected List<JSONObject> process() {
                return datasourceService.tablecolumn(vo.getUserId(), vo.getSourceId(), vo.getTableName(), vo.getIsIncludePart(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "columnForSyncopate")
    @ApiOperation(value = "返回切分键需要的列名")
    public R<Set<JSONObject>> columnForSyncopate(@RequestBody BatchDataSourceColumnForSyncopateVO vo) {
        return new APITemplate<Set<JSONObject>>() {
            @Override
            protected Set<JSONObject> process() {
                return datasourceService.columnForSyncopate(vo.getUserId(), vo.getSourceId(), vo.getTableName(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "getHivePartitions")
    @ApiOperation(value = "获取hive分区")
    public R<Set<String>> getHivePartitions(@RequestBody BatchDataSourceTableLocationVO vo) {
        return new APITemplate<Set<String>>() {
            @Override
            protected Set<String> process() {
                return datasourceService.getHivePartitions(vo.getSourceId(), vo.getTableName());
            }
        }.execute();
    }

    @PostMapping(value = "preview")
    @ApiOperation(value = "获取预览数据")
    public R<JSONObject> preview(@RequestBody BatchDataSourcePreviewVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return datasourceService.preview(vo.getSourceId(), vo.getTableName(),vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "getAllSchemas")
    @ApiOperation(value = "获取所有schema")
    public R<List<String>> getAllSchemas(@RequestBody BatchDataSourceGetVO vo) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return datasourceService.getAllSchemas(vo.getSourceId(), vo.getSchema());
            }
        }.execute();
    }

    @ApiOperation(value = "获取生成目标表建表SQL")
    @PostMapping(value = "getCreateTargetTableSql")
    public R<String> getCreateTargetTableSql(@RequestBody BatchDatasourceTableCreateSQLVO tableVO) {
        return new APITemplate<String>() {
            @Override
            protected String process() {
                return datasourceService.getCreateTargetTableSql(tableVO.getOriginSourceId(), tableVO.getTargetSourceId(),
                        tableVO.getTableName(), tableVO.getPartition(), tableVO.getOriginSchema(), tableVO.getTargetSchema());
            }
        }.execute();
    }

    @ApiOperation(value = "执行生成目标表SQL")
    @PostMapping(value = "ddlCreateTable")
    public R<String> ddlCreateTable(@RequestBody BatchDatasourceTableCreateVO tableVO) {
        return new APITemplate<String>() {
            @Override
            protected String process() {
                return datasourceService.ddlCreateTable(tableVO.getSql(), tableVO.getSourceId(), tableVO.getTargetSchema());
            }
        }.execute();
    }

}
