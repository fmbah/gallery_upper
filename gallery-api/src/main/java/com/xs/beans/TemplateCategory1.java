package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "tb_template_category")
public class TemplateCategory1 extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "自增ID(新增时不用传)")
    private Integer id;

    @ApiModelProperty(value = "标题")
    @Length(max = 45,message = "返利名称最长45个字符!")
    @NotBlank(message = "标题不能为空")
    private String title;


    @ApiModelProperty(value = "简介")
    @Length(max = 128,message = "简介最长128个字符!")
    @NotBlank(message = "简介不能为空")
    private String introduction;

    /**
     * 分类背景图
     */
    @Column(name = "background_image_url")
    @ApiModelProperty(value = "分类背景图")
    @Length(max = 192,message = "分类背景图最长192个字符!")
    @NotBlank(message = "分类背景图不能为空")
    private String backgroundImageUrl;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }
}