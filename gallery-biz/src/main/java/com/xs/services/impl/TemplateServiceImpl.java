package com.xs.services.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.*;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.*;
import com.xs.services.TemplateCategoryService;
import com.xs.services.TemplateService;
import com.xs.core.sservice.AbstractService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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

@Service("templateService")
@Transactional
public class TemplateServiceImpl extends AbstractService<Template> implements TemplateService {
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TemplateCategoryMapper templateCategoryMapper;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;
    @Autowired
    private TemplateLabelsMapper templateLabelsMapper;
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public Object queryWithPage(int page, int size, Boolean isEnabled, Byte ratio, Integer categoryId, String name, Integer brandId, Boolean isBrand) {

        PageHelper.startPage(page, size);

        Condition condition = new Condition(Template.class);
        Example.Criteria criteria = condition.createCriteria();

        if (isEnabled != null) {
            criteria.andEqualTo("isEnabled", isEnabled);
        }
        if (ratio != null) {
            criteria.andEqualTo("ratio", ratio);
        }
        if (categoryId != null) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%"+ name +"%");
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

        List<Template> list = super.findByCondition(condition);

        for (int i = 0, j = list.size(); i < j; i++) {

            TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(list.get(i).getCategoryId());
            if (templateCategory != null) {
                list.get(i).setCategoryName(templateCategory.getTitle());
            }

            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(list.get(i).getBrandId());
            if (companyBrand != null) {
                list.get(i).setBrandName(companyBrand.getName());
            }

        }

        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    public void save(Template model) {

        TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(model.getCategoryId());
        if (templateCategory == null) {
            throw new ServiceException("模块分类数据不存在或已删除");
        }

        if (model.getBrandId() != null && model.getBrandId().intValue() != 0) {
            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(model.getBrandId());
            if (companyBrand == null) {
                throw new ServiceException("品牌数据不存在或已删除");
            }
        }

        if (StringUtils.isEmpty(model.getLabelIds())) {
            throw new ServiceException("标签数据不可为空");
        } else {
            for (String labelId : model.getLabelIds().split(",")) {
                Label label = labelMapper.selectByPrimaryKey(Integer.valueOf(labelId));
                if (label == null) {
                    throw new ServiceException("标签数据不存在或已删除");
                }
            }
        }

        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        model.setDescri(StringUtils.EMPTY);

        super.save(model);

        for (String labelId : model.getLabelIds().split(",")) {
            TemplateLabels templateLabels = new TemplateLabels();
            templateLabels.setGmtCreate(new Date());
            templateLabels.setLabelId(Integer.valueOf(labelId));
            templateLabels.setTemplateId(model.getId());
            templateLabelsMapper.insert(templateLabels);
        }
    }

    @Override
    public void update(Template model) {
        TemplateCategory templateCategory = templateCategoryMapper.selectByPrimaryKey(model.getCategoryId());
        if (templateCategory == null) {
            throw new ServiceException("模块分类数据不存在或已删除");
        }

        Template template = this.findById(model.getId());
        if (template == null) {
            throw new ServiceException("模块数据不存在或已删除");
        }
        if (model.getBrandId() != null && model.getBrandId().intValue() != 0) {
            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(model.getBrandId());
            if (companyBrand == null) {
                throw new ServiceException("品牌数据不存在或已删除");
            }
        }

        if (StringUtils.isEmpty(model.getLabelIds())) {
            throw new ServiceException("标签数据不可为空");
        } else {
            for (String labelId : model.getLabelIds().split(",")) {
                Label label = labelMapper.selectByPrimaryKey(Integer.valueOf(labelId));
                if (label == null) {
                    throw new ServiceException("标签数据不存在或已删除");
                }
            }
        }

        BeanUtils.copyProperties(model, template);

        template.setGmtModified(new Date());
        super.update(template);

        Condition templateLabelsCondition = new Condition(TemplateLabels.class);
        Example.Criteria templateLabelsConditionCriteria = templateLabelsCondition.createCriteria();
        templateLabelsConditionCriteria.andEqualTo("templateId", template.getId());
        templateLabelsMapper.deleteByCondition(templateLabelsCondition);

        for (String labelId : model.getLabelIds().split(",")) {
            TemplateLabels templateLabels = new TemplateLabels();
            templateLabels.setGmtCreate(new Date());
            templateLabels.setLabelId(Integer.valueOf(labelId));
            templateLabels.setTemplateId(template.getId());
            templateLabelsMapper.insert(templateLabels);
        }
    }

    @Override
    public Template findById(Integer id) {
        Template template = super.findById(id);
        if (template == null) {
            throw new ServiceException("模板数据不存在或已删除");
        }
        Condition templateLabelsCondition = new Condition(TemplateLabels.class);
        Example.Criteria templateLabelsConditionCriteria = templateLabelsCondition.createCriteria();
        templateLabelsConditionCriteria.andEqualTo("templateId", template.getId());
        List<TemplateLabels> templateLabels = templateLabelsMapper.selectByCondition(templateLabelsCondition);
        if (templateLabels != null) {
            List<HashMap> lidsList = new ArrayList<>();
            for(TemplateLabels tl : templateLabels) {
                Label label = labelMapper.selectByPrimaryKey(tl.getLabelId());
                if (label != null) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("name", label.getName());
                    hashMap.put("id", label.getId());
                    lidsList.add(hashMap);
                }
            }
            if (!lidsList.isEmpty()) {
                template.setLabelIds(JSONObject.toJSONString(lidsList));
            }
        }
        return template;
    }
}
