package com.xs.services;
import com.github.pagehelper.PageInfo;
import com.xs.beans.User;
import com.xs.core.sservice.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
\* User: zhaoxin
\* Date: 2018/10/19
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface UserService extends Service<User> {

    Map<String, Object> login(String code, String signature, String rawData, String encryptedData, String iv, HttpServletRequest request, String recommendId);


    /**
     *
     * 功能描述: 用户管理分页数据
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午1:43
     */
    Object queryWithPage(int page, int size, Boolean isMember, Byte memberType, Boolean isAgent, String sTime,
                           String eTime, Integer id, String nickname, Boolean isExport, Integer brandId);

    /**
     *
     * 功能描述: 开通或取消代理
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-19 下午2:55
     */
    Object modifiedAgentStatus(Integer id, Boolean isAgent);
}
