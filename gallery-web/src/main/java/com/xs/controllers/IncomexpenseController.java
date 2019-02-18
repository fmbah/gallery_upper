package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.Incomexpense;
import com.xs.services.IncomexpenseService;
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
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api
@RestController
@RequestMapping(value="/incomexpense")
public class IncomexpenseController extends BaseController{
    @Autowired
    private IncomexpenseService incomexpenseService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody Incomexpense incomexpense, BindingResult bindingResult) {
//        incomexpenseService.save(incomexpense);
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
//        incomexpenseService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody Incomexpense incomexpense, BindingResult bindingResult) {
//        incomexpenseService.update(incomexpense);
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
//        Incomexpense incomexpense = incomexpenseService.findById(id);
//        return ResultGenerator.genSuccessResult(incomexpense);
//    }

    /***
     * 分页列表
     * @return
     */
    @ApiOperation(value = "获益详情分页列表",notes = "获益详情分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "用户id",name = "userId", paramType = "query"),
            @ApiImplicitParam(value = "类型,分享:'SHARE_PROFIT',提现:'WITHDRAW_CASH",name = "type", paramType = "query"),
            @ApiImplicitParam(value = "数据类型, 0非分享获益,1一级分成, 2二级分成",name = "subType", paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,@RequestParam(required = false,defaultValue = "0") Integer size
            ,@RequestParam String type
            ,@RequestParam Byte subType
            ,@RequestParam Integer userId
    ){
        return ResultGenerator.genSuccessResult(incomexpenseService.list(page, size, type, subType, userId));
    }
}
