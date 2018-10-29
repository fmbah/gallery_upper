package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.TemplateStatistics;
import com.xs.services.TemplateStatisticsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.core.scontroller.BaseController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "统计管理")
@RestController
@RequestMapping(value="/api/back/template/statistics")
public class TemplateStatisticsController extends BaseController{
    @Autowired
    private TemplateStatisticsService templateStatisticsService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody TemplateStatistics templateStatistics, BindingResult bindingResult) {
//        templateStatisticsService.save(templateStatistics);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 删除
//    * @return
//    */
//    @ApiOperation(value = "删除",notes = "删除")
//    @ApiImplicitParams({
//    @ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")
//    })
//    @DeleteMapping(value = "/delete",produces = "application/json;charset=utf-8")
//    public Object delete(@RequestParam Integer id) {
//        templateStatisticsService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody TemplateStatistics templateStatistics, BindingResult bindingResult) {
//        templateStatisticsService.update(templateStatistics);
//        return ResultGenerator.genSuccessResult();
//    }
//
    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        TemplateStatistics templateStatistics = templateStatisticsService.findById(id);
        return ResultGenerator.genSuccessResult(templateStatistics);
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "分类id",name = "categoryId",paramType = "query"),
            @ApiImplicitParam(value = "品牌id",name = "brandId",paramType = "query"),
            @ApiImplicitParam(value = "开始时间",name = "sTime",paramType = "query"),
            @ApiImplicitParam(value = "结束时间",name = "eTime",paramType = "query"),
            @ApiImplicitParam(value = "名称",name = "name",paramType = "query"),
            @ApiImplicitParam(value = "是否品牌",name = "isBrand",paramType = "query", required = true),
            @ApiImplicitParam(value = "是否导出",name = "isExport",paramType = "query", required = true)
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(required = false) Integer brandId,
                       @RequestParam(required = false) String sTime,
                       @RequestParam(required = false) String eTime,
                       @RequestParam(required = false) String name,
                       @RequestParam Boolean isBrand
            , @RequestParam Boolean isExport

    ){
        return templateStatisticsService.queryWithPage(page, size, categoryId, name, brandId, isBrand, sTime, eTime, isExport);
    }


    /***
     * 统计分类数据
     * @return
     */
    @ApiOperation(value = "统计分类数据",notes = "统计分类数据")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "开始时间",name = "sTime",paramType = "query"),
            @ApiImplicitParam(value = "结束时间",name = "eTime",paramType = "query")
    })
    @GetMapping(value = "/queryCategoryDatas",produces = "application/json;charset=utf-8")
    public Object queryCategoryDatas(
                       @RequestParam String sTime,
                       @RequestParam String eTime

    ){
        return ResultGenerator.genSuccessResult(templateStatisticsService.queryCategoryDatas(sTime, eTime));
    }
}
