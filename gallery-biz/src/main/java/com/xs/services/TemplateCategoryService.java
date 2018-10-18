package com.xs.services;
import com.xs.beans.TemplateCategory;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface TemplateCategoryService extends Service<TemplateCategory> {

    /**
     *
     * 功能描述: 分类集合
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 下午3:43
     */
    Object queryWithPage(int page, int size, Boolean isHot, String title);

    /**
     *
     * 功能描述: 修改滤镜
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 下午4:06
     */
    Object updateFilters(Integer id, String filters);

}
