package com.xs.core;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:26
 * @Description: 项目常量
 */
public final class ProjectConstant {
    public static final String BASE_PACKAGE = "com.xs";//基础扫描包

    public static final String MODEL_PACKAGE = BASE_PACKAGE + ".beans";//生成的Model所在包
    public static final String MAPPER_PACKAGE = BASE_PACKAGE + ".daos";//生成的Mapper所在包
    public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".services";//生成的Service所在包
    public static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";//生成的ServiceImpl所在包
    public static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controllers";//生成的Controller所在包
    public static final String MAPPER_INTERFACE_REFERENCE = BASE_PACKAGE + ".core.smapper.SMapper";//Mapper插件基础接口的完全限定名


    public static final String WEB_BACK_DOMAIN = "https://scottwdr.4kb.cn";//后台域名

    //阿里配置
    public static final String ALIYUN_OSS_IMG_ADDRESS="https://daily-test.oss-cn-hangzhou.aliyuncs.com/";


    //后台配置
    public static final String BACK_MANAGER_KEY = "managerKey";
    public static final String BACK_MANAGER_MENUID = "MENUID";
    public static final String BACK_DEFAULT_PASS = "123456";
    public static final String BACK_LOGIN_BZ = "ACTIVE_USERS";
    public static final String INTERCEPT_BACK_URL = "/api/back/**";//后台管理系统拦截路径
    public static final String INTERCEPT_WX_URL = "/api/wx/app/**";//小程序微信拦截路径

    //redis配置
    public static final String COMPANY_BRAND_CDK = "companyBrandCdk";
    public static final String TEMPLATE_VISITOR = "templateVisitor:templateId:%s:categoryId:%s:brandId:%s";//计数器存储
    public static final String TEMPLATE_SHARE = "templateShare:templateId:%s:categoryId:%s:brandId:%s";
    public static final String TEMPLATE_USED = "templateUsed:templateId:%s:categoryId:%s:brandId:%s";
    public static final String USER_TEMPLATE_COLLECTIONS = "userTemplateCollection:userId:%s";
    public static final String USER_DRAWCASHLOG = "userDrawcashlog:userId:%s";
    public static final String WX_USER_TOKEN = "wxUserToken:userId:%s";
    public static final String WX_USER_FONT_TOKEN = "TOKEN";
    public static final String WX_MSG = "wxMsg";



}
