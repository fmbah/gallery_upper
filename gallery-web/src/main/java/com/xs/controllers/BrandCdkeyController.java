package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.BrandCdkey;
import com.xs.services.BrandCdkeyService;
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

@Api(description = "品牌激活码")
@RestController
@RequestMapping(value="/api/back/brand/cdkey")
public class BrandCdkeyController extends BaseController{
    @Autowired
    private BrandCdkeyService brandCdkeyService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody BrandCdkey brandCdkey, BindingResult bindingResult) {
//        brandCdkeyService.save(brandCdkey);
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
//        brandCdkeyService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody BrandCdkey brandCdkey, BindingResult bindingResult) {
//        brandCdkeyService.update(brandCdkey);
//        return ResultGenerator.genSuccessResult();
//    }

    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        BrandCdkey brandCdkey = brandCdkeyService.findById(id);
        return ResultGenerator.genSuccessResult(brandCdkey);
    }

    @GetMapping("/cdkExport")
    @ApiOperation(value = "激活码数据导出", notes = "激活码数据导出")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "品牌id",name = "brandId",paramType = "query", required = true)
    })
    public Object cdkExport(@RequestParam(required = false,defaultValue = "0") Integer page,
                            @RequestParam(required = false,defaultValue = "0") Integer size,
                            @RequestParam(required = false) Integer brandId){


        return ResultGenerator.genSuccessResult(brandCdkeyService.cdkExport(page, size, brandId));
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "验证码",name = "code",paramType = "query"),
            @ApiImplicitParam(value = "使用状态(0 未使用, 1 已使用)",name = "isUsed",paramType = "query"),
            @ApiImplicitParam(value = "品牌id",name = "brandId",paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) String code,
                       @RequestParam(required = false) String isUsed,
                                   @RequestParam(required = false) Integer brandId
    ){
        return brandCdkeyService.queryWithPage(page, size, code, isUsed, brandId);
    }
}
