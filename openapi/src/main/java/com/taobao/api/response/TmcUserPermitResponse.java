package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tmc.user.permit response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TmcUserPermitResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6121431114682449454L;

	/** 
	 * 是否成功
	 */
	@ApiField("is_success")
	private Boolean isSuccess;


	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Boolean getIsSuccess( ) {
		return this.isSuccess;
	}
	


}
