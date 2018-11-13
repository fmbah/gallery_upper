package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.Template;
import com.xs.services.TemplateService;
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
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "模板管理")
@RestController
@RequestMapping(value="/api/back/template")
public class TemplateController extends BaseController{
    @Autowired
    private TemplateService templateService;


    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody Template template, BindingResult bindingResult) {
        templateService.save(template);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 删除
    * @return
    */
    @ApiOperation(value = "删除",notes = "删除")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")
    })
    @DeleteMapping(value = "/delete",produces = "application/json;charset=utf-8")
    public Object delete(@RequestParam Integer id) {
        templateService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody Template template, BindingResult bindingResult) {
        templateService.update(template);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        Template template = templateService.queryTemplateInfo(id);
        return ResultGenerator.genSuccessResult(template);
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
            @ApiImplicitParam(value = "比例",name = "ratio",paramType = "query"),
            @ApiImplicitParam(value = "是否启用",name = "enabled",paramType = "query"),
            @ApiImplicitParam(value = "名称",name = "name",paramType = "query"),
            @ApiImplicitParam(value = "是否品牌",name = "isBrand",paramType = "query", required = true)
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(required = false) Integer brandId,
                       @RequestParam(required = false) Byte ratio,
                                   @RequestParam(required = false) Boolean enabled,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = true) Boolean isBrand

    ){
        return templateService.queryWithPage(page, size, enabled, ratio, categoryId, name, brandId, isBrand);
    }
}
