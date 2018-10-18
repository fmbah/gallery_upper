package com.xs.services;
import com.xs.beans.Label;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface LabelService extends Service<Label> {


    /**
     *
     * 功能描述: 获取标签数据集和
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 下午5:26
     */
    Object queryWithPage(int page, int size, String name);
}
