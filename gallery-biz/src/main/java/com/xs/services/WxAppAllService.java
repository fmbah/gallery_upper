package com.xs.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.*;
import com.xs.core.ResultGenerator;
import com.xs.daos.TemplateMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import static com.xs.core.ProjectConstant.USER_TEMPLATE_COLLECTIONS;

/**
 * @ClassName WxAppAllService
 * @Description
 * @Author root
 * @Date 18-10-19 下午3:51
 * @Version 1.0
 **/
@Service
@Transactional
public class WxAppAllService {

    @Autowired
    private UserService userService;
    @Autowired
    private SlideService slideService;

    @Autowired
    private TemplateCategoryService templateCategoryService;

    @Autowired
    private ActiveCdkService activeCdkService;

    @Autowired
    private CompanyBrandService companyBrandService;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private BrandCdkeyService brandCdkeyService;

    /**
     *
     * 功能描述: 首页轮播图
     *
     * @param: 位置（首页：1；分享获益：2；会员权益：3；）
     * @return: 
     * @auther: Fmbah
     * @date: 18-10-19 下午3:55
     */
    public Object getSlides(Integer type) {
        Condition condition = new Condition(Slide.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("type", type);
        condition.setOrderByClause(" weight desc");
        List<Slide> slides = slideService.findByCondition(condition);
        return ResultGenerator.genSuccessResult(slides);
    }

    /**
     *
     * 功能描述: 首页分类接口
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午4:00
     */
    public Object getIndexCategorys() {
        Condition condition = new Condition(TemplateCategory.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("isHot", true);
        condition.setOrderByClause(" weight desc");
        List<TemplateCategory> templateCategories = templateCategoryService.findByCondition(condition);
        return ResultGenerator.genSuccessResult(templateCategories);
    }


    /**
     *
     * 功能描述: 点击品牌中心分类名称后验证是否是品牌会员,如果是 显示品牌数据,以及品牌模板数据；
     *                                              如果不是,提示加入品牌,验证品牌码
     *
     * @param:
     * @return: {data:"['brandName':'','templates':'']", code:...}
     * @auther: Fmbah
     * @date: 18-10-19 下午4:04
     */
    public Object openBrandDatas(Integer id) {

        HashMap result = new HashMap();
        Condition activeCdkCon = new Condition(ActiveCdk.class);
        Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
        activeCdkConCriteria.andEqualTo("usedUserId", id);
        List<ActiveCdk> activeCdks = activeCdkService.findByCondition(activeCdkCon);

        List<CompanyBrand> companyBrands = null;
        if (activeCdks != null && !activeCdks.isEmpty()) {
            HashSet<Integer> brandIds = new HashSet<>();
            for (ActiveCdk activeCdk : activeCdks) {
                brandIds.add(activeCdk.getBrandId());
            }

            companyBrands = companyBrandService.findByIds(StringUtils.join(brandIds, ","));
            if (companyBrands != null && !companyBrands.isEmpty()) {
                for (CompanyBrand companyBrand : companyBrands) {
                    Condition templateCondition = new Condition(Template.class);
                    Example.Criteria templateConditionCriteria = templateCondition.createCriteria();
                    templateConditionCriteria.andEqualTo("brandId", companyBrand.getId());
                    List<Template> templateList = templateService.findByCondition(templateCondition);
                    companyBrand.setTemplateList(templateList);
                }
            }
        }
        result.put("companyBrands", companyBrands);
        return ResultGenerator.genSuccessResult(result);
    }


    /**
     *
     * 功能描述: 搜索功能,按照模板标题,模板类别,模板标签搜索数据, 首页分类
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午7:21
     */
    public Object searchTemplates(SearchTemplates searchTemplates) {

        if (StringUtils.isEmpty(searchTemplates.get_lNames())) {
            searchTemplates.setlNames(new String[0]);
        } else {
            searchTemplates.setlNames(searchTemplates.get_lNames().split(","));
        }

        return ResultGenerator.genSuccessResult(templateMapper.searchTemplates(searchTemplates));
    }

    /**
     *
     * 功能描述: 模板详情
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午8:20
     */
    public Object templateInfo(Integer userId,Integer id) {
        Template template = templateService.findById(id);
        if (template == null) {
            return ResultGenerator.genFailResult("模板数据不存在或已删除");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            Long zrank = jedis.zrank(String.format(USER_TEMPLATE_COLLECTIONS, String.valueOf(userId)), String.valueOf(id));
            if (zrank == null || zrank == 0) {
                template.setHasCollection(false);
            } else {
                template.setHasCollection(true);
            }
        }

        return ResultGenerator.genSuccessResult(template);
    }

    /**
     *
     * 功能描述: 模板中心 分类/筛选 搜索
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午8:27
     */
    public Object templateCenter(int page, int size, Integer categoryId, Byte ratio){

        HashMap result = new HashMap();

        PageHelper.startPage(page, size);
        Condition condition = new Condition(Template.class);
        Example.Criteria criteria = condition.createCriteria();
        if (categoryId != null) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (ratio != null) {
            criteria.andEqualTo("ratio", ratio);
        }
        List<Template> templates = templateService.findByCondition(condition);
        PageInfo pageInfo = new PageInfo(templates);

        result.put("categoryId", categoryId);
        result.put("pageInfo", pageInfo);

        Condition categoryCondition = new Condition(TemplateCategory.class);
        Example.Criteria categoryConditionCriteria = categoryCondition.createCriteria();
        if (categoryId != null) {
            categoryConditionCriteria.andEqualTo("id", categoryId);
        }
        categoryCondition.setOrderByClause(" weight desc");
        List<TemplateCategory> templateCategories = templateCategoryService.findByCondition(categoryCondition);
        result.put("templateCategories", templateCategories);

        return ResultGenerator.genSuccessResult(result);
    }

    /**
     *
     * 功能描述: 模板收藏与取消收藏
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午8:58
     */
    public Object saveCollection(Integer userId, Integer templateId) {

        try (Jedis jedis = jedisPool.getResource()){

            Long zrank = jedis.zrank(String.format(USER_TEMPLATE_COLLECTIONS, String.valueOf(userId)), String.valueOf(templateId));
            if (zrank == null) {
                jedis.zadd(String.format(USER_TEMPLATE_COLLECTIONS, String.valueOf(userId)), System.currentTimeMillis(), String.valueOf(templateId));
            } else {
                jedis.zrem(String.format(USER_TEMPLATE_COLLECTIONS, String.valueOf(userId)), String.valueOf(templateId));
            }

        }

        return ResultGenerator.genSuccessResult();
    }

    /**
     *
     * 功能描述: 用户收藏模板集合
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午9:03
     */
    public Object userCollections(Integer userId) {

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> templateIds = jedis.zrange(String.format(USER_TEMPLATE_COLLECTIONS, String.valueOf(userId)), 0, -1);

            List<Template> templates = null;
            if (templateIds != null && templateIds.size() > 0) {
                templates = templateService.findByIds(StringUtils.join(templateIds, ","));

                if (templates != null && templates.size() > 0) {
                    for (Template template : templates) {
                        template.setHasCollection(true);
                    }
                }
            }

            return ResultGenerator.genSuccessResult(templates);
        }
    }

    /**
     *
     * 功能描述: 验证品牌激活码
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午9:18
     */
    public Object verifyBrandCode(Integer userId, String code) {

        ActiveCdk activeCdk = activeCdkService.findBy("code", code);
        if (activeCdk == null) {
            return ResultGenerator.genFailResult("品牌激活码数据不存在或已被其他用户使用");
        }

        BrandCdkey brandCdkey = brandCdkeyService.findBy("code", code);
        brandCdkey.setIsUsed(new Byte("1"));
        brandCdkey.setUsedTime(new Date());
        brandCdkey.setUsedUserId(userId);
        brandCdkey.setGmtModified(new Date());

        brandCdkeyService.update(brandCdkey);
        return ResultGenerator.genSuccessResult();
    }

    public Object findUserById(Integer id) {

        User user = userService.findById(id);

        HashMap result = new HashMap();
        Condition activeCdkCon = new Condition(ActiveCdk.class);
        Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
        activeCdkConCriteria.andEqualTo("usedUserId", id);
        List<ActiveCdk> activeCdks = activeCdkService.findByCondition(activeCdkCon);

        List<CompanyBrand> companyBrands = null;
        if (activeCdks != null && !activeCdks.isEmpty()) {
            HashSet<Integer> brandIds = new HashSet<>();
            for (ActiveCdk activeCdk : activeCdks) {
                brandIds.add(activeCdk.getBrandId());
            }

            companyBrands = companyBrandService.findByIds(StringUtils.join(brandIds, ","));
            if (companyBrands != null && !companyBrands.isEmpty()) {
                for (CompanyBrand companyBrand : companyBrands) {
                    Condition templateCondition = new Condition(Template.class);
                    Example.Criteria templateConditionCriteria = templateCondition.createCriteria();
                    templateConditionCriteria.andEqualTo("brandId", companyBrand.getId());
                    List<Template> templateList = templateService.findByCondition(templateCondition);
                    companyBrand.setTemplateList(templateList);
                }
            }
        }
        result.put("companyBrands", companyBrands);
        result.put("companyBrandsNum", companyBrands != null && companyBrands.size() > 0 ?  companyBrands.size() : 0);
        result.put("user", companyBrands);

        return ResultGenerator.genSuccessResult(result);
    }






}
