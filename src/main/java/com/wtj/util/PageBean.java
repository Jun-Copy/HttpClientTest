package com.wtj.util;

import java.io.Serializable;

/**
 * 分页bean
 * @author zheng.lin
 * @date 2019年5月13日 下午3:36:50
 */
public class PageBean implements Serializable{

	private static final long serialVersionUID = 7895746654116926052L;
	
	private int currentPage = 1;// 当前页数

	public int totalPages = 0;// 总页数

	private int pageSize = 0;// 每页显示数

	private int totalRows = 0;// 总数据数

	private int nextPage = 0;// 下一页

	private int previousPage = 0;// 上一页

	private boolean hasNextPage = false;// 是否有下一页

	private boolean hasPreviousPage = false;// 是否有前一页
	
	public static final int DEFAULT_PAGE_SIZE = 50;//默认每页50
	
	public PageBean(int pageSize, int currentPage, int totalRows) {

		this.pageSize = pageSize;
		this.currentPage = currentPage;
		this.totalRows = totalRows;

		if ((totalRows % pageSize) == 0) {
			totalPages = totalRows / pageSize;
		} else {
			totalPages = totalRows / pageSize + 1;
		}

		/*if (currentPage >= totalPages) {
			hasNextPage = false;
			currentPage = totalPages;
		} else {
			hasNextPage = true;
		}

		if (currentPage <= 1) {
			hasPreviousPage = false;
			currentPage = 1;
		} else {
			hasPreviousPage = true;
		}

		nextPage = currentPage + 1;

		if (nextPage >= totalPages) {
			nextPage = totalPages;
		}

		previousPage = currentPage - 1;

		if (previousPage <= 1) {
			previousPage = 1;
		}*/

	}

	public boolean isHasNextPage() {
		if (currentPage >= totalPages) {
			hasNextPage = false;
			currentPage = totalPages;
		} else {
			hasNextPage = true;
		}
		return hasNextPage;
	}

	public boolean isHasPreviousPage() {
		if (currentPage <= 1) {
			hasPreviousPage = false;
			currentPage = 1;
		} else {
			hasPreviousPage = true;
		}
		return hasPreviousPage;
	}

	/**
	* @return the nextPage
	*/
	public int getNextPage() {
		nextPage = currentPage + 1;
		if (nextPage >= totalPages) {
			nextPage = totalPages;
		}
		return nextPage;
	}

	/**
	* @param nextPage
	*            the nextPage to set
	*/
	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	/**
	* @return the previousPage
	*/
	public int getPreviousPage() {
		previousPage = currentPage - 1;

		if (previousPage <= 1) {
			previousPage = 1;
		}
		return previousPage;
	}

	/**
	* @param previousPage
	*            the previousPage to set
	*/
	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}

	/**
	* @return the currentPage
	*/
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	* @param currentPage
	*            the currentPage to set
	*/
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	* @return the pageSize
	*/
	public int getPageSize() {
		return pageSize;
	}

	/**
	* @param pageSize
	*            the pageSize to set
	*/
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	* @return the totalPages
	*/
	public int getTotalPages() {
		return totalPages;
	}

	/**
	* @param totalPages
	*            the totalPages to set
	*/
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	* @return the totalRows
	*/
	public int getTotalRows() {
		return totalRows;
	}

	/**
	* @param totalRows
	*            the totalRows to set
	*/
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	/**
	* @param hasNextPage
	*            the hasNextPage to set
	*/
	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	/**
	* @param hasPreviousPage
	*            the hasPreviousPage to set
	*/
	public void setHasPreviousPage(boolean hasPreviousPage) {
		this.hasPreviousPage = hasPreviousPage;
	}

}
