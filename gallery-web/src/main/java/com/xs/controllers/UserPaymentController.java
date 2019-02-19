package com.xs.controllers;

import com.xs.core.scontroller.BaseController;
import com.xs.services.UserPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "充值消费记录")
@RestController
@RequestMapping(value="/api/back/user/payment")
public class UserPaymentController extends BaseController{
    @Autowired
    private UserPaymentService userPaymentService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody UserPayment userPayment, BindingResult bindingResult) {
//        userPaymentService.save(userPayment);
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
//        userPaymentService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody UserPayment userPayment, BindingResult bindingResult) {
//        userPaymentService.update(userPayment);
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
//        UserPayment userPayment = userPaymentService.findById(id);
//        return ResultGenerator.genSuccessResult(userPayment);
//    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "用户id",name = "userId",paramType = "query"),
            @ApiImplicitParam(value = "用户昵称",name = "userName",paramType = "query"),
            @ApiImplicitParam(value = "开始时间",name = "sTime",paramType = "query"),
            @ApiImplicitParam(value = "结束时间",name = "eTime",paramType = "query"),
            @ApiImplicitParam(value = "一级分益人id",name = "sp1Id",paramType = "query"),
            @ApiImplicitParam(value = "一级分益人名称",name = "sp1Name",paramType = "query"),
            @ApiImplicitParam(value = "充值类型(支付类型：5: 半年会员6: 全年会员10: 终身会员0: 非会员1: 品牌会员)",name = "type",paramType = "query"),
            @ApiImplicitParam(value = "是否导出",name = "isExport",paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Integer userId,
                       @RequestParam(required = false) String userName,
                       @RequestParam(required = false) String sTime,
                       @RequestParam(required = false) String eTime,
                       @RequestParam(required = false) Integer sp1Id,
                       @RequestParam(required = false) String sp1Name,
                       @RequestParam(required = false) Byte type,
                       @RequestParam Boolean isExport
    ){
        return userPaymentService.queryWithPage(page, size, userId,
                userName,
                sTime,
                eTime,
                sp1Id,
                sp1Name,
                type,
                isExport);
    }
}
