package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.User;
import com.xs.beans.UserPayment;
import com.xs.daos.IncomexpenseMapper;
import com.xs.beans.Incomexpense;
import com.xs.daos.UserMapper;
import com.xs.daos.UserPaymentMapper;
import com.xs.services.IncomexpenseService;
import com.xs.core.sservice.AbstractService;
import com.xs.services.PayBearingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("incomexpenseService")
@Transactional
public class IncomexpenseServiceImpl extends AbstractService<Incomexpense> implements IncomexpenseService {
    @Autowired
    private IncomexpenseMapper incomexpenseMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserPaymentMapper userPaymentMapper;



    @Override
    public Object list(Integer page, Integer size, String type, Byte subType, Integer userId) {


        PageHelper.startPage(page, size);
        Condition condition = new Condition(Incomexpense.class);
        Example.Criteria criteria = condition.createCriteria();
        if (null != userId) {
            criteria.andEqualTo("userId", userId);
        }
        if (null != subType) {
            criteria.andEqualTo("subType", subType);
        }
        if (!StringUtils.isEmpty(type)) {
            criteria.andEqualTo("type", type);
        }
        condition.setOrderByClause(" id desc");
        List<Incomexpense> list = incomexpenseMapper.selectByCondition(condition);
        if (null != list) {
            for (Incomexpense incomexpense : list) {
                User user1 = userMapper.selectByPrimaryKey(incomexpense.getUserId());
                if (null != user1) {
                    incomexpense.setNickName(user1.getNickname());
                    incomexpense.setUserPic(user1.getWxHeadimgurl());
                }
                User user2 = userMapper.selectByPrimaryKey(incomexpense.getShareProfitId());
                if (null != user2) {
                    incomexpense.setNickName1(user2.getNickname());
                    incomexpense.setUserPic1(user1.getWxHeadimgurl());
                }
                if (null != incomexpense.getPaymentId() && 0 != incomexpense.getPaymentId()) {
                    UserPayment userPayment = userPaymentMapper.selectByPrimaryKey(incomexpense.getPaymentId());
                    if (null != userPayment) {
                        incomexpense.setPayType(userPayment.getRechargeType().toString());
                        incomexpense.setPayAmount(userPayment.getAmount());

                    }
                } else {//老数据处理
                    String remark = incomexpense.getRemark();
                    if (null != remark && "SHARE_PROFIT".equals(incomexpense.getType())) {
                        String[] splits = remark.split("-");
                        if (splits.length == 3) {
                            incomexpense.setPayType(null);
                            incomexpense.setPayAmount(new BigDecimal(splits[1]));
                        }
                    }
                }
            }
        }



        PageInfo pageInfo = new PageInfo(list);

        return pageInfo;
    }
}
