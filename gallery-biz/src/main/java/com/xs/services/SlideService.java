package com.xs.services;
import com.xs.beans.Slide;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/18
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface SlideService extends Service<Slide> {

    /**
     *
     * 功能描述:
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-18 下午1:39
     */
    Object queryWithPage(int page, int size, Integer type);

}
