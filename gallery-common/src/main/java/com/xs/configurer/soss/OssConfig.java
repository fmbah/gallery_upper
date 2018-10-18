package com.xs.configurer.soss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * \* 杭州桃子网络科技股份有限公司
 * \* User: wht
 * \* Date: 18/5/16
 * \* Time: 下午2:47
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Component
@ConfigurationProperties(prefix =OssConfig.OssPrefix)
public class OssConfig {

    public static final String OssPrefix="aliyun.oss";

    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String bucket;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

}
