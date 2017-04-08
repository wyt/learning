package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.User;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.user.seller.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class UserSellerGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 8225223814789989867L;

	/** 
	 * 用户信息
	 */
	@ApiField("user")
	private User user;


	public void setUser(User user) {
		this.user = user;
	}
	public User getUser( ) {
		return this.user;
	}
	


}
