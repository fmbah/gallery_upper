package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.Admin;
import com.xs.beans.AdminRole;
import com.xs.core.sexception.ServiceException;
import com.xs.core.sservice.AbstractService;
import com.xs.daos.AdminMapper;
import com.xs.daos.AdminRoleMapper;
import com.xs.services.AdminRoleService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;


/**
\* User: zhaoxin
\* Date: 2018/10/16
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("adminroleService")
@Transactional
public class AdminRoleServiceImpl extends AbstractService<AdminRole> implements AdminRoleService {
    @Autowired
    private AdminRoleMapper adminroleMapper;
    @Autowired
    private AdminMapper adminMapper;


    @Override
    public void save(AdminRole model) {
        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        super.save(model);
    }

    @Override
    public void update(AdminRole model) {
        if (model.getId() == null) {
            throw new ServiceException("角色id为空");
        }
        AdminRole adminRole = this.findById(model.getId().intValue());
        if (adminRole == null) {
            throw new ServiceException("角色数据不存在或已删除");
        }
        try {
            BeanUtils.copyProperties(adminRole, model);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        adminRole.setGmtModified(new Date());
        super.update(adminRole);
    }

    @Override
    public AdminRole findById(Integer id) {
        return super.findById(id);
    }

    @Override
    public PageInfo queryWithPage(int page, int size) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(AdminRole.class);
        condition.setOrderByClause(" gmt_modified desc");
        List<AdminRole> adminRoleList = super.findByCondition(condition);
        PageInfo pageInfo = new PageInfo(adminRoleList);

        return pageInfo;
    }


    @Override
    public void deleteById(Integer id) {

        Condition condition = new Condition(Admin.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("roleId", id);
        List<Admin> admins = adminMapper.selectByCondition(condition);
        if (admins != null && !admins.isEmpty()) {
            throw new ServiceException("该角色下已绑定帐号");
        }

        super.deleteById(id);
    }
}
