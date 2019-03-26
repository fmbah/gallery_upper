package com.xs.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.gson.Gson;
import org.apache.http.ssl.SSLContexts;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Connection;

/**
 * @ClassName TestJava
 * @Description
 * @Author root
 * @Date 18-10-24 上午9:54
 * @Version 1.0
 **/
public class TestJava {

    @Test
    public void test() {
        Integer integer0 = new Integer(1);
        Integer integer1 = new Integer(1);
        Integer integer2 = Integer.valueOf(1);
        Integer integer3 = Integer.valueOf(1);

        System.out.println(integer0 == integer1);
        System.out.println(integer0 == integer2);
        System.out.println(integer2 == integer3);


        JSONObject jsonObject = JSON.parseObject("{'shareProfitName':'谁充值了的名称', 'recharge':'365', 'profit':'150'}");
        System.out.println(jsonObject);

        JSONObject jsonObject1 = JSON.parseObject("{}");
        jsonObject1.put("a", "av");
        System.out.println(jsonObject1);
        String str = "[\"1\", '2', '3']";
        JSONArray jsonArray = JSON.parseArray(str);
        System.out.println(jsonArray.size());

    }

    @Test
    public void guavaTest() {
        long l = System.currentTimeMillis();
        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), 10000000, 0.01);
        for (int i = 0; i < 10000000; i++) {
            filter.put(i);
        }
        Assert.assertTrue(filter.mightContain(1));
        long s = System.currentTimeMillis();
        System.out.println("执行时间: " + (s - l));
    }

    @Test
    public void testFont() throws ClassNotFoundException {
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        String[] fontFamilies = ge.getAvailableFontFamilyNames();
//        for (String f : fontFamilies) {
//            System.out.println(f);
//        }


        System.out.println((byte)"a".charAt(0));
    }

    @Test
    public void testHtmlToImage() {
        try {
//            generateOutput();

            String text = "<span style='font-size:1px;color:rgba(0,0,0,0);'>空</span>";
            System.out.println(text.replaceAll("<br/>", "/n").replaceAll("\\<span\\sstyle\\=\\'font\\-size:(0|([1-9]\\d*))(\\.\\d+)?px\\;color\\:rgba\\(0\\,0\\,0\\,0\\)\\;\\'\\>空\\<\\/span\\>", "/s"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void generateOutput() throws Exception {

        //load the webpage into the editor
        //JEditorPane ed = new JEditorPane(new URL("http://www.google.com"));
        JEditorPane ed = new JEditorPane(new URL("https://daily-test.mxth.com/1546056198856_temp7069828511555224172.png"));
        ed.setSize(2000, 2000);

        //create a new image
        BufferedImage image = new BufferedImage(ed.getWidth(), ed.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        //paint the editor onto the image
        SwingUtilities.paintComponent(image.createGraphics(),
                ed,
                new JPanel(),
                0, 0, image.getWidth(), image.getHeight());
        //save the image to file
        ImageIO.write((RenderedImage) image, "png", new File("html.png"));

    }

    @Test
    public void p12Test() {
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = Resources.getResourceAsStream("apiclient_cert.p12");
            char[] partnerId2charArray = "1517295821".toCharArray();
            keystore.load((InputStream)inputStream, partnerId2charArray);
            SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
