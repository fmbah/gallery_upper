package com.xs.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Auther: Fmbah
 * @Date: 18-10-16 下午5:24
 * @Description: OSS工具类
 *  * OSS Java API手册：http://aliyun_portal_storage.oss.aliyuncs.com/oss_api/oss_javahtml/index.html?spm=5176.7150518.1996836753.8.d5TjaG
 *  * OSS Java SDK开发包:http://help.aliyun.com/view/13438814.html
 *  * OSSClient:www.mvnrepository.com/artifact/cglib/cglib/2.2
 */
public class OssUpLoadUtil {

    //log
    private static final Logger LOG = LoggerFactory.getLogger(OssUpLoadUtil.class);


    /**
     * 获取阿里云OSS客户端对象
     * */
    public static final OSSClient getOSSClient(String endpoint, String access_key, String access_key_secret){
        return new OSSClient(endpoint,access_key, access_key_secret);
    }

    /**
     * 新建Bucket  --Bucket权限:私有
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     * */
    public static final boolean createBucket(OSSClient client, String bucketName){
        Bucket bucket = client.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }

    /**
     * 查找Object是否存在
     * @param bucketName bucket名称
     * @param objKey object名称
     * */
    public static final boolean objectIsExist(OSSClient client, String bucketName, String objKey){
        return client.doesObjectExist(bucketName, objKey);
    }

    /**
     * 删除Bucket中的单个Object
     * @param bucketName bucket名称
     * @param objKey object名称
     * */
    public static final void deleteObjectByBucket(OSSClient client, String bucketName, String objKey){
        client.deleteObject(bucketName, objKey);
        LOG.info("删除" +bucketName+"中的"+ objKey + "成功");
    }

    /**
     * 批量删除Bucket中的单个Object
     * @param bucketName bucket名称
     * @param keys object名称集合
     * */
    public static final void deleteObjects(OSSClient client, String bucketName, List<String> keys){
        DeleteObjectsResult deleteObjectsResult = client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
        LOG.info("批量删除成功");
    }


    /**
     * 删除Bucket
     * @param bucketName bucket名称
     * */
    public static final void deleteBucket(OSSClient client, String bucketName){
        client.deleteBucket(bucketName);
        LOG.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 向阿里云的OSS存储中存储文件  --file也可以用InputStream替代
     * @param client OSS客户端
     * @param file 上传文件
     * @param bucketName bucket名称
     * @param diskName 上传文件的目录  --bucket下文件的路径
     * @return String 唯一MD5数字签名
     * */
    public static final String uploadObject2OSS(OSSClient client, File file, String bucketName, String diskName) throws IOException {
        String resultStr = null;
        InputStream is = new FileInputStream(file);
        String fileName = file.getName();
        Long fileSize = file.length();
        //创建上传Object的Metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(is.available());
        metadata.setCacheControl("no-cache");
        metadata.setHeader("Pragma", "no-cache");
        metadata.setContentEncoding("utf-8");
        metadata.setContentType(getContentType(fileName));
        metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");

        byte[] bt = ByteToInputStream.input2byte(is);
        String isoString = new String(bt,"ISO-8859-1");
        LOG.info(MD5.MD5(isoString).toUpperCase());
        String md5 = MD5.MD5(isoString).toUpperCase();
        LOG.info("md5=" + md5);
        metadata.setContentMD5(md5);
        //上传文件
        PutObjectResult putResult = client.putObject(bucketName, diskName + fileName, is, metadata);
        //解析结果
        return putResult.getETag();


    }

    /**
     * 根据key获取OSS服务器上的文件输入流
     * @param client OSS客户端
     * @param bucketName bucket名称
     * @param diskName 文件路径
     * @param key Bucket下的文件的路径名+文件名
     */
    public static final InputStream getOSS2InputStream(OSSClient client, String bucketName, String diskName, String key){
        OSSObject ossObj = client.getObject(bucketName, diskName + key);
        return ossObj.getObjectContent();
    }

    /**
     * 根据key删除OSS服务器上的文件
     * @param client OSS客户端
     * @param bucketName bucket名称
     * @param diskName 文件路径
     * @param key Bucket下的文件的路径名+文件名
     */
    public static void deleteFile(OSSClient client, String bucketName, String diskName, String key){
        client.deleteObject(bucketName, diskName + key);
        LOG.info("删除" + bucketName + "下的文件" + diskName + key + "成功");
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static final String getContentType(String fileName){
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if("bmp".equalsIgnoreCase(fileExtension)) return "image/bmp";
        if("gif".equalsIgnoreCase(fileExtension)) return "image/gif";
        if("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension)  || "png".equalsIgnoreCase(fileExtension) ) return "image/jpeg";
        if("html".equalsIgnoreCase(fileExtension)) return "text/html";
        if("txt".equalsIgnoreCase(fileExtension)) return "text/plain";
        if("vsd".equalsIgnoreCase(fileExtension)) return "application/vnd.visio";
        if("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) return "application/vnd.ms-powerpoint";
        if("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) return "application/msword";
        if("xml".equalsIgnoreCase(fileExtension)) return "text/xml";
        return "text/html";
    }


    /***
     * 获取文件MD5
     * @param filePath  文件路径
     * @return 文件流MD5值
     * @throws IOException
     */
    public static String getFileMd5(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        byte[] bt = ByteToInputStream.input2byte(inputStream);
        String isoString = new String(bt, "ISO-8859-1");
        String md5 = MD5.MD5(isoString);
        LOG.info("md5=" + md5);
        return md5;
    }

    /***
     * 上传文件到OSS中
     * @param filePath   文件路径
     * @param bucket     oss上bucket名称
     * @throws Exception
     */
    public static String upload(OSSClient ossClient, String bucket, String filePath) throws Exception {
            File tempFile =new File(filePath.trim());
            String fileName = tempFile.getName();
            InputStream inputStream = new FileInputStream(filePath);
            PutObjectResult putResult = ossClient.putObject(bucket, "a/"+fileName, inputStream);
            LOG.info("ETag:" + putResult.getETag());
        // 设置URL过期时间为10年 3600l* 1000*24*365*10

            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
            // 生成URL

            URL url = ossClient.generatePresignedUrl(bucket, fileName, expiration);
            if (url != null) {
                return url.toString();
            }
            return null;
    }

    /***
     * 从OSS下载文件
     * @param objKey    Bucket中的文件名称
     * @param filePath  文件存储路径
     * @param bucket    oss上bucket名称
     */
    public static void downLoad(OSSClient ossClient, String bucket, String objKey, String filePath){
        // 创建OSSClient实例
        ossClient.getObject(new GetObjectRequest(bucket, objKey), new File(filePath));
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public static String getUrl(OSSClient ossClient, String bucket, String key) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10

        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL

        URL url = ossClient.generatePresignedUrl(bucket, key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    /***
     * 暂不可用，开发中.....
     * @throws IOException
     * @throws InterruptedException
     */
    public  static void restore(OSSClient ossClient) throws IOException, InterruptedException {

        ObjectMetadata objectMetadata = ossClient.getObjectMetadata("ossforszf", "arrowright.jpg");
        // check whether the object is archive class
        StorageClass storageClass = objectMetadata.getObjectStorageClass();
        if (storageClass == StorageClass.Archive) {
            // restore object
            ossClient.restoreObject("ossforszf", "arrowright.jpg");
            // wait for restore completed
            do {
                Thread.sleep(1000);
                objectMetadata = ossClient.getObjectMetadata("ossforszf", "arrowright.jpg");
            } while (!objectMetadata.isRestoreCompleted());
        }
        // get restored object
        OSSObject ossObject = ossClient.getObject("ossforszf", "arrowright.jpg");
        ossObject.getObjectContent().close();
    }

    public static void main(String[] args) throws Exception {

        String[] folders = {"a", "b", "c", "d", "e", "f", "g", "h", "z"};
        for (int i= 0; i< 100; i++) {
            System.out.println(new Random().nextInt(folders.length));
        }

//        upload(getOSSClient("http://oss-cn-hangzhou.aliyuncs.com","LTAIAop7Bgx35OgG","ydBCcgGiwqNZDrN2d4d1XbL954eYuZ"),"daily-test","/root/图片/2018-09-18 11-22-55 的屏幕截图.png");
//        downLoad();
//        uploadObject2OSS(getOSSClient("http://oss-cn-hangzhou.aliyuncs.com","LTAIAop7Bgx35OgG","ydBCcgGiwqNZDrN2d4d1XbL954eYuZ"),new File("/root/图片/2018-09-18 11-22-55 的屏幕截图.png"), "daily-test", "a");
    }
}






