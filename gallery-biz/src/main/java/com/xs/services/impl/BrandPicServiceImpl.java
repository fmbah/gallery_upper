package com.xs.services.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.xs.beans.BrandPic;
import com.xs.beans.CompanyBrand;
import com.xs.beans.Template;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.core.sservice.AbstractService;
import com.xs.daos.BrandPicMapper;
import com.xs.daos.CompanyBrandMapper;
import com.xs.daos.TemplateMapper;
import com.xs.services.BrandPicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.*;


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
	@Transactional(rollbackFor = ServiceException.class)
    public void save(BrandPic model) {
		JSONArray array = JSONObject.parseArray(model.getLatestApplySrc());
		if (array == null || array.isEmpty()) {
			throw new ServiceException("品牌图片文件数据为空");
		}
        Date now = new Date();
        model.setGmtCreate(now);
        model.setGmtModified(now);
        model.setMiniappDisplaySrc(StringUtils.EMPTY);
        model.setRemark(StringUtils.EMPTY);
        model.setTemplateId("0");

        // 平台方添加,直接进入模板中
		Template template = null;
		List<String> templateids = new ArrayList<>();
        if (model.getSource() == 1) {
			model.setMiniappDisplaySrc(model.getLatestApplySrc());
			model.setStatus(new Byte("1"));
			JSONArray jsonArray = JSONObject.parseArray(model.getLatestApplySrc());
			for (int i = 0; i < jsonArray.size(); i++) {
				template = new Template();
				template.setCategoryId(0);
				template.setBrandId(model.getBrandId());
				template.setRatio(new Byte("0"));
				template.setEnabled(true);
				template.setPreviewImageUrl(jsonArray.getString(i));
				template.setDescri(StringUtils.EMPTY);
				template.setName(model.getPicName());
				template.setGmtModified(now);
				template.setGmtCreate(now);
				template.setGratis(false);
				template.setPhonePreviewImageUrl(StringUtils.EMPTY);
				templateMapper.insert(template);

				templateids.add(template.getId().toString());
			}
		}
		if (template != null) {
			model.setTemplateId(JSONObject.toJSONString(templateids));
		}

        super.save(model);
    }

    @Override
	@Transactional(rollbackFor = ServiceException.class)
    public void deleteById(Integer id) {
        BrandPic brandPic = brandpicMapper.selectByPrimaryKey(id);
        if (brandPic == null) {
            throw new ServiceException("品牌图片数据不存在");
        }
        if (!brandPic.getTemplateId().equals("0")) {
			JSONArray templateIds = JSONArray.parseArray(brandPic.getTemplateId());
			for (int i =0; i < templateIds.size(); i++) {
				if (templateIds.get(i) != null) {
					templateMapper.deleteByPrimaryKey(templateIds.getInteger(i));
				}
			}
        }
        super.deleteById(id);
    }

    @Override
	@Transactional(rollbackFor = ServiceException.class)
    public void update(BrandPic model) {
		BrandPic brandPic = this.findById(model.getId());
		if (brandPic == null) {
			throw new ServiceException("品牌图片数据不存在");
		}
		if (brandPic.getSource() == 1) {
			model.setStatus(new Byte("1"));
		}
        Date now = new Date();
        model.setGmtModified(now);
        Template template = null;
        List<String> templateids = new ArrayList<>();
        if (model.getStatus().byteValue() == 1) {
			// "[\"1\", '2', '3']"
            model.setMiniappDisplaySrc(model.getLatestApplySrc());
            if (brandPic.getTemplateId() == null || brandPic.getTemplateId().equals("0")) {
				JSONArray jsonArray = JSONObject.parseArray(model.getLatestApplySrc());
				if (jsonArray == null || jsonArray.isEmpty()) {
					throw new ServiceException("品牌图片文件数据为空");
				}
				for (int i = 0; i < jsonArray.size(); i++) {
					template = new Template();
					template.setCategoryId(0);
					template.setBrandId(model.getBrandId());
					template.setRatio(new Byte("0"));
					template.setEnabled(true);
					template.setPreviewImageUrl(jsonArray.getString(i));
					template.setDescri(StringUtils.EMPTY);
					template.setName(model.getPicName());
					template.setGmtModified(now);
					template.setGmtCreate(now);
					template.setGratis(false);
					template.setPhonePreviewImageUrl(StringUtils.EMPTY);
					templateMapper.insert(template);

					templateids.add(template.getId().toString());
				}
            } else {
            	// 如果已存在图片模板的话,先将图片模板删除掉,然后重新添加
				JSONArray parseArray = JSONArray.parseArray(brandPic.getTemplateId());

				JSONArray jsonArray = JSONObject.parseArray(model.getLatestApplySrc());
				int size = jsonArray.size();
				int index = 0;
				if (jsonArray == null || jsonArray.isEmpty()) {
					throw new ServiceException("品牌图片文件数据为空");
				}

				for (int i = 0; i < parseArray.size(); i++) {
					Template tmp = templateMapper.selectByPrimaryKey(parseArray.getInteger(i));
					if (tmp != null) {
						if (index < size) {
							tmp.setGmtModified(now);
							tmp.setPreviewImageUrl(jsonArray.getString(index++));
							templateMapper.updateByPrimaryKey(tmp);
							templateids.add(tmp.getId().toString());
						} else {
							templateMapper.deleteByPrimaryKey(tmp.getId());

						}
					}
				}
				while (index < size) {
					template = new Template();
					template.setCategoryId(0);
					template.setBrandId(model.getBrandId());
					template.setRatio(new Byte("0"));
					template.setEnabled(true);
					template.setPreviewImageUrl(jsonArray.getString(index++));
					template.setDescri(StringUtils.EMPTY);
					template.setName(model.getPicName());
					template.setGmtModified(now);
					template.setGmtCreate(now);
					template.setGratis(false);
					template.setPhonePreviewImageUrl(StringUtils.EMPTY);
					templateMapper.insert(template);

					templateids.add(template.getId().toString());
				}
            }
        }
        if (model.getStatus().byteValue() == 2) {

        }
        if (!templateids.isEmpty() || template != null) {
            model.setTemplateId(JSONObject.toJSONString(templateids));
        }
        super.update(model);
    }

    @Override
    public BrandPic findById(Integer id) {

		BrandPic brandPic = super.findById(id);
		if (brandPic == null) {
			throw new ServiceException("图片数据不存在或已删除");
		}
		brandPic.setMiniappDisplaySrcs(StringUtils.isEmpty(brandPic.getMiniappDisplaySrc()) || "0".equals(brandPic.getMiniappDisplaySrc()) ? JSONArray.parseArray("[]") : JSONObject.parseArray(brandPic.getMiniappDisplaySrc()));
		brandPic.setLatestApplySrcs(StringUtils.isEmpty(brandPic.getLatestApplySrc()) || "0".equals(brandPic.getLatestApplySrc()) ? JSONArray.parseArray("[]") : JSONObject.parseArray(brandPic.getLatestApplySrc()));
		brandPic.setTemplateIds(StringUtils.isEmpty(brandPic.getTemplateId()) || "0".equals(brandPic.getTemplateId()) ? JSONArray.parseArray("[]") : JSONObject.parseArray(brandPic.getTemplateId()));
		return brandPic;
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
			list.get(i).setMiniappDisplaySrcs(StringUtils.isEmpty(list.get(i).getMiniappDisplaySrc()) || "0".equals(list.get(i).getMiniappDisplaySrc()) ? JSONArray.parseArray("[]") : JSONObject.parseArray(list.get(i).getMiniappDisplaySrc()));
			list.get(i).setLatestApplySrcs(StringUtils.isEmpty(list.get(i).getLatestApplySrc()) || "0".equals(list.get(i).getLatestApplySrc()) ? JSONArray.parseArray("[]") : JSONObject.parseArray(list.get(i).getLatestApplySrc()));
			list.get(i).setTemplateIds(StringUtils.isEmpty(list.get(i).getTemplateId()) || "0".equals(list.get(i).getTemplateId()) ? JSONArray.parseArray("[]") : JSONObject.parseArray(list.get(i).getTemplateId()));
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
