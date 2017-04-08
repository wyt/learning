package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.wlb.item.add response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class WlbItemAddResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3659679547541814911L;

	/** 
	 * 新增的商品
	 */
	@ApiField("item_id")
	private String itemId;


	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId( ) {
		return this.itemId;
	}
	


}
