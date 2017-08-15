package com.mesb.MESB.bussiness.model;

public class Errr {
	
	private Integer errId;
	private String errMsg;
	private String errUrl;
	
	public Errr() {}

	public Errr(Integer errId, String errMsg, String errUrl) {
		super();
		this.errId = errId;
		this.errMsg = errMsg;
		this.errUrl = errUrl;
	}

	public Integer getErrId() {
		return errId;
	}

	public void setErrId(Integer errId) {
		this.errId = errId;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getErrUrl() {
		return errUrl;
	}

	public void setErrUrl(String errUrl) {
		this.errUrl = errUrl;
	}

	@Override
	public String toString() {
		return "Errr [errId=" + errId + ", errMsg=" + errMsg + ", errUrl=" + errUrl + "]";
	}
	
}
