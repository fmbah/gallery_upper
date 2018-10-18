package com.xs.services.impl;

import com.xs.beans.AdminRoleMenu;
import com.xs.core.ResultGenerator;
import com.xs.core.sservice.AbstractService;
import com.xs.daos.AdminRoleMenuMapper;
import com.xs.services.AdminRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/17
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("adminrolemenuService")
@Transactional
public class AdminRoleMenuServiceImpl extends AbstractService<AdminRoleMenu> implements AdminRoleMenuService {
    @Autowired
    private AdminRoleMenuMapper adminrolemenuMapper;


    @Override
    public Object configRoleMenu(Integer roleId, String menuIds) {

        Condition condition = new Condition(AdminRoleMenu.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("roleId", roleId);
        List<AdminRoleMenu> byCondition = super.findByCondition(condition);
        for (int i = 0, j = byCondition.size(); i < j; i++) {
            super.deleteById(byCondition.get(i).getId().intValue());
        }

        for (String menuId : menuIds.split(",")) {
            AdminRoleMenu adminRoleMenu = new AdminRoleMenu();
            adminRoleMenu.setGmtCreate(new Date());
            adminRoleMenu.setGmtModified(new Date());
            adminRoleMenu.setMenuId(Long.valueOf(menuId));
            adminRoleMenu.setRoleId(Integer.toUnsignedLong(roleId));
            super.save(adminRoleMenu);
        }

        return ResultGenerator.genSuccessResult();
    }
}
