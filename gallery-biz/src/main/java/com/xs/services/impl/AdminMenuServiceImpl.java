package com.xs.services.impl;

import com.xs.beans.AdminRoleMenu;
import com.xs.core.ResultGenerator;
import com.xs.daos.AdminMenuMapper;
import com.xs.beans.AdminMenu;
import com.xs.daos.AdminRoleMenuMapper;
import com.xs.services.AdminMenuService;
import com.xs.core.sservice.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("adminmenuService")
@Transactional
public class AdminMenuServiceImpl extends AbstractService<AdminMenu> implements AdminMenuService {
    @Autowired
    private AdminMenuMapper adminmenuMapper;
    @Autowired
    private AdminRoleMenuMapper adminRoleMenuMapper;


    @Override
    public List<AdminMenu> allMenu() {
        Condition condition = new Condition(AdminMenu.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("pId", 0);
        List<AdminMenu> result = super.findByCondition(condition);
        for (int i = 0, j = result.size(); i < j; i++) {
            Condition condition1 = new Condition(AdminMenu.class);
            Example.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("pId", result.get(i).getId());
            result.get(i).setSubMenu(super.findByCondition(condition1));
        }
        return result;
    }

    @Override
    public Object getMenusByRoleId(Long roleId) {

        Condition arm = new Condition(AdminRoleMenu.class);
        Example.Criteria armCriteria = arm.createCriteria();
        armCriteria.andEqualTo("roleId", roleId);
        List<AdminRoleMenu> adminRoleMenus = adminRoleMenuMapper.selectByCondition(arm);


        Condition condition = new Condition(AdminMenu.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("pId", 0);
        condition.orderBy("weight").desc();
        List<AdminMenu> adminMenus = super.findByCondition(condition);
        for (int i = 0, j = adminMenus.size(); i < j; i++) {
            Condition condition1 = new Condition(AdminMenu.class);
            Example.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("pId", adminMenus.get(i).getId());
            adminMenus.get(i).setSubMenu(super.findByCondition(condition1));
        }

        //所有菜单,已有菜单,
        Iterator<AdminMenu> iterator = adminMenus.iterator();
        while(iterator.hasNext()) {
            AdminMenu next = iterator.next();
            boolean hasDel = false;
            for (AdminRoleMenu adminRoleMenu : adminRoleMenus) {
                if (next.getId().equals(adminRoleMenu.getMenuId())) {
                    hasDel = true;
                    break;
                }
            }
            if (!hasDel) {
//                iterator.remove();
                next.setHasSelected(false);
            } else {
                next.setHasSelected(true);
            }

            List<AdminMenu> subMenu = next.getSubMenu();
            if (subMenu != null) {
                boolean hasDel1 = false;
                for (AdminMenu adminMenu : subMenu) {
                    for (AdminRoleMenu adminRoleMenu : adminRoleMenus) {
                        if (adminMenu.getId().equals(adminRoleMenu.getMenuId())) {
                            hasDel1 = true;
                            break;
                        }
                    }

                    if (!hasDel1) {
                        adminMenu.setHasSelected(false);
                    } else {
                        adminMenu.setHasSelected(true);
                    }
                }
            }

        }

        return ResultGenerator.genSuccessResult(adminMenus);
    }
}
