package com.xs.controllers;

import com.xs.beans.AdminRole;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.AdminRoleService;
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
\* Date: 2018/10/16
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(value = "AdminRoleController", description = "角色管理")
@RestController
@RequestMapping(value="/api/back/admin/role")
public class AdminRoleController extends BaseController{
    @Autowired
    private AdminRoleService adminRoleService;

    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody AdminRole adminRole, BindingResult bindingResult) {
        adminRoleService.save(adminRole);
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
        adminRoleService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody AdminRole adminRole, BindingResult bindingResult) {
        adminRoleService.update(adminRole);
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
        AdminRole adminRole = adminRoleService.findById(id);
        return ResultGenerator.genSuccessResult(adminRole);
    }

    /***
     * 获取角色数据集合，不含分页
     * @return
     */
    @ApiOperation(value = "获取角色数据集合，不含分页",notes = "获取角色数据集合，不含分页")
    @GetMapping(value = "/allRole",produces = "application/json;charset=utf-8")
    public Object allRole() {
        return ResultGenerator.genSuccessResult(adminRoleService.findAll());
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,@RequestParam(required = false,defaultValue = "0") Integer size
    ){
        return ResultGenerator.genSuccessResult(adminRoleService.queryWithPage(page, size));
    }
}
