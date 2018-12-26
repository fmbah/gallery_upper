package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.*;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.*;
import com.xs.services.CompanyBrandService;
import com.xs.core.sservice.AbstractService;
import com.xs.services.UserService;
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
    @Autowired
    private ActiveCdkMapper activeCdkMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;


    @Override
    public Object queryWithPage(int page, int size, String contactPhone, String contactPerson, String name) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(CompanyBrand.class);
        Example.Criteria criteria = condition.createCriteria();
        if (!StringUtils.isEmpty(contactPhone)) {
            criteria.andLike("contactPerson", "%" + contactPerson + "%");
        }
        if (!StringUtils.isEmpty(contactPhone)) {
            criteria.andLike("contactPhone", "%" + contactPhone + "%");
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

            User user = userService.findById(list.get(i).getBrandPersonalUserid());
            if (user != null) {
                list.get(i).setBrandPersonalUserName(user.getNickname());
                list.get(i).setBrandPersonalUserPic(user.getWxHeadimgurl());
            }

        }
        PageInfo pageInfo = new PageInfo(list);

        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public synchronized void save(CompanyBrand model) {

        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.YEAR, 1);
        model.setExpiredTime(instance.getTime());

        if (model.getBrandPersonalUserid() != null && model.getBrandPersonalUserid() != 0) {
            Condition companyBrandCondition = new Condition(CompanyBrand.class);
            Example.Criteria companyBrandConditionCriteria = companyBrandCondition.createCriteria();
            companyBrandConditionCriteria.andEqualTo("brandPersonalUserid", model.getBrandPersonalUserid());
            List<CompanyBrand> companyBrands = mapper.selectByCondition(companyBrandCondition);
            if (companyBrands == null || (companyBrands != null && companyBrands.isEmpty())) {
                User user = userService.findById(model.getBrandPersonalUserid());
                if (user == null) {
                    throw new ServiceException("用户数据不存在或已删除!");
                }
                user.setMemberType(new Byte("10"));
                user.setIsAgent(true);
                user.setGmtModified(new Date());
                instance = Calendar.getInstance();
                instance.add(Calendar.YEAR, 99);
                user.setMemberExpired(instance.getTime());
                userMapper.updateByPrimaryKey(user);
            } else {
                throw new ServiceException("该用户已绑定品牌!");
            }
        }

        super.save(model);

        this.addCdkey(model.getId(), 1);

        Condition condition = new Condition(BrandCdkey.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("brandId", model.getId());
        criteria.andEqualTo("isUsed", 0);
        List<BrandCdkey> brandCdkeys = brandCdkeyMapper.selectByCondition(condition);
        if (brandCdkeys != null && brandCdkeys.size() >0) {

            BrandCdkey brandCdkey = brandCdkeys.get(0);

            brandCdkey.setIsUsed(new Byte("1"));
            brandCdkey.setUsedTime(new Date());
            brandCdkey.setUsedUserId(model.getBrandPersonalUserid());
            brandCdkey.setGmtModified(new Date());

            brandCdkeyMapper.updateByPrimaryKey(brandCdkey);
        } else {
            throw new ServiceException("该品牌激活码数量不足!");
        }
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
        if (companyBrand.getBrandPersonalUserid() != model.getBrandPersonalUserid()) {
            throw new ServiceException("品牌个人号不可修改!");
        }


        BeanUtils.copyProperties(model, companyBrand);
        companyBrand.setGmtModified(new Date());

        if (companyBrand.getBrandPersonalUserid() != null && companyBrand.getBrandPersonalUserid() != 0) {
            User user = userService.findById(companyBrand.getBrandPersonalUserid());
            if (user == null) {
                throw new ServiceException("用户数据不存在或已删除!");
            }
        }

        super.update(companyBrand);
    }

    @Override
    public CompanyBrand findById(Integer id) {

        CompanyBrand companyBrand = super.findById(id);

        User user = userService.findById(companyBrand.getBrandPersonalUserid());
        if (user != null) {
            companyBrand.setBrandPersonalUserName(user.getNickname());
        }
        return companyBrand;
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

    @Override
    public void deleteById(Integer id) {

        Condition condition = new Condition(Template.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("brandId", id);
        List<Template> templates = templateMapper.selectByCondition(condition);
        if (templates != null && !templates.isEmpty()) {
            throw new ServiceException("该品牌已存在模板, 不可删除");
        }

        Condition cdkCondition = new Condition(ActiveCdk.class);
        Example.Criteria cdkConditionCriteria = cdkCondition.createCriteria();
        cdkConditionCriteria.andEqualTo("brandId", id);
        List<ActiveCdk> activeCdks = activeCdkMapper.selectByCondition(cdkCondition);
        if (activeCdks != null && !activeCdks.isEmpty()) {
            throw new ServiceException("该品牌已存在已激活激活码, 不可删除");
        }

        super.deleteById(id);
    }
}
