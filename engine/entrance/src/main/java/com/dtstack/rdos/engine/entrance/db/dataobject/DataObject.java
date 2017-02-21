package com.dtstack.rdos.engine.entrance.db.dataobject;

import java.util.Date;

import com.dtstack.rdos.common.util.DateUtil;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class DataObject {
	
	
	private long id;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 创建时间
	 */
	private Date gmtCreate;

	/**
	 * 修改时间
	 */
	private Date gmtModified;
	
	
	/**
	 * setter for column 创建时间
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * getter for column 创建时间
	 */
	public String getGmtCreate() {
		return DateUtil.getDate(this.gmtCreate, "yyyy-MM-dd");
	}

	/**
	 * setter for column 修改时间
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * getter for column 修改时间
	 */
	public String getGmtModified() {
		return DateUtil.getDate(this.gmtModified,"yyyy-MM-dd");
	}

}
