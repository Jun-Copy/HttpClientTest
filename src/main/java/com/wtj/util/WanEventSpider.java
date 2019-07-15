package com.wtj.util;

/**
 * @author tingjun.wang
 * @date 2019/7/9 19:05下午
 */
public class WanEventSpider extends HttpClientUtil {

	private String uri = "https://data.wanplus.com/data.php";

	@Override
	protected String getRequestURI() {
		return uri;
	}

	@Override
	protected DataType getDataType() {
		return DataType.赛事;
	}
}
