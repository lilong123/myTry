package com.nineShadow.model;

import java.math.BigDecimal;
import java.util.Date;

public class PlayerBase {

    private Integer id;

    private String realname;

    private String idcard;

    private String nickname;

    private String phone;

    private String postcode;

    private String telname;

    private String telphone;

    private String password;

    private Integer sex;

    private String headurl;

    private String address;

    private String openid;

    private String unionid;

    private Integer isLocked;

    private String parentId;
    
    public String getRealname() {
        return realname;
    }

    public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTelname() {
        return telname;
    }

    public void setTelname(String telname) {
        this.telname = telname;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public Integer getIsRnregistration() {
        return isRnregistration;
    }

    public void setIsRnregistration(Integer isRnregistration) {
        this.isRnregistration = isRnregistration;
    }

    public Integer getIsBingding() {
        return isBingding;
    }

    public void setIsBingding(Integer isBingding) {
        this.isBingding = isBingding;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    private Integer isRnregistration;

    private Integer isBingding;

    private Date registerTime;

    private Date bindtime;

    private BigDecimal point;
    
    public Date getBindtime() {
		return bindtime;
	}

	public void setBindtime(Date bindtime) {
		this.bindtime = bindtime;
	}



	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl == null ? null : headurl.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? null : unionid.trim();
    }

    public Integer getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

	@Override
	public String toString() {
		return "PlayerBase [id=" + id + ", realname=" + realname + ", idcard=" + idcard + ", nickname=" + nickname
				+ ", phone=" + phone + ", postcode=" + postcode + ", telname=" + telname + ", telphone=" + telphone
				+ ", password=" + password + ", sex=" + sex + ", headurl=" + headurl + ", address=" + address
				+ ", openid=" + openid + ", unionid=" + unionid + ", isLocked=" + isLocked + ", parentId=" + parentId
				+ ", isRnregistration=" + isRnregistration + ", isBingding=" + isBingding + ", registerTime="
				+ registerTime + ", bindtime=" + bindtime + ", point=" + point + "]";
	}
    
}