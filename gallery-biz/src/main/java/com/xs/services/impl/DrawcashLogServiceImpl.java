package com.xs.services.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.ServiceException;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.beans.Admin;
import com.xs.beans.DrawcashLog;
import com.xs.beans.Incomexpense;
import com.xs.beans.User;
import com.xs.configurer.soss.OssConfig;
import com.xs.core.ResultGenerator;
import com.xs.core.sservice.AbstractService;
import com.xs.core.sservice.SWxPayService;
import com.xs.daos.DrawcashLogMapper;
import com.xs.daos.IncomexpenseMapper;
import com.xs.daos.UserMapper;
import com.xs.services.AdminService;
import com.xs.services.DrawcashLogService;
import com.xs.services.UpLoadService;
import com.xs.utils.GenerateOrderno;
import com.xs.utils.IpUtils;
import com.xs.utils.JxlsExportUtil;
import com.xs.utils.OssUpLoadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xs.core.ProjectConstant.*;


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
	@Resource(name="wxPayService")
	private WxPayService wxService;
	@Value("${wechat.mp.appId}")
	private String mpappId;
	@Value("${wechat.pay.mchId}")
	private String mchId;
	@Value("${wechat.pay.mchKey}")
	private String mchKey;
	@Value("${spring.profiles.active}")
	private String env;
	@Autowired
	private AdminService adminService;


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
	@Transactional(rollbackFor = ServiceException.class)
	public Object auditor(HttpServletRequest request, Integer adminId, Integer id, Boolean hasPass, String failMsg) {

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
		if (StringUtils.isEmpty(user.getWxOpenid())) {
			return ResultGenerator.genFailResult("该提现记录的申请者数据无OPENID");
		}

		String partnerTradeNo = null;
		String paymentNo = null;
		String paymentTime = null;
		if (hasPass) {
			if (user.getCashBalance().compareTo(drawcashLog.getDrawCash()) < 0) {
				return ResultGenerator.genFailResult("该提现记录申请者余额不足,请联系管理员进行处理");
			}

			EntPayRequest entPayRequest = new EntPayRequest();
			entPayRequest.setAppid(mpappId);
			entPayRequest.setMchId(mchId);
			// 保证一笔提现对应一个商户订单号，后续如果失败的情况，使用原商户订单号调起付款接口，避免重复支付
			String orderno = null;
			try (Jedis jedis = jedisPool.getResource()){
				String cacheOrderno = jedis.hget(DRAWCASH_LOG_MAP, DRAWCASH_LOG + drawcashLog.getId());
				if (cacheOrderno == null || StringUtils.EMPTY.equals(cacheOrderno)) {
					cacheOrderno = GenerateOrderno.get();
					jedis.hset(DRAWCASH_LOG_MAP, DRAWCASH_LOG + drawcashLog.getId(), orderno);
				}
				orderno = cacheOrderno;
			}
			entPayRequest.setPartnerTradeNo(orderno);

			entPayRequest.setOpenid(user.getWxOpenid());
			entPayRequest.setCheckName("NO_CHECK");
			entPayRequest.setAmount(!"dev".equals(env) ? drawcashLog.getDrawCash().multiply(new BigDecimal("100")).intValue() : 100);
			Admin admin = adminService.findById(adminId);
			if (admin == null) {
				return ResultGenerator.genFailResult("当前操作者数据不存在或已删除");
			}
			String remark = user.getNickname() + "申请提现{" + drawcashLog.getDrawCash() + "}, 当前操作者：【" + admin.getUsername() + "】";
			entPayRequest.setDescription(remark);
			entPayRequest.setSpbillCreateIp(IpUtils.getIpAddr(request));


			try {
				EntPayResult payResult = this.wxService.getEntPayService().entPay(entPayRequest);
				if (payResult == null) {
					return ResultGenerator.genFailResult("申请微信企业付款接口返回值为空");
				}

				String returnCode = payResult.getReturnCode();
				if (!"SUCCESS".equals(returnCode)) {
					return ResultGenerator.genFailResult("企业付款交易通讯处于非正常状态, 原因： " + payResult.getReturnMsg());
				}
				// 交易通讯正常
				String resultCode = payResult.getResultCode();
				// 交易失败
				if (!"SUCCESS".equals(resultCode)) {
					return ResultGenerator.genFailResult("企业付款交易失败, 原因： " + payResult.getReturnMsg());
				}
				// 交易成功
				partnerTradeNo = payResult.getPartnerTradeNo();
				paymentNo = payResult.getPaymentNo();
				paymentTime = payResult.getPaymentTime();

			} catch (WxPayException e) {
				e.printStackTrace();
				return ResultGenerator.genFailResult("申请微信企业付款接口异常：" + e.getMessage());
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
			incomexpense.setRemark(remark);
			incomexpense.setSubType(new Byte("0"));
			incomexpense.setPaymentId(0);

			userMapper.updateByPrimaryKey(user);
			incomexpenseMapper.insert(incomexpense);
		} else {
			if (StringUtils.isEmpty(failMsg)) {
				return ResultGenerator.genFailResult("拒绝申请提现时,请填写拒绝理由");
			}
		}

		drawcashLog.setPartnerTradeNo(partnerTradeNo);
		drawcashLog.setPaymentNo(paymentNo);
		drawcashLog.setPaymentTime(paymentTime);
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
