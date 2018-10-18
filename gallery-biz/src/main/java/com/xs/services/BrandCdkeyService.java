package com.xs.services;
import com.xs.beans.BrandCdkey;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface BrandCdkeyService extends Service<BrandCdkey> {

    /**
     *
     * 功能描述: 
     *
     * @param: 
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 上午10:47
     */
    Object queryWithPage(int page, int size, String code, String isUsed, Integer brandId);


    /**
     *
     * 功能描述: 激活码导出
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 上午11:30
     */
    String cdkExport(int page, int size, Integer brandId);
}
