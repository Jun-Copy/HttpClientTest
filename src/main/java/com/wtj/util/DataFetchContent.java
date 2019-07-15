package com.wtj.util;

import java.util.*;

/**
 * 每次数据抓取的上下文,用于记录每次从数据源抓取数据的一些信息
 */
public class DataFetchContent<T> {
	
	private PageBean pageBean;//分页对象
	private Map<String, Object> params;//请求参数
    private DataType dataType; //抓取的数据类型
    private Calendar startFetchTime; //开始抓取的时间
    private Calendar endFetchTime; //结束抓取的时间

    private String pageContent; //http响应内容
    private long pageLenght; //http响应的页面长度
    private String pageLastModified;//http响应的最后修改时间

    private Map<String, T> result; //这次解析的DTO
    private List<T> chargeData; //和上次相比变化的DTO,对应增加的和有变动的
    private List<T> removeData; //和上次比减少的的DTO
    
    //只针对比赛时间数据，按对阵id分组事件
    private Map<Long, List<T>> matchEvents;

    public DataFetchContent(DataType dataType) {
    	this.params = new HashMap<String, Object>();
        this.pageContent = "";
        this.pageLenght = 0L;
        this.pageLastModified = "";
        this.result = new HashMap<String, T>();
        this.chargeData = new ArrayList<T>();
        this.removeData = new ArrayList<T>();
        this.matchEvents = new HashMap<Long, List<T>>();
        this.dataType = dataType;
    }
    
    public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public DataType getDataType() {
        return dataType;
    }

    public Calendar getStartFetchTime() {
        return startFetchTime;
    }

    public Calendar getEndFetchTime() {
        return endFetchTime;
    }

    public long getPageLenght() {
        return pageLenght;
    }

    public String getPageLastModified() {
        return pageLastModified;
    }

    public Map<String, T> getResult() {
        return result;
    }

    public List<T> getChargeData() {
        return chargeData;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setStartFetchTime(Calendar startFetchTime) {
        this.startFetchTime = startFetchTime;
    }

    public void setEndFetchTime(Calendar endFetchTime) {
        this.endFetchTime = endFetchTime;
    }

    public void setPageLenght(long pageLenght) {
        this.pageLenght = pageLenght;
    }

    public void setPageLastModified(String pageLastModified) {
        this.pageLastModified = pageLastModified;
    }

    public void setResult(Map<String, T> result) {
        this.result = result;
    }

    public void setChargeData(List<T> chargeData) {
        this.chargeData = chargeData;
    }

    public void addOneChargeData(T chargeData) {
        this.chargeData.add(chargeData);
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }
    
    public Map<Long, List<T>> getMatchEvents() {
        return matchEvents;
    }

    public void setMatchEvents(Map<Long, List<T>> matchEvents) {
        this.matchEvents = matchEvents;
    }

    public List<T> getRemoveData() {
        return removeData;
    }

    public void setRemoveData(List<T> removeData) {
        this.removeData = removeData;
    }
    
    public void addOneRemovedData(T chargeData) {
        this.removeData.add(chargeData);
    }

    /**
     * 判断本次取到的原始数据和上次有没有不同
     * @param localpageLength
     * @param localLastModifiedTime
     * @return
     * @create_time 2011-9-16 下午04:43:02
     */
    public boolean hasCharge(long localpageLength, String localLastModifiedTime) {
        if (localpageLength != 0 && this.pageLenght != localpageLength && !localLastModifiedTime.equals("")
                && !this.pageLastModified.equals(localLastModifiedTime)) {
            return true;
        }
        return false;
    }

    /**
     * 比较新抓取的数据和上次抓取的数据有没有变化
     * @param key DTO的唯一标识
     * @param hashcode 计算的hashcode
     * @return
     * @create_time 2011-9-16 下午07:45:04
     */
    public boolean hasChangeForOne(String key, int hashcode) {
        return !result.containsKey(key) || result.get(key).hashCode() != hashcode;
    }

	public PageBean getPageBean() {
		return pageBean;
	}

	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}
	
	/**
	 * 清空分页bean
	 */
	public void clearPageBean(){
        this.pageBean = null;
	}
    
}
