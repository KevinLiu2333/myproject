package com.wonders.zx.at;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/zx")
public class ZxAt {
	@Inject
	private Dao dao;
	
	@At
	@Ok("jsp:jsp.zx.zt.people")
	public Map<String, Object> toZtPeople(HttpSession session){
		
		return null;
		
	}
	
	/**
	 * 主题分析中的房屋基本情况.
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:jsp.zx.legalPeople.legalStatus")
	public Map<String, Object> toLegalPeople(HttpSession session){
		
		return null;
		
	}
	@At
	@Ok("jsp:jsp.zx.zt.building")
	public Map<String, Object> toZtBuilding()throws Exception{
		return null;
	}
	
	/**
	 * 主题分析中的法人基本情况.
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:jsp.zx.zt.frjbqk")
	public Map<String, Object> toFrjbqk()throws Exception{
		return null;
	}
	
	/**
	 * 主题分析中的法人差异化情况.
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:jsp.zx.zt.frcyhqk")
	public Map<String, Object> toFrcyhqk()throws Exception{
		return null;
	}
	
	@At
	@Ok("jsp:jsp.zx.zt.peopledifferent")
	public Map<String, Object> toZtPeopledifferent(HttpSession session){
		
		return null;
	}
	
	/**
	 * 主题分析中的房屋基本情况.
	 * @return
	 * @throws Exception
	 */

	
	//===============================
	/**
	 * 进入人口基础数据查询列表页面.
	 * @return
	 */
	@At
	@Ok("jsp:jsp.zx.cx.people-info")
	public Map<String, Object> toPeopleData()throws Exception{
		return null;
	}
	/**
	 * 进入房屋数据基础查询列表页面.
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:jsp.zx.cx.building-info")
	public Map<String, Object> toBuildingData()throws Exception{
		return null;
	}
	
	/**
	 * 进入法人数据基础查询列表页面.
	 * @return
	 * @throws Exception 
	 */
	@At
	@Ok("jsp:jsp.zx.cx.corporation-info")
	public Map<String, Object> toCorporationData()throws Exception{
		return null;
	}
	
	/**
	 * 进入一键检索页面.
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:jsp.zx.cx.onekeysearch")
	public Map<String, Object> toOneKeySearch()throws Exception{
		
		return null;
	}
	
	/**
	 * 进入全文检索首页.
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:jsp.zx.cx.fulltextretrieval")
	public Map<String, Object> toFulltextRetrieval()throws Exception{
		return null;
	}

}
