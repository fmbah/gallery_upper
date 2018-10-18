package com.xs.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.Admin;
import com.xs.beans.AdminRole;
import com.xs.beans.CompanyBrand;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.core.sservice.AbstractService;
import com.xs.daos.AdminMapper;
import com.xs.daos.AdminRoleMapper;
import com.xs.daos.CompanyBrandMapper;
import com.xs.services.AdminService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import static com.xs.core.ProjectConstant.BACK_DEFAULT_PASS;
import static com.xs.core.ProjectConstant.BACK_LOGIN_BZ;


/**
\* User: zhaoxin
\* Date: 2018/10/16
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("adminService")
@Transactional
public class AdminServiceImpl extends AbstractService<Admin> implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private CompanyBrandMapper companyBrandMapper;


    @Override
    public void save(Admin model) {

        Condition condition = new Condition(Admin.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("username", model.getUsername());
        List<Admin> byCondition = super.findByCondition(condition);
        if (byCondition != null && byCondition.size() != 0) {
            throw new ServiceException("该帐号已存在");
        }

        model.setGmtCreate(new Date());
        model.setGmtModified(new Date());
        model.setHashedPwd(DigestUtils.md5Hex(BACK_DEFAULT_PASS));
        super.save(model);
    }

    @Override
    public void update(Admin model) {
        if (model.getId() == null) {
            throw new ServiceException("帐号id为空");
        }
        Admin admin = this.findById(model.getId().intValue());
        if (admin == null) {
            throw new ServiceException("帐号数据不存在或已删除");
        }
        try {
            BeanUtils.copyProperties(admin, model);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        admin.setGmtModified(new Date());
        super.update(admin);
    }

    @Override
    public Admin findById(Integer id) {
        return super.findById(id);
    }

    @Override
    public PageInfo queryWithPage(int page, int size, Integer roleId, String username, Integer brandId, Boolean isBrand) {

        PageHelper.startPage(page, size);
        Condition condition = new Condition(Admin.class);
        Example.Criteria criteria = condition.createCriteria();
        if (StringUtils.isNotBlank(username)) {
            criteria.andLike("username", "%" + username + "%");
        }
        if (roleId != null) {
            criteria.andEqualTo("roleId", roleId);
        }
        if (!isBrand) {
            criteria.andEqualTo("brandId", brandId);
        } else {
            criteria.andNotEqualTo("brandId", 0);
            if (brandId != 0 && brandId != null) {
                criteria.andEqualTo("brandId", brandId);
            }
        }
        condition.setOrderByClause(" gmt_modified desc");
        List<Admin> adminRoleList = super.findByCondition(condition);

        for (int i = 0, j = adminRoleList.size(); i < j; i++) {
            AdminRole adminRole = adminRoleMapper.selectByPrimaryKey(adminRoleList.get(i).getRoleId());
            if (adminRole != null) {
                adminRoleList.get(i).setRoleName(adminRole.getName());
            }
            CompanyBrand companyBrand = companyBrandMapper.selectByPrimaryKey(adminRoleList.get(i).getBrandId());
            if (companyBrand != null) {
                adminRoleList.get(i).setBrandName(companyBrand.getName());
            }
        }

        PageInfo pageInfo = new PageInfo(adminRoleList);

        return pageInfo;
    }

    @Override
    public Object updatePass(String username, String password, String oldPassword) {

        Condition condition = new Condition(Admin.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("username", username);
        List<Admin> byCondition = super.findByCondition(condition);
        if (byCondition == null || byCondition.size() == 0) {
            throw new ServiceException("该帐号不存在或已删除");
        }

        if(byCondition != null && byCondition.size() != 1) {
            throw new ServiceException("该帐号数据有误，请联系管理员处理");
        }


        Admin admin = byCondition.get(0);
        if (!admin.getHashedPwd().equalsIgnoreCase(DigestUtils.md5Hex(oldPassword))) {
            throw new ServiceException("原密码有误,请重新输入");
        }

        admin.setHashedPwd(DigestUtils.md5Hex(password));
        admin.setGmtModified(new Date());
        this.update(admin);

        return ResultGenerator.genSuccessResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object resetPass(Integer id) {

        Admin admin = this.findById(id);
        if (admin == null) {
            throw new ServiceException("帐号数据不存在或已删除");
        }
        admin.setGmtModified(new Date());
        admin.setHashedPwd(DigestUtils.md5Hex(BACK_DEFAULT_PASS));
        super.update(admin);

        try (Jedis jedis = jedisPool.getResource()) {
            Long zrem = jedis.zrem(BACK_LOGIN_BZ, admin.getUsername());
            if (zrem != null && zrem != 0) {
                System.out.println("redis 删除ok ");
            } else {
                System.out.println("redis 删除error ");
            }
        }

        return ResultGenerator.genSuccessResult();
    }
}
