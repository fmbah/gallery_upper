package com.xs.services.timers;

import com.xs.beans.User;
import com.xs.daos.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: Fmbah
 * @Date: 18-10-25 上午9:13
 * @Description: 检查用户状态是否已经过期,可能会延迟,但是只是展示上有问题,不会影响功能
 */
@Service
@EnableScheduling
public class OneDayWriteUserMemberTypeSimpleJob0 {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 29 * * * ?")
    public void runSchedule1() {

        logger.info("OneDayWriteUserMemberTypeSimpleJob0......");

        List<User> users = userMapper.selectAll();
        if (users != null && !users.isEmpty()) {
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.YEAR, 1970);
            instance.set(Calendar.MONTH, 0);
            instance.set(Calendar.DATE, 1);
            instance.set(Calendar.HOUR, 0);
            instance.set(Calendar.MINUTE, 0);
            instance.set(Calendar.SECOND, 0);
            Date instanceTime = instance.getTime();
            Date now = new Date();
            for (User user : users) {
                if (user.getMemberType().byteValue() != 0 && user.getMemberExpired().before(now)) {
                    user.setMemberType(new Byte("0"));
                    user.setGmtModified(now);
                    user.setMemberExpired(instanceTime);
                    logger.warn("OneDayWriteUserMemberTypeSimpleJob0: 用户会员身份已经过期,重置用户身份! 用户id[{}]", user.getId());
                    userMapper.updateByPrimaryKey(user);
                }
            }
        }

    }
}
