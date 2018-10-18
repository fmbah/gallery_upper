package com.xs.services;
import com.xs.beans.CompanyBrand;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface CompanyBrandService extends Service<CompanyBrand> {

    /**
     *
     * 功能描述: 品牌列表
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 上午9:11
     */
    Object queryWithPage(int page, int size, String contactPhone, String contactPerson, String name);

    /**
     *
     * 功能描述: 品牌id, 数量
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 上午10:20
     */
    Object addCdkey(Integer brandId, Integer num);

}
