package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.BrandPic;
import com.xs.services.BrandPicService;
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
\* Date: 2019/01/02
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "品牌图片数据")
@RestController
@RequestMapping(value="/api/back/brand/pic")
public class BrandPicController extends BaseController{
    @Autowired
    private BrandPicService brandPicService;


    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody BrandPic brandPic, BindingResult bindingResult) {
        brandPicService.save(brandPic);
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
        brandPicService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody BrandPic brandPic, BindingResult bindingResult) {
        brandPicService.update(brandPic);
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
        BrandPic brandPic = brandPicService.findById(id);
        return ResultGenerator.genSuccessResult(brandPic);
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "品牌图片分页列表",notes = "品牌图片分页列表(根据数据提交方/审核方显示不同的状态)")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)",name = "status", paramType = "query"),
            @ApiImplicitParam(value = "图片名称",name = "picName", paramType = "query"),
            @ApiImplicitParam(value = "品牌id",name = "brandId", paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Byte status,
                       @RequestParam(required = false) String picName,
                       @RequestParam(required = false) Integer brandId
    ){
        return brandPicService.queryWithPage(page, size, status, picName, brandId);
    }
}
