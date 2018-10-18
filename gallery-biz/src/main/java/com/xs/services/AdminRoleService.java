package com.xs.services;
import com.github.pagehelper.PageInfo;
import com.xs.beans.AdminRole;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/16
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface AdminRoleService extends Service<AdminRole> {

    /**
     *
     * 功能描述: 角色分页数据
     *
     * @param: page：页码   size：每页容量
     * @return:
     * @auther: Fmbah
     * @date: 18-10-16 下午6:33
     */
    PageInfo queryWithPage(int page, int size);


}
