package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.lineage.impl.LineageDataSourceService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author tengzhen
 * @Description: 数据源信息
 * @Date: Created in 2:04 下午 2020/11/3
 */
@RestController
@RequestMapping("/node/dataSource")
@Api(value = "/node/dataSource", tags = {"数据源接口"})
public class LineageDataSourceController {

    @Autowired
    private LineageDataSourceService dataSourceService;

    @RequestMapping(value = "/addOrUpdateDataSource",method = RequestMethod.POST)
    @ApiOperation(value = "新增或修改逻辑数据源")
    public void addOrUpdateDataSource(@RequestBody DataSourceDTO dataSourceDTO){

    }

    /**
     * @author zyd
     * @Description 根据appType分页查询逻辑数据源列表
     * @Date 2020/10/30 11:55 上午
     * @param appType:
     * @return: java.util.List<com.dtstack.engine.api.domain.LineageDataSource>
     **/
    @RequestMapping(value = "/pageQuery",method = RequestMethod.POST)
    @ApiOperation(value = "分页查找逻辑数据源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appType", value = "应用类型"),
            @ApiImplicitParam(name = "currentPage", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数")
    }
    )
    PageResult<List<LineageDataSource>> pageQueryDataSourceByAppType(@DtRequestParam("appType") Integer appType,
                                                                     @DtRequestParam("currentPage") Integer currentPage,
                                                                     @DtRequestParam("pageSize") Integer pageSize){

        return null;
    }


    /**
     * @author zyd
     * @Description 根据id查询逻辑数据源信息
     * @Date 2020/10/30 2:25 下午
     * @param id:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    @RequestMapping(value = "/queryById",method = RequestMethod.POST)
    @ApiOperation(value = "根据id查找逻辑数据源")
    @ApiImplicitParam(name="id",value = "逻辑数据源id")
    LineageDataSource getDataSourceById(Integer id){
        return null;
    }
}
