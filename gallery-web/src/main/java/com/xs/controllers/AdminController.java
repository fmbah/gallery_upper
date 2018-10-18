package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.Admin;
import com.xs.services.AdminService;
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
\* Date: 2018/10/16
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(value = "AdminController", description = "后台运维帐号管理")
@RestController
@RequestMapping(value="/api/back/admin")
public class AdminController extends BaseController{
    @Autowired
    private AdminService adminService;


    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody Admin admin, BindingResult bindingResult) {
        adminService.save(admin);
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
        adminService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody Admin admin, BindingResult bindingResult) {
        adminService.update(admin);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "修改帐号密码",notes = "修改帐号密码")
    @PostMapping(value = "/updatePass/{username}/{password}/{oldPassword}",produces = "application/json;charset=utf-8")
    public Object update(@PathVariable(value = "username") String username, @PathVariable(value = "password") String password
            , @PathVariable(value = "oldPassword") String oldPassword) {
        return adminService.updatePass(username, password, oldPassword);
    }

    @ApiOperation(value = "重置帐号密码",notes = "重置帐号密码")
    @PostMapping(value = "/resetPass/{id}/",produces = "application/json;charset=utf-8")
    public Object resetPass(@PathVariable Integer id) {
        return adminService.resetPass(id);
    }

    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        Admin admin = adminService.findById(id);
        return ResultGenerator.genSuccessResult(admin);
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "角色id",name = "roleId",paramType = "query"),
            @ApiImplicitParam(value = "帐号名称",name = "username",paramType = "query"),
            @ApiImplicitParam(value = "是否品牌帐号",name = "isBrand",paramType = "query", required = true)
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Integer roleId,
                       @RequestParam(required = false) String username,
                       @RequestParam(defaultValue = "false") Boolean isBrand
    ){
        return ResultGenerator.genSuccessResult(adminService.queryWithPage(page, size, roleId, username, 0, isBrand));
    }
}
