package com.xs.configurer.sencryptions;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.xs.utils.DigestUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName EncryptionPropertyConfig
 * @Description
 * @Author root
 * @Date 18-11-3 下午7:14
 * @Version 1.0
 **/
@Configuration
public class EncryptionPropertyConfig {

    @Bean(name="encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver() {
        return new EncryptionPropertyResolver();
    }

    class EncryptionPropertyResolver implements EncryptablePropertyResolver {

        @Override
        public String resolvePropertyValue(String value) {
            if(StringUtils.isBlank(value)) {
                return value;
            }
            // 值以DES@开头的均为DES加密,需要解密
            if(value.startsWith("DES@")) {
                return resolveDESValue(value.substring(4));
            }
            // 不需要解密的值直接返回
            return value;
        }

        private String resolveDESValue(String value) {
            // 自定义DES密文解密
            try {
                String decrypt = DigestUtil.decrypt(value);
                return decrypt;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
