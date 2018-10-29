package com.xs.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xs.beans.Admin;
import com.xs.beans.AdminMenu;
import com.xs.beans.AdminRoleMenu;
import com.xs.core.ResponseBean;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.AdminMenuService;
import com.xs.services.AdminRoleMenuService;
import com.xs.services.AdminService;
import io.swagger.annotations.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.xs.core.ProjectConstant.BACK_LOGIN_BZ;
import static com.xs.core.ProjectConstant.BACK_MANAGER_KEY;

/**
 * @ClassName HiController
 * @Description
 * @Author root
 * @Date 18-10-12 下午2:35
 * @Version 1.0
 **/
@Api(description = "后台登录管理")
@RestController
@RequestMapping(value = "/back/manager")
public class LoginController extends BaseController {

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminRoleMenuService adminRoleMenuService;
    @Autowired
    private AdminMenuService adminMenuService;

    @GetMapping("/")
    public Object hi() {
        return "hi";
    }


    @ApiOperation(value = "后台应用获取验证码",notes = "后台应用获取验证码")
    @GetMapping(value = "/code",produces = "application/json;charset=utf-8")
    public Object code() {
        String code = RandomStringUtils.randomAlphanumeric(4);
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(code, code);
            jedis.expire(code, 60);
        }
        return ResultGenerator.genSuccessResult(code);
    }

    @ApiOperation(value = "后台管理系统登录", notes = "后台管理系统登录")
    @PostMapping(value = "/login/{code}/{username}/{password}",produces = "application/json;charset=utf-8")
    public Object login(@ApiParam(value = "验证码",name="code",required = true) @PathVariable String code,
                        @ApiParam(value = "帐号",name="username",required = true) @PathVariable String username,
                        @ApiParam(value = "密码",name="password",required = true) @PathVariable String password) {

        try(Jedis jedis = jedisPool.getResource()) {
            String codeStr = jedis.get(code);
            if (StringUtils.isEmpty(codeStr)) {
                return ResultGenerator.genFailResult("验证码有误或验证码失效");
            }

            Condition condition = new Condition(Admin.class);
            Example.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("username", username);
            List<Admin> byCondition = adminService.findByCondition(condition);
            if (byCondition != null && byCondition.size() == 1) {
                Admin admin = byCondition.get(0);
                if (admin.getHashedPwd().equalsIgnoreCase(DigestUtils.md5Hex(password))) {
//                    Cookie[] cookies = request.getCookies();
//                    boolean isHas = true;
//                    Cookie targetCookie = null;
//                    if (cookies != null && cookies.length > 0) {
//                        for (Cookie cookie : cookies) {
//                            if (cookie.getName().equals(BACK_MANAGER_KEY)) {
//                                isHas = false;
//                                targetCookie = cookie;
//                                break;
//                            }
//                        }
//                    }

//                    if (isHas) {
                        Condition armCondition = new Condition(AdminRoleMenu.class);
                        Example.Criteria armConditionCriteria = armCondition.createCriteria();
                        armConditionCriteria.andEqualTo("roleId", admin.getRoleId());
                        List<AdminRoleMenu> adminRoleMenus = adminRoleMenuService.findByCondition(armCondition);
                        if (adminRoleMenus == null || adminRoleMenus.size() == 0) {
                            return ResultGenerator.genFailResult("当前帐号无权限登录运维平台,请联系管理员处理");
                        }
                        String menuIds = "";
                        for (AdminRoleMenu adminRoleMenu : adminRoleMenus) {
                            AdminMenu adminMenu = adminMenuService.findById(adminRoleMenu.getMenuId().intValue());
                            if (adminMenu != null) {
                                menuIds = menuIds + "," + adminRoleMenu.getMenuId();
                                while (adminMenu != null && adminMenu.getpId() != 0) {
                                    adminMenu = adminMenuService.findById(adminMenu.getpId().intValue());
                                    if (adminMenu != null) {
                                        menuIds = menuIds + "," + adminMenu.getId();
                                    }
                                }
                            }
                        }

                        String encodeMenuId = URLEncoder.encode(menuIds.substring(1), "utf-8");
                        Cookie cookie = new Cookie(BACK_MANAGER_KEY, username + "#" + encodeMenuId);
                        cookie.setMaxAge(60000);
                        cookie.setPath("/");
                        this.response.addCookie(cookie);
                        System.out.println("cookies 设置ok ");

                        Long zadd = jedis.zadd(BACK_LOGIN_BZ, System.currentTimeMillis(), username);
                        if (zadd != null && zadd != 0) {
                            System.out.println("redis 设置ok ");
                        } else {
                            System.out.println("redis 设置error ");
                        }

//                    } else {
//                        System.out.println(targetCookie.getValue());
//                    }

                    HashMap result = new HashMap();
                    result.put("user", admin);
                    Object menus = adminMenuService.getMenusByRoleId(admin.getRoleId());
                    if (menus != null) {
                        Gson gson = new Gson();
                        ResponseBean responseBean = gson.fromJson(gson.toJson(menus), new TypeToken<ResponseBean>() {
                        }.getType());
                        result.put("menus", responseBean.getData());
                    } else {
                        result.put("menus", null);
                    }

                    return ResultGenerator.genSuccessResult(result);
                } else {
                    return ResultGenerator.genFailResult("帐号或密码不正确，请重新输入或联系管理员处理");
                }
            } else {
                return ResultGenerator.genFailResult("当前帐号有误，请重新输入或联系管理员处理");
            }
        } catch (UnsupportedEncodingException e) {
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    @PostMapping(value = "/loginOut/{username}",produces = "application/json;charset=utf-8")
    public Object loginOut(@PathVariable String username) {

        try (Jedis jedis = jedisPool.getResource()) {

            Long zrem = jedis.zrem(BACK_LOGIN_BZ, username);
            if (zrem != null && zrem != 0) {
                System.out.println("redis 删除ok ");
            } else {
                System.out.println("redis 删除error ");
            }

            Cookie[] cookies = request.getCookies();
            boolean isHas = false;
            Cookie targetCookie = null;
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(BACK_MANAGER_KEY)) {
                        isHas = true;
                        targetCookie = cookie;
                        break;
                    }
                }
            }

            if (isHas) {
                targetCookie.setMaxAge(0);
                targetCookie.setValue(null);
                targetCookie.setPath("/");
                this.response.addCookie(targetCookie);
            }
        }

        return ResultGenerator.genSuccessResult();
    }


}
