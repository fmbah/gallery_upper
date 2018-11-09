package com.xs;

import com.xs.core.shandler.BackHandler;
import com.xs.core.shandler.WxHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static com.xs.core.ProjectConstant.INTERCEPT_BACK_URL;
import static com.xs.core.ProjectConstant.INTERCEPT_WX_URL;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:27
 * @Description:
 */
@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter {

    @Value("${spring.profiles.active}")
    private String env;//当前激活的配置文件

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        if (!"dev".equals(env)) { //开发环境忽略签名认证
        registry.addInterceptor(new WxHandler()).addPathPatterns(INTERCEPT_WX_URL);
            registry.addInterceptor(new BackHandler()).addPathPatterns(INTERCEPT_BACK_URL);
        }
        super.addInterceptors(registry);

    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(Application.class);
    }
}

