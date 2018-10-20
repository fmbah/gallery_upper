package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.BrandCdkey;
import com.xs.beans.Template;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.BrandCdkeyMapper;
import com.xs.daos.CompanyBrandMapper;
import com.xs.beans.CompanyBrand;
import com.xs.daos.TemplateMapper;
import com.xs.services.CompanyBrandService;
import com.xs.core.sservice.AbstractService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import static com.xs.core.ProjectConstant.COMPANY_BRAND_CDK;


/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("companybrandService")
@Transactional
public class CompanyBrandServiceImpl extends AbstractService<CompanyBrand> implements CompanyBrandService {
    @Autowired
    private CompanyBrandMapper companybrandMapper;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private BrandCdkeyMapper brandCdkeyMapper;


    @Override
    public Object queryWithPage(int page, int size, String contactPhone, String contactPerson, String name) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(CompanyBrand.class);
        Example.Criteria criteria = condition.createCriteria();
        if (!StringUtils.isEmpty(contactPhone)) {
            criteria.andLike("contactPerson", "%" + contactPerson + "%");
        }
        if (!StringUtils.isEmpty(contactPerson)) {
            criteria.andLike("contactPerson", "%" + contactPerson + "%");
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        condition.setOrderByClause(" gmt_modified desc");

        List<CompanyBrand> list = super.findByCondition(condition);
        for (int i = 0, j = list.size(); i < j; i++) {

            Condition tmpTemplate = new Condition(Template.class);
            Example.Criteria tmpTemplateCriteria = tmpTemplate.createCriteria();
            tmpTemplateCriteria.andEqualTo("brandId", list.get(i).getId());
            List<Template> templates = templateMapper.selectByCondition(tmpTemplate);
            list.get(i).setTemplateCount(templates == null || templates.size() == 0 ? 0 : templates.size());

            Condition cdkCondition = new Condition(BrandCdkey.class);
            Example.Criteria cdkConditionCriteria = cdkCondition.createCriteria();
            cdkConditionCriteria.andEqualTo("brandId", list.get(i).getId());
//            cdkConditionCriteria.andEqualTo("isUsed", 1);
//            cdkConditionCriteria.andNotEqualTo("usedUserId", 0);
            List<BrandCdkey> brandCdkeys = brandCdkeyMapper.selectByCondition(cdkCondition);
            if (brandCdkeys != null && brandCdkeys.size() > 0) {
                HashSet<Integer> uIds = new HashSet<>();
                for (BrandCdkey brandCdkey : brandCdkeys) {
                    if (brandCdkey.getUsedUserId() != null && brandCdkey.getUsedUserId() != 0 && brandCdkey.getIsUsed().byteValue() == 1) {
                        uIds.add(brandCdkey.getUsedUserId());
                    }
                }
                list.get(i).setUserCount(uIds.size());
                list.get(i).setCkdNum(brandCdkeys.size());
            } else {
                list.get(i).setUserCount(0);
                list.get(i).setCkdNum(0);
            }

        }
        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    public void save(CompanyBrand model) {

        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.YEAR, 1);
        model.setExpiredTime(instance.getTime());

        super.save(model);
    }

    @Override
    public void update(CompanyBrand model) {

        CompanyBrand companyBrand = this.findById(model.getId());
        if (companyBrand == null) {
            throw new ServiceException("品牌数据不存在或删除");
        }
        if (model.getExpiredTime() == null) {
            throw new ServiceException("过期时间不可为空");
        }

        BeanUtils.copyProperties(model, companyBrand);
        companyBrand.setGmtModified(new Date());
        super.update(model);
    }

    @Override
    public CompanyBrand findById(Integer id) {
        return super.findById(id);
    }


    @Override
    public Object addCdkey(Integer brandId, Integer num) {

        try (Jedis jedis = jedisPool.getResource()) {

            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.YEAR, 1970);
            instance.set(Calendar.MONTH, 0);
            instance.set(Calendar.DATE, 1);
            instance.set(Calendar.HOUR, 0);
            instance.set(Calendar.MINUTE, 0);
            instance.set(Calendar.SECOND, 0);
            Date initDate = instance.getTime();
            List<BrandCdkey> brandCdkeys = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                String rpop = jedis.rpop(COMPANY_BRAND_CDK);
                if (StringUtils.isEmpty(rpop)) {
                    for (int s = 0; s < 100000; s++) {
                        jedis.lpush(COMPANY_BRAND_CDK, RandomStringUtils.randomAlphanumeric(8));
                    }
                    rpop = jedis.rpop(COMPANY_BRAND_CDK);
                }

                BrandCdkey brandCdkey = new BrandCdkey();
                brandCdkey.setCode(rpop);
                brandCdkey.setBrandId(brandId);
                brandCdkey.setIsUsed(new Byte("0"));
                brandCdkey.setUsedUserId(0);
                brandCdkey.setUsedTime(initDate);
                brandCdkey.setGmtCreate(new Date());
                brandCdkey.setGmtModified(new Date());
                brandCdkeys.add(brandCdkey);
            }
            brandCdkeyMapper.insertList(brandCdkeys);

        }

        return ResultGenerator.genSuccessResult();
    }
}
