package com.dtstack.batch.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.batch.mapstruct.vo.DataSourceMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.vo.BatchTableMetaInfoDTO;
import com.dtstack.batch.vo.DataSourceTypeVO;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.batch.vo.FtpRegexVO;
import com.dtstack.batch.web.datasource.vo.query.*;
import com.dtstack.batch.web.datasource.vo.result.*;
import com.dtstack.batch.web.pager.PageResult;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Api(value = "数据源管理", tags = {"数据源管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchDataSource")
public class BatchDataSourceController {

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @PostMapping(value = "canSetIncreConf")
    @ApiOperation(value = "判断任务是否可以配置增量标识")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    public R<Boolean> canSetIncreConf(@RequestBody BatchDataSourceIncreVO vo) {
        return new APITemplate<Boolean>() {
            @Override
            protected Boolean process() {
                return batchDataSourceService.canSetIncreConf(vo.getId());
            }
        }.execute();
    }

    @PostMapping(value = "trace")
    @ApiOperation(value = "追踪")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<JSONObject> trace(@RequestBody BatchDataSourceTraceVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchDataSourceService.trace(vo.getTaskId());
            }
        }.execute();
    }

    @PostMapping(value = "list")
    @ApiOperation(value = "获得项目下所有数据源")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<BatchDataSourceResultVO>> list(@RequestBody(required = false) BatchDataSourceListVO vo) {
        return new APITemplate< List<BatchDataSourceResultVO>>() {
            @Override
            protected  List<BatchDataSourceResultVO> process() {
                List<DataSourceVO> list = batchDataSourceService.list(vo.getProjectId());
                return DataSourceMapstructTransfer.INSTANCE.newListDataSourceVoToDataSourceResultVo(list);
            }
        }.execute();
    }

    @PostMapping(value = "tableLocation")
    @ApiOperation(value = "获取表的底层存储信息")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<JSONObject> tableLocation(@RequestBody BatchDataSourceTableLocationVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchDataSourceService.tableLocation(vo.getSourceId(), vo.getTableName());
            }
        }.execute();
    }

    @PostMapping(value = "tablelist")
    @ApiOperation(value = "获取表列表")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<String>> tablelist(@RequestBody(required = false) BatchDataSourceTableListVO sourceVO) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return batchDataSourceService.tablelist(sourceVO.getProjectId(), sourceVO.getSourceId(),
                        sourceVO.getTenantId(), sourceVO.getSchema(), sourceVO.getName(), sourceVO.getIsAll(),
                        sourceVO.getIsRead());
            }
        }.execute();
    }

    @PostMapping(value = "tablecolumn")
    @ApiOperation(value = "获取表字段信息")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<JSONObject>> tablecolumn(@RequestBody BatchDataSourceTableColumnVO vo) {
        return new APITemplate<List<JSONObject>>() {
            @Override
            protected List<JSONObject> process() {
                return batchDataSourceService.tablecolumn(vo.getProjectId(), vo.getUserId(), vo.getSourceId(), vo.getTableName(), vo.getIsIncludePart(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "getHivePartitions")
    @ApiOperation(value = "获取hive分区")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<Set<String>> getHivePartitions(@RequestBody BatchDataSourceTableLocationVO vo) {
        return new APITemplate<Set<String>>() {
            @Override
            protected Set<String> process() {
                return batchDataSourceService.getHivePartitions(vo.getSourceId(), vo.getTableName());
            }
        }.execute();
    }

    @PostMapping(value = "getIncreColumn")
    @ApiOperation(value = "获取可以作为增量标识的字段")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<JSONObject>> getIncreColumn(@RequestBody(required = false) BatchDataSourceIncreColumnVO vo) {
        return new APITemplate<List<JSONObject>>() {
            @Override
            protected List<JSONObject> process() {
                return batchDataSourceService.getIncreColumn(vo.getSourceId(), vo.getTableName(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "preview")
    @ApiOperation(value = "获取预览数据")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<JSONObject> preview(@RequestBody BatchDataSourcePreviewVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchDataSourceService.preview(vo.getProjectId(), vo.getUserId(), vo.getSourceId(), vo.getTableName(),
                        vo.getPartition(), vo.getTenantId(), vo.getDtuicTenantId(), vo.getIsRoot(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "getBySourceId")
    @ApiOperation(value = "根据ID获取数据源信息")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<BatchDataSourceResultVO> getBySourceId(@RequestBody BatchDataSourceGetVO vo) {
        return new APITemplate<BatchDataSourceResultVO>() {
            @Override
            protected BatchDataSourceResultVO process() {
                DataSourceVO sourceVO = batchDataSourceService.getBySourceId(vo.getSourceId());
                return DataSourceMapstructTransfer.INSTANCE.newDataSourceVoToDataSourceResultVo(sourceVO);
            }
        }.execute();
    }

    @PostMapping(value = "getTypes")
    @ApiOperation(value = "获取数据源类型")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<BatchDataSourceTypeResultVO>> getTypes(@RequestBody(required = false) BatchDataSourceListVO vo) {
        return new APITemplate<List<BatchDataSourceTypeResultVO>>() {
            @Override
            protected List<BatchDataSourceTypeResultVO> process() {
                List<DataSourceTypeVO> list = batchDataSourceService.getTypes();
                return DataSourceMapstructTransfer.INSTANCE.newDataSourceTypeVoToDataSourceTypeResultVo(list);
            }
        }.execute();
    }


    @PostMapping(value = "getAnalysisSource")
    @ApiOperation(value = "获取分析数据源信息")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<BatchDataSourceResultVO>> getAnalysisSource(@RequestBody(required = false) BatchDataSourceListVO vo) {
        return new APITemplate<List<BatchDataSourceResultVO>>() {
            @Override
            protected List<BatchDataSourceResultVO> process() {
                List<DataSourceVO> list = batchDataSourceService.getAnalysisSource(vo.getTenantId(), vo.getProjectId());
                return DataSourceMapstructTransfer.INSTANCE.newListDataSourceVoToDataSourceResultVo(list);
            }
        }.execute();
    }


    @PostMapping(value = "linkDataSource")
    @ApiOperation(value = "关联数据源")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_EDIT)
    public R<Void> linkDataSource(@RequestBody BatchDataSourceLinkVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchDataSourceService.linkDataSource(vo.getTenantId(), vo.getProjectId(), vo.getSourceId(), vo.getLinkSourceId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "parseDataJsonForView")
    @ApiOperation(value = "对外展示数据源信息")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<JSONObject> parseDataJsonForView(@RequestBody BatchDataSourceBaseVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchDataSourceService.parseDataJsonForView(DataSourceMapstructTransfer.INSTANCE.newDataSourceBaseVOToDataSourceVo(vo));
            }
        }.execute();
    }

    @PostMapping(value = "getSourceTaskRef")
    @ApiOperation(value = "获取使用该数据源任务列表")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<PageResult<List<BatchDataSourceTaskRefResultVO>>> getSourceTaskRef(@RequestBody BatchDataSourceTaskRefVO vo) {
        return new APITemplate<PageResult<List<BatchDataSourceTaskRefResultVO>>>() {
            @Override
            protected PageResult<List<BatchDataSourceTaskRefResultVO>> process() {
                PageResult<List<JSONObject>> sourceTaskRef = batchDataSourceService.getSourceTaskRef(vo.getSourceId(), vo.getPageSize(), vo.getCurrentPage(), vo.getTaskName());
                return JSONObject.parseObject(JSON.toJSONString(sourceTaskRef), new TypeReference<PageResult<List<BatchDataSourceTaskRefResultVO>>>(){});
            }
        }.execute();
    }

    @PostMapping(value = "columnForSyncopate")
    @ApiOperation(value = "返回切分键需要的列名")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<Set<JSONObject>> columnForSyncopate(@RequestBody BatchDataSourceColumnForSyncopateVO vo) {
        return new APITemplate<Set<JSONObject>>() {
            @Override
            protected Set<JSONObject> process() {
                return batchDataSourceService.columnForSyncopate(vo.getProjectId(), vo.getUserId(), vo.getSourceId(), vo.getTableName(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "getDataSourceInBingProject")
    @ApiOperation(value = "获取绑定项目下的数据源")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<JSONObject> getDataSourceInBingProject(@RequestBody BatchDataSourceBindVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchDataSourceService.getDataSourceInBingProject(vo.getTenantId(), vo.getProjectId(), vo.getDataSourceId());
            }
        }.execute();
    }

    @PostMapping(value = "convertToHiveColumns")
    @ApiOperation(value = "字段类型转成hive类型")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<JSONObject> convertToHiveColumns(@RequestBody BatchDataSourceColumnsVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchDataSourceService.convertToHiveColumns(vo.getColumns());
            }
        }.execute();
    }

    @PostMapping(value = "isNativeHive")
    @ApiOperation(value = "判断是否是标准分区表")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<Boolean> isNativeHive(@RequestBody BatchDataSourceTableLocationVO vo) {
        return new APITemplate<Boolean>() {
            @Override
            protected Boolean process() {
                return batchDataSourceService.isNativeHive(vo.getSourceId(), vo.getTableName(), vo.getTenantId());
            }
        }.execute();
    }

    @PostMapping(value = "getDataSourcePassword")
    @ApiOperation(value = "获取指定数据源密码")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_EDIT)
    public R<String> getDataSourcePassword(@RequestBody BatchDataSourceGetVO vo) {
        return new APITemplate<String>() {
            @Override
            protected String process() {
                return batchDataSourceService.getDataSourcePassword(vo.getSourceId(), vo.getTenantId(), vo.getProjectId());
            }
        }.execute();
    }

    @PostMapping(value = "getAllSchemas")
    @ApiOperation(value = "获取所有schema")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<List<String>> getAllSchemas(@RequestBody BatchDataSourceGetVO vo) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return batchDataSourceService.getAllSchemas(vo.getSourceId(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "checkPermission")
    @ApiOperation(value = "检查权限")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_EDIT)
    public R<Void> checkPermission() {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchDataSourceService.checkPermission();
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getTableInfoByDataSource")
    @ApiOperation(value = "根据数据源获取表信息")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<BatchDataSourceTableInfoResultVO> getTableInfoByDataSource(@RequestBody BatchDataSourceTableInfoQueryVO vo) {
        return new APITemplate<BatchDataSourceTableInfoResultVO>() {
            @Override
            protected BatchDataSourceTableInfoResultVO process() {
                BatchTableMetaInfoDTO tableMetaInfoDTO = batchDataSourceService.getTableInfoByDataSource(vo.getDataSourceId(), vo.getSchema(), vo.getTableName());
                return DataSourceMapstructTransfer.INSTANCE.tableInfoToBatchDataSourceTableInfoResultVO(tableMetaInfoDTO);
            }
        }.execute();
    }

    @PostMapping(value = "ftpRegexPre")
    @ApiOperation(value = "根据ftp 正则表达式 获取 匹配的记录")
    public R<BatchFtpPreResultVO> ftpRegexPre(@RequestBody BatchFtpPreQueryVO vo) {
        return new APITemplate<BatchFtpPreResultVO>() {
            @Override
            protected BatchFtpPreResultVO process() {
                FtpRegexVO ftpRegexVO = batchDataSourceService.ftpRegexPre(vo.getTaskParamList(), vo.getSourceId(), vo.getRegexStr());
                return DataSourceMapstructTransfer.INSTANCE.ftpRegexVOToBatchFtpPreResultVO(ftpRegexVO);
            }
        }.execute();
    }


    @PostMapping(value = "queryAllowImportDataSource")
    @ApiOperation(value = "获取所有可引入的数据源列表")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<PageResult<List<BatchDataSourceAllowImportResultVO>>> queryAllowImportDataSource(@RequestBody BatchDataSourceAllowImportVO vo) {
        return new APITemplate<PageResult<List<BatchDataSourceAllowImportResultVO>>>() {
            @Override
            protected PageResult<List<BatchDataSourceAllowImportResultVO>> process() {
                return batchDataSourceService.queryAllowImportDataSource(vo);
            }
        }.execute();
    }

    @PostMapping(value = "queryHaveImportedDataSource")
    @ApiOperation(value = "获取所有已经引入的数据源列表")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<PageResult<List<BatchDataSourceHaveImportResultVO>>> queryHaveImportedDataSource(@RequestBody BatchDataSourceHaveImportVO vo) {
        return new APITemplate<PageResult<List<BatchDataSourceHaveImportResultVO>>>() {
            @Override
            protected PageResult<List<BatchDataSourceHaveImportResultVO>> process() {
                return batchDataSourceService.queryHaveImportedDataSourceView(vo);
            }
        }.execute();
    }


    @PostMapping(value = "importDataSource")
    @ApiOperation(value = "引入数据源")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<Void> importDataSource(@RequestBody BatchDataSourceImportVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchDataSourceService.importDataSource(vo);
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "cancelImportDataSource")
    @ApiOperation(value = "取消引入数据源")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<Void> cancelImportDataSource(@RequestBody BatchDataSourceCancelImportVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchDataSourceService.cancelImportDataSource(vo);
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getDataSourceVersion")
    @ApiOperation(value = "查询数据源的版本")
    @Security(code = AuthCode.DATAINTEGRATION_BATCH_QUERY)
    public R<String> getDataSourceVersion(@RequestBody BatchDataSourceVersionQueryVO vo) {
        return new APITemplate<String>() {
            @Override
            protected String process() {
                return batchDataSourceService.getDataSourceVersion(vo.getDataSourceId());
            }
        }.execute();
    }

}
