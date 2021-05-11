package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 
 *
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
@ApiModel
public class DataObject {
	
	
	private long id;
	
	/**
	 * 创建时间
	 */
	@ApiModelProperty(notes = "创建时间")
	private Date gmtCreate;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(notes = "修改时间")
	private Date gmtModified;
	
	/**
	 * 是否逻辑删除
	 */
	@ApiModelProperty(notes = "是否逻辑删除")
	private Integer isDeleted;
	
	
	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	/**
	 * setter for column 创建时间
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * getter for column 创建时间
	 */
	public Date getGmtCreate() {
		return this.gmtCreate;
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
	public Date getGmtModified() {
		return this.gmtModified;
	}

}
