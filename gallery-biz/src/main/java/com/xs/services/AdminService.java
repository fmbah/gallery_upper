package com.xs.services;
import com.github.pagehelper.PageInfo;
import com.xs.beans.Admin;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/16
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface AdminService extends Service<Admin> {

    /**
     *
     * 功能描述: 后台帐号分页数据
     *
     * @param: page：页码   size：每页容量
     *          roleId: 角色id username：用户名
     *          brandId: 品牌id
     *          isBrand：是否品牌帐号
     * @return:
     * @auther: Fmbah
     * @date: 18-10-16 下午6:33
     */
    PageInfo queryWithPage(int page, int size, Integer roleId, String username, Integer brandId, Boolean isBrand, String brandName);


    /**
     *
     * 功能描述: 修改帐号密码
     *
     * @param: username：帐号
     *          password：密码
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 上午9:23
     */
    Object updatePass(String username, String password, String oldPassword);

    /**
     *
     * 功能描述: 重置帐号密码
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 下午2:22
     */
    Object resetPass(Integer id);
}
