package com.xs.daos;

import com.xs.beans.TemplateStatistics;
import com.xs.core.smapper.SMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.HashMap;
import java.util.List;

public interface TemplateStatisticsMapper extends SMapper<TemplateStatistics> {

    /**
     *
     * 功能描述: 统计管理分类数据
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 下午4:29
     */
    List<HashMap> queryCategoryDatas(TemplateStatistics templateStatistics);

    /**
     *
     * 功能描述: 普通模板统计
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-25 下午3:07
     */
    List<HashMap> queryTemplateCensusDatas(TemplateStatistics templateStatistics);

    /**
     *
     * 功能描述: 品牌模板统计
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-25 下午3:07
     */
    List<HashMap> queryBrandTemplateCensusDatas(TemplateStatistics templateStatistics);
}