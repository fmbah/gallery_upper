package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xs.beans.Template;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.TemplateCategoryMapper;
import com.xs.beans.TemplateCategory;
import com.xs.daos.TemplateMapper;
import com.xs.services.TemplateCategoryService;
import com.xs.core.sservice.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("templatecategoryService")
@Transactional
public class TemplateCategoryServiceImpl extends AbstractService<TemplateCategory> implements TemplateCategoryService {
    @Autowired
    private TemplateCategoryMapper templatecategoryMapper;
    @Autowired
    private TemplateMapper templateMapper;


    @Override
    public Object queryWithPage(int page, int size, Boolean isHot, String title) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(TemplateCategory.class);
        Example.Criteria criteria = condition.createCriteria();
        if (isHot != null) {
            criteria.andEqualTo("isHot", isHot);
        }
        if (!StringUtils.isEmpty(title)) {
            criteria.andLike("title", "%" + title + "%");
        }
        condition.setOrderByClause(" gmt_modified desc");
        List<TemplateCategory> templateCategoryList = super.findByCondition(condition);

        Gson gson = new Gson();
        for (int i = 0, j = templateCategoryList.size(); i < j; i++) {
            if (!StringUtils.isEmpty(templateCategoryList.get(i).getTemplateFilters())) {
                List<HashMap> filters = gson.fromJson(templateCategoryList.get(i).getTemplateFilters(), new TypeToken<List<HashMap>>(){}.getType());
                templateCategoryList.get(i).setFiltersCount(filters == null || filters.size() == 0 ? 0 : filters.size());
            }

            Condition temCondition = new Condition(Template.class);
            Example.Criteria temConditionCriteria = temCondition.createCriteria();
            temConditionCriteria.andEqualTo("categoryId", templateCategoryList.get(i).getId());
            List<Template> templates = templateMapper.selectByCondition(temCondition);
            templateCategoryList.get(i).setTemplateCount(templates == null ? 0 : templates.size());

        }

        PageInfo pageInfo = new PageInfo(templateCategoryList);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public Object updateFilters(Integer id, String filters) {

        TemplateCategory templateCategory = this.findById(id);
        if (templateCategory == null) {
            return ResultGenerator.genFailResult("分类数据不存在或已删除");
        }

        templateCategory.setGmtModified(new Date());
        templateCategory.setTemplateFilters(filters);
        super.update(templateCategory);

        return ResultGenerator.genSuccessResult();
    }


    @Override
    public void save(TemplateCategory model) {
        model.setGmtModified(new Date());
        model.setGmtCreate(new Date());
        super.save(model);
    }

    @Override
    public void update(TemplateCategory model) {

        TemplateCategory templateCategory = super.findById(model.getId());
        if (templateCategory == null) {
            throw new ServiceException("分类数据不存在或已删除");
        }

        BeanUtils.copyProperties(model, templateCategory);
        templateCategory.setGmtModified(new Date());

        super.update(model);
    }

    @Override
    public TemplateCategory findById(Integer id) {

        TemplateCategory templateCategory = super.findById(id);
        if (templateCategory == null) {
            throw new ServiceException("分类数据不存在或已删除");
        }
        if (!StringUtils.isEmpty(templateCategory.getTemplateFilters())) {
            Gson gson = new Gson();
            List<HashMap> filters = gson.fromJson(templateCategory.getTemplateFilters(), new TypeToken<List<HashMap>>(){}.getType());
            templateCategory.setFiltersCount(filters == null || filters.size() == 0 ? 0 : filters.size());
        }

        Condition temCondition = new Condition(Template.class);
        Example.Criteria temConditionCriteria = temCondition.createCriteria();
        temConditionCriteria.andEqualTo("categoryId", templateCategory.getId());
        List<Template> templates = templateMapper.selectByCondition(temCondition);
        templateCategory.setTemplateCount(templates == null ? 0 : templates.size());

        return templateCategory;
    }
}
