package com.xs.core.shandler;

import com.xs.core.ResponseBean;
import com.xs.core.ResultGenerator;
import com.xs.utils.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * \* 杭州桃子网络科技股份有限公司
 * \* User: apple
 * \* Date: 18/5/18
 * \* Time: 上午11:34
 * \* To change this template use File | Settings | File Templates.
 * \* Description:统一异常处理
 * \
 */

@RestControllerAdvice
public class ExceptionHandle {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /***
     * 拦截所有异常信息
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseBean handle(Exception exception, HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        logger.error("=====================================================logger异常开始=====================================================================");
        logger.error("异常发生时间:{}" ,sdf.format(new Date()));
        logger.error("请求设备信息:{}" , request.getHeader("User-Agent"));
        logger.error("请求ip地址:{}" ,RequestUtil.getIpAddr(request));
        logger.error("请求url:{}" , request.getQueryString() == null ? requestURL + "" : (requestURL + "?" + request.getQueryString()));
        logger.error("请求参数:{}",RequestUtil.getResuestParams(request));
        logger.error("异常详细信息:{}", exception);
        logger.error("=====================================================logger异常结束=====================================================================");
        return ResultGenerator.exceptionResult(exception.getMessage());
    }
}
