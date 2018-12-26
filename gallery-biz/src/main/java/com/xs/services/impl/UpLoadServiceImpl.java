package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.xs.beans.Base64ToUrl;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ProjectConstant;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.services.UpLoadService;
import com.xs.utils.OssUpLoadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public final Logger logger = LoggerFactory.getLogger(UpLoadServiceImpl.class);

    @Autowired
    private OssConfig ossConfig;
    @Value("${gallery.domain.url}")
    private String url;
    @Value("${aliyun.oss.cdnurl}")
    private String cdnurl;

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

    public String upFile(File file) {
        if(file != null && file.exists()){
            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            String fileName = new Date().getTime() + "_" + file.getName();
            try {
                ossClient.putObject(ossConfig.getBucket(), fileName, new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            SetBucketCORSRequest request = new SetBucketCORSRequest(ossConfig.getBucket());

            setParams(request);

            ossClient.setBucketCORS(request);

            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), fileName, new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
            if(null!=url){
                return cdnurl + fileName;
            }
        }
        throw  new ServiceException("上传文件为空，请重新上传");
    }

    public String upFileStream(InputStream is) {
            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            String fileName = new Date().getTime() + "";
            ossClient.putObject(ossConfig.getBucket(), fileName, is);
            SetBucketCORSRequest request = new SetBucketCORSRequest(ossConfig.getBucket());
            setParams(request);
            ossClient.setBucketCORS(request);
            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), fileName, new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
            if(null!=url){
                return cdnurl + fileName;
            }

            return null;
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
                SetBucketCORSRequest request = new SetBucketCORSRequest(ossConfig.getBucket());

                setParams(request);

                ossClient.setBucketCORS(request);

                URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), fileName, new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
                if(null!=url){
                    return cdnurl + fileName;
                }
            }
            throw  new RuntimeException("图片太大，最大10MB");
        }
        throw  new ServiceException("上传文件为空，请重新上传");
    }

    public void setParams(SetBucketCORSRequest request) {
        // 跨域资源共享规则的容器，每个存储空间最多允许10条规则。
        ArrayList<SetBucketCORSRequest.CORSRule> putCorsRules = new ArrayList<SetBucketCORSRequest.CORSRule>();

        SetBucketCORSRequest.CORSRule corRule = new SetBucketCORSRequest.CORSRule();

        ArrayList<String> allowedOrigin = new ArrayList<String>();
// 指定允许跨域请求的来源。
        allowedOrigin.add(url);

        ArrayList<String> allowedMethod = new ArrayList<String>();
// 指定允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD)。
        allowedMethod.add("GET");

        ArrayList<String> allowedHeader = new ArrayList<String>();
// 是否允许预取指令（OPTIONS）中Access-Control-Request-Headers头中指定的Header。
//        allowedHeader.add("x-oss-test");
        allowedHeader.add("*");

        ArrayList<String> exposedHeader = new ArrayList<String>();
// 指定允许用户从应用程序中访问的响应头。
        exposedHeader.add("x-oss-test1");
// AllowedOrigins和AllowedMethods最多支持一个星号（*）通配符。星号（*）表示允许所有的域来源或者操作。
        corRule.setAllowedMethods(allowedMethod);
        corRule.setAllowedOrigins(allowedOrigin);
// AllowedHeaders和ExposeHeaders不支持通配符。
        corRule.setAllowedHeaders(allowedHeader);
        corRule.setExposeHeaders(exposedHeader);
// 指定浏览器对特定资源的预取（OPTIONS）请求返回结果的缓存时间，单位为秒。
        corRule.setMaxAgeSeconds(10);

// 最多允许10条规则。
        putCorsRules.add(corRule);
// 已存在的规则将被覆盖。
        request.setCorsRules(putCorsRules);

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

    @Override
    public Object base64ToUrl(Base64ToUrl base64ToUrl) {

//        BASE64Decoder decoder = new BASE64Decoder();
        File template = null;
        try {
            String s1 = base64ToUrl.getBase64Var().split("data:image/")[1];
            template = File.createTempFile("template", ".".concat(s1.substring(0, s1.indexOf(";"))));
            FileOutputStream write = new FileOutputStream(template);
//            byte[] decoderBytes = decoder.decodeBuffer(base64ToUrl.getBase64Var().split(",")[1]);
            String base64Str = base64ToUrl.getBase64Var().split(",")[1];

            logger.warn("executorService start:" + System.currentTimeMillis());
//            ExecutorService executorService = Executors.newFixedThreadPool(3);
//            Future<byte[]> submit = executorService.submit(() -> java.util.Base64.getDecoder().decode(base64Str));
//            executorService.shutdown();

//            byte[] decoderBytes = Base64.decodeFast(base64Str);
            byte[] decoderBytes = java.util.Base64.getDecoder().decode(base64Str);
//            byte[] decoderBytes = submit.get();
            write.write(decoderBytes);
            write.close();
            logger.warn("executorService end:" + System.currentTimeMillis());

            logger.warn("oss start:" + System.currentTimeMillis());
            OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            try {
                ossClient.putObject(ossConfig.getBucket(), template.getName(), new FileInputStream(template));

                SetBucketCORSRequest request = new SetBucketCORSRequest(ossConfig.getBucket());
                setParams(request);
                ossClient.setBucketCORS(request);

            } catch (IOException e) {
                e.printStackTrace();
            }
            URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), template.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
            if(url != null) {
                base64ToUrl.setBase64Var(cdnurl + template.getName());
            }
            logger.warn("oss end:" + System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        finally {
            if (template != null) {
                template.delete();
            }
        }


        return ResultGenerator.genSuccessResult(base64ToUrl.getBase64Var());
//        return ResultGenerator.genSuccessResult("https://daily-test.mxth.com/template5043290964231378426.png");

    }

    @Override
    public String up1(MultipartFile file) throws IOException {

        Path path = Paths.get(this.getClass().getResource("tmpImg").getPath() + File.separator + System.currentTimeMillis() + "_" + file.getOriginalFilename());
        Files.write(path, file.getBytes());

        return null;
    }
}
