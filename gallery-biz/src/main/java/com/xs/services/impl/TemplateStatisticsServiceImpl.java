package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.CompanyBrand;
import com.xs.beans.Template;
import com.xs.beans.TemplateCategory;
import com.xs.core.ResultGenerator;
import com.xs.daos.CompanyBrandMapper;
import com.xs.daos.TemplateCategoryMapper;
import com.xs.daos.TemplateMapper;
import com.xs.daos.TemplateStatisticsMapper;
import com.xs.beans.TemplateStatistics;
import com.xs.services.TemplateStatisticsService;
import com.xs.core.sservice.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.xml.transform.Templates;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("templatestatisticsService")
@Transactional
public class TemplateStatisticsServiceImpl extends AbstractService<TemplateStatistics> implements TemplateStatisticsService {
    @Autowired
    private TemplateStatisticsMapper templatestatisticsMapper;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;
    @Autowired
    private TemplateCategoryMapper templateCategoryMapper;


    @Override
    public Object queryWithPage(int page, int size, Integer categoryId, String name, Integer brandId, Boolean isBrand, String sTime, String eTime) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(TemplateStatistics.class);
        Example.Criteria criteria = condition.createCriteria();
        if (categoryId != null) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (isBrand) {
            if (brandId != null) {
                criteria.andEqualTo("brandId", brandId);
            } else {
                criteria.andNotEqualTo("brandId", 0);
            }
        } else {
            criteria.andEqualTo("brandId", 0);
        }

        if (!StringUtils.isEmpty(sTime)) {
            criteria.andGreaterThanOrEqualTo("gmtCreate", sTime);
        }
        if (!StringUtils.isEmpty(eTime)) {
            criteria.andLessThanOrEqualTo("gmtCreate", eTime);
        }

        List<TemplateStatistics> list = super.findByCondition(condition);
        if (!StringUtils.isEmpty(name) && list != null && !list.isEmpty()) {
            Iterator<TemplateStatistics> iterator = list.iterator();
            while (iterator.hasNext()) {
                TemplateStatistics next = iterator.next();
                Template template = templateMapper.selectByPrimaryKey(next.getTemplateId());
                if (template.getName().indexOf(name) < 0) {
                    iterator.remove();
                }
            }
        }

        if (list != null && !list.isEmpty()) {
            for (TemplateStatistics templateStatistics : list) {
                CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(templateStatistics.getBrandId());
                if (companyBrand != null) {
                    templateStatistics.setBrandName(companyBrand.getName());
                }
                TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(templateStatistics.getCategoryId());
                if (templateCategory != null) {
                    templateStatistics.setCategoryName(templateCategory.getTitle());
                }
                Template template = templateMapper.selectByPrimaryKey(templateStatistics.getTemplateId());
                if (template != null) {
                    templateStatistics.setTemplateName(template.getName());
                }
            }
        }
        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    public List<HashMap> queryCategoryDatas(String sTime, String eTime) {
        TemplateStatistics templateStatistics = new TemplateStatistics();
        templateStatistics.setsTime(sTime);
        templateStatistics.seteTime(eTime);
        return templatestatisticsMapper.queryCategoryDatas(templateStatistics);
    }
}
