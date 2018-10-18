package com.xs.core.scontroller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:27
 * @Description: 
 */
public class BaseController {

    public final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

}
