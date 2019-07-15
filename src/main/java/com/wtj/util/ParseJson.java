package com.wtj.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

/**
 * @author tingjun.wang
 * @date 2019/7/9 12:02下午
 */
public class ParseJson {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	@Test
	public void getURL(){
		String host = "https://data.wanplus.com" ;
		String method = "GET";
		String appKey = "ek9gj5ay3eh8sz2ha8wn4yi6ag9gs3pa&";
		String uriPre = "/data.php";

		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Event");
		param.put("m","eventDetail");
		param.put("appid","21");
		param.put("eid","819");
		param.put("token","e94ab4");
		param.put("ts",String.valueOf(Instant.now().getEpochSecond()));
		//param.put("page","1");


		//url编码
		String s = formatUrlParam(param, "utf-8", false);

		String uriUrlPre = "";
		try {
			uriUrlPre = URLEncoder.encode(uriPre,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.out.println("编码前的uri："+uriPre);
		System.out.println("编码后的uri："+uriUrlPre);

		//组装源串
		String scorceStr = method+"&"+uriUrlPre+"&"+s;
		System.out.println("加密的appkey："+appKey);
		System.out.println("加密前的原串："+scorceStr);
		String sig = this.genHMAC(scorceStr,appKey);
		System.out.println("加密后的sig："+sig);

		String uri = this.getUri(param);

		//拼接url
		System.out.println("最终拼接的url："+host+"/"+uriPre+"?"+uri+"&"+"sig="+sig);
		param.put("sig",sig);
		uri = this.getUri(param);
		String result = host+"/"+uriPre+"?"+uri;
		System.out.println("最终拼接的转码的url："+result);

		//return uri;

	}

	/**
	 * @param param 参数
	 * @param encode 编码
	 * @param isLower 是否小写
	 * @return
	 */
	public String formatUrlParam(Map<String, Object> param, String encode, boolean isLower) {
		String params = "";

		try {
			List<Map.Entry<String, Object>> itmes = new ArrayList<>(param.entrySet());

			//对所有传入的参数按照字段名从小到大排序
			//Collections.sort(items); 默认正序
			//可通过实现Comparator接口的compare方法来完成自定义排序
			Collections.sort(itmes, new Comparator<Map.Entry<String, Object>>() {
				@Override
				public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
					// TODO Auto-generated method stub
					return (o1.getKey().toString().compareTo(o2.getKey()));
				}
			});

			//构造URL 键值对的形式
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, Object> item : itmes) {
				if (item.getKey() != null && item.getKey() != "") {
					String key = item.getKey();
					Object val = item.getValue();
					if (isLower) {
						sb.append(key.toLowerCase() + "=" + val);
					} else {
						sb.append(key + "=" + val);
					}
					sb.append("&");
				}
			}
			String strUri = sb.toString();
			if (!strUri.isEmpty()) {
				strUri = strUri.substring(0, strUri.length() - 1);
			}
			//编码
			System.out.println("编码前的参数："+strUri);
			String encodeUri = URLEncoder.encode(strUri, encode);
			params = encodeUri.replaceAll("\\+", "%2B");
			System.out.println("编码的参数后："+encodeUri);

		} catch (Exception e) {
			return "报错";
		}
		return params;
	}


	/**
	 * 使用 HMAC-SHA1 签名方法对data进行签名
	 * @param data 被签名的字符串
	 * @param key    密钥
	 * @return
	加密后的字符串
	 */
	public String genHMAC(String data, String key) {
		byte[] result = null;
		try {
			//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
			//生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			//用给定密钥初始化 Mac 对象
			mac.init(signinKey);
			//完成 Mac 操作
			byte[] rawHmac = mac.doFinal(data.getBytes());
			result = Base64.encodeBase64(rawHmac);

		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		if (null != result) {
			return new String(result);
		} else {
			return null;
		}
	}

	//map转a=b&
	public String getUri(Map<String,Object> params){
		StringBuilder sb = new StringBuilder("");
		if(params != null){
			try {
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					URLCodec codec = new URLCodec();
					sb.append(codec.encode(entry.getKey(),"UTF-8")).append("=")
							.append(codec.encode(entry.getValue().toString(),"UTF-8")).append("&");
				}
				sb.deleteCharAt(sb.lastIndexOf("&"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
