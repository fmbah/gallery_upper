package com.xs.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xs.beans.*;
import com.xs.beans.Label;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.LabelMapper;
import com.xs.daos.TemplateLabelsMapper;
import com.xs.daos.TemplateMapper;
import com.xs.daos.UserPaymentMapper;
import com.xs.utils.GenerateOrderno;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.xs.core.ProjectConstant.*;

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

    public final Logger logger = LoggerFactory.getLogger(WxAppAllService.class);

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
    @Autowired
    private UserPaymentMapper userPaymentMapper;
    @Autowired
    private TemplateLabelsMapper templateLabelsMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private UpLoadService upLoadService;

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

        User user = userService.findById(id);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

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
                    templateConditionCriteria.andEqualTo("enabled", true);
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

        User user = userService.findById(searchTemplates.getUserId());
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        if (!StringUtils.isEmpty(searchTemplates.getSearchValue())) {
            searchTemplates.setlNames(searchTemplates.getSearchValue().split(","));
            searchTemplates.setTcTitle(searchTemplates.getSearchValue());
            searchTemplates.settName(searchTemplates.gettName());
        }

        if (StringUtils.isEmpty(searchTemplates.gettRatio())) {
            searchTemplates.set_tRatios(new String[0]);
        } else {
            searchTemplates.set_tRatios(searchTemplates.gettRatio().split(","));
        }

        List<HashMap> list = templateMapper.searchTemplates(searchTemplates);

        //查看当前用户的品牌id集合,用来判断搜索结果集中是否可显示模板数据
        Condition activeCdkCon = new Condition(ActiveCdk.class);
        Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
        activeCdkConCriteria.andEqualTo("usedUserId", searchTemplates.getUserId());
        List<ActiveCdk> activeCdks = activeCdkService.findByCondition(activeCdkCon);

        HashSet<Integer> brandIds = new HashSet<>();
        if (activeCdks != null && !activeCdks.isEmpty()) {
            for (ActiveCdk activeCdk : activeCdks) {
                brandIds.add(activeCdk.getBrandId());
            }
        }

        if (list != null && !list.isEmpty()) {
            Iterator<HashMap> iterator = list.iterator();
            while(iterator.hasNext()) {
                HashMap hashMap = iterator.next();
                Object brandIdObject = hashMap.get("brandId");
                if (brandIdObject != null) {
                    Integer brandId = ((Long) brandIdObject).intValue();
                    if ((brandIds.isEmpty() && brandId == 0) || (brandId != 0 && !brandIds.isEmpty() && !brandIds.contains(brandId))) {
                        iterator.remove();
                    }
                }
            }
        }

        return ResultGenerator.genSuccessResult(list);
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
        if (!template.getEnabled()) {
            return ResultGenerator.genFailResult("模板数据未启用");
        }
        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        TemplateCategory templateCategory = templateCategoryService.findById(template.getCategoryId());
        if (templateCategory == null) {
            return ResultGenerator.genFailResult("模板分类数据不存在或已删除");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            Long zrank = jedis.zrank(String.format(USER_TEMPLATE_COLLECTIONS, String.valueOf(userId)), String.valueOf(id));
            if (zrank == null) {
                template.setHasCollection(false);
            } else {
                template.setHasCollection(true);
            }
        }

        //查看当前用户的品牌id集合,用来判断搜索结果集中是否可显示模板数据
        Condition activeCdkCon = new Condition(ActiveCdk.class);
        Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
        activeCdkConCriteria.andEqualTo("usedUserId", userId);
        List<ActiveCdk> activeCdks = activeCdkService.findByCondition(activeCdkCon);

        HashSet<Integer> brandIds = new HashSet<>();
        if (activeCdks != null && !activeCdks.isEmpty()) {
            for (ActiveCdk activeCdk : activeCdks) {
                brandIds.add(activeCdk.getBrandId());
            }
        }

        boolean canUse = false;
        boolean isMember = false;
        if (user.getMemberExpired().after(new Date())) {//是会员且未过期
            isMember = true;
        }

        //模板分为两类,品牌模板(只有拥有此品牌的品牌会员可查看分享使用)/普通模板(品牌会员或会员可使用,分享和查看无限制)
        if (template.getBrandId() != 0) {//品牌模板
            if (!brandIds.isEmpty() && brandIds.contains(template.getBrandId())) {
                canUse = true;
            } else {
                throw new ServiceException("暂无权限查看此模板");
            }
        } else {//非品牌模板
            if (isMember || !brandIds.isEmpty() || template.getGratis()) {
                canUse = true;
            } else {
                //查看权限不限制
            }
        }

        template.setCanUse(canUse);
        template.setTemplateCategory(templateCategory);

        this.templateIncr(userId, template.getId(), 3);

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
    public Object templateCenter(int page, int size, Integer categoryId, String ratio, Integer userId, Boolean isBrand){

        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        HashMap result = new HashMap();

        PageHelper.startPage(page, size);
        Condition condition = new Condition(Template.class);
        Example.Criteria criteria = condition.createCriteria();
        if (categoryId != null) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (!StringUtils.isEmpty(ratio)) {
            criteria.andIn("ratio", Arrays.asList(ratio.split(",")));
        }
        //查看当前用户的品牌id集合,用来判断搜索结果集中是否可显示模板数据
        Condition activeCdkCon = new Condition(ActiveCdk.class);
        Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
        activeCdkConCriteria.andEqualTo("usedUserId", userId);
        List<ActiveCdk> activeCdks = activeCdkService.findByCondition(activeCdkCon);

        HashSet<Integer> brandIds = new HashSet<>();
        if (activeCdks != null && !activeCdks.isEmpty()) {
            for (ActiveCdk activeCdk : activeCdks) {
                brandIds.add(activeCdk.getBrandId());
            }
        }

        if (isBrand == null) {//全部为null, 具体分类为false,品牌为true
            brandIds.add(0);
            criteria.andIn("brandId", brandIds);
        } else if (isBrand != null && isBrand.booleanValue()) {
            criteria.andIn("brandId", brandIds);
        } else if (isBrand != null && !isBrand.booleanValue()) {
            if (!brandIds.isEmpty()) {
                brandIds.add(0);
                criteria.andIn("brandId", brandIds);
            } else {
                criteria.andEqualTo("brandId", 0);
            }
        } else {
            throw new ServiceException("系统故障,请联系管理员处理");
        }

        criteria.andEqualTo("enabled", true);
        condition.setOrderByClause(" id desc");
        List<Template> templates = templateService.findByCondition(condition);
        for (int i = 0, j = templates.size(); i < j; i++) {
            templates.get(i).setDescri(null);
        }
        PageInfo pageInfo = new PageInfo(templates);

        result.put("categoryId", categoryId);
        result.put("pageInfo", pageInfo);

        Condition categoryCondition = new Condition(TemplateCategory.class);
//        Example.Criteria categoryConditionCriteria = categoryCondition.createCriteria();
//        if (categoryId != null) {
//            categoryConditionCriteria.orEqualTo("id", categoryId);
//        }
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

        Template template = templateService.findById(templateId);
        if (template == null) {
            return ResultGenerator.genFailResult("模板数据不存在或已删除");
        }
        if (!template.getEnabled()) {
            return ResultGenerator.genFailResult("模板数据未启用");
        }
        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

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
    public Object userCollections(Integer userId, String searchText) {

        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }
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

            //根据搜索文字将检索出来过滤下,根据标题和标签
            if (!StringUtils.isEmpty(searchText)) {
                if (templates != null && !templates.isEmpty()) {
                    Iterator<Template> iterator = templates.iterator();
                    while (iterator.hasNext()) {
                        Template next = iterator.next();

                        next.setDescri(null);

                        if (!next.getEnabled()) {
                            iterator.remove();
                            continue;
                        }

                        if (next.getBrandId() != null && next.getBrandId().intValue() != 0) {
                            CompanyBrand companyBrand = companyBrandService.findById(next.getBrandId());
                            if (companyBrand == null || (companyBrand != null && companyBrand.getExpiredTime().before(new Date()))) {
                                iterator.remove();
                                continue;
                            }
                        }

                        if (next.getName().indexOf(searchText) >= 0) {
                            continue;
                        } else {
                            boolean hasRemove = true;
                            Condition tlCondition = new Condition(TemplateLabels.class);
                            Example.Criteria tlConditionCriteria = tlCondition.createCriteria();
                            tlConditionCriteria.andEqualTo("templateId", next.getId());
                            List<TemplateLabels> templateLabels = templateLabelsMapper.selectByCondition(tlCondition);

                            if (templateLabels != null && !templateLabels.isEmpty()) {
                                HashSet<Integer> lIds = new HashSet<>();
                                for (TemplateLabels tl : templateLabels) {
                                    lIds.add(tl.getLabelId());
                                }

                                Condition lCondition = new Condition(Label.class);
                                Example.Criteria lConditionCriteria = lCondition.createCriteria();
                                lConditionCriteria.andIn("id", lIds);
                                List<Label> labels = labelMapper.selectByCondition(lCondition);

                                for (Label l : labels) {
                                    if (l.getName().indexOf(searchText) >= 0) {
                                        hasRemove = false;
                                        break;
                                    }
                                }

                            }

                            if (hasRemove) {
                                iterator.remove();
                            }
                        }
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
    public synchronized Object verifyBrandCode(Integer userId, String code) {

        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }
        ActiveCdk activeCdk = activeCdkService.findBy("code", code);
        if (activeCdk != null) {
            return ResultGenerator.genFailResult("品牌激活码已被其他用户使用");
        }
        BrandCdkey brandCdkey = brandCdkeyService.findBy("code", code);
        if (brandCdkey == null) {
            return ResultGenerator.genFailResult("品牌激活码数据不存在或已被删除");
        }
        if (brandCdkey.getIsUsed().byteValue() == 1) {
            return ResultGenerator.genFailResult("品牌激活码已被其他用户使用");
        }
        Condition condition3 = new Condition(BrandCdkey.class);
        Example.Criteria criteria3 = condition3.createCriteria();
        criteria3.andEqualTo("brandId", brandCdkey.getBrandId());
        criteria3.andEqualTo("isUsed", 1);
        criteria3.andEqualTo("usedUserId", userId);
        List<BrandCdkey> brandCdkeys = brandCdkeyService.findByCondition(condition3);
        if (brandCdkeys != null && !brandCdkeys.isEmpty()) {
            return ResultGenerator.genFailResult("该品牌已验证!");
        }

        //update by zx on 20181116 11:31 start

        Condition condition = new Condition(UserPayment.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("rechargeType", Byte.valueOf("1"));
        criteria.andEqualTo("status", "paid");
        List<UserPayment> userPayments = userPaymentMapper.selectByCondition(condition);

        if (userPayments != null && !userPayments.isEmpty()) {//如果此用户已经购买过品牌会员,那么就直接激活其它品牌激活码

            brandCdkey.setIsUsed(new Byte("1"));
            brandCdkey.setUsedTime(new Date());
            brandCdkey.setUsedUserId(userId);
            brandCdkey.setGmtModified(new Date());

            brandCdkeyService.update(brandCdkey);

            return ResultGenerator.genSuccessResult();
        } else {

            //如果code存在订单中,并且已支付完成,那么提示激活码已被使用
            Condition condition1 = new Condition(UserPayment.class);
            Example.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("cdkCode", code);
            criteria1.andEqualTo("status", "paid");
            criteria1.andEqualTo("rechargeType", Byte.valueOf("1"));
            List<UserPayment> userPayments1 = userPaymentMapper.selectByCondition(condition1);
            if (userPayments1 != null && !userPayments1.isEmpty()) {
                return ResultGenerator.genFailResult("激活码已被使用,请联系客服进行处理");
            } else {
                Condition condition2 = new Condition(UserPayment.class);
                Example.Criteria criteria2 = condition2.createCriteria();
                criteria2.andEqualTo("cdkCode", code);
                criteria2.andEqualTo("status", "unpay");
                criteria2.andEqualTo("rechargeType", Byte.valueOf("1"));
                condition2.setOrderByClause(" gmt_create desc");
                List<UserPayment> userPayments2 = userPaymentMapper.selectByCondition(condition2);
                if (userPayments2 != null && !userPayments2.isEmpty()) {
                    Calendar gmtCreate = Calendar.getInstance();
                    gmtCreate.setTime(userPayments2.get(0).getGmtCreate());
                    gmtCreate.add(Calendar.HOUR, 1);
                    if (gmtCreate.getTime().after(new Date())) {
                        return ResultGenerator.genFailResult("激活码已被锁定,请联系客服进行处理");
                    }
                }

                return orderDown(userId, Byte.valueOf("1"), code);
            }

        }


    }

    /**
     *
     * 功能描述: 用户详情
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 下午2:45
     */
    public Object findUserById(Integer id) {

        User user = userService.findById(id);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

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
        result.put("user", user);




        return ResultGenerator.genSuccessResult(result);
    }



    /**
     *
     * 功能描述: 下订单
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 下午5:59
     */
    public synchronized Object orderDown(Integer userId, Byte rechargeType, String code){

        Date now = new Date();
        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        if (user.getMemberExpired().after(now)  && rechargeType.byteValue() != 1) {//未过期,保证向上充值
            if (user.getMemberType().byteValue() > rechargeType) {

                if (user.getMemberType().byteValue() == 5) {
                    user.setMemberTypeStr("金卡会员");
                } else if(user.getMemberType().byteValue() == 6) {
                    user.setMemberTypeStr("铂金会员");
                } else if(user.getMemberType().byteValue() == 10) {
                    user.setMemberTypeStr("钻石会员");
                } else {
                    user.setMemberTypeStr("");
                }

                String rechargeTypeStr = "";
                switch (rechargeType) {
                    case 5 :
                        rechargeTypeStr = "金卡会员";
                        break;
                    case 6 :
                        rechargeTypeStr = "铂金会员";
                        break;
                    case 10 :
                        rechargeTypeStr = "钻石会员";
                        break;
                    default:
                        rechargeTypeStr = "";
                        break;
                }
                return ResultGenerator.genFailResult("您现在是"+ user.getMemberTypeStr() +"，无法充值"+ rechargeTypeStr +"！");
            }
        }

        //先从库里找下,用户支付记录中(用户id,未支付,相应支付类型),是否存在数据,如果存在,直接返回第一条数据,如果不存在,生成新订单
        //保证每次下单不会生成垃圾数据
        Condition userPaymentCondition = new Condition(UserPayment.class);
        Example.Criteria userPaymentConditionCriteria = userPaymentCondition.createCriteria();
        userPaymentConditionCriteria.andEqualTo("userId", userId);
        userPaymentConditionCriteria.andEqualTo("status", "unpay");
        userPaymentConditionCriteria.andEqualTo("rechargeType", rechargeType);
        List<UserPayment> userPayments = userPaymentMapper.selectByCondition(userPaymentCondition);
        if (userPayments != null && !userPayments.isEmpty()) {
            UserPayment userPayment = userPayments.get(0);
            boolean needModify = true;
            if (!StringUtils.isEmpty(userPayment.getCdkCode()) && userPayment.getCdkCode().equals(code)) {
                needModify = false;
            }
            if (needModify) {
                userPayment.setCdkCode(StringUtils.isEmpty(code) ? StringUtils.EMPTY : code);
                userPayment.setGmtModified(new Date());
                userPaymentMapper.updateByPrimaryKey(userPayment);
            }
            return ResultGenerator.genSuccessResult(userPayment.getId());
        }

        Calendar.getInstance();
        UserPayment userPayment = new UserPayment();
        userPayment.setUserId(userId);
        userPayment.setOrderNo(GenerateOrderno.get());
        userPayment.setStatus("unpay");
        userPayment.setTransactionId(StringUtils.EMPTY);
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.YEAR, 1970);
        instance.set(Calendar.MONTH, 0);
        instance.set(Calendar.DATE, 1);
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        Date initDate = instance.getTime();
        userPayment.setGmtPayment(initDate);
        userPayment.setGmtCreate(now);
        userPayment.setGmtModified(now);
        userPayment.setRechargeType(rechargeType);

        if (rechargeType.byteValue() == 5) {
            userPayment.setAmount(new BigDecimal(268));
            userPayment.setCdkCode(StringUtils.EMPTY);
            userPayment.setRemark(StringUtils.EMPTY);
        } else if (rechargeType.byteValue() == 6) {
            userPayment.setAmount(new BigDecimal(365));
            userPayment.setCdkCode(StringUtils.EMPTY);
            userPayment.setRemark(StringUtils.EMPTY);
        } else if (rechargeType.byteValue() == 10) {
            userPayment.setAmount(new BigDecimal(899));
            userPayment.setCdkCode(StringUtils.EMPTY);
            userPayment.setRemark(StringUtils.EMPTY);
        } else if (rechargeType.byteValue() == 1) {
            try (Jedis jedis = jedisPool.getResource()) {
                String code_price = jedis.get(CODE_PRICE);
                if (StringUtils.isEmpty(code_price)) {
                    logger.error("激活码价格数据不存在或已删除");
                    return ResultGenerator.genFailResult("激活码价格数据不存在或已删除");
                }
                try {
                    userPayment.setAmount(new BigDecimal(code_price));
                } catch (Exception e) {
                    logger.error("激活码价格数据有误");
                    return ResultGenerator.genFailResult("激活码价格数据有误");
                }

                Condition condition = new Condition(BrandCdkey.class);
                Example.Criteria criteria = condition.createCriteria();
                criteria.andEqualTo("code", code);
                criteria.andEqualTo("isUsed", new Byte("0"));
                List<BrandCdkey> brandCdkeys = brandCdkeyService.findByCondition(condition);
                if (brandCdkeys == null || (brandCdkeys != null && brandCdkeys.isEmpty())) {
                    logger.error("激活码数据有误或已被使用");
                    return ResultGenerator.genFailResult("激活码数据有误或已被使用");
                }

                Integer brandId = brandCdkeys.get(0).getBrandId();
                CompanyBrand companyBrand = companyBrandService.findById(brandId);
                if (companyBrand == null) {
                    logger.error("品牌数据不存在或已删除");
                    return ResultGenerator.genFailResult("品牌数据不存在或已删除");
                }

                userPayment.setCdkCode(code);
                userPayment.setRemark(companyBrand.getId() + "_" + companyBrand.getName());
            }
        }  else {
            logger.error("购买类型有误");
            return ResultGenerator.genFailResult("购买类型有误");
        }
        userPaymentMapper.insert(userPayment);



        return ResultGenerator.genSuccessResult(userPayment.getId());
    }

    /**
     *
     * 功能描述: 查看和分享和使用调用此方法,用来统计数据
     *
     * @param: type 类型(1:分享 2:使用 3:查看)
     * @return:
     * @auther: Fmbah
     * @date: 18-10-25 上午11:42
     */
    public Object templateIncr(Integer userId, Integer templateId, Integer type) {

        Template template = templateService.findById(templateId);
        if (template == null) {
            return ResultGenerator.genFailResult("模板数据不存在或已删除");
        }
        if (!template.getEnabled()) {
            return ResultGenerator.genFailResult("模板数据未启用");
        }

        if (type != 3) {
            User user = userService.findById(userId);
            if (user == null) {
                return ResultGenerator.genFailResult("用户数据不存在或已删除");
            }

            //查看当前用户的品牌id集合,用来判断搜索结果集中是否可显示模板数据
            Condition activeCdkCon = new Condition(ActiveCdk.class);
            Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
            activeCdkConCriteria.andEqualTo("usedUserId", userId);
            List<ActiveCdk> activeCdks = activeCdkService.findByCondition(activeCdkCon);

            HashSet<Integer> brandIds = new HashSet<>();
            if (activeCdks != null && !activeCdks.isEmpty()) {
                for (ActiveCdk activeCdk : activeCdks) {
                    brandIds.add(activeCdk.getBrandId());
                }
            }

            boolean canUse = false;
            boolean isMember = false;
            if (user.getMemberExpired().after(new Date())) {//是会员且未过期
                isMember = true;
            }

            //模板分为两类,品牌模板(只有拥有此品牌的品牌会员可查看分享使用)/普通模板(品牌会员或会员可使用,分享和查看无限制)
            if (template.getBrandId() != 0) {//品牌模板
                if (!brandIds.isEmpty() && brandIds.contains(template.getBrandId())) {
                    canUse = true;
                }
            } else {//非品牌模板
                if (isMember || !brandIds.isEmpty() || template.getGratis()) {
                    canUse = true;
                } else {//对于类型为使用抛出异常
                    if (type != 2) {
                        canUse = true;
                    }
                }
            }

            if (!canUse) {
                throw new ServiceException("暂无权限使用此模板");
            }
        }

        try (Jedis jedis = jedisPool.getResource()) {
            //查看
            //计数器+1
            //放到字符串存储起来
            //定时写到库里,将计数器清空
            if (type == 3) {
                String visitorKey = String.format(TEMPLATE_VISITOR, templateId, template.getCategoryId(), template.getBrandId());
                jedis.incr(visitorKey);
            }

            //分享
            if (type == 1) {
                String shareKey = String.format(TEMPLATE_SHARE, templateId, template.getCategoryId(), template.getBrandId());
                jedis.incr(shareKey);
            }

            //使用
            if (type == 2) {
                String usedKey = String.format(TEMPLATE_USED, templateId, template.getCategoryId(), template.getBrandId());
                jedis.incr(usedKey);
            }
        }

        return ResultGenerator.genSuccessResult();
    }


    public Object base64ToUrl(Base64ToUrl base64ToUrl) {
        return upLoadService.base64ToUrl(base64ToUrl);
    }

    public Object fileToUrl(MultipartFile file) throws IOException {
        return ResultGenerator.genSuccessResult(upLoadService.up(file));
    }

    public Object drawFonts() {

        File temp = null;
        File temp1 = null;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontFamilies = ge.getAvailableFontFamilyNames();
            for (String f : fontFamilies) {
                System.out.println(f);
            }
            URL url1 = this.getClass().getClassLoader().getResource("/font/ff36c1b5b09d25073107197263597513.ttf");
            URL url2 = this.getClass().getClassLoader().getResource("/font/汉仪大黑简.ttf");
            String pathString = null;
            String pathString1 = null;
            File dynamicFile = null;
            File dynamicFile1 = null;
            if (url1 != null) {
                pathString = url1.getFile();
                dynamicFile = new File(pathString);
            } else {
                ClassPathResource classPathResource = new ClassPathResource("/font/ff36c1b5b09d25073107197263597513.ttf");
                if (classPathResource != null) {
                    dynamicFile = classPathResource.getFile();
                }
            }
            if (url2 != null) {
                pathString1 = url2.getFile();
                dynamicFile1 = new File(pathString1);
            } else {
                ClassPathResource classPathResource = new ClassPathResource("/font/汉仪大黑简.ttf");
                if (classPathResource != null) {
                    dynamicFile1 = classPathResource.getFile();
                }
            }

            if (dynamicFile1== null || dynamicFile == null) {
                return ResultGenerator.genFailResult("未获取到字体文件");
            }

            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, dynamicFile));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, dynamicFile1));

            Font font = Font.createFont(Font.TRUETYPE_FONT, dynamicFile);

            font = font.deriveFont(18f);

            Font font1 = Font.createFont(Font.TRUETYPE_FONT, dynamicFile1);

            font1 = font1.deriveFont(18f);

            BufferedImage bufferedImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);

            Graphics2D graphics = bufferedImage.createGraphics();

            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, 500, 500);

//            graphics.setColor(Color.WHITE);
//            graphics.fillOval(0, 0, 500, 500);
//
//            graphics.setFont(font);
//            graphics.setColor(Color.BLUE);
//            graphics.drawString("绽放汉字之美1", 100, 160);
//
//            graphics.setFont(font1);
//            AffineTransform toCenterAt = new AffineTransform();
//            toCenterAt.rotate(30, 0, 0);
//            graphics.transform(toCenterAt);
//            graphics.setColor(Color.BLUE);
//            graphics.drawString("绽放汉字之美2", 100, 210);
//
//
//            Font ftmp = new Font("default", Font.BOLD, 36);
//            graphics.setFont(ftmp);
//            graphics.setColor(Color.BLUE);
//            graphics.drawString("绽放汉字之美3", 100, 310);
//
//            ftmp = new Font("default", Font.PLAIN, 36);
//            graphics.setFont(ftmp);
//            graphics.setColor(Color.BLUE);
//            graphics.drawString("绽放汉字之美3", 100, 380);

            //1. 背景图
            BufferedImage backPic = ImageIO.read(new URL("https://daily-test.mxth.com/1545102996778_temp7317092677352562376.png"));
            Graphics2D backPicGraphics = backPic.createGraphics();
            //2. 画文字图片, 居中
            BufferedImage fontImage = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2DFont = fontImage.createGraphics();
            graphics2DFont.setColor(Color.BLUE);
            graphics2DFont.fillRect(0, 0, 100, 50);

            FontMetrics fontMetrics = graphics2DFont.getFontMetrics();
            int x = (100 - fontMetrics.stringWidth("绽放汉字之美2")) / 2;
            int y = (fontMetrics.getAscent() + (50 - (fontMetrics.getAscent() + fontMetrics.getDescent())) / 2);
            graphics2DFont.setFont(font);
            graphics2DFont.setColor(Color.white);
            graphics2DFont.drawString("绽放汉字之美2", x, y);
            graphics2DFont.dispose();
            backPicGraphics.drawImage(fontImage.getScaledInstance(100, 50, Image.SCALE_SMOOTH), 0, 0, null);

            //2. 画文字图片, 左对齐
            BufferedImage fontImage1 = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2DFont1 = fontImage1.createGraphics();
            graphics2DFont1.setColor(Color.BLUE);
            graphics2DFont1.fillRect(0, 0, 100, 50);
            FontMetrics fontMetrics1 = graphics2DFont1.getFontMetrics();
            int len = fontMetrics.stringWidth("123");
            System.out.println("len: " + len);
            int x1 = 0;
            int y1 = (fontMetrics1.getAscent() + (50 - (fontMetrics1.getAscent() + fontMetrics1.getDescent())) / 2);

            graphics2DFont1.setColor(Color.WHITE);
            graphics2DFont1.drawString("123456四五六", x1, y1);
            graphics2DFont1.setFont(font1);
            graphics2DFont1.drawString("123456四五六", x1, y1 + fontMetrics.getHeight());
            graphics2DFont1.dispose();
            backPicGraphics.drawImage(fontImage1.getScaledInstance(100, 50, Image.SCALE_SMOOTH), 0, 70, null);

            //2. 画文字图片, 右对齐
            BufferedImage fontImage2 = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2DFont2 = fontImage2.createGraphics();
            graphics2DFont2.setColor(Color.BLUE);
            graphics2DFont2.fillRect(0, 0, 100, 50);
            graphics2DFont2.dispose();


//            temp1 = File.createTempFile("temp", ".png");
//            ImageIO.write(fontImage2, "png", temp1);
//            BufferedImage temp1BufferedImage = ImageIO.read(temp1);

            int tx = 0 + fontImage2.getMinX() + fontImage2.getWidth() / 2;
            int ty = 140 + fontImage2.getMinY() + fontImage2.getHeight() / 2;
            Graphics2D backPicGraphics1 = backPic.createGraphics();
//            AffineTransform transform = new AffineTransform();
//            transform.rotate(Math.toRadians(45), tx, ty);
            System.out.println("tx: " + tx + ", ty: " + ty);
//            backPicGraphics1.transform(transform);
            backPicGraphics1.rotate(45 * Math.PI / 180, tx, ty);
            backPicGraphics1.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,1.0f)); //透明度设置开始
            backPicGraphics1.drawImage(fontImage2.getScaledInstance(100, 50, Image.SCALE_SMOOTH), 0, 140, null);
            backPicGraphics1.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER)); //透明度设置 结束

            Graphics2D temp1BufferedImageGraphics = fontImage2.createGraphics();
            FontMetrics fontMetrics2 = temp1BufferedImageGraphics.getFontMetrics();
            String s = "旋转失真了";
            int len1 = fontMetrics2.stringWidth(s);
            System.out.println("len1: " + len1);
            int x2 = 100 - len1;
            int y2 = (fontMetrics2.getAscent() + (50 - (fontMetrics2.getAscent() + fontMetrics2.getDescent())) / 2);
            temp1BufferedImageGraphics.setPaint(new Color(255, 255, 255, (int)Math.round(1 / 1 * 255)));
//            temp1BufferedImageGraphics.rotate(Math.toRadians(45), x2 + len1 / 2, y2);
            backPicGraphics1.drawString(s, 0 + x2, 140 + y2);
            temp1BufferedImageGraphics.dispose();

            backPicGraphics1.dispose();

//            backPicGraphics.rotate(-30 * Math.PI / 180, 50 - tx, ty - 210);

            BufferedImage fontImage3 = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
            Graphics2D fontImage3Graphics = fontImage3.createGraphics();
            fontImage3Graphics.setColor(Color.BLUE);
            fontImage3Graphics.fillRect(0, 0, 100, 50);
            fontImage3Graphics.setColor(new Color(0, 0, 0, (int)Math.round(1 / 1 * 255)));
            String str = "1231111";
            FontMetrics fontMetrics3 = fontImage3Graphics.getFontMetrics();
            int i = fontMetrics3.stringWidth(str);
            int x3 = i / 2;
            int y3 = (fontMetrics3.getAscent() + (50 - (fontMetrics3.getAscent() + fontMetrics3.getDescent())) / 2);
            fontImage3Graphics.rotate(Math.toRadians(45), x3, y3);
            fontImage3Graphics.drawString(str, 0, y3);
            Graphics2D fontImage3Graphics1 = fontImage3.createGraphics();
            String hrStr = "你好,Fmbah,我要换行了,我要换行了,我要换行了,我要换行了,我要换行了,我要换行了";
            int size = 0;

            FontMetrics fontMetrics4 = fontImage3Graphics.getFontMetrics();
            int i2 = fontMetrics4.stringWidth(hrStr);
            int sizey = 10;
            for (int j = 0; j < i2; j++) {
                String s1 = String.valueOf(hrStr.charAt(j));
                int i1 = fontMetrics4.stringWidth(s1);
                if (size + i1 <= 100) {
                    fontImage3Graphics1.drawString(s1, size, sizey);
                    size += i1;
                } else {
                    sizey += fontMetrics4.getHeight();
                    if (sizey > 50) {
                        break;
                    }
                    fontImage3Graphics1.drawString(s1, 0, sizey);
                    size = i1;
                }

            }
            fontImage3Graphics1.dispose();
            fontImage3Graphics.dispose();
            backPicGraphics.drawImage(fontImage3, 0, 230, null);

            BufferedImage fontImage4 = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
            Graphics2D fontImage4Graphics = fontImage4.createGraphics();
            fontImage4Graphics.setColor(Color.BLUE);
            fontImage4Graphics.fillRect(0, 0, 100, 50);
            fontImage4Graphics.setColor(new Color(255, 255, 255, (int)(Math.round(1 / 1 * 255))));
            String fontImage4Str = "旋转失真了";
            FontMetrics fontMetrics5 = fontImage4Graphics.getFontMetrics();
            int font4Width = fontMetrics5.stringWidth(fontImage4Str);
            int x4 = font4Width / 2;
            int y4 = (fontMetrics5.getAscent() + (50 - (fontMetrics5.getAscent() + fontMetrics5.getDescent())) / 2);
            fontImage4Graphics.rotate(Math.toRadians(45), x4, y4);
            fontImage4Graphics.drawString(fontImage4Str, 0, y4);
            fontImage4Graphics.dispose();
            backPicGraphics.drawImage(fontImage4, 0, 310, null);


            backPicGraphics.dispose();

            graphics.dispose();
            temp = File.createTempFile("temp", ".png");
            ImageIO.write(backPic, "png", temp);


            return ResultGenerator.genSuccessResult(upLoadService.upFile(temp));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (temp != null && temp.exists()) {
                temp.delete();
            }
            if (temp1 != null && temp1.exists()) {
                temp1.delete();
            }
        }

        return null;
    }

    public Object drawFontsToPic(String fontToPics, String pic) {

        System.out.println("fontToPics: " + fontToPics);
        System.out.println("pic: " + pic);

        Gson gson = new Gson();
        List<FontToPic> fontToPicList = gson.fromJson(fontToPics, new TypeToken<List<FontToPic>>(){}.getType());
        System.out.println("fontToPicList size: " + fontToPicList.size());




        return ResultGenerator.genSuccessResult();
    }



    public Object getFont() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://www.hanyi.studio/webfontmanagement/webfontmanagerhandler.ashx";
        JSONObject json = new JSONObject();
        json.put("userGuid","698F3099-E62F-4C3B-B30A-04FB735069FE");
        json.put("productId","1269");
        json.put("chas","中华人民共和国");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("opera", "GetWebFontItem")
                .queryParam("json", json.toString());

        ResponseEntity<String> exchange = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);

        JSONObject object = JSONObject.parseObject(exchange.getBody());

        return ResultGenerator.genSuccessResult(object);
    }

}
