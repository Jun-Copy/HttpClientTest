package com.wtj.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * @author tingjun.wang
 * @date 2019/7/9 12:01下午
 */
public abstract class HttpClientUtil {

	String host = "https://data.wanplus.com" ;
	String reqMethod = "GET";
	String appKey = "ek9gj5ay3eh8sz2ha8wn4yi6ag9gs3pa&";
	String uriPre = "/data.php";

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	protected final Logger logger = LoggerFactory.getLogger("getDataLog");

	private HttpClient client;

	protected HttpClientUtil() {
		HttpClientParams clientParams = new HttpClientParams();
		clientParams.setParameter(HttpClientParams.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0");
		//响应超时时间2分钟
		clientParams.setSoTimeout(120000);
		//Http连接超时15秒
		clientParams.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 15000);
		clientParams.setConnectionManagerTimeout(15000);
		client = new HttpClient(clientParams);
	}

	public void getDataFromLeiData(DataFetchContent context) {
		//记录数据抓取的开始时间
		context.setStartFetchTime(Calendar.getInstance());
		//将上次抓取的内容置为空
		context.setPageContent("");
		//Get、POST方法,并添加参数
		HttpMethod method = this.createHttpRequestMethod(context.getParams());
		//设置请求头
		method.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		//设置内容编码方式
		//method.setRequestHeader("Accept-Encoding", "deflate");
		method.setRequestHeader("Connection", "Keep-Alive");
		//method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0");
		if (!StringUtils.isBlank(context.getPageLastModified())) {
			method.setRequestHeader("If-Modified-Since", context.getPageLastModified());
		}
		//设置在网络异常时重复三次去请求
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		String logUrlMsg = "";
		try {
			logUrlMsg = getRequestURI()+"?"+method.getQueryString();
			System.out.println("httpClient请求的uri："+logUrlMsg);
			int statusCode = client.executeMethod(method);
			//记录数据抓取结束时间
			context.setEndFetchTime(Calendar.getInstance());
			//Long responseTimeLong = DateUtil.compareTimeInMillis(context.getEndFetchTime(), context.getStartFetchTime());
			/*if(responseTimeLong > 10000){
				logger.info("[{}]抓取所用的毫秒数超过10000，实际所用时间：{}",logUrlMsg,responseTimeLong);
			}*/
			//304
			/*if(!isIgnoreNotmodify() && statusCode == HttpStatus.SC_NOT_MODIFIED) {
				logger.info("[{}]抓取[{}]时,请求内容无修改,响应码:{}", new Object[]{logUrlMsg,this.getDataType(), statusCode});
				return;
			}
			if (statusCode != HttpStatus.SC_OK) {
				logger.warn("[{}]抓取[{}]时,http响应异常,响应码:{}", new Object[]{logUrlMsg,this.getDataType(), statusCode});
				return;
			}*/
			if(null!=method.getResponseHeader("Content-Length")) {
				context.setPageLenght(NumberUtils.toLong(method.getResponseHeader("Content-Length").getValue()));
			}

			if(method.getResponseHeader("Last-Modified") != null) {
				context.setPageLastModified(method.getResponseHeader("Last-Modified").getValue());
			}
			System.out.println(method.getResponseBodyAsStream());
			System.out.println(this.getChartset());
			String result = IOUtils.toString(method.getResponseBodyAsStream(), this.getChartset());
			System.out.println("最初结果："+result);
			context.setPageContent(unicodeToCn(result.getBytes()));
			//context.setPageContent(IOUtils.toString(method.getResponseBodyAsStream(), this.getChartset()));
			if(isRecordLog()) {
				logger.info("[{}]成功抓取[{}]数据.", logUrlMsg);
			}
			return;

		} catch (HttpException e) {
			//logger.error("[{}]抓取[{}]时发生httpException,mess:{}", new Object[]{logUrlMsg,this.getDataType(), e.getMessage()});
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			//记录数据抓取超时的时间
			context.setEndFetchTime(Calendar.getInstance());
			//Long responseTimeLong = DateUtil.compareTimeInMillis(context.getEndFetchTime(), context.getStartFetchTime());
			//logger.error("[{}]抓取[{}]时发生IOException,所用时间：{}，mess:{}", new Object[]{logUrlMsg, this.getDataType(), responseTimeLong, e.getMessage()});
			logger.error(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * <p>
	 * 创建一个符合httpclient格式的httpMethod
	 * </p>
	 * <p>
	 * 父类返回GET请求方法，若需要POST或其他，由字类实现
	 * </p>
	 * @throws Exception
	 */
	public HttpMethod createHttpRequestMethod(Map<String, Object> params) {
		GetMethod method = new GetMethod(getRequestURI());
		if(params != null){
			//StringBuilder sb = new StringBuilder("");

			//url编码
			String s = formatUrlParam(params, "utf-8", false);

			String uriUrlPre = "";
			try {
				uriUrlPre = URLEncoder.encode(uriPre,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			System.out.println("编码前的uri："+uriPre);
			System.out.println("编码后的uri："+uriUrlPre);

			//组装源串
			String scorceStr = reqMethod+"&"+uriUrlPre+"&"+s;
			System.out.println("加密的appkey："+appKey);
			System.out.println("加密前的原串："+scorceStr);
			String sig = this.genHMAC(scorceStr,appKey);
			System.out.println("加密后的sig："+sig);

			String uri = this.getUri(params);
			params.put("sig",sig);
			uri = this.getUri(params);
			String result = host+"/"+uriPre+"?"+uri;
			System.out.println("最终拼接的转码的url："+result);
			method.setQueryString(uri);
		}
		return method;

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

	/**
	 * Unicode转中文
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String unicodeToCn(byte[] str) throws UnsupportedEncodingException {
		String s=new String(str,"UTF-8");
		char aChar;
		int len= s.length();
		StringBuffer outBuffer=new StringBuffer(len);
		for(int x =0; x <len;){
			aChar= s.charAt(x++);
			if(aChar=='\\'){
				aChar= s.charAt(x++);
				if(aChar=='u'){
					int value =0;
					for(int i=0;i<4;i++){
						aChar= s.charAt(x++);
						switch(aChar){
							case'0':
							case'1':
							case'2':
							case'3':
							case'4':
							case'5':
							case'6':
							case'7':
							case'8':
							case'9':
								value=(value <<4)+aChar-'0';
								break;
							case'a':
							case'b':
							case'c':
							case'd':
							case'e':
							case'f':
								value=(value <<4)+10+aChar-'a';
								break;
							case'A':
							case'B':
							case'C':
							case'D':
							case'E':
							case'F':
								value=(value <<4)+10+aChar-'A';
								break;
							default:
								throw new IllegalArgumentException(
										"Malformed   \\uxxxx  encoding.");
						}
					}
					outBuffer.append((char) value);
				}else{
					if(aChar=='t')
						aChar='\t';
					else if(aChar=='r')
						aChar='\r';
					else if(aChar=='n')
						aChar='\n';
					else if(aChar=='f')
						aChar='\f';
					outBuffer.append("\\"+aChar);
				}
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	/**
	 * 获得json数据，如果是首次请求，会设置分页bean
	 * @param context
	 * @return
	 */
	public String getJsonData(DataFetchContent context){
		String pageContent = context.getPageContent();
		String jsonData = "";
		try {
			if(StringUtils.isNotBlank(pageContent) && !"null".equals(pageContent)){
				PageBean pageBean = context.getPageBean();
				if(pageBean == null){
					//首次请求，设置分页bean
					String xStr = pageContent.substring(0, pageContent.indexOf("["));
					String xTotal = xStr.substring(xStr.indexOf("X-Total:")+8).trim();
					//获取传参的“每页多少条数据”
					Map<String, Object> params = context.getParams();
					Object pageSizeObj = params.get("per_page");
					Integer pageSize = pageSizeObj==null?PageBean.DEFAULT_PAGE_SIZE:(Integer)pageSizeObj;
					pageBean = new PageBean(pageSize, 1, Integer.valueOf(xTotal));
					context.setPageBean(pageBean);
				}
				//获得json数据
				jsonData = pageContent.substring(pageContent.indexOf("["));
			}else {
				logger.error("解析请求结果，返回:{}",pageContent);
			}
		} catch (Exception e) {
			logger.error("解析请求结果异常，结果:{}",pageContent);
			e.printStackTrace();
		}
		return jsonData;
	}

	/**
	 * 请求地址
	 */
	protected abstract String getRequestURI();

	/**
	 * 得到抓取的数据种类
	 */
	protected abstract DataType getDataType();

	/**
	 * 是否记录成功抓取的日志
	 */
	protected boolean isRecordLog() {
		return true;
	}

	/**
	 * 是否忽略内容无改变判断
	 */
	protected boolean isIgnoreNotmodify() {
		return false;
	}

	/**
	 * 设置请求结果编码方式
	 * @return
	 */
	protected String getChartset() {
		return "utf-8";
	}

	public static void main(String[] args) {
		String a="null";
		String substring = a.substring(a.indexOf("["));
		System.out.println(substring);
	}

}
