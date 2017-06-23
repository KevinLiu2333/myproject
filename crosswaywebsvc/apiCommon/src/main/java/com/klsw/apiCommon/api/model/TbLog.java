package com.klsw.apiCommon.api.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_Log")
public class TbLog {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SerialNumber")
    private String serialnumber;

    @Column(name = "VersionNo")
    private String versionno;

    @Column(name = "VersionPlatfrom")
    private String versionplatfrom;

    @Column(name = "Time")
    private Date time;

    @Column(name = "LogPath")
    private String logpath;

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return SerialNumber
     */
    public String getSerialnumber() {
        return serialnumber;
    }

    /**
     * @param serialnumber
     */
    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    /**
     * @return VersionNo
     */
    public String getVersionno() {
        return versionno;
    }

    /**
     * @param versionno
     */
    public void setVersionno(String versionno) {
        this.versionno = versionno;
    }

    /**
     * @return VersionPlatfrom
     */
    public String getVersionplatfrom() {
        return versionplatfrom;
    }

    /**
     * @param versionplatfrom
     */
    public void setVersionplatfrom(String versionplatfrom) {
        this.versionplatfrom = versionplatfrom;
    }

    /**
     * @return Time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return LogPath
     */
    public String getLogpath() {
        return logpath;
    }

    /**
     * @param logpath
     */
    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }
}