package com.xs.services.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xs.beans.FontToPic;
import com.xs.core.ResultGenerator;
import com.xs.core.sservice.SWxMenuService;
import com.xs.services.UpLoadService;
import com.xs.services.WxMenuService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: Fmbah
 * @Date: 18-10-31 下午4:17
 * @Description:
 */
@Service
public class WxMenuServiceImpl implements WxMenuService {

    public final Logger logger = LoggerFactory.getLogger(WxMenuServiceImpl.class);
    @Autowired
    SWxMenuService sWxMenuService;
    @Autowired
    private UpLoadService upLoadService;

    @Override
    public String menuCreate(String json) {
        try {
            return sWxMenuService.menuCreate(json);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object drawpic(String fontToPics, String pic) {

            logger.info("fontToPics: [{}], pic: [{}]", fontToPics, pic);

            Gson gson = new Gson();
            JSONObject jsonObject = JSONObject.parseObject(fontToPics);
            Object fontToPicsObject = jsonObject.get("fontToPics");
            if (fontToPicsObject == null) {
                return ResultGenerator.genSuccessResult(pic);
            }

            List<FontToPic> fontToPicList = gson.fromJson(fontToPicsObject.toString(), new TypeToken<List<FontToPic>>() {
            }.getType());

            if (fontToPicList == null || (fontToPicList != null && fontToPicList.isEmpty())) {
                return ResultGenerator.genSuccessResult(pic);
            }

            File temp = null;
            StringBuilder errMsg = new StringBuilder();

            try {
                //加载前端已生成图片
                long startT0 = System.currentTimeMillis();
                BufferedImage backPic = ImageIO.read(new URL(pic));//slow code.............
                logger.info("背景图读取完成.....耗时: {}ms", System.currentTimeMillis() - startT0);
                int backPicWidth = backPic.getWidth();
                int backPicHeight = backPic.getHeight();
                System.out.println(backPicWidth + ", " + backPicHeight);
                Graphics2D backPicGraphics = backPic.createGraphics();

                AtomicInteger index = new AtomicInteger();
                long startT = System.currentTimeMillis();
                logger.info("开始处理背景图文字合成.....");

                for (FontToPic fontToPic : fontToPicList) {
                    logger.info("开始处理第{}个文字描述,并合并图片....", index.get());

                    String text = fontToPic.getText();
                    String writingMode = fontToPic.getWritingMode();//书写模式,空则横版,否则竖着从左到右一竖排书写
                    float rotate = fontToPic.getRotate();//旋转角度
                    float w = fontToPic.getW();//div宽
                    float h = fontToPic.getH();//div高
                    float l = fontToPic.getL();//div距离原点左侧距离
                    float t = fontToPic.getT();//div距离原点上侧距离
                    String align = fontToPic.getAlign();//'left', 'right', 'center'
                    String weight = fontToPic.getWeight();//'normal', 'bold'
                    int size = fontToPic.getSize();
                    String color = fontToPic.getColor();//"rgba(234, 12, 12, 1)"
                    String family = fontToPic.getFamily();

                    if (StringUtils.isEmpty(text) || StringUtils.isEmpty(align)
                            || StringUtils.isEmpty(weight) || StringUtils.isEmpty(color)
                            || StringUtils.isEmpty(family)) {
                        logger.warn("字体描述中有值为空.....");
                        continue;
                    }

                    if (color.startsWith("#")) {
                        logger.error("图片颜色值设置不正确....当前颜色值: {}", color);
                        return ResultGenerator.genFailResult("图片颜色值设置不正确,请联系管理员进行处理,当前颜色值: " + color);
                    }

                    String[] colors = color.substring(color.indexOf("(") + 1, color.indexOf(")")).split(",");

                    //创建相应字体
//                    String tmpFamily = fontMap.get(family);
//                    if (tmpFamily == null) {
//                        logger.error("字体描述有误,系统中不存在此字体!, {}", family);
//                        return ResultGenerator.genFailResult("图片字体值设置不正确,请联系管理员进行处理,当前字体: " + family);
//                    }
                    Font font = new Font(Font.DIALOG, "normal".equals(weight) ? Font.PLAIN : Font.BOLD, size);

                    //画div框
                    int wr = Math.round(w);
                    int hr = Math.round(h);
                    int lr = Math.round(l);
                    int tr = Math.round(t);

                    BufferedImage divBufferedImage = new BufferedImage(wr, hr, BufferedImage.TYPE_INT_RGB);
                    Graphics2D divGraphics2D = divBufferedImage.createGraphics();
                    divBufferedImage = divGraphics2D.getDeviceConfiguration().createCompatibleImage(wr, hr, Transparency.TRANSLUCENT);
                    Graphics2D divGraphics2D_A = divBufferedImage.createGraphics();
                    FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
                    divGraphics2D_A.setFont(font);
                    divGraphics2D_A.setColor(new Color(Integer.valueOf(colors[0].trim()), Integer.valueOf(colors[1].trim()), Integer.valueOf(colors[2].trim()), (int) Math.round(Double.valueOf(colors[3].trim()) * 255)));

                    if (StringUtils.isEmpty(writingMode)) {
                        int textWidth = fontMetrics.stringWidth(text);
                        int fx = 0;
                        int fy = 0;
                        fy = fontMetrics.getAscent();
                        //如果文字的宽度大于容纳文字的框的宽度,那么注定要开始进行换行操作,并且根据文字对齐方式进行计算文字的摆放位置
                        //合成根据文字宽度计算出来每行容纳的文字集,截断成多行的文字,分别进行渲染
                        if (textWidth > wr) {
                            List<String> textStrs = new ArrayList<>();
                            StringBuilder sb = new StringBuilder();

                            int textLength = text.length();
                            int subTextWidth = 0;
                            for (int i = 0; i < textLength; i++) {
                                String s1 = String.valueOf(text.charAt(i));
                                int i1 = fontMetrics.stringWidth(s1);
                                subTextWidth += i1;
                                if (subTextWidth <= wr) {
                                    sb.append(s1);
                                } else {
                                    textStrs.add(sb.toString());
                                    sb = new StringBuilder(s1);
                                    subTextWidth = i1;
                                }
                            }

                            textStrs.add(sb.toString());

                            int tmpindex = 0;
                            for (String textStr : textStrs) {

                                tmpindex++;

                                textWidth = fontMetrics.stringWidth(textStr);

                                if ("center".equals(align)) {
                                    fx = (wr - textWidth) / 2;//文字距离左侧距离
                                } else if ("left".equals(align)) {
                                    fx = 0;//文字距离左侧距离
                                } else {
                                    fx = wr - textWidth;//文字距离左侧距离

                                }

                                int sizex = fx;
                                int sizey = tmpindex * fontMetrics.getAscent() + tmpindex * fontMetrics.getDescent();
                                int sizex_max = sizex + wr;
                                textLength = textStr.length();

                                for (int j = 0; j < textLength; j++) {
                                    String s1 = String.valueOf(textStr.charAt(j));
                                    int i1 = fontMetrics.stringWidth(s1);
                                    if (sizex + i1 <= sizex_max) {
                                        divGraphics2D_A.drawString(s1, sizex, sizey);
                                        sizex += i1;
                                    } else {
                                        sizey += fontMetrics.getHeight();
                                        divGraphics2D_A.drawString(s1, fx, sizey);
                                        sizex = i1;
                                    }
                                }
                            }

                        } else {
                            if ("center".equals(align)) {
                                fx = (wr - textWidth) / 2;//文字距离左侧距离
                            } else if ("left".equals(align)) {
                                fx = 0;//文字距离左侧距离
                            } else {
                                fx = wr - textWidth;//文字距离左侧距离

                            }

                            int sizex = fx;
                            int sizey = fy;
                            int sizex_max = sizex + wr;
                            int textLength = text.length();

                            for (int j = 0; j < textLength; j++) {
                                String s1 = String.valueOf(text.charAt(j));
                                int i1 = fontMetrics.stringWidth(s1);
                                if (sizex + i1 <= sizex_max) {
                                    divGraphics2D_A.drawString(s1, sizex, sizey);
                                    sizex += i1;
                                } else {
                                    sizey += fontMetrics.getHeight();
                                    divGraphics2D_A.drawString(s1, fx, sizey);
                                    sizex = i1;
                                }
                            }
                        }
                    } else {
                        if ("vertical-lr".equals(writingMode)) {//默认就这一种
                            //计算文字总高度(包括中英文)
                            //分析共?列文字,超出列不显示
                            //计算文字每列容纳的文字集合,并存储起来
                            //对齐方式,如居左(顶头书写)/居右(底书写)/居中(上下留有等距离空间书写)
                            int t_length = text.length();

                            List<String> textStrs = new ArrayList<>();
                            StringBuilder sb = new StringBuilder();

                            int subTextHeight = 0;
                            for (int i = 0; i < t_length; i++) {
                                char c = text.charAt(i);
                                String s1 = String.valueOf(c);
                                int i1;
                                if(!(19968 <= (int)c && (int)c <40869)) {
                                    i1 = fontMetrics.stringWidth(s1);
                                } else {
                                    i1 = fontMetrics.getHeight();
                                }

                                subTextHeight += i1;
                                if (subTextHeight <= hr) {
                                    sb.append(s1);
                                } else {
                                    textStrs.add(sb.toString());
                                    sb = new StringBuilder(s1);
                                    subTextHeight = i1;
                                }
                            }

                            textStrs.add(sb.toString());

                            int fx = 0;
                            int fy = 0;
                            int t_multiple = textStrs.size() - 1;//共?列
                            int t_remainder = 0;//最后一列高度
                            String laststr = textStrs.get(t_multiple);
                            for (int b = 0; b < laststr.length(); b++) {
                                char c = laststr.charAt(b);
                                String s1 = String.valueOf(c);
                                int i1;
                                if(!(19968 <= (int)c && (int)c <40869)) {
                                    i1 = fontMetrics.stringWidth(s1);
                                } else {
                                    i1 = fontMetrics.getHeight();
                                }
                                t_remainder += i1;
                            }


                            if (t_multiple == 0) {
                                if ("center".equals(align)) {
                                    fy = (hr - t_remainder) / 2;
                                } else if ("left".equals(align)) {
                                    fy = fontMetrics.getAscent();
                                } else {
                                    fy = hr - t_remainder;
                                }

                                int sizex = fx;
                                int sizey = fy;

                                for (int j = 0; j < t_length; j++) {
                                    Graphics2D graphics = (Graphics2D)divGraphics2D_A.create();
                                    char c = text.charAt(j);
                                    String s1 = String.valueOf(c);
                                    BufferedImage letter = null;
//                                    if(!(19968 <= (int)c && (int)c <40869)) {
//                                        letter = new BufferedImage(fontMetrics.getHeight(), fontMetrics.getHeight(), BufferedImage.TYPE_INT_RGB);
//                                        Graphics2D graphics1 = (Graphics2D)letter.getGraphics();
//                                        letter = graphics1.getDeviceConfiguration().createCompatibleImage(fontMetrics.getHeight(), fontMetrics.getHeight(), Transparency.TRANSLUCENT);
//                                        Graphics2D letterGraphics = letter.createGraphics();
//                                        letterGraphics.setFont(font);
//                                        letterGraphics.setColor(new Color(Integer.valueOf(colors[0].trim()), Integer.valueOf(colors[1].trim()), Integer.valueOf(colors[2].trim()), (int) Math.round(Double.valueOf(colors[3].trim()) * 255)));
//                                        letterGraphics.rotate(Math.toRadians(90), fontMetrics.getHeight() / 2, fontMetrics.getHeight() / 2);
//                                        letterGraphics.drawString(s1, fontMetrics.getDescent(), fontMetrics.getAscent());
//                                        letterGraphics.dispose();
//                                        graphics1.dispose();
//                                    }

                                    if (letter != null) {
                                        graphics.drawImage(letter.getScaledInstance(letter.getWidth(), letter.getHeight(), Image.SCALE_SMOOTH), sizex, sizey - fontMetrics.getAscent(), null);
                                    } else {
                                        graphics.drawString(s1, sizex, sizey);
                                    }
                                    sizey += fontMetrics.getHeight();
                                    graphics.dispose();
                                }
                            } else {
                                int sizex = fx;
                                int sizey = fontMetrics.getAscent();
                                for (int x = 0; x < t_multiple; x++) {
                                    String textStr = textStrs.get(x);
                                    for (char c: textStr.toCharArray()) {
                                        Graphics2D graphics = (Graphics2D)divGraphics2D_A.create();
                                        String s1 = String.valueOf(c);
                                        BufferedImage letter = null;
//                                    if(!(19968 <= (int)c && (int)c <40869)) {
//                                        letter = new BufferedImage(fontMetrics.getHeight(), fontMetrics.getHeight(), BufferedImage.TYPE_INT_RGB);
//                                        Graphics2D graphics1 = (Graphics2D)letter.getGraphics();
//                                        letter = graphics1.getDeviceConfiguration().createCompatibleImage(fontMetrics.getHeight(), fontMetrics.getHeight(), Transparency.TRANSLUCENT);
//                                        Graphics2D letterGraphics = letter.createGraphics();
//                                        letterGraphics.setFont(font);
//                                        letterGraphics.setColor(new Color(Integer.valueOf(colors[0].trim()), Integer.valueOf(colors[1].trim()), Integer.valueOf(colors[2].trim()), (int) Math.round(Double.valueOf(colors[3].trim()) * 255)));
//                                        letterGraphics.rotate(Math.toRadians(90), fontMetrics.getHeight() / 2, fontMetrics.getHeight() / 2);
//                                        letterGraphics.drawString(s1, fontMetrics.getDescent(), fontMetrics.getAscent());
//                                        letterGraphics.dispose();
//                                        graphics1.dispose();
//                                    }

                                        if (letter != null) {
                                            graphics.drawImage(letter.getScaledInstance(letter.getWidth(), letter.getHeight(), Image.SCALE_SMOOTH), sizex, sizey - fontMetrics.getAscent(), null);
                                        } else {
                                            graphics.drawString(s1, sizex, sizey);
                                        }
                                        sizey += fontMetrics.getHeight();
                                        graphics.dispose();
                                    }
                                    int width = fontMetrics.stringWidth(String.valueOf(textStr.charAt(0)));
                                    sizex += width;
                                    sizey = fontMetrics.getAscent();
                                }

                                if ("center".equals(align)) {
                                    fy = (hr - t_remainder) / 2;
                                } else if ("left".equals(align)) {
                                    fy = fontMetrics.getAscent();
                                } else {
                                    fy = hr - t_remainder;
                                }

                                sizey = fy;
                                t_length = textStrs.get(t_multiple).length();

                                for (int j = 0; j < t_length; j++) {
                                    Graphics2D graphics = (Graphics2D)divGraphics2D_A.create();
                                    char c = textStrs.get(t_multiple).charAt(j);
                                    String s1 = String.valueOf(c);
                                    BufferedImage letter = null;
//                                    if(!(19968 <= (int)c && (int)c <40869)) {
//                                        letter = new BufferedImage(fontMetrics.getHeight(), fontMetrics.getHeight(), BufferedImage.TYPE_INT_RGB);
//                                        Graphics2D graphics1 = (Graphics2D)letter.getGraphics();
//                                        letter = graphics1.getDeviceConfiguration().createCompatibleImage(fontMetrics.getHeight(), fontMetrics.getHeight(), Transparency.TRANSLUCENT);
//                                        Graphics2D letterGraphics = letter.createGraphics();
//                                        letterGraphics.setFont(font);
//                                        letterGraphics.setColor(new Color(Integer.valueOf(colors[0].trim()), Integer.valueOf(colors[1].trim()), Integer.valueOf(colors[2].trim()), (int) Math.round(Double.valueOf(colors[3].trim()) * 255)));
//                                        letterGraphics.rotate(Math.toRadians(90), fontMetrics.getHeight() / 2, fontMetrics.getHeight() / 2);
//                                        letterGraphics.drawString(s1, fontMetrics.getDescent(), fontMetrics.getAscent());
//                                        letterGraphics.dispose();
//                                        graphics1.dispose();
//                                    }

                                    if (letter != null) {
                                        graphics.drawImage(letter.getScaledInstance(letter.getWidth(), letter.getHeight(), Image.SCALE_SMOOTH), sizex, sizey - fontMetrics.getAscent(), null);
                                    } else {
                                        graphics.drawString(s1, sizex, sizey);
                                    }
                                    sizey += fontMetrics.getHeight();
                                    graphics.dispose();
                                }
                            }

                        }
                    }
                    divGraphics2D.dispose();

                    backPicGraphics.drawImage(divBufferedImage.getScaledInstance(wr, hr, Image.SCALE_SMOOTH), lr, tr, null);
                    index.getAndIncrement();
                }

//                if (!StringUtils.isEmpty(filterPic)) {
//                    long startT_T = System.currentTimeMillis();
//                    BufferedImage filterPIcBufferedImage = ImageIO.read(new URL(filterPic));
//                    backPicGraphics.drawImage(filterPIcBufferedImage.getScaledInstance(backPicWidth, backPicHeight, Image.SCALE_SMOOTH), 0, 0, null);
//                    logger.info("过滤层图片读取合并到背景图完成.....耗时: {}ms", System.currentTimeMillis() - startT_T);
//                }

                backPicGraphics.dispose();

                temp = File.createTempFile("temp", ".png");
//            ImageIO.write(backPic, "JPG", temp);//faster 不支持透明度
                BufferedOutputStream imageOutputStream = new BufferedOutputStream(new FileOutputStream(temp));
                ImageIO.write(backPic, "PNG", imageOutputStream);//Mildly faster
                imageOutputStream.close();
                logger.info("开始处理背景图文字合成.....共耗时: {}ms", (System.currentTimeMillis() - startT));

                return ResultGenerator.genSuccessResult(upLoadService.upFile(temp));//调用阿里oss上传文件接口,并返回文件cdn路径
            } catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
                errMsg.append(e.getMessage() + "\n");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                errMsg.append(e.getMessage() + "\n");
            } finally {
                if (temp != null && temp.exists()) {
                    temp.delete();
                }
            }
            return ResultGenerator.genFailResult(errMsg.length() == 0 ? "图片保存失败" : errMsg.toString());
    }
}
