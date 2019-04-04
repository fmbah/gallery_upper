package com.xs.utils;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
//import sun.misc.BASE64Encoder;

/**
 \* User: zhaoxin
 \* Date: 2018/6/7
 \* Time: 17:54
 \* Description: 生成摘要工具类
 \*/
public class DigestUtil implements Serializable {

	private final static String DES = "DES";
	public final static String key = "fmbah!@#$%";

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
	public static void main(String[] args) throws Exception {
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("POLICYNO","1387");
//		jsonObject.put("ORDERCOUNT", "1625");
//		jsonObject.put("SUMAMOUNT","2016-11-01 14:48:45");
//		jsonObject.put("SUMPREMIUM","2658");
//		jsonObject.put("STARTDATE","中通十二位主面单");
//		String params =jsonObject.toString();
//		String digest = "";
//		try {
//			//生成签名
//			digest = DigestUtil.digest(params, "zto123456", "utf-8");
//			System.out.println("签名："+digest);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//
//		String data = "jdbc:mysql://10.220.110.166:3306/gallery?useSSL=true&useUnicode=true&characterEncoding=utf-8";
//		String encrypt = encrypt(data, key);
//		System.out.println("des 加密:" + encrypt);
//		System.out.println("des 解密:" + decrypt(encrypt, key));

		String data = "jdbc:mysql://10.220.120.111:3306/gallery?useSSL=false&useUnicode=true&characterEncoding=utf-8";
		String encoded = encrypt(data);
		System.out.println("加密:" + encoded);
		String decoded = decrypt("cUwRBuLxs3ffp3DGsFaw4A");
		System.out.println("解密:" + decoded);
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


	/**
	 * Description 根据键值进行加密
	 * @param data
	 * @param key  加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		String strs = new BASE64Encoder().encode(bt);
		return strs;
	}

	/**
	 * Description 根据键值进行解密
	 * @param data
	 * @param key  加密键byte数组
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String decrypt(String data, String key) throws IOException,
			Exception {
		if (data == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] buf = decoder.decodeBuffer(data);
		byte[] bt = decrypt(buf,key.getBytes());
		return new String(bt);
	}

	/**
	 * Description 根据键值进行加密
	 * @param data
	 * @param key  加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}


	/**
	 * Description 根据键值进行解密
	 * @param data
	 * @param key  加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}



	private static org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64(true);

	//encrypt using blowfish algorithm
	public static String encrypt(String Data)throws Exception{

		SecretKeySpec key = new SecretKeySpec(DigestUtil.key.getBytes("UTF8"), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return base64.encodeToString(cipher.doFinal(Data.getBytes("UTF8")));

	}

	//decrypt using blow fish algorithm
	public static String decrypt(String encrypted)throws Exception{
		byte[] encryptedData = base64.decodeBase64(encrypted);
		SecretKeySpec key = new SecretKeySpec(DigestUtil.key.getBytes("UTF8"), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encryptedData);
		return new String(decrypted);

	}
}
