package com.xs.services;
import com.xs.beans.AdminRoleMenu;
import com.xs.core.sservice.Service;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface AdminRoleMenuService extends Service<AdminRoleMenu> {


    /**
     *
     * 功能描述: 配置角色拥有菜单
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 上午9:56
     */
    Object configRoleMenu(Integer roleId, String menuIds);

}
