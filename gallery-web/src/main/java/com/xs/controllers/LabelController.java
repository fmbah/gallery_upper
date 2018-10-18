package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.Label;
import com.xs.services.LabelService;
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
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "模板标签")
@RestController
@RequestMapping(value="/api/back/label")
public class LabelController extends BaseController{
    @Autowired
    private LabelService labelService;


    /***
    * 新增
    * @return
    */
    @ApiOperation(value = "录入",notes = "录入")
    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
    public Object add(@Valid @RequestBody Label label, BindingResult bindingResult) {
        labelService.save(label);
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
        labelService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    /***
    * 修改
    * @return
    */
    @ApiOperation(value = "修改",notes = "修改")
    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
    public Object update(@Valid @RequestBody Label label, BindingResult bindingResult) {
        labelService.update(label);
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
        Label label = labelService.findById(id);
        return ResultGenerator.genSuccessResult(label);
    }

    /***
     * 获取全部标签数据,不含分页
     * @return
     */
    @ApiOperation(value = "获取全部标签数据,不含分页",notes = "获取全部标签数据,不含分页")
    @GetMapping(value = "/queryAll",produces = "application/json;charset=utf-8")
    public Object queryAll() {
        return ResultGenerator.genSuccessResult(labelService.findAll());
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "名称",name = "name",paramType = "query",defaultValue = "0")
    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,@RequestParam(required = false,defaultValue = "0") Integer size
         ,@RequestParam(required = false) String name
    ){
        return labelService.queryWithPage(page, size, name);
    }
}
