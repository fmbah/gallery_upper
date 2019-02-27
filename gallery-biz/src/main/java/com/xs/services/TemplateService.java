package com.xs.services;
import com.xs.beans.Template;
import com.xs.core.sservice.Service;
import io.swagger.models.auth.In;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface TemplateService extends Service<Template> {


    /**
     * 功能描述: 模板数据集合
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 下午6:20
     */
    Object queryWithPage(int page, int size, Boolean enabled, Byte ratio, Integer categoryId, String name, Integer brandId, Boolean isBrand, Boolean gratis);

    Template queryTemplateInfo(Integer id);
}