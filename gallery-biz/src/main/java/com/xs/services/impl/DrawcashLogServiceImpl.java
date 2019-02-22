package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.DrawcashLog;
import com.xs.beans.Incomexpense;
import com.xs.beans.User;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.core.sservice.AbstractService;
import com.xs.daos.DrawcashLogMapper;
import com.xs.daos.IncomexpenseMapper;
import com.xs.daos.UserMapper;
import com.xs.services.DrawcashLogService;
import com.xs.services.UpLoadService;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xs.core.ProjectConstant.USER_DRAWCASHLOG;
import static com.xs.core.ProjectConstant.USER_DRAWCASHLOG_OK;


/**
\* User: zhaoxin
\* Date: 2018/10/22
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("drawcashlogService")
@Transactional
public class DrawcashLogServiceImpl extends AbstractService<DrawcashLog> implements DrawcashLogService {
    @Autowired
    private DrawcashLogMapper drawcashlogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private IncomexpenseMapper incomexpenseMapper;
    @Autowired
    private UpLoadService upLoadService;


    @Override
    public Object queryWithPage(int page, int size, String sTime, String eTime, Integer userId, String userName, String status, Boolean isExport) {
        PageHelper.startPage(page, size);
        DrawcashLog drawcashLog = new DrawcashLog();
        drawcashLog.setsTime(sTime);
        drawcashLog.seteTime(eTime);
        drawcashLog.setUserId(userId);
        drawcashLog.setNickname(userName);
        drawcashLog.setStatus(status);
        List<DrawcashLog> list = drawcashlogMapper
                .queryWithPage(drawcashLog);
        PageInfo pageInfo = new PageInfo(list);

        if (isExport) {
            File exportFile = null;
            try{
                Map<String,Object> model = new HashMap<>();
                model.put("users", list);
                exportFile = File.createTempFile("分益提现数据",".xlsx");
                JxlsExportUtil.exportExcel("static/template_file/drawcashlogs.xlsx","static/template_file/drawcashlogs.xml",exportFile,model);

//                OSSClient ossClient =OssUpLoadUtil.getOSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
//                try {
//                    ossClient.putObject(ossConfig.getBucket(), exportFile.getName(), new FileInputStream(exportFile));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                URL url = ossClient.generatePresignedUrl(ossConfig.getBucket(), exportFile.getName(),  new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//                return ResultGenerator.genSuccessResult(url.toString().replaceAll("http", "https"));
                return ResultGenerator.genSuccessResult(upLoadService.upFile(exportFile));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(exportFile != null) {
                    exportFile.delete();
                }
            }
        }

        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public Object auditor(Integer id, Boolean hasPass, String failMsg) {

        DrawcashLog drawcashLog = super.findById(id);
        if (drawcashLog == null) {
            return ResultGenerator.genFailResult("该提现记录不存在或已删除");
        }
        if (!drawcashLog.getStatus().equals("WAIT_PROCESS")) {
            return ResultGenerator.genFailResult("该提现记录状态有误,请联系管理员进行处理");
        }
        User user = userMapper.selectByPrimaryKey(drawcashLog.getUserId());
        if (user == null) {
            return ResultGenerator.genFailResult("该提现记录的申请者数据不存在或已删除");
        }


        if (hasPass) {
            if (user.getCashBalance().compareTo(drawcashLog.getDrawCash()) < 0) {
                return ResultGenerator.genFailResult("该提现记录申请者余额不足,请联系管理员进行处理");
            }
            user.setCashBalance(user.getCashBalance().subtract(drawcashLog.getDrawCash()));
            user.setGmtModified(new Date());

            //收入支付记录数据
            Incomexpense incomexpense = new Incomexpense();
            incomexpense.setUserId(user.getId());
            incomexpense.setType("WITHDRAW_CASH");
            incomexpense.setIncome(BigDecimal.ZERO);
            incomexpense.setExpense(drawcashLog.getDrawCash());
            incomexpense.setBalance(user.getCashBalance());
            incomexpense.setTradedate(new Date());
            incomexpense.setGmtCreate(new Date());
            incomexpense.setShareProfitId(0);
            incomexpense.setRemark(user.getNickname() + "申请提现{" + drawcashLog.getDrawCash() + "}");
            incomexpense.setSubType(new Byte("0"));
            incomexpense.setPaymentId(0);

            userMapper.updateByPrimaryKey(user);
            incomexpenseMapper.insert(incomexpense);
        } else {
            if (StringUtils.isEmpty(failMsg)) {
                return ResultGenerator.genFailResult("拒绝申请提现时,请填写拒绝理由");
            }
        }

        drawcashLog.setFailMsg(StringUtils.isEmpty(failMsg) ? StringUtils.EMPTY : failMsg);
        drawcashLog.setGmtModified(new Date());
        drawcashLog.setStatus(hasPass ? "FINISHED" : "FAIL");
        drawcashlogMapper.updateByPrimaryKey(drawcashLog);

        if (!hasPass) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set(String.format(USER_DRAWCASHLOG, user.getId()), "" + drawcashLog.getId());
            }
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set(String.format(USER_DRAWCASHLOG_OK, user.getId()), "" + drawcashLog.getId());
            }
        }

        return ResultGenerator.genSuccessResult();
    }
}
