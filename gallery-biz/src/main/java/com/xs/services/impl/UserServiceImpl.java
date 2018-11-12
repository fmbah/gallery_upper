package com.xs.services.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.ActiveCdk;
import com.xs.beans.BrandCdkey;
import com.xs.beans.ShareProfit;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.Result;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.daos.*;
import com.xs.beans.User;
import com.xs.services.ActiveCdkService;
import com.xs.services.UserService;
import com.xs.core.sservice.AbstractService;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.xs.core.ProjectConstant.WX_USER_TOKEN;


/**
\* User: zhaoxin
\* Date: 2018/10/19
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("userService")
@Transactional
public class UserServiceImpl extends AbstractService<User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    //小程序工具类
    @Autowired
    private WxMaService wxService;
    @Autowired
    private ActiveCdkMapper activeCdkMapper;
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private ShareProfitMapper shareProfitMapper;
    @Autowired
    private JedisPool jedisPool;


    @Override
    public Map<String, Object> login(String code, String signature, String rawData,
                                     String encryptedData, String iv, HttpServletRequest request, String recommendId) {

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> infoMap = this.getSessionInfo(code, rawData, signature, encryptedData, iv);
        WxMaUserInfo userInfo = (WxMaUserInfo) infoMap.get("userInfo");
        User user = this.findBy("wxMiniOpenid", userInfo.getOpenId());
        if (user == null) {
            user = this.findBy("wxUnionid", userInfo.getUnionId());
        }
        user = saveUser(user, userInfo, recommendId);
        result.put("wxUser", user);

        try(Jedis jedis = jedisPool.getResource()) {
            String key = String.format(WX_USER_TOKEN, user.getId() + "");
            String token = RandomStringUtils.randomAlphanumeric(9).concat("_").concat(user.getId().toString());
            jedis.set(key, token);
            jedis.expire(key, 60 * 60 * 24 * 7);
            result.put("token", token);
        }

        return result;
    }

    private Map<String, Object> getSessionInfo(String code, String rawData, String signature, String encryptedData, String iv) {
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxService.getUserService().getSessionInfo(code);
            if (null != sessionInfo) {
                // 用户信息校验
                if (this.wxService.getUserService().checkUserInfo(sessionInfo.getSessionKey(), rawData, signature)) {
                    WxMaUserInfo userInfo = this.wxService.getUserService().getUserInfo(sessionInfo.getSessionKey(), encryptedData, iv);
                    if (null != userInfo) {//&& null != phoneNoInfo
                        Map<String, Object> result = new HashedMap();
                        result.put("userInfo", userInfo);
                        result.put("sessionKey", sessionInfo.getSessionKey());
                        return result;
                    }
                }
            }
        } catch (WxErrorException e) {
            this.logger.error(e.getMessage(), e);
            throw new ServiceException("微信认证失败");
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            throw new ServiceException("系统异常");
        }
        return null;
    }

    private User saveUser(User user, WxMaUserInfo userInfo, String recommendId) {
        logger.info("****************saveUser*****************");
        try {
            if (null == user) {
                logger.info("****************first***come***in*****************recommendId: ["+ recommendId +"]");
                user = new User();
                user.setGmtCreate(new Date());
                Calendar instance = Calendar.getInstance();
                instance.set(1970, 0, 1);
                instance.set(Calendar.HOUR, 0);
                instance.set(Calendar.MINUTE, 0);
                instance.set(Calendar.SECOND, 0);
                Date time = instance.getTime();
                user.setMemberExpired(time);
                user.setWxOpenid(StringUtils.EMPTY);
                user.setRecommendId(StringUtils.isEmpty(recommendId) ? 0 : Integer.valueOf(recommendId));
                user.setGmtCreate(new Date());
                user.setMemberType(Byte.valueOf("0"));
                user.setIsAgent(false);
                user.setCashBalance(BigDecimal.ZERO);
            }

            user.setWxUnionid(userInfo.getUnionId());
            user.setWxMiniOpenid(userInfo.getOpenId());
            user.setNickname(userInfo.getNickName());
            user.setWxHeadimgurl(userInfo.getAvatarUrl());
            user.setGmtModified(new Date());
            user.setWxSex(Byte.valueOf(userInfo.getGender()));

            if (user.getId() != null) {
                super.update(user);
            } else {
                super.save(user);
            }
            return user;
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            throw new ServiceException("保存用户失败");
        }
    }


    @Override
    public Object queryWithPage(int page, int size, Boolean isMember, Byte memberType, Boolean isAgent,
                                  String sTime, String eTime, Integer id, String nickname, Boolean isExport, Integer brandId) {

        HashSet<Integer> brandUserIds = new HashSet<>();
        if (brandId != null) {
            Condition activeCdkCondition = new Condition(ActiveCdk.class);
            Example.Criteria activeCdkConditionCriteria = activeCdkCondition.createCriteria();
            activeCdkConditionCriteria.andEqualTo("brandId", brandId);
            List<ActiveCdk> activeCdks = activeCdkMapper.selectByCondition(activeCdkCondition);

            if (activeCdks != null && activeCdks.size() > 0) {
                for (ActiveCdk activeCdk : activeCdks) {
                    brandUserIds.add(activeCdk.getUsedUserId());
                }
            }

        }

        PageHelper.startPage(page, size);
        Condition condition = new Condition(User.class);
        Example.Criteria criteria = condition.createCriteria();
        if (isMember != null) {
            if (isMember.booleanValue()) {
                criteria.andNotEqualTo("memberType", Byte.valueOf("0"));
            } else {
                criteria.andEqualTo("memberType", Byte.valueOf("0"));
            }
        }
        if (memberType != null) {
            criteria.andEqualTo("memberType", memberType);
        }
        if (isAgent != null) {
            criteria.andEqualTo("isAgent", isAgent);
        }
        if (id != null) {
            criteria.andEqualTo("id", id);
        }
        if (!brandUserIds.isEmpty()) {
            criteria.andIn("id", brandUserIds);
        }
        if (!StringUtils.isEmpty(nickname)) {
            criteria.andLike("nickname", "%"+ nickname +"%");
        }
        if (!StringUtils.isEmpty(sTime)) {
            criteria.andGreaterThanOrEqualTo("gmtCreate", sTime);
        }
        if (!StringUtils.isEmpty(eTime)) {
            criteria.andLessThanOrEqualTo("gmtCreate", eTime);
        }

        List<User> list = super.findByCondition(condition);
        if (list != null && !list.isEmpty()) {
            for (User user : list) {

                Condition activeCdkCon = new Condition(ActiveCdk.class);
                Example.Criteria activeCdkConCriteria = activeCdkCon.createCriteria();
                activeCdkConCriteria.andEqualTo("usedUserId", user.getId());
                List<ActiveCdk> activeCdks = activeCdkMapper.selectByCondition(activeCdkCon);
                if (activeCdks != null && !activeCdks.isEmpty()) {
                    Set<Integer> brandIds = new HashSet<>();
                    for (ActiveCdk activeCdk : activeCdks) {
                        brandIds.add(activeCdk.getBrandId());
                    }
                    user.setJoinBrandCount(brandIds.size());
                } else {
                    user.setJoinBrandCount(0);
                }

                user.setProfitUserId(user.getRecommendId());
                User tmpUser = this.findById(user.getRecommendId());
                user.setProfitUserName(tmpUser != null ? tmpUser.getNickname() : "");


                Condition userCondition = new Condition(User.class);
                Example.Criteria userConditionCriteria = userCondition.createCriteria();
                userConditionCriteria.andEqualTo("recommendId", user.getId());
                List<User> recommendUsers = userMapper.selectByCondition(userCondition);
                if (recommendUsers != null && !recommendUsers.isEmpty()) {
                    user.setRecommendCount(recommendUsers.size());
                } else {
                    user.setRecommendCount(0);
                }

                if (user.getMemberType().byteValue() == 0) {
                    user.setIsMemberStr("否");
                    user.setMemberTypeStr("");
                    user.setMemberExpired(null);
                } else {
                    user.setIsMemberStr("是");
                    if (user.getMemberType().byteValue() == 5) {
                        user.setMemberTypeStr("半年会员");
                    } else if(user.getMemberType().byteValue() == 6) {
                        user.setMemberTypeStr("全年会员");
                    } else if(user.getMemberType().byteValue() == 10) {
                        user.setMemberTypeStr("终身会员");
                    } else {
                        user.setMemberTypeStr("");
                    }
                }

                Condition shareProfitCondition = new Condition(ShareProfit.class);
                Example.Criteria shareProfitConditionCriteria = shareProfitCondition.createCriteria();
                shareProfitConditionCriteria.andEqualTo("userId", user.getId());
                List<ShareProfit> shareProfits = shareProfitMapper.selectByCondition(shareProfitCondition);
                if (shareProfits != null) {
                    BigDecimal allProfit = new BigDecimal(0);
                    for (ShareProfit shareProfit : shareProfits) {
                        allProfit = allProfit.add(shareProfit.getProfit());
                    }
                    user.setShareProfitAmount(allProfit);
                }

            }
        }

        PageInfo pageInfo = new PageInfo(list);


        if (isExport != null && isExport) {
            File exportFile = null;
            try{
                Map<String,Object> model = new HashMap<>();
                model.put("users", list);
                exportFile = File.createTempFile("users",".xlsx");
                JxlsExportUtil.exportExcel("static/template_file/users.xlsx","static/template_file/users.xml",exportFile,model);

                OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
                try {
                    ossClient.putObject(ossConfig.getBucket(), exportFile.getName(), new FileInputStream(exportFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), exportFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
                return ResultGenerator.genSuccessResult(url.toString().replaceAll("http", "https"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(exportFile != null) {
                    exportFile.delete();
                }
            }
        }

        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @Override
    public Object modifiedAgentStatus(Integer id, Boolean isAgent) {

        User user = this.findById(id);
        if (user == null) {
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }

        user.setIsAgent(isAgent);
        user.setGmtModified(new Date());

        super.update(user);
        return ResultGenerator.genSuccessResult();
    }
}
