package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ProjectConstant;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.services.UpLoadService;
import com.xs.utils.OssUpLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * \* 杭州桃子网络科技股份有限公司
 * \* User: wht
 * \* Date: 18/6/12
 * \* Time: 下午8:37
 * \* To change this template use File | Settings | File Templates.
 * \* Description: 文件上传、订单导出导入业务
 * \
 */

@Service("upLoadService")
public class UpLoadServiceImpl implements UpLoadService {

    @Autowired
    private OssConfig ossConfig;

    /***
     * 多图片上传
     * @param files  文件数组
     * @return  多文件url路径,逗号拼接
     * @throws IOException
     */
    @Override
    public Object upLoad(MultipartFile[] files)throws IOException {
        String urls="";
        if(files.length>0){
                for(MultipartFile file : files){
                    try{
                        urls+=up(file)+",";
                    }catch (IOException e){
                        e.printStackTrace();
                        throw new ServiceException("系统错误，请联系管理员进行处理");
                    }
                }
                return ResultGenerator.genSuccessResult(urls.substring(0, urls.length() - 1));
        }
        return ResultGenerator.genFailResult("文件数量为空，请重新上传");
    }


    /***
     * 图片上传
     * @param file 文件
     * @return 文件路径
     * @throws IOException
     */
    @Override
    public String up(MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            if(file.getSize()<10485760){
                OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
                String fileName = new Date().getTime() + "_" + file.getOriginalFilename();
                ossClient.putObject(ossConfig.getBucket(), fileName, file.getInputStream());
                URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), fileName, new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
                if(null!=url){
                    return ProjectConstant.ALIYUN_OSS_IMG_ADDRESS + fileName;
                }
            }
            throw  new RuntimeException("图片太大，最大200KB");
        }
        throw  new ServiceException("上传文件为空，请重新上传");
    }

    public Object remove(String fileName){
        OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
        ossClient.deleteObject(ossConfig.getBucket(),fileName);
        return ResultGenerator.genSuccessResult();
    }

    @Override
    public String orderExport(HttpServletRequest request, HttpServletResponse response, int page, int size) {
//        PageHelper.startPage(page, size);
//        List<HashMap<String, Object>> orders = orderService.queryForExport();
//        PageInfo pageInfo = new PageInfo(orders);
//        File exportFile = null;
//        if(orders != null && !orders.isEmpty()) {
//            try {
//                Map<String,Object> model = new HashMap<>();
//                model.put("orders", pageInfo.getList());
//                exportFile = File.createTempFile("Orders",".xlsx");
//                JxlsExportUtil.exportExcel("static/template_file/订单数据.xlsx","static/template_file/订单数据.xml",exportFile,model);
//
//                OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
//                try {
//                    ossClient.putObject(ossConfig.getBucket(), exportFile.getName(), new FileInputStream(exportFile));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), exportFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//                return url.toString().replaceAll("http", "https");
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if(exportFile != null) {
//                    exportFile.delete();
//                }
//            }
//        }
        return null;
    }

    @Override
    public Object orderImport(MultipartFile file) {
//        try {
//            LogisticsExportBound bean = JxlsImportUtil.importFromExcelFile(file.getInputStream(), "static/template_file/logistics.xml", "bean", LogisticsExportBound.class);
//            Map<String, Object> result = new HashedMap();
//            List<LogisticsExport> errorList = new ArrayList<>();
//            if(bean != null) {
//                List<LogisticsExport> logisticsExportList = bean.getLogisticsExportList();
//                if(logisticsExportList != null && !logisticsExportList.isEmpty()) {
//                    logisticsExportList.forEach(model->{
//                        boolean isError = false;
//                        if(model.getOrderNo() != null && model.getOrderNo().length() > 0 &&
//                                model.getLogisticsNo() != null && model.getLogisticsNo().length() > 0 &&
//                                model.getLogisticsCode() != null && model.getLogisticsCode().length() > 0) {
//                            Map<String, Object> logistic = fastMail100Service.execSubscribeLogistic(model.getLogisticsNo(), model.getLogisticsCode(), model.getOrderNo());
//                            Boolean flag = (Boolean) logistic.get("flag");
//                            if(!flag) {
//                                isError = true;
//                            }
//                        } else {
//                            isError = true;
//                        }
//                        if(isError) {
//                            errorList.add(model);
//                        }
//                    });
//                }
//            }
//            if(!errorList.isEmpty()) {
//                result.put("data", JSON.toJSONString(errorList));
//                result.put("flag", "false");
//                return result;
//            }
//            result.put("flag", "true");
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

}
