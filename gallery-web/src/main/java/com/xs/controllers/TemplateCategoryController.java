package com.xs.controllers;
import com.xs.beans.TemplateCategory1;
import com.xs.core.ResultGenerator;
import com.xs.beans.TemplateCategory;
import com.xs.services.TemplateCategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.core.scontroller.BaseController;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "模板分类功能")
@RestController
@RequestMapping(value="/api/back/template/category")
public class TemplateCategoryController extends BaseController{
    @Autowired
    private TemplateCategoryService templateCategoryService;


    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody TemplateCategory templateCategory, BindingResult bindingResult) {
        templateCategoryService.save(templateCategory);
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
        templateCategoryService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody TemplateCategory templateCategory, BindingResult bindingResult) {
        templateCategoryService.update(templateCategory);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "修改滤镜",notes = "修改滤镜")
    @PostMapping(value = "/updateFilters/{id}/",produces = "application/json;charset=utf-8")
    public Object updateFilters(@PathVariable Integer id, @RequestParam String filters) {
        return templateCategoryService.updateFilters(id, filters);
    }

    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        TemplateCategory templateCategory = templateCategoryService.findById(id);
        return ResultGenerator.genSuccessResult(templateCategory);
    }

    /***
     * 品牌中心分类数据详情
     * @return
     */
    @ApiOperation(value = "品牌中心分类数据详情",notes = "品牌中心分类数据详情")
    @GetMapping(value = "/getBrandCenterCategoryInfo",produces = "application/json;charset=utf-8")
    public Object getBrandCenterCategoryInfo() {
        return templateCategoryService.getBrandCenterCategoryInfo();
    }

    /***
     * 品牌中心数据保存
     * @return
     */
    @ApiOperation(value = "品牌中心数据保存",notes = "品牌中心数据保存")
    @PostMapping(value = "/saveBrandCenterData",produces = "application/json;charset=utf-8")
    public Object saveBrandCenterData(@Valid @RequestBody TemplateCategory1 templateCategory1) {
        return templateCategoryService.saveBrandCenterData(templateCategory1.getTitle(), templateCategory1.getIntroduction(), templateCategory1.getBackgroundImageUrl(), templateCategory1.getId());
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "是否热门",name = "isHot",paramType = "query"),
            @ApiImplicitParam(value = "名称",name = "title",paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,@RequestParam(required = false,defaultValue = "0") Integer size
                       ,@RequestParam(required = false) Boolean isHot,
                       @RequestParam(required = false) String title
    ){
        return templateCategoryService.queryWithPage(page, size, isHot, title);
    }
}
