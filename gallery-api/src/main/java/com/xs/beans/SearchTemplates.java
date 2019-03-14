package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName SearchTemplates
 * @Description
 * @Author root
 * @Date 18-10-20 上午9:57
 * @Version 1.0
 **/
public class SearchTemplates extends BaseBean {

    @ApiModelProperty(value = "分类标题")
    String tcTitle;
    @ApiModelProperty(value = "模板名称")
    String tName;
    @ApiModelProperty(value = "标签名称,多个以逗号分开")
    String _lNames;
    @ApiModelProperty(value = "分类id")
    Integer tcId;
    @ApiModelProperty(value = "比例")
    String tRatio;

    @ApiModelProperty(value = "标签名称,内部系统使用")
    String[] lNames;

    @ApiModelProperty(value = "当前用户id")
    Integer userId;

    @ApiModelProperty(value = "多个比例,内部系统使用")
    String[] _tRatios;

    @ApiModelProperty(value = "模糊搜索文字(可能模板名称/分类标题/标签名称)")
    String searchValue;

    @ApiModelProperty(value = "免费")
    String tGratis;


    public String gettGratis() {
        return tGratis;
    }

    public void settGratis(String tGratis) {
        this.tGratis = tGratis;
    }

    public String getTcTitle() {
        return tcTitle;
    }

    public void setTcTitle(String tcTitle) {
        this.tcTitle = tcTitle;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String get_lNames() {
        return _lNames;
    }

    public void set_lNames(String _lNames) {
        this._lNames = _lNames;
    }

    public Integer getTcId() {
        return tcId;
    }

    public void setTcId(Integer tcId) {
        this.tcId = tcId;
    }

    public String gettRatio() {
        return tRatio;
    }

    public void settRatio(String tRatio) {
        this.tRatio = tRatio;
    }

    public String[] getlNames() {
        return lNames;
    }

    public void setlNames(String[] lNames) {
        this.lNames = lNames;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String[] get_tRatios() {
        return _tRatios;
    }

    public void set_tRatios(String[] _tRatios) {
        this._tRatios = _tRatios;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
}

