package com.klsw.apiCommon.api.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_UserAvailable")
public class TbUseravailable {
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "NewNumber")
    private String newnumber;

    @Column(name = "AddDateTime")
    private Date adddatetime;

    @Column(name = "Nickname")
    private String nickname;

    @Column(name = "IsActivation")
    private Boolean isactivation;

    /**
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return NewNumber
     */
    public String getNewnumber() {
        return newnumber;
    }

    /**
     * @param newnumber
     */
    public void setNewnumber(String newnumber) {
        this.newnumber = newnumber;
    }

    /**
     * @return AddDateTime
     */
    public Date getAdddatetime() {
        return adddatetime;
    }

    /**
     * @param adddatetime
     */
    public void setAdddatetime(Date adddatetime) {
        this.adddatetime = adddatetime;
    }

    /**
     * @return Nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return IsActivation
     */
    public Boolean getIsactivation() {
        return isactivation;
    }

    /**
     * @param isactivation
     */
    public void setIsactivation(Boolean isactivation) {
        this.isactivation = isactivation;
    }
}