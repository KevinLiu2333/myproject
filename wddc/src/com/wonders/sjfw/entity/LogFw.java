package com.wonders.sjfw.entity;

import java.util.Date;
import java.util.UUID;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;

/**
 * 服务调用日志.
 */
@Table("PF_LOG_FW")
public class LogFw {
    /**
     * 主键
     */
    @Name
    @Prev(els = @EL("$me.initUUID()"))
    @Column("FW_LOG_ID")
    private String fwLogId;
    /**
     * 调用单位id
     */
    @Column("UNIT_ID")
    private String unitId;
    /**
     * 调用单位
     */
    @Column("UNIT_NAME")
    private String unitName;
    /**
     *调用专题
     */
    @Column("OP_CONTENT")
    private String opContent;
    /**
     *调用参数值
     */
    @Column("OP_PARAM")
    private String opParam;
    /**
     *调用结果值
     */
    @Column("OP_RESULT")
    private String opResult;
    /**
     *结果数量
     */
    @Column("RESULT_COUNT")
    private Integer resultCount;
    /**
     *调用状态
     */
    @Column("OP_STATUS")
    private String opStatus;
    /**
     *调用开始时间
     */
    @Column("STATR_TIME")
    private Date startTime;
    /**
     *调用结束时间
     */
    @Column("END_TIME")
    private Date endTime;
    /**
     *调用用时
     */
    @Column("USED_TIME")
    private Integer usedTime;

    //userKey 
    @Column("USER_KEY")
    private String userKey;
    //methodKey 
    @Column("METHOD_KEY")
    private String methodKey;
    //服务Id 
    @Column("FW_INFO_ID")
    private String fwInfoId;
    //服务代码
    @Column("FW_CODE")
    private String fwCode;
    //服务名称
    @Column("FW_NAME")
    private String fwName;
    //错误标识
    @Column("ERROR_CODE")
    private String errorCode;

    /**
     * 秘钥的后四位
     * @return
     */
    public String getUnitKeyDisplay() {
       String showString = "********"+this.userKey.substring(15);
        return showString;
    }
    /**
     * 秘钥的后四位
     * @return
     */
    public String getMethodKeyDisplay() {
       String showString = "********"+this.methodKey.substring(11);
        return showString;
    }
    /**
     * 生成uuid主键
     * @return
     */
    public String initUUID() {
        if (Strings.isEmpty(fwLogId))
            return UUID.randomUUID().toString();
        return this.fwLogId;
    }

    public String getFwLogId() {
        return fwLogId;
    }

    public void setFwLogId(String fwLogId) {
        this.fwLogId = fwLogId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getOpContent() {
        return opContent;
    }

    public void setOpContent(String opContent) {
        this.opContent = opContent;
    }

    public String getOpParam() {
        return opParam;
    }

    public void setOpParam(String opParam) {
        this.opParam = opParam;
    }

    public String getOpResult() {
        return opResult;
    }

    public void setOpResult(String opResult) {
        this.opResult = opResult;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public String getOpStatus() {
        return opStatus;
    }

    public void setOpStatus(String opStatus) {
        this.opStatus = opStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Integer usedTime) {
        this.usedTime = usedTime;
    }

	public String getFwInfoId() {
		return fwInfoId;
	}

	public void setFwInfoId(String fwInfoId) {
		this.fwInfoId = fwInfoId;
	}

	
	
	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getMethodKey() {
		return methodKey;
	}

	public void setMethodKey(String methodKey) {
		this.methodKey = methodKey;
	}
	public String getFwCode() {
		return fwCode;
	}
	public void setFwCode(String fwCode) {
		this.fwCode = fwCode;
	}
	public String getFwName() {
		return fwName;
	}
	public void setFwName(String fwName) {
		this.fwName = fwName;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
    
	
    
}
