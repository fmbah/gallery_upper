package com.xs.controllers;

import com.xs.beans.AdminMenu;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.AdminMenuService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "菜单数据集合")
@RestController
@RequestMapping(value="/api/back/admin/menu")
public class AdminMenuController extends BaseController{
    @Autowired
    private AdminMenuService adminMenuService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody AdminMenu adminMenu, BindingResult bindingResult) {
//        adminMenuService.save(adminMenu);
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
//        adminMenuService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody AdminMenu adminMenu, BindingResult bindingResult) {
//        adminMenuService.update(adminMenu);
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
        AdminMenu adminMenu = adminMenuService.findById(id);
        return ResultGenerator.genSuccessResult(adminMenu);
    }

    /***
     * 获取菜单数据集合，不含分页
     * @return
     */
    @ApiOperation(value = "获取菜单数据集合，不含分页",notes = "获取角色数据集合，不含分页")
    @GetMapping(value = "/allMenu",produces = "application/json;charset=utf-8")
    public Object allMenu() {
        return ResultGenerator.genSuccessResult(adminMenuService.allMenu());
    }

    /***
     * 获取相应角色对应的菜单数据
     * @return
     */
    @ApiOperation(value = "获取相应角色对应的菜单数据",notes = "获取相应角色对应的菜单数据")
    @GetMapping(value = "/getMenusByRoleId/{roleId}",produces = "application/json;charset=utf-8")
    public Object getMenusByRoleId(@ApiParam(name = "roleId", value = "角色id", required = true) @PathVariable(value = "roleId") Long roleId) {
        return adminMenuService.getMenusByRoleId(roleId);
    }

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
//        List<AdminMenu> list = adminMenuService.findAll();
//        PageInfo pageInfo = new PageInfo(list);
//        return ResultGenerator.genSuccessResult(pageInfo);
//    }
}
