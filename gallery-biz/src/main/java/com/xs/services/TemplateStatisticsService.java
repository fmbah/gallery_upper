package com.xs.services;

import com.xs.beans.TemplateStatistics;
import com.xs.core.sservice.Service;

import java.util.HashMap;
import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface TemplateStatisticsService extends Service<TemplateStatistics> {

    /**
     *
     * 功能描述: 统计分页数据
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 下午3:15
     */
    Object queryWithPage(int page, int size, Integer categoryId,
                         String name, Integer brandId, Boolean isBrand,
                         String sTime, String eTime);


    /**
     *
     * 功能描述: 统计管理分类数据
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 下午4:29
     */
    List<HashMap> queryCategoryDatas(String sTime, String eTime);

}
