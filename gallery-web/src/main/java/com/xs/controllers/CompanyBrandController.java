package com.xs.controllers;

import com.xs.beans.CompanyBrand;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.CompanyBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "品牌管理")
@RestController
@RequestMapping(value="/api/back/company/brand")
public class CompanyBrandController extends BaseController{
    @Autowired
    private CompanyBrandService companyBrandService;


    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody CompanyBrand companyBrand, BindingResult bindingResult) {
        companyBrandService.save(companyBrand);
        return ResultGenerator.genSuccessResult();
    }

    /***
     * 新增激活码
     * @return
     */
    @ApiOperation(value = "新增激活码",notes = "新增激活码")
    @PutMapping(value = "/addCdkey/{num}/{brandId}",produces = "application/json;charset=utf-8")
    public Object addCdkey(@PathVariable Integer num,
                           @PathVariable Integer brandId) {
        return companyBrandService.addCdkey(brandId, num);
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


        companyBrandService.deleteById(id);



        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody CompanyBrand companyBrand, BindingResult bindingResult) {
        companyBrandService.update(companyBrand);
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

        CompanyBrand companyBrand = companyBrandService.findById(id);

        return ResultGenerator.genSuccessResult(companyBrand);
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "联系人",name = "contactPerson",paramType = "query"),
            @ApiImplicitParam(value = "联系方式",name = "contactPhone",paramType = "query"),
            @ApiImplicitParam(value = "名称",name = "name",paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) String contactPerson,
                       @RequestParam(required = false) String contactPhone,
                                   @RequestParam(required = false) String name
    ){
        return companyBrandService.queryWithPage(page, size, contactPhone, contactPerson, name);
    }
}
