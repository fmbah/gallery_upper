package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.ServiceException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.*;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.daos.*;
import com.xs.services.UserPaymentService;
import com.xs.core.sservice.AbstractService;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;


/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("userpaymentService")
@Transactional
public class UserPaymentServiceImpl extends AbstractService<UserPayment> implements UserPaymentService {
    @Autowired
    private UserPaymentMapper userpaymentMapper;
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ActiveCdkMapper activeCdkMapper;
    @Autowired
    private ShareProfitMapper shareProfitMapper;
    @Autowired
    private IncomexpenseMapper incomexpenseMapper;
    @Autowired
    private BrandCdkeyMapper brandCdkeyMapper;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;


    @Override
    public Object queryWithPage(int page, int size, Integer userId, String userName, String sTime, String eTime, Integer sp1Id, String sp1Name, Byte type, Boolean isExport) {

        PageHelper.startPage(page, size);
        UserPayment userPayment = new UserPayment();
        userPayment.setUserName(userName);
        userPayment.setsTime(sTime);
        userPayment.seteTime(eTime);
        userPayment.setSp1Id(sp1Id);
        userPayment.setSp1Name(sp1Name);
        userPayment.setUserId(userId);
        userPayment.setRechargeType(type);
        List<UserPayment> list = userpaymentMapper.queryWithPage(userPayment);
        PageInfo pageInfo = new PageInfo(list);

        if (isExport) {
            File exportFile = null;
            try{
                Map<String,Object> model = new HashMap<>();
                model.put("users", list);
                exportFile = File.createTempFile("充值消费数据",".xlsx");
                JxlsExportUtil.exportExcel("static/template_file/userPayments.xlsx","static/template_file/userPayments.xml",exportFile,model);

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
    @Transactional(rollbackFor = ServiceException.class)
    public void sumOfMoney(List<UserPayment> userPaymentList) {

        Date now = new Date();

        for (int i = 0; i < userPaymentList.size(); i++) {

            userpaymentMapper.updateByPrimaryKey(userPaymentList.get(i));//处理订单状态

            //计算分摊
            User user = userMapper.selectByPrimaryKey(userPaymentList.get(i).getUserId());
            if (user != null) {

                //改变当前用户的会员类别以及会员过期时间 , 不包含充值类型为品牌会员
                if (userPaymentList.get(i).getRechargeType() != null
                        && !userPaymentList.get(i).getRechargeType().equals(new Byte("1"))) {
                    Calendar instance = Calendar.getInstance();
                    switch (userPaymentList.get(i).getRechargeType()) {
                        case 5:
                            instance.add(Calendar.MONTH, 6);
                            break;
                        case 6:
                            instance.add(Calendar.YEAR, 1);
                            break;
                        case 10:
                            instance.add(Calendar.YEAR, 99);
                            break;
                    }
                    Date expireTime = instance.getTime();
                    user.setMemberExpired(expireTime);
                    user.setMemberType(userPaymentList.get(i).getRechargeType());
                    user.setGmtModified(new Date());
                    userMapper.updateByPrimaryKey(user);
                } else if (userPaymentList.get(i).getRechargeType() != null
                        && userPaymentList.get(i).getRechargeType().equals(new Byte("1"))) {//充值类型为品牌会员(第一次使用激活码),则将充值用户的推荐人修改为品牌对应的个人号

                    //如果用户已经是品牌个人号,验证其它品牌激活码(付费)后,不需要去改推荐人id,引用分摊逻辑到代理就结束了,品牌个人号对应用户是代理,还是终身会员
//                    Condition companyBrandCondition = new Condition(CompanyBrand.class);
//                    Example.Criteria companyBrandConditionCriteria = companyBrandCondition.createCriteria();
//                    companyBrandConditionCriteria.andEqualTo("brandPersonalUserid", user.getId());
//                    List<CompanyBrand> companyBrands = companyBrandMapper.selectByCondition(companyBrandCondition);
//                    if (companyBrands == null || (companyBrands != null && companyBrands.isEmpty())) {
                        String remark = userPaymentList.get(i).getRemark();//格式:brandId_brandName
                        if (remark != null && remark.split("_").length > 1) {
                            Integer brandId = Integer.valueOf(remark.split("_")[0]);
                            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(brandId);

                            if (companyBrand != null && companyBrand.getBrandPersonalUserid() != null) {
                                user.setRecommendId(companyBrand.getBrandPersonalUserid());
                                user.setGmtModified(new Date());
                                userMapper.updateByPrimaryKey(user);
                            }
                        }
//                    }

                }

                if (userPaymentList.get(i).getRechargeType().byteValue() == 1) {

                    if (!StringUtils.isEmpty(userPaymentList.get(i).getCdkCode())) {

                        Condition condition = new Condition(BrandCdkey.class);
                        Example.Criteria criteria = condition.createCriteria();
                        criteria.andEqualTo("code", userPaymentList.get(i).getCdkCode());
                        List<BrandCdkey> brandCdkeys = brandCdkeyMapper.selectByCondition(condition);
                        if (brandCdkeys == null || (brandCdkeys != null && brandCdkeys.isEmpty())) {
                            logger.info("未找到激活码数据,激活码[{}]", userPaymentList.get(i).getCdkCode());
                            continue;
                        }
                        BrandCdkey brandCdkey = brandCdkeys.get(0);
                        brandCdkey.setIsUsed(new Byte("1"));
                        brandCdkey.setUsedTime(now);
                        brandCdkey.setUsedUserId(user.getId());
                        brandCdkey.setGmtModified(now);

                        brandCdkeyMapper.updateByPrimaryKey(brandCdkey);
                    }
                    continue;
                }


                if (user.getRecommendId() == null || user.getRecommendId() == 0) {//无直接邀请人,不需要分摊

                } else {
                    User sp1User = userMapper.selectByPrimaryKey(user.getRecommendId());//一级分摊用户
                    if (sp1User == null) {//一级分摊用户不存在,不需要分摊

                    } else {
                        Byte rechargeType = userPaymentList.get(i).getRechargeType();
                        if (rechargeType == null) {

                        } else {
                            byte type = rechargeType.byteValue();//类型不同,获益方式不同

                            Byte memberType = sp1User.getMemberType();
                            byte sp1UserMemberType = memberType == null ? 0 : memberType.byteValue();
//                                0: 非会员
//                                5: 金卡会员
//                                6: 铂金会员
//                                10: 钻石会员
                            Condition cdkCondition = new Condition(ActiveCdk.class);
                            Example.Criteria cdkConditionCriteria = cdkCondition.createCriteria();
                            cdkConditionCriteria.andEqualTo("usedUserId", sp1User.getId());
                            List<ActiveCdk> activeCdks = activeCdkMapper.selectByCondition(cdkCondition);
                            boolean isBrandMember = activeCdks != null && activeCdks.size() > 0 ? true : false;//品牌会员或品牌个人号

                            boolean hasShareProfit = false;//需要进行分摊标识
                            if (sp1UserMemberType == 0) {//非会员
                                if (isBrandMember) {
                                    hasShareProfit = true;
                                }
                            } else if (sp1UserMemberType == 6 || sp1UserMemberType == 10) {//一年会员或钻石会员
                                hasShareProfit = true;
                            } else if (sp1UserMemberType == 5) {//金卡会员没钱分
                                if (isBrandMember) {
                                    hasShareProfit = true;
                                }
                            }

                            if (type == 6) {//铂金会员


                                if (hasShareProfit) {
                                    //用户数据
                                    sp1User.setCashBalance(sp1User.getCashBalance().add(new BigDecimal(150)));//余额加150

                                    //分享收益数据
                                    ShareProfit shareProfit = new ShareProfit();
                                    shareProfit.setGmtCreate(now);
                                    shareProfit.setGmtModified(now);
                                    shareProfit.setRemark(StringUtils.EMPTY);
                                    shareProfit.setSubType(new Byte("1"));
                                    shareProfit.setProfit(new BigDecimal(150));
                                    shareProfit.setPaymnetId(userPaymentList.get(i).getId());
                                    shareProfit.setUserId(sp1User.getId());

                                    //收入支付记录数据
                                    Incomexpense incomexpense = new Incomexpense();
                                    incomexpense.setUserId(sp1User.getId());
                                    incomexpense.setType("SHARE_PROFIT");
                                    incomexpense.setIncome(new BigDecimal(150));
                                    incomexpense.setExpense(new BigDecimal(0));
                                    incomexpense.setBalance(sp1User.getCashBalance().add(new BigDecimal(150)));
                                    incomexpense.setTradedate(now);
                                    incomexpense.setGmtCreate(now);
                                    incomexpense.setShareProfitId(userPaymentList.get(i).getUserId());
                                    incomexpense.setRemark(user.getNickname() + "-" + 365 + "-" + 150);

                                    userMapper.updateByPrimaryKey(sp1User);
                                    shareProfitMapper.insert(shareProfit);
                                    incomexpenseMapper.insert(incomexpense);

                                    if (sp1User.getRecommendId() == null || sp1User.getRecommendId() == 0
                                            || sp1User.getIsAgent()) {//是代理或者无推荐人则不进行二级分成

                                    } else {
                                        User sp2User = userMapper.selectByPrimaryKey(sp1User.getRecommendId());
                                        while (sp2User != null && !sp2User.getIsAgent()) {
                                            sp2User = userMapper.selectByPrimaryKey(sp2User.getRecommendId());
                                        }
                                        if (sp2User != null && sp2User.getIsAgent()
                                                && sp2User.getMemberType() != null
                                                && !sp2User.getMemberType().equals(new Byte("0"))
                                                && !sp2User.getMemberType().equals(new Byte("5"))) {
                                            //用户数据
                                            sp2User.setCashBalance(sp2User.getCashBalance().add(new BigDecimal(50)));//余额加50

                                            //分享收益数据
                                            ShareProfit shareProfit2 = new ShareProfit();
                                            shareProfit2.setGmtCreate(now);
                                            shareProfit2.setGmtModified(now);
                                            shareProfit2.setRemark(StringUtils.EMPTY);
                                            shareProfit2.setSubType(new Byte("2"));
                                            shareProfit2.setProfit(new BigDecimal(50));
                                            shareProfit2.setPaymnetId(userPaymentList.get(i).getId());
                                            shareProfit2.setUserId(sp2User.getId());

                                            //收入支付记录数据
                                            Incomexpense incomexpense2 = new Incomexpense();
                                            incomexpense2.setUserId(sp2User.getId());
                                            incomexpense2.setType("SHARE_PROFIT");
                                            incomexpense2.setIncome(new BigDecimal(50));
                                            incomexpense2.setExpense(new BigDecimal(0));
                                            incomexpense2.setBalance(sp2User.getCashBalance().add(new BigDecimal(50)));
                                            incomexpense2.setTradedate(now);
                                            incomexpense2.setGmtCreate(now);
                                            incomexpense2.setShareProfitId(userPaymentList.get(i).getUserId());
                                            incomexpense2.setRemark(user.getNickname() + "-" + 365 + "-" + 50);

                                            userMapper.updateByPrimaryKey(sp2User);
                                            shareProfitMapper.insert(shareProfit2);
                                            incomexpenseMapper.insert(incomexpense2);
                                        }
                                    }
                                }

                            } else if (type == 10) {//钻石会员

                                if (hasShareProfit) {
                                    //用户数据
                                    sp1User.setCashBalance(sp1User.getCashBalance().add(new BigDecimal(sp1UserMemberType == 10 ? 350 : 150)));//余额加150

                                    //分享收益数据
                                    ShareProfit shareProfit = new ShareProfit();
                                    shareProfit.setGmtCreate(now);
                                    shareProfit.setGmtModified(now);
                                    shareProfit.setRemark(StringUtils.EMPTY);
                                    shareProfit.setSubType(new Byte("1"));
                                    shareProfit.setProfit(new BigDecimal(sp1UserMemberType == 10 ? 350 : 150));
                                    shareProfit.setPaymnetId(userPaymentList.get(i).getId());
                                    shareProfit.setUserId(sp1User.getId());

                                    //收入支付记录数据
                                    Incomexpense incomexpense = new Incomexpense();
                                    incomexpense.setUserId(sp1User.getId());
                                    incomexpense.setType("SHARE_PROFIT");
                                    incomexpense.setIncome(new BigDecimal(sp1UserMemberType == 10 ? 350 : 150));
                                    incomexpense.setExpense(new BigDecimal(0));
                                    incomexpense.setBalance(sp1User.getCashBalance().add(new BigDecimal(sp1UserMemberType == 10 ? 350 : 150)));
                                    incomexpense.setTradedate(now);
                                    incomexpense.setGmtCreate(now);
                                    incomexpense.setShareProfitId(userPaymentList.get(i).getUserId());
                                    incomexpense.setRemark(user.getNickname() + "-" + 899 + "-" + (sp1UserMemberType == 10 ? 350 : 150));

                                    userMapper.updateByPrimaryKey(sp1User);
                                    shareProfitMapper.insert(shareProfit);
                                    incomexpenseMapper.insert(incomexpense);

                                    if (sp1User.getRecommendId() == null || sp1User.getRecommendId() == 0
                                            || sp1User.getIsAgent()) {

                                    } else {
                                        User sp2User = userMapper.selectByPrimaryKey(sp1User.getRecommendId());
                                        while (sp2User != null && !sp2User.getIsAgent()) {
                                            sp2User = userMapper.selectByPrimaryKey(sp2User.getRecommendId());
                                        }

                                        if (sp2User != null && sp2User.getIsAgent()
                                                && sp2User.getMemberType() != null
                                                && !sp2User.getMemberType().equals(new Byte("0"))
                                                && !sp2User.getMemberType().equals(new Byte("5"))) {

                                            boolean hasShareProfit2 = false;
                                            Condition cdkCondition2 = new Condition(ActiveCdk.class);
                                            Example.Criteria cdkConditionCriteria2 = cdkCondition2.createCriteria();
                                            cdkConditionCriteria2.andEqualTo("usedUserId", sp2User.getId());
                                            List<ActiveCdk> activeCdks2 = activeCdkMapper.selectByCondition(cdkCondition2);
                                            boolean isBrandMember2 = activeCdks2 != null && activeCdks2.size() > 0 ? true : false;//品牌会员

                                            byte type2 = sp2User.getMemberType() == null ? 0 : sp2User.getMemberType().byteValue();
                                            if (type2 == 0) {
                                                if (isBrandMember2) {
                                                    hasShareProfit2 = true;
                                                }
                                            } else if (type2 == 6 || type2 == 10) {
                                                hasShareProfit2 = true;
                                            } else if (type2 == 5) {
                                                if (isBrandMember2) {
                                                    hasShareProfit2 = true;
                                                }
                                            }

                                            if (hasShareProfit2) {
                                                //用户数据
                                                sp2User.setCashBalance(sp2User.getCashBalance().add(new BigDecimal(type2 == 10 ? 120 : 50)));//代理+钻石会员=120 代理+会员/品牌=50

                                                //分享收益数据
                                                ShareProfit shareProfit2 = new ShareProfit();
                                                shareProfit2.setGmtCreate(now);
                                                shareProfit2.setGmtModified(now);
                                                shareProfit2.setRemark(StringUtils.EMPTY);
                                                shareProfit2.setSubType(new Byte("2"));
                                                shareProfit2.setProfit(new BigDecimal(type2 == 10 ? 120 : 50));
                                                shareProfit2.setPaymnetId(userPaymentList.get(i).getId());
                                                shareProfit2.setUserId(sp2User.getId());

                                                //收入支付记录数据
                                                Incomexpense incomexpense2 = new Incomexpense();
                                                incomexpense2.setUserId(sp2User.getId());
                                                incomexpense2.setType("SHARE_PROFIT");
                                                incomexpense2.setIncome(new BigDecimal(type2 == 10 ? 120 : 50));
                                                incomexpense2.setExpense(new BigDecimal(0));
                                                incomexpense2.setBalance(sp2User.getCashBalance().add(new BigDecimal(type2 == 10 ? 120 : 50)));
                                                incomexpense2.setTradedate(now);
                                                incomexpense2.setGmtCreate(now);
                                                incomexpense2.setShareProfitId(userPaymentList.get(i).getUserId());
                                                incomexpense2.setRemark(user.getNickname() + "-" + 899 + "-" + (type2 == 10 ? 120 : 50));

                                                userMapper.updateByPrimaryKey(sp2User);
                                                shareProfitMapper.insert(shareProfit2);
                                                incomexpenseMapper.insert(incomexpense2);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }
}
