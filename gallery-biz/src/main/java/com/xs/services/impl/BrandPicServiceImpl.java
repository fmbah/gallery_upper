package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.CompanyBrand;
import com.xs.beans.Template;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.BrandPicMapper;
import com.xs.beans.BrandPic;
import com.xs.daos.CompanyBrandMapper;
import com.xs.daos.TemplateMapper;
import com.xs.services.BrandPicService;
import com.xs.core.sservice.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2019/01/02
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("brandpicService")
@Transactional
public class BrandPicServiceImpl extends AbstractService<BrandPic> implements BrandPicService {
    @Autowired
    private BrandPicMapper brandpicMapper;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;

    @Override
    public void save(BrandPic model) {
        Date now = new Date();
        model.setGmtCreate(now);
        model.setGmtModified(now);
        model.setMiniappDisplaySrc(StringUtils.EMPTY);
        model.setRemark(StringUtils.EMPTY);
        model.setTemplateId(0);
        super.save(model);
    }

    @Override
    public void deleteById(Integer id) {
        BrandPic brandPic = brandpicMapper.selectByPrimaryKey(id);
        if (brandPic == null) {
            throw new ServiceException("品牌图片数据不存在");
        }
        if (brandPic.getTemplateId() != 0) {
            templateMapper.deleteByPrimaryKey(brandPic.getTemplateId());
        }
        super.deleteById(id);
    }

    @Override
    public void update(BrandPic model) {
        Date now = new Date();
        model.setGmtModified(now);
        Template template = null;
        if (model.getStatus().byteValue() == 1) {
            model.setMiniappDisplaySrc(model.getLatestApplySrc());
            if (model.getTemplateId() == null || model.getTemplateId() == 0) {
                template = new Template();
                template.setCategoryId(0);
                template.setBrandId(model.getBrandId());
                template.setRatio(new Byte("0"));
                template.setEnabled(true);
                template.setPreviewImageUrl(model.getLatestApplySrc());
                template.setDescri(StringUtils.EMPTY);
                template.setName(model.getPicName());
                template.setGmtModified(now);
                template.setGmtCreate(now);
                template.setGratis(false);
                template.setPhonePreviewImageUrl(StringUtils.EMPTY);
                templateMapper.insert(template);
            } else {
                template = templateMapper.selectByPrimaryKey(model.getTemplateId());
                template.setGmtModified(now);
                template.setPreviewImageUrl(model.getLatestApplySrc());
                templateMapper.updateByPrimaryKey(template);
            }
        }
        if (model.getStatus().byteValue() == 2) {

        }
        if (template != null) {
            model.setTemplateId(template.getId());
        }
        super.update(model);
    }

    @Override
    public BrandPic findById(Integer id) {
        return super.findById(id);
    }

    @Override
    public List<BrandPic> findAll() {
        return super.findAll();
    }

    @Override
    public Object queryWithPage(int page, int size, Byte status, String picName, Integer brandId) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(BrandPic.class);
        Example.Criteria criteria = condition.createCriteria();
        if (status != null) {
            criteria.andEqualTo("status", status);
        }
        if (!StringUtils.isEmpty(picName)) {
            criteria.andLike("picName", "%"+ picName +"%");
        }
        if (brandId != null) {
            criteria.andEqualTo("brandId", brandId);
        }
        condition.setOrderByClause(" gmt_modified desc");
        List<BrandPic> list = brandpicMapper.selectByCondition(condition);
        HashMap<Integer, CompanyBrand> cache = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompanyBrand tmp = cache.get(list.get(i).getBrandId());
            if (tmp != null) {
                list.get(i).setBrandName(tmp.getName());
            } else {
                CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(list.get(i).getBrandId());
                if (null != companyBrand) {
                    cache.put(list.get(i).getBrandId(), companyBrand);
                    list.get(i).setBrandName(companyBrand.getName());
                }
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public void audit(Integer id, Byte status, String remark) {
        BrandPic brandPic = this.findById(id);
        if (brandPic == null) {
            throw new ServiceException("品牌图片数据不存在");
        }

        brandPic.setStatus(status);
        brandPic.setRemark(StringUtils.isEmpty(remark) ? StringUtils.EMPTY : remark);
        this.update(brandPic);
    }
}
