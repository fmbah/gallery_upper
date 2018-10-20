package com.xs.beans;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Table(name = "tb_admin_menu")
public class AdminMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 父id
     */
    @Column(name = "p_id")
    private Long pId;

    /**
     * 菜单名
     */
    @Column(name = "menu_name")
    private String menuName;

    /**
     * 角标
     */
    @Column(name = "menu_icon")
    private String menuIcon;

    /**
     * 页面跳转路径
     */
    @Column(name = "menu_address")
    private String menuAddress;

    /**
     * 排序权重，从大到小
     */
    private Integer weight;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;


    @Transient
    private List<AdminMenu> children;
    @Transient
    @ApiModelProperty(value = "是否选中")
    private Boolean hasSelected;
    @Transient
    private String label;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取父id
     *
     * @return p_id - 父id
     */
    public Long getpId() {
        return pId;
    }

    /**
     * 设置父id
     *
     * @param pId 父id
     */
    public void setpId(Long pId) {
        this.pId = pId;
    }

    /**
     * 获取菜单名
     *
     * @return menu_name - 菜单名
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * 设置菜单名
     *
     * @param menuName 菜单名
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * 获取角标
     *
     * @return menu_icon - 角标
     */
    public String getMenuIcon() {
        return menuIcon;
    }

    /**
     * 设置角标
     *
     * @param menuIcon 角标
     */
    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    /**
     * 获取页面跳转路径
     *
     * @return menu_address - 页面跳转路径
     */
    public String getMenuAddress() {
        return menuAddress;
    }

    /**
     * 设置页面跳转路径
     *
     * @param menuAddress 页面跳转路径
     */
    public void setMenuAddress(String menuAddress) {
        this.menuAddress = menuAddress;
    }

    /**
     * 获取排序权重，从大到小
     *
     * @return weight - 排序权重，从大到小
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * 设置排序权重，从大到小
     *
     * @param weight 排序权重，从大到小
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * @return gmt_create
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * @param gmtCreate
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * @return gmt_modified
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * @param gmtModified
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public List<AdminMenu> getChildren() {
        return children;
    }

    public void setChildren(List<AdminMenu> children) {
        this.children = children;
    }

    public Boolean getHasSelected() {
        return hasSelected;
    }

    public void setHasSelected(Boolean hasSelected) {
        this.hasSelected = hasSelected;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}