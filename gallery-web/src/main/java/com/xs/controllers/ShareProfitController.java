//package com.xs.controllers;
//import com.xs.core.ResultGenerator;
//import com.xs.beans.ShareProfit;
//import com.xs.services.ShareProfitService;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.xs.core.scontroller.BaseController;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import javax.validation.Valid;
//import org.springframework.validation.BindingResult;
//import java.util.List;
//
///**
//\* User: zhaoxin
//\* Date: 2018/10/22
//\* To change this template use File | Settings | File Templates.
//\* Description:
//\*/
//
//@Api
//@RestController
//@RequestMapping(value="/share/profit")
//public class ShareProfitController extends BaseController{
//    @Autowired
//    private ShareProfitService shareProfitService;
//
//
//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody ShareProfit shareProfit, BindingResult bindingResult) {
//        shareProfitService.save(shareProfit);
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
//        shareProfitService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody ShareProfit shareProfit, BindingResult bindingResult) {
//        shareProfitService.update(shareProfit);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 详情
//    * @return
//    */
//    @ApiOperation(value = "详情",notes = "详情")
//    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
//    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
//    public Object detail(@RequestParam Integer id) {
//        ShareProfit shareProfit = shareProfitService.findById(id);
//        return ResultGenerator.genSuccessResult(shareProfit);
//    }
//
//    /***
//    * 分页列表
//    * @return
//    */
//    @ApiOperation(value = "分页列表",notes = "分页列表")
//    @ApiImplicitParams({
//    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
//    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0")
//    })
//    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
//    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,@RequestParam(required = false,defaultValue = "0") Integer size
//    ){
//        PageHelper.startPage(page, size);
//        List<ShareProfit> list = shareProfitService.findAll();
//        PageInfo pageInfo = new PageInfo(list);
//        return ResultGenerator.genSuccessResult(pageInfo);
//    }
//}
