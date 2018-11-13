package com.xs.utils;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @ClassName ImageBase64Utils
 * @Description
 * @Author root
 * @Date 18-10-31 下午2:56
 * @Version 1.0
 **/
public class ImageBase64Utils {


    public static String bytesToBase64(byte[] bytes) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);// 返回Base64编码过的字节数组字符串
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 图片路径
     * @return base64字符串
     */
    public static String imageToBase64(String path) throws IOException {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return org.apache.commons.codec.binary.Base64.encodeBase64String(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * 处理Base64解码并写图片到指定位置
     *
     * @param base64 图片Base64数据
     * @param path   图片保存路径
     * @return
     */
    public static boolean base64ToImageFile(String base64, String path) throws IOException {// 对字节数组字符串进行Base64解码并生成图片
        // 生成jpeg图片
        try {
            OutputStream out = new FileOutputStream(path);
            return base64ToImageOutput(base64, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理Base64解码并输出流
     *
     * @param base64
     * @param out
     * @return
     */
    public static boolean base64ToImageOutput(String base64, OutputStream out) throws IOException {
        if (base64 == null) { // 图像数据为空
            return false;
        }
        try {
            // Base64解码
            byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            out.write(bytes);
            out.flush();
            return true;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main (String args[]) {
//        InputStream bytesInput = HttpClientHelper.INSTANCE.get("http://ww4.sinaimg.cn/bmiddle/66640ec4jw1euxo80dk39j20c80c83z6.jpg", null, null).getEntity().getContent();
//
// byte[] bytes = IOUtils.toByteArray(bytesInput);
//        String s = ImageBase64Utils.bytesToBase64(bytes);
        String s = "data:image/png;base64,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";

        BASE64Decoder decoder = new BASE64Decoder();
        try {
//            String s1 = s.split("data:image/")[1];
//            System.out.println(s1.indexOf(";"));
//            FileOutputStream write = new FileOutputStream(new File("/tmp/b." + s1.substring(0, s1.indexOf(";"))));
            FileOutputStream write = new FileOutputStream(new File("/tmp/b.png"));
//            byte[] decoderBytes = decoder.decodeBuffer(s.split(",")[1]);
            byte[] decoderBytes = decoder.decodeBuffer(imgBase64("https://daily-test.oss-cn-hangzhou.aliyuncs.com/template3319744083692731075.png"));
            write.write(decoderBytes);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            String base64 = imageToBase64("/tmp/a.png");
//            System.out.println(base64);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 将网络图片转成Base64码，此方法可以解决解码后图片显示不完整的问题
     * @param imgURL图片地址。
     * 例如：http://***.com/271025191524034.jpg
     * @return
     */
    public static String imgBase64(String imgURL) {
        ByteArrayOutputStream outPut = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        try {
            // 创建URL
            URL url = new URL(imgURL);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "fail";//连接失败/链接失效/图片不存在
            }
            InputStream inStream = conn.getInputStream();
            int len = -1;
            while ((len = inStream.read(data)) != -1) {
                outPut.write(data, 0, len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outPut.toByteArray()).replaceAll("\r|\n", "");
    }

//    public static void main (String args[]) {
//        String s = imgBase64("https://daily-test.oss-cn-hangzhou.aliyuncs.com/template3319744083692731075.png");
//        System.out.println(s);
//    }
}
