package com.wonders.sjfw.platform.handle;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import com.wonders.sjfw.entity.FwAccess;
import com.wonders.sjfw.entity.FwConfig;
import com.wonders.sjfw.entity.FwInfo;
import com.wonders.sjfw.platform.jk.FwHandle;
import com.wonders.wddc.suite.user.entity.UserUnitBo;
/**
 * 数据库数据获取和处理的操作.
 *
 */
@IocBean(fields = "dao")
public class DataHandle implements FwHandle {

	public Dao dao;
    

	/**
	 * 设置调用次数.
	 * @param fwInfoId 服务主键
	 */
	public void addFwUsedCount(String fwInfoId){
	    FwInfo fwInfo = dao.fetch(FwInfo.class,fwInfoId);
        fwInfo.setUsedCount(fwInfo.getUsedCount()+1);
        dao.update(fwInfo);
	}
	/**
	 * 获取用户信息.
	 * @param unitKey unitKey
	 * @return
	 */
	public UserUnitBo getUserUnitByUnitKey(String unitKey){
	    UserUnitBo userUnit = dao.fetch(UserUnitBo.class, Cnd.where("unitKey","=",unitKey));
	    return userUnit;
	}
	
	/**
	 * 获取授期信息.
	 * @param methodKey methodKey
	 * @return
	 */
	public FwAccess getFwAccessByMethodKey(String methodKey){
		FwAccess fwAccess = dao.fetch(FwAccess.class, Cnd.where("methodKey","=",methodKey));
	    return fwAccess;
	}
	
	/**
	 * 获取授期信息.
	 * @param methodKey methodKey
	 * @return
	 */
	public FwAccess getFwAccessByFwInfoId(String unitKey,String fwInfoId){
		FwAccess fwAccess = dao.fetch(FwAccess.class, Cnd.where("fwInfoId","=",fwInfoId).and("unitKey","=",unitKey));
	    return fwAccess;
	}
	
	
	/**
	 * 服务配置对象.
	 * @param fwInfoId 服务主键
	 * @return
	 */
	public List<FwConfig> findFwConfigByFwInfoId(String fwInfoId){
	    List<FwConfig> fwConfigList = dao.query(FwConfig.class, Cnd.where("fwInfoId","=",fwInfoId));
	    return fwConfigList;
	}
	/**
	 * 获取服务对象.
	 * @param fwInfoId 主键
	 * @return
	 */
	public FwInfo getFwInfoByCode(String fwCode){
	    FwInfo fwInfo = dao.fetch(FwInfo.class,Cnd.where("fwCode","=",fwCode));
	    return fwInfo;
	}
	/**
	 * 获取服务对象.
	 * @param fwInfoId 主键
	 * @return
	 */
	public FwInfo getFwInfoById(String fwInfoId){
	    FwInfo fwInfo = dao.fetch(FwInfo.class,fwInfoId);
	    return fwInfo;
	}
	/**
	 * 获取服务对象.
	 * @param fwInfoId 主键
	 * @return
	 */
	public void saveAutoAccess(FwAccess fwAccess){
	    dao.insert(fwAccess);
	}
	 /**
     * 执行SQl
     * @param sql
     */
    public void executeSql(Sql sql){
    	dao.execute(sql);
	}
	
}
