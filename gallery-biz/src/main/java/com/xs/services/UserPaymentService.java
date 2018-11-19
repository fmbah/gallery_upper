package com.xs.services;
import com.xs.beans.UserPayment;
import com.xs.core.sservice.Service;

import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface UserPaymentService extends Service<UserPayment> {

    Object queryWithPage(int page, int size, Integer userId,
                         String userName,
                         String sTime,
                         String eTime,
                         Integer sp1Id,
                         String sp1Name,
                         Byte type,
                         Boolean isExport);

    /**
     *
     * 功能描述: 支付回调订单完成,处理订单状态,处理获益分摊,处理相应用户余额,收入,分享收益数据
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 下午3:34
     */
    void sumOfMoney (List<UserPayment> userPaymentList);
}
