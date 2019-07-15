package com.wtj.util;

import org.junit.Test;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tingjun.wang
 * @date 2019/7/9 19:05下午
 */
public class WanPlusEventTask {

	private WanEventSpider wanEventSpider = new WanEventSpider();

	private DataFetchContent content;

	public WanPlusEventTask() {
		content = new DataFetchContent(DataType.赛事);
	}

	/**
	 * 赛事列表
	 */
	@Test
	public void eventList(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Event");
		param.put("m","eventList");
		param.put("appid",21);
		param.put("gm",2);
		param.put("token","7d6967");
		param.put("page",2);
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}

	/**
	 * 赛事详情
	 */
	@Test
	public void eventDetail(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Event");
		param.put("m","eventDetail");
		param.put("appid",21);
		param.put("gm",2);
		param.put("eid",830);
		param.put("token","b8ab7a");
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}

	/**
	 * 赛程详情
	 */
	@Test
	public void schedule(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Schedule");
		param.put("m","schedule");
		param.put("appid",21);
		param.put("scheduleid",55334);
		param.put("eid",829);
		param.put("token","6732b3");
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());

	}

	/**
	 * 战队详情
	 */
	@Test
	public void team(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Team");
		param.put("m","team");
		param.put("appid",21);
		param.put("gm",2);
		param.put("eid",830);
		param.put("teamid",5149);
		param.put("token","17ef1f");
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}

	/**
	 * 战队赛事详情
	 */
	@Test
	public void teamEvent(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Team");
		param.put("m","teamEvent");
		param.put("appid",21);
		param.put("gm",2);
		param.put("teamid",199);
		param.put("eid",829);
		param.put("token","56892d");
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}

	/**
	 * 选手基础数据
	 */
	@Test
	public void player(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Player");
		param.put("m","player");
		param.put("appid",21);
		param.put("gm",2);
		param.put("eid",829);
		param.put("playerid",24700);
		param.put("token","85dd33");
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}

	/**
	 * 选手赛事数据
	 */
	@Test
	public void playerEvent(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Player");
		param.put("m","playerEvent");
		param.put("appid",21);
		param.put("gm",2);
		param.put("eid",829);
		param.put("playerid",24700);
		param.put("token","85dd33");
		param.put("ts",Instant.now().getEpochSecond());
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}
	/**
	 * 选手排行榜  根据赛事id，赛事token，请求页，页大小，排序字段
	 */
	@Test
	public void rankPlayer(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Rank");
		param.put("m","rank");
		param.put("appid",21);
		param.put("gm",2);
		param.put("ts",Instant.now().getEpochSecond());
		param.put("eid",829);
		param.put("token","078131");
		param.put("type","player");
		param.put("field","playerid"); //排序字段
		param.put("order","desc");
		param.put("page",1);
		param.put("length",100);//一页最大100
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}
	/**
	 * 战队排行榜  根据赛事id，赛事token，请求页，页大小，排序字段
	 */
	@Test
	public void rankTeam(){
		Map<String, Object> param = new HashMap<>();
		param.put("c","Data_Rank");
		param.put("m","rank");
		param.put("appid",21);
		param.put("gm",2);
		param.put("ts",Instant.now().getEpochSecond());
		param.put("eid",829);
		param.put("token","078131");
		param.put("type","team");
		param.put("field","kda"); //排序字段
		param.put("order","desc");
		param.put("page",1);
		param.put("length",100);//一页最大100
		content.setParams(param);
		wanEventSpider.getDataFromLeiData(content);
		System.out.println(content.getPageContent());
	}
}
