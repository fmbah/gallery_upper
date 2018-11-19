package com.xs.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.*;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.LabelMapper;
import com.xs.daos.TemplateLabelsMapper;
import com.xs.daos.TemplateMapper;
import com.xs.daos.UserPaymentMapper;
import com.xs.utils.GenerateOrderno;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

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
            if (isMember || !brandIds.isEmpty()) {
                canUse = true;
            } else {//对于类型为使用抛出异常

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
            criteria.andEqualTo("brandId", 0);
        } else {
            throw new ServiceException("系统故障,请联系管理员处理");
        }

        criteria.andEqualTo("enabled", true);
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

        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }


        //update by zx on 20181116 11:31 start
        //

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
    public Object orderDown(Integer userId, Byte rechargeType, String code){

        Date now = new Date();
        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        if (user.getMemberExpired().after(now) && user.getMemberType().byteValue() != 1) {//未过期,保证向上充值
            if (user.getMemberType().byteValue() >= rechargeType) {

                if (user.getMemberType().byteValue() == 5) {
                    user.setMemberTypeStr("半年会员");
                } else if(user.getMemberType().byteValue() == 6) {
                    user.setMemberTypeStr("全年会员");
                } else if(user.getMemberType().byteValue() == 10) {
                    user.setMemberTypeStr("终身会员");
                } else {
                    user.setMemberTypeStr("");
                }

                String rechargeTypeStr = "";
                switch (rechargeType) {
                    case 5 :
                        rechargeTypeStr = "半年会员";
                        break;
                    case 6 :
                        rechargeTypeStr = "全年会员";
                        break;
                    case 10 :
                        rechargeTypeStr = "终身会员";
                        break;
                    default:
                        rechargeTypeStr = "";
                        break;
                }
                return ResultGenerator.genFailResult("您现在是"+ user.getMemberTypeStr() +"会员，无法充值"+ rechargeTypeStr +"会员！");
            }
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
            userPayment.setAmount(new BigDecimal(100));

            Condition condition = new Condition(BrandCdkey.class);
            Example.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("code", code);
            criteria.andEqualTo("isUsed", new Byte("0"));
            List<BrandCdkey> brandCdkeys = brandCdkeyService.findByCondition(condition);
            if (brandCdkeys == null || (brandCdkeys != null && brandCdkeys.isEmpty())) {
                return ResultGenerator.genFailResult("激活码数据有误或已被使用");
            }

            Integer brandId = brandCdkeys.get(0).getBrandId();
            CompanyBrand companyBrand = companyBrandService.findById(brandId);
            if (companyBrand == null) {
                return ResultGenerator.genFailResult("品牌数据不存在或已删除");
            }

            userPayment.setCdkCode(code);
            userPayment.setRemark(companyBrand.getId() + "_" + companyBrand.getName());
        }  else {
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
                if (isMember || !brandIds.isEmpty()) {
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
}
