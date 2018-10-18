package com.xs.services.timers;

import com.xs.beans.TemplateStatistics;
import com.xs.daos.TemplateStatisticsMapper;
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

//    @Scheduled(fixedDelay = 100)
    @Scheduled(cron = "*/5 * * * * ?")
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
                    String shareCount = String.format(TEMPLATE_SHARE, templateId, categoryId, brandId);
                    String usedCount = String.format(TEMPLATE_USED, templateId, categoryId, brandId);

                    TemplateStatistics templateStatistics = new TemplateStatistics();
                    templateStatistics.setBrandId(Integer.valueOf(templateId));
                    templateStatistics.setCategoryId(Integer.valueOf(categoryId));
                    templateStatistics.setBrandId(Integer.valueOf(brandId));
                    templateStatistics.setGmtCreate(new Date());
                    templateStatistics.setVisitorCount(Integer.valueOf(visitorCount));
                    templateStatistics.setShareCount(Integer.valueOf(shareCount));
                    templateStatistics.setUsedCount(Integer.valueOf(usedCount));
                    templateStatisticsMapper.insert(templateStatistics);
                }
            }
        }

    }
}
