package com.xs.services;
import com.xs.beans.AdminMenu;
import com.xs.core.sservice.Service;

import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

public interface AdminMenuService extends Service<AdminMenu> {

    /**
     *
     * 功能描述: 获取菜单数据集合,不含分页
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 上午9:42
     */
    List<AdminMenu> allMenu();

    /**
     *
     * 功能描述: 角色拥有的菜单数据集合
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-17 上午10:07
     */
    Object getMenusByRoleId(Long roleId);

}
