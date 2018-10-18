package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.AdminRoleMenu;
import com.xs.services.AdminRoleMenuService;
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

@Api(description = "角色菜单关系数据")
@RestController
@RequestMapping(value="/api/back/admin/role/menu")
public class AdminRoleMenuController extends BaseController{
    @Autowired
    private AdminRoleMenuService adminRoleMenuService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody AdminRoleMenu adminRoleMenu, BindingResult bindingResult) {
//        adminRoleMenuService.save(adminRoleMenu);
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
//        adminRoleMenuService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody AdminRoleMenu adminRoleMenu, BindingResult bindingResult) {
//        adminRoleMenuService.update(adminRoleMenu);
//        return ResultGenerator.genSuccessResult();
//    }
//
    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        AdminRoleMenu adminRoleMenu = adminRoleMenuService.findById(id);
        return ResultGenerator.genSuccessResult(adminRoleMenu);
    }

    /***
     * 配置角色菜单关系数据
     * @return
     */
    @ApiOperation(value = "配置角色菜单关系数据",notes = "配置角色菜单关系数据")
    @GetMapping(value = "/ownMenu/{roleId}/{menuIds}",produces = "application/json;charset=utf-8")
    public Object configRoleMenu(@ApiParam(value = "角色id", name = "roleId", required = true) @PathVariable(value = "roleId") Integer roleId,
                                 @ApiParam(value = "菜单id集合,使用英文逗号分开", name = "menuIds", required = true) @PathVariable(value = "menuIds") String menuIds) {
        return adminRoleMenuService.configRoleMenu(roleId, menuIds);
    }

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
//        List<AdminRoleMenu> list = adminRoleMenuService.findAll();
//        PageInfo pageInfo = new PageInfo(list);
//        return ResultGenerator.genSuccessResult(pageInfo);
//    }
}
