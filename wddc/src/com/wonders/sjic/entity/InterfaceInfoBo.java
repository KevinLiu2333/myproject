package com.wonders.sjic.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author dwl
 *接口主表实体
 */
@Table("sjic_interface_info")
public class InterfaceInfoBo {
	/**
	 * 接口id
	 */
	@Name
	@Column("id")
	private String id;
	/**
	 * 接口名称
	 */
	@Column("name")
	private String  name;
	/**
	 * 接口类型
	 */
	@Column("type")
	private String type;
	/**
	 * 部门
	 */
	@Column("department")
	private String department;
	/**
	 * 接口地址
	 */
	@Column("address")
	private String address;
	/**
	 * 更新时间
	 */
	@Column("update_time")
	private Date updateTime;
	/**
	 * 接口状态
	 */
	@Column("status")
	private String status;
	/**
	 * 空间名
	 */
	@Column("space_name")
	private String spaceName;
	/**
	 * 服务名
	 */
	@Column("service_name")
	private String serviceName;
	/**
	 * 参数
	 */
	@Column("param")
	private String param;
	/**
	 * 参数类型
	 */
	@Column("param_type")
	private String paramType;
	/**
	 * 系统id
	 */
	@Column("join_id")
	private String joinId;
	
	public String getJoinId() {
		return joinId;
	}
	public void setJoinId(String joinId) {
		this.joinId = joinId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
	
}
