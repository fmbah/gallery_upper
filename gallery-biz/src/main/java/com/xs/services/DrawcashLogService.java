package com.xs.services;
import com.xs.beans.DrawcashLog;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface DrawcashLogService extends Service<DrawcashLog> {

    /**
     *
     * 功能描述: 提现列表
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 上午11:56
     */
    Object queryWithPage(int page, int size, String sTime, String eTime, Integer userId, String userName, String status, Boolean isExport);


    /**
     *
     * 功能描述: 审核操作
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 下午2:10
     */
    Object auditor(Integer id, Boolean hasPass, String failMsg);
}

