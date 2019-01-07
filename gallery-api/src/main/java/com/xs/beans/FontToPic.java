package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;

/**
 * @ClassName FontToPic
 * @Description 协助前端,将文字对应属性合成一张图片
 * @Author root
 * @Date 18-12-18 下午7:15
 * @Version 1.0
 **/
@ApiModel(value = "FontToPic",description = "协助前端,将文字对应属性合成一张图片")
public class FontToPic extends BaseBean {

    private String align;
    private String color;
    private String family;
    private String source;
    private float h;
    private float w;
    private float t;
    private float l;
    private float rotate;
    private int size;
    private String text;
    private String weight;

    private float textW;

    private String writingMode;//从左向右


    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public float getL() {
        return l;
    }

    public void setL(float l) {
        this.l = l;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    public float getTextW() {
        return textW;
    }

    public void setTextW(float textW) {
        this.textW = textW;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWritingMode() {
        return writingMode;
    }

    public void setWritingMode(String writingMode) {
        this.writingMode = writingMode;
    }
}
