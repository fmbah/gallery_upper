package com.xs.services.impl;

import com.xs.core.sservice.SWxMenuService;
import com.xs.services.WxMenuService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: Fmbah
 * @Date: 18-10-31 下午4:17
 * @Description:
 */
@Service
public class WxMenuServiceImpl implements WxMenuService {

    public final Logger logger = LoggerFactory.getLogger(WxMenuServiceImpl.class);
    @Autowired
    SWxMenuService sWxMenuService;

    @Override
    public String menuCreate(String json) {
        try {
            return sWxMenuService.menuCreate(json);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

}
