package com.xs.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xs.beans.*;
import com.xs.core.ResponseBean;
import com.xs.core.ResultGenerator;
import com.xs.daos.*;
import com.xs.services.SWxAuthService;
import com.xs.services.SlideService;
import com.xs.services.UserService;
import com.xs.services.WxAppAllService;
import com.xs.utils.CalendarUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.xs.core.ProjectConstant.USER_DRAWCASHLOG;
import static com.xs.core.ProjectConstant.WX_MP_USER_TOKEN;

/**
 \* 杭州桃子网络科技股份有限公司
 \* User: zhaoxin
 \* Date: 2018/7/6
 \* Time: 9:02
 \* Description: 
 \*/
@Service("sWxAuthService")
@Transactional
public class SWxAuthServiceImpl implements SWxAuthService {

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private UserService userService;
    @Autowired
    private SlideService slideService;
    @Autowired
    private IncomexpenseMapper incomexpenseMapper;
    @Autowired
    private DrawcashLogMapper drawcashLogMapper;
    @Autowired
    private WxAppAllService wxAppAllService;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;
    @Autowired
    private ActiveCdkMapper activeCdkMapper;
    @Autowired
    private UserPaymentMapper userPaymentMapper;


    @Override
    public Object distributionCenterAuth(HttpServletRequest request) {
        String code = request.getParameter("code");
        try {
            if (code != null && code.length() > 0) {
                String result = "";
                WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
                if (wxMpOAuth2AccessToken != null) {
                    User user = userService.findBy("wxOpenid", wxMpOAuth2AccessToken.getOpenId());
                    if (user == null && wxMpOAuth2AccessToken.getUnionId() != null) {
                        user = userService.findBy("wxUnionid", wxMpOAuth2AccessToken.getUnionId());
                    }
                    if (user == null) {
                        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMpOAuth2AccessToken.getOpenId());
                        if (wxMpUser != null) {
                            user = new User();
                            user.setShareProfitAmount(BigDecimal.ZERO);
                            user.setWxOpenid(wxMpUser.getOpenId());
                            user.setWxUnionid(wxMpUser.getUnionId());
                            user.setWxMiniOpenid(StringUtils.EMPTY);
                            user.setWxSex(Byte.valueOf(wxMpUser.getSex().toString()));
                            user.setWxHeadimgurl(wxMpUser.getHeadImgUrl());
                            user.setMemberType(new Byte("0"));
                            Calendar instance = Calendar.getInstance();
                            instance.set(Calendar.YEAR, 1970);
                            instance.set(Calendar.MONTH, 0);
                            instance.set(Calendar.DATE, 1);
                            instance.set(Calendar.HOUR, 0);
                            instance.set(Calendar.MINUTE, 0);
                            instance.set(Calendar.SECOND, 0);
                            user.setMemberExpired(instance.getTime());
                            user.setRecommendId(0);
                            user.setGmtCreate(new Date());
                            user.setGmtModified(new Date());
                            user.setIsAgent(false);
                            user.setCashBalance(BigDecimal.ZERO);
                            user.setNickname(wxMpUser.getNickname());
                            userService.save(user);
                        }
                    }
                    if (user != null) {
                        try(Jedis jedis = jedisPool.getResource()) {
                            String key = String.format(WX_MP_USER_TOKEN, user.getId() + "");
                            String token = RandomStringUtils.randomAlphanumeric(9).concat("_").concat(user.getId().toString());
                            Boolean exists = jedis.exists(key);
                            if (exists) {
                                token = jedis.get(key);
                            } else {
                                jedis.set(key, token);
                            }
                            jedis.expire(key, 60 * 60 * 24 * 7);

                            result = "?userId=" + user.getId() +
                                    "&openId=" + wxMpOAuth2AccessToken.getOpenId() +
                                    "&unionId=" + wxMpOAuth2AccessToken.getUnionId() +
                                    "&token=" + token;
                            return result;
                        }
                    }
                }
            }
        } catch (WxErrorException e1) {
            e1.printStackTrace();
        }
        return null;
    }


    @Override
    public Object getPersonal(Integer userId) {

        HashMap result = new HashMap();

        User user = userService.findById(userId);
        result.put("user", user);

        Condition condition = new Condition(Slide.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("type", 2);
        List<Slide> slideList = slideService.findByCondition(condition);
        result.put("slide", slideList != null && slideList.size() > 0 ? slideList.get(0) : null);

        //共获得收益:收益总和
        Condition iceCondition = new Condition(Incomexpense.class);
        Example.Criteria iceConditionCriteria = iceCondition.createCriteria();
        iceConditionCriteria.andEqualTo("userId", userId);
        iceConditionCriteria.andEqualTo("type", "SHARE_PROFIT");
        iceCondition.setOrderByClause(" gmt_create desc");
        List<Incomexpense> incomexpenseList = incomexpenseMapper.selectByCondition(iceCondition);
        BigDecimal incomeSum = BigDecimal.ZERO;
        if (incomexpenseList != null && incomexpenseList.size() > 0) {
            for (int i = 0; i < incomexpenseList.size(); i++) {
                incomeSum = incomeSum.add(incomexpenseList.get(i).getIncome());
                if (incomexpenseList.get(i).getShareProfitId() != null && incomexpenseList.get(i).getShareProfitId() != 0) {
                    User tmpUser = userService.findById(incomexpenseList.get(i).getShareProfitId());
                    if (tmpUser != null) {
                        incomexpenseList.get(i).setShareProfitName(tmpUser.getNickname());
                    }
                }
            }
        }
        result.put("incomeSum", incomeSum);
        result.put("incomexpenseList", incomexpenseList);

        //已提现:提现通过总和
        Condition drawedCondition = new Condition(DrawcashLog.class);
        Example.Criteria drawedConditionCriteria = drawedCondition.createCriteria();
        drawedConditionCriteria.andEqualTo("userId", userId);
        drawedConditionCriteria.andEqualTo("status", "FINISHED");
        List<DrawcashLog> drawcashLogeds = drawcashLogMapper.selectByCondition(drawedCondition);
        BigDecimal drawedSum = BigDecimal.ZERO;
        if (drawcashLogeds != null && drawcashLogeds.size() > 0) {
            for (int i = 0; i < drawcashLogeds.size(); i++) {
                drawedSum = drawedSum.add(drawcashLogeds.get(i).getDrawCash());
            }
        }
        result.put("drawedSum", drawedSum);

        //提现中(审核中):提现审核中总和
        Condition drawingCondition = new Condition(DrawcashLog.class);
        Example.Criteria drawingConditionCriteria = drawingCondition.createCriteria();
        drawingConditionCriteria.andEqualTo("userId", userId);
        drawingConditionCriteria.andEqualTo("status", "WAIT_PROCESS");
        List<DrawcashLog> drawcashLogings = drawcashLogMapper.selectByCondition(drawingCondition);
        BigDecimal drawingSum = BigDecimal.ZERO;
        if (drawcashLogings != null && drawcashLogings.size() > 0) {
            for (int i = 0; i < drawcashLogings.size(); i++) {
                drawingSum = drawingSum.add(drawcashLogings.get(i).getDrawCash());
            }
        }
        result.put("drawingSum", drawingSum);

        //可提现 = 用户余额 - 提现中
        BigDecimal canDraws = (user == null ? BigDecimal.ZERO : user.getCashBalance()).subtract(drawingSum);
        result.put("canDraws", canDraws);

        //我的品牌数量
        Object brandDatas = wxAppAllService.openBrandDatas(userId);
        if (brandDatas != null) {
            Gson gson = new Gson();
            ResponseBean responseBean = gson.fromJson(gson.toJson(brandDatas), new TypeToken<ResponseBean>() {
            }.getType());
            if (responseBean != null) {
                Object responseBeanData = responseBean.getData();
                if (responseBeanData != null) {
                    HashMap data = gson.fromJson(gson.toJson(responseBeanData), new TypeToken<HashMap>() {
                    }.getType());
                    if (data != null) {
                        Object companyBrands = data.get("companyBrands");
                        if (companyBrands != null) {
                            List<CompanyBrand> cbs = gson.fromJson(gson.toJson(companyBrands), new TypeToken<List<CompanyBrand>>() {
                            }.getType());
                            result.put("brandDatasNum", cbs != null ? cbs.size() : 0);
                        }
                    }
                }
            }
        }

        if (result.get("brandDatasNum") == null) {
            result.put("brandDatasNum", 0);
        }

        //申请提现按钮是否置灰, 可提现为0或有提现中的记录
        Condition dlCondition = new Condition(DrawcashLog.class);
        Example.Criteria dlConditionCriteria = dlCondition.createCriteria();
        dlConditionCriteria.andEqualTo("userId", userId);
        dlConditionCriteria.andEqualTo("status", "WAIT_PROCESS");
        List<DrawcashLog> drawcashLogs = drawcashLogMapper.selectByCondition(dlCondition);
        if (drawcashLogs != null && !drawcashLogs.isEmpty()) {
            result.put("drawcashLogsNum", true);
        } else {
            result.put("drawcashLogsNum", false);
        }

        //是否弹出框显示提现拒绝
        try (Jedis jedis = jedisPool.getResource()) {
            String jValue = jedis.get(String.format(USER_DRAWCASHLOG, userId));
            if (StringUtils.isEmpty(jValue)) {
                result.put("hasAlertMsg", false);
            } else {
                result.put("hasAlertMsg", true);

                jedis.expire(String.format(USER_DRAWCASHLOG, userId), 60 * 10);

                Condition dlCondition1 = new Condition(DrawcashLog.class);
                Example.Criteria dlConditionCriteria1 = dlCondition1.createCriteria();
                dlConditionCriteria1.andEqualTo("userId", userId);
                HashSet<String> statuss = new HashSet<>();
                statuss.add("FAIL");
                dlConditionCriteria1.andIn("status", statuss);
                dlCondition1.setOrderByClause(" id desc");
                List<DrawcashLog> drawcashLogs1 = drawcashLogMapper.selectByCondition(dlCondition1);
                if (drawcashLogs1 != null && !drawcashLogs1.isEmpty()) {
                    DrawcashLog drawcashLog = drawcashLogs1.get(0);

                    result.put("alertMsg", drawcashLog.getFailMsg());
                } else {
                    result.put("alertMsg", null);
                }
            }
        }

        //用户是否为品牌个人号,品牌个人号显示(付费激活数,品牌用户数)
        Condition companyBrandCondition = new Condition(CompanyBrand.class);
        Example.Criteria companyBrandConditionCriteria = companyBrandCondition.createCriteria();
        companyBrandConditionCriteria.andEqualTo("brandPersonalUserid", userId);
        List<CompanyBrand> companyBrands = companyBrandMapper.selectByCondition(companyBrandCondition);
        if (companyBrands != null && companyBrands.size() > 0) {

            result.put("isBrandPersonalNum", true);

            Condition activeCdkCondition = new Condition(ActiveCdk.class);
            Example.Criteria activeCdkConditionCriteria = activeCdkCondition.createCriteria();
            Set<Integer> brandIds = new HashSet<>();
            companyBrands.forEach(companyBrand -> {
                brandIds.add(companyBrand.getId());
            });
            activeCdkConditionCriteria.andIn("brandId", brandIds);
            List<ActiveCdk> activeCdks = activeCdkMapper.selectByCondition(activeCdkCondition);
            if (activeCdks != null && !activeCdks.isEmpty()) {
                result.put("brandUserNum", activeCdks.size() - brandIds.size());

                Integer payBrandUserCount = userPaymentMapper.getPayBrandUserCount(new ArrayList<>(brandIds));
                result.put("payBrandUserNum", payBrandUserCount == null ? 0 : payBrandUserCount);
            } else {
                result.put("brandUserNum", 0);
                result.put("payBrandUserNum", 0);
            }


        } else {
            result.put("isBrandPersonalNum", false);
            result.put("brandUserNum", 0);
            result.put("payBrandUserNum", 0);
        }



        return ResultGenerator.genSuccessResult(result);
    }

    @Override
    public Object askForCash(Integer userId) {

        User user = userService.findById(userId);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        Condition dlCondition = new Condition(DrawcashLog.class);
        Example.Criteria dlConditionCriteria = dlCondition.createCriteria();
        dlConditionCriteria.andEqualTo("userId", userId);
        dlConditionCriteria.andEqualTo("status", "WAIT_PROCESS");
        List<DrawcashLog> drawcashLogs = drawcashLogMapper.selectByCondition(dlCondition);
        if (drawcashLogs != null && !drawcashLogs.isEmpty()) {
            return ResultGenerator.genFailResult("上一次提现申请未处理,请等候管理员处理完成");
        }

        if (user.getCashBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return ResultGenerator.genFailResult("用户余额不足");
        }

        Condition dlCondition1 = new Condition(DrawcashLog.class);
        Example.Criteria dlConditionCriteria1 = dlCondition1.createCriteria();
        dlConditionCriteria1.andEqualTo("userId", userId);
        HashSet<String> statuss = new HashSet<>();
        statuss.add("FINISHED");
        statuss.add("FAIL");
        dlConditionCriteria1.andIn("status", statuss);
        dlCondition1.setOrderByClause(" id desc");
        List<DrawcashLog> drawcashLogs1 = drawcashLogMapper.selectByCondition(dlCondition1);
        if (drawcashLogs1 != null && !drawcashLogs1.isEmpty()) {
            DrawcashLog drawcashLog = drawcashLogs1.get(0);

            int intervalDays = CalendarUtil.getIntervalDays(drawcashLog.getGmtModified(), new Date());
            if (Math.abs(intervalDays) < 7) {
                return ResultGenerator.genFailResult("提现时间间隔7天");
            }
        }


        DrawcashLog drawcashLog = new DrawcashLog();
        drawcashLog.setUserId(user.getId());
        drawcashLog.setDrawCash(user.getCashBalance());
        drawcashLog.setTaxationCash(BigDecimal.ZERO);
        drawcashLog.setType("WX_WALLET");
        drawcashLog.setGmtCreate(new Date());
        drawcashLog.setGmtModified(new Date());
        drawcashLog.setRealname(StringUtils.EMPTY);
        drawcashLog.setStatus("WAIT_PROCESS");
        drawcashLog.setFailMsg(StringUtils.EMPTY);
        drawcashLog.setRemark(StringUtils.EMPTY);
        drawcashLogMapper.insert(drawcashLog);

        return ResultGenerator.genSuccessResult();
    }

}
