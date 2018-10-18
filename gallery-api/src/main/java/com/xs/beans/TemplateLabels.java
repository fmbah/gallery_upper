package com.xs.beans;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_template_labels")
public class TemplateLabels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "label_id")
    private Integer labelId;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return template_id
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId
     */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    /**
     * @return label_id
     */
    public Integer getLabelId() {
        return labelId;
    }

    /**
     * @param labelId
     */
    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
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
}