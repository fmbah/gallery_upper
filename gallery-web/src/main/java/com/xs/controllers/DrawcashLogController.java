package com.xs.controllers;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.DrawcashLog;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.core.sexception.ServiceException;
import com.xs.services.DrawcashLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.xs.core.ProjectConstant.SESSION_ADMIN_ID;

/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(value = "分益提现管理")
@RestController
@RequestMapping(value="/api/back/drawcash/log")
public class DrawcashLogController extends BaseController{
    @Autowired
    private DrawcashLogService drawcashLogService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody DrawcashLog drawcashLog, BindingResult bindingResult) {
//        drawcashLogService.save(drawcashLog);
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
//        drawcashLogService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody DrawcashLog drawcashLog, BindingResult bindingResult) {
//        drawcashLogService.update(drawcashLog);
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
//        DrawcashLog drawcashLog = drawcashLogService.findById(id);
//        return ResultGenerator.genSuccessResult(drawcashLog);
//    }

    /***
    * 审核操作/通过拒绝
    * @return
    */
    @ApiOperation(value = "审核操作/通过拒绝",notes = "审核操作/通过拒绝")
    @PostMapping(value = "/auditor/{id}/{hasPass}",produces = "application/json;charset=utf-8")
    public Object auditor(@ApiParam(name = "id", value = "当前数据id", required = true) @PathVariable Integer id,
                         @ApiParam(name = "hasPass", value = "是否通过", required = true) @PathVariable Boolean hasPass,
                         @ApiParam(name = "failMsg", value = "拒绝理由") @RequestParam(required = false) String failMsg) {

        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(SESSION_ADMIN_ID);
        Integer adminId = null;
        try {
           adminId = Integer.valueOf(attribute.toString());
       } catch (ClassCastException e) {
           throw new ServiceException("用户会话失效，请重新登录");
       }

        return drawcashLogService.auditor(request, adminId, id, hasPass, failMsg);
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "开始时间",name = "sTime",paramType = "query"),
            @ApiImplicitParam(value = "结束时间",name = "eTime",paramType = "query"),
            @ApiImplicitParam(value = "用户id",name = "userId",paramType = "query"),
            @ApiImplicitParam(value = "用户昵称",name = "userName",paramType = "query"),
            @ApiImplicitParam(value = "提现状态(WAIT_PROCESS: 审核中, 'FINISHED': 已完成, 'FAIL: 已拒绝)",name = "status",paramType = "query"),
            @ApiImplicitParam(value = "是否导出",name = "isExport",paramType = "query")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Integer userId,
                       @RequestParam(required = false) String userName,
                       @RequestParam(required = false) String sTime,
                       @RequestParam(required = false) String eTime,
                       @RequestParam(required = false) String status,
                       @RequestParam Boolean isExport
    ){
        return drawcashLogService.queryWithPage(page, size, sTime, eTime, userId, userName, status, isExport);
    }
}
