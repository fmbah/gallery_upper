package com.xs.controllers;

import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.UpLoadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 * 功能描述: 文件上传控制器
 *
 * @param: 
 * @return:
 * @auther: Fmbah
 * @date: 18-10-16 下午5:35
 */
@Api(value = "UpLoadController",description = "文件上传管理")
@RestController
@RequestMapping("/api/back/upload")
public class UpLoadController extends BaseController {

    @Autowired
    private UpLoadService upLoadService;

    /***
     * 上传
     * @param file  文件
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/up",produces = "application/json")
    @IgnoreAuth
    public Object up(@ApiParam(value = "文件")@RequestParam MultipartFile file) throws IOException {
        return ResultGenerator.genSuccessResult(upLoadService.up(file));
    }

    /***
     * 多文件上传
     * @param imageUrls 文件数组
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/upLoad",produces = "application/json")
    @IgnoreAuth
    public Object upLoad(@ApiParam(value = "文件")@RequestParam MultipartFile[] imageUrls) throws IOException {
        return upLoadService.upLoad(imageUrls);
    }

    /***
     * 删除文件
     * @param fileName  文件名
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/remove",produces = "application/json")
    @IgnoreAuth
    public Object remove(@ApiParam(value = "文件")@RequestParam String fileName) throws IOException {
        return upLoadService.remove(fileName);
    }

//    /**
//    * @Description: 订单数据导出
//    * @Param:
//    * @return:
//    * @Author: zhaoxin
//    * @Date: 2018/6/20
//    **/
//    @GetMapping("/orderExport")
//    @ApiOperation(value = "订单数据导出", notes = "订单数据导出")
//    public Object orderExport(int page, int size){
//        return ResultGenerator.genSuccessResult(upLoadService.orderExport(this.request, this.response, page, size));
//    }
//
//    /**
//    * @Description: 物流信息导入
//    * @Param:  file：导入文件
//    * @return:
//    * @Author: zhaoxin
//    * @Date: 2018/6/20
//    **/
//    @PostMapping(value = "/logisticsImport",produces = "application/json")
//    @ApiOperation(value = "物流信息导入", notes = "物流信息导入,模板下载地址（http://domain:port/template_file/logistics.xlsx）")
//    public Object logisticsImport(@ApiParam(value = "文件")@RequestParam MultipartFile file) throws IOException {
//        return ResultGenerator.genSuccessResult(upLoadService.orderImport(file));
//    }

}
