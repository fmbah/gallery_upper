package com.xs.services.timers;

import com.xs.beans.TemplateStatistics;
import com.xs.daos.TemplateStatisticsMapper;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Set;

import static com.xs.core.ProjectConstant.*;

/**
 * @ClassName OneHourWriteRedisDataSimpleJob0
 * @Description
 * @Author root
 * @Date 18-10-18 下午2:28
 * @Version 1.0
 **/
@Service
@EnableScheduling
public class OneHourWriteRedisDataSimpleJob0 {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private TemplateStatisticsMapper templateStatisticsMapper;

    @Scheduled(cron = "* 30 * * * ?")
    public void runSchedule() {

        logger.info("OneHourWriteRedisDataSimpleJob0......");
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> visitors = jedis.keys(String.format(TEMPLATE_VISITOR, "*", "*", "*"));
            if (visitors != null) {
                for (String visitor : visitors) {
                    String templateId = visitor.split(":")[2];
                    String categoryId = visitor.split(":")[4];
                    String brandId = visitor.split(":")[6];

                    String visitorCount = jedis.get(visitor);

                    boolean flag = true;
                    if (StringUtils.isEmpty(templateId) || StringUtils.isEmpty(categoryId)) {
                        flag = false;
                    }

                    if (!StringUtils.isEmpty(visitorCount) && flag) {
                        TemplateStatistics templateStatistics = new TemplateStatistics();
                        templateStatistics.setTemplateId(Integer.valueOf(templateId));
                        templateStatistics.setCategoryId(Integer.valueOf(categoryId));
                        templateStatistics.setBrandId(Integer.valueOf(StringUtils.isEmpty(brandId) ? "0" : brandId));
                        templateStatistics.setGmtCreate(new Date());
                        templateStatistics.setVisitorCount(Integer.valueOf(visitorCount));
                        templateStatistics.setShareCount(0);
                        templateStatistics.setUsedCount(0);
                        templateStatisticsMapper.insert(templateStatistics);
                    }

                    jedis.del(visitor);
                }
            }

            Set<String> shares = jedis.keys(String.format(TEMPLATE_SHARE, "*", "*", "*"));
            if (shares != null) {
                for (String share : shares) {
                    String templateId = share.split(":")[2];
                    String categoryId = share.split(":")[4];
                    String brandId = share.split(":")[6];

                    String shareCount = jedis.get(share);

                    boolean flag = true;
                    if (StringUtils.isEmpty(templateId) || StringUtils.isEmpty(categoryId)) {
                        flag = false;
                    }

                    if (!StringUtils.isEmpty(shareCount) && flag) {
                        TemplateStatistics templateStatistics = new TemplateStatistics();
                        templateStatistics.setTemplateId(Integer.valueOf(templateId));
                        templateStatistics.setCategoryId(Integer.valueOf(categoryId));
                        templateStatistics.setBrandId(Integer.valueOf(StringUtils.isEmpty(brandId) ? "0" : brandId));
                        templateStatistics.setGmtCreate(new Date());
                        templateStatistics.setVisitorCount(0);
                        templateStatistics.setShareCount(Integer.valueOf(shareCount));
                        templateStatistics.setUsedCount(0);
                        templateStatisticsMapper.insert(templateStatistics);
                    }

                    jedis.del(share);
                }
            }

            Set<String> useds = jedis.keys(String.format(TEMPLATE_USED, "*", "*", "*"));
            if (useds != null) {
                for (String used : useds) {
                    String templateId = used.split(":")[2];
                    String categoryId = used.split(":")[4];
                    String brandId = used.split(":")[6];

                    String usedCount = jedis.get(used);

                    boolean flag = true;
                    if (StringUtils.isEmpty(templateId) || StringUtils.isEmpty(categoryId)) {
                        flag = false;
                    }

                    if (!StringUtils.isEmpty(usedCount) && flag) {
                        TemplateStatistics templateStatistics = new TemplateStatistics();
                        templateStatistics.setTemplateId(Integer.valueOf(templateId));
                        templateStatistics.setCategoryId(Integer.valueOf(categoryId));
                        templateStatistics.setBrandId(Integer.valueOf(StringUtils.isEmpty(brandId) ? "0" : brandId));
                        templateStatistics.setGmtCreate(new Date());
                        templateStatistics.setVisitorCount(0);
                        templateStatistics.setShareCount(0);
                        templateStatistics.setUsedCount(Integer.valueOf(usedCount));
                        templateStatisticsMapper.insert(templateStatistics);
                    }

                    jedis.del(used);
                }
            }

        }

    }
}
