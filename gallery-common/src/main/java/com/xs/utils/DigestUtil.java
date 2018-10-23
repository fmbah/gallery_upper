package com.xs.utils;

import net.sf.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Random;
//import sun.misc.BASE64Encoder;

/**
 \* 杭州桃子网络科技股份有限公司
 \* User: zhaoxin
 \* Date: 2018/6/7
 \* Time: 17:54
 \* Description: 生成摘要工具类
 \*/
public class DigestUtil implements Serializable {

	/**
	 * MD5
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	/**
	 * base64
	 * 
	 * @param md5
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] md5) throws Exception {
//		return (new BASE64Encoder()).encodeBuffer(md5);
		return Base64.encode(md5);
	}

	/**
	 * 摘要生成
	 * 
	 * @param data
	 *            请求数据
	 * @param sign
	 *            签名秘钥(key或者parternID)
	 * @param charset
	 *            编码格式
	 * @return 摘要
	 * @throws Exception
	 */
	public static String digest(String data, String sign, String charset)
			throws Exception {
		String t = encryptBASE64(encryptMD5((data + sign).getBytes(charset)));
		if (System.getProperty("line.separator").equals("\n")) {
			String t2 = t.replaceAll("\\n", "\r\n");
			return t2;
		} else {
			return t;
		}
	}
	
	/**  
	 * 测试方法
	 */  
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("POLICYNO","1387");
		jsonObject.put("ORDERCOUNT", "1625");
		jsonObject.put("SUMAMOUNT","2016-11-01 14:48:45");
		jsonObject.put("SUMPREMIUM","2658");
		jsonObject.put("STARTDATE","中通十二位主面单");
		String params =jsonObject.toString();
		String digest = "";
		try {
			//生成签名
			digest = DigestUtil.digest(params, "zto123456", "utf-8");
			System.out.println("签名："+digest);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/** 
	* @Description: 获取一定长度的随机字符串 
	* @Param: length：长度
	 * 		   isNum：是否是纯数字（true：是  false：否）
	* @return:
	* @Author: zhaoxin
	* @Date: 2018/6/13 
	**/ 
	public static String getRandomStringByLength(boolean isNum, int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; ) {
			int number = random.nextInt(base.length());
			char charAt = base.charAt(number);
			if(isNum) {
				if(charAt < 48 || charAt > 57) {
					continue;
				}
			}
			sb.append(charAt);
			i++;
		}
		return sb.toString();
	}


}
