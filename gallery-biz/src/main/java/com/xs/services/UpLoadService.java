package com.xs.services;

import com.xs.beans.Base64ToUrl;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * \* 杭州桃子网络科技股份有限公司
 * \* User: apple
 * \* Date: 18/6/12
 * \* Time: 下午8:37
 * \* To change this template use File | Settings | File Templates.
 * \* Description:  文件上传、订单导出导入业务
 * \
 */
public interface UpLoadService {

    /****
     *  杭州桃子网络科技股份有限公司
     *  @User: wht
     *  @Date: 18/7/6下午2:54
     *  @param: files               文件数组
     *  @return: java.lang.Object
     *  @Description:               多文件上传
     *
     */
    Object upLoad(MultipartFile[] files) throws IOException;

    /****
     *  杭州桃子网络科技股份有限公司
     *  @User: wht
     *  @Date: 18/7/6下午2:54
     *  @param: file                文件
     *  @return: java.lang.Object
     *  @Description:               文件上传
     *
     */
    String up(MultipartFile file) throws IOException;

    Object remove(String fileName);

    Object base64ToUrl(Base64ToUrl base64ToUrl);
    
    /** 
    * @Description: 订单导出 
    * @Param:
    * @return:  
    * @Author: zhaoxin
    * @Date: 2018/6/19 
    **/
    String orderExport(HttpServletRequest request, HttpServletResponse response, int page, int size);
    
    /** 
    * @Description: 物流数据导入 
    * @Param: file：文件 
    * @return:
    * @Author: zhaoxin
    * @Date: 2018/6/20 
    **/
    Object orderImport(MultipartFile file);

    Object upFile(File file);
    Object upFileStream(InputStream is);


}
