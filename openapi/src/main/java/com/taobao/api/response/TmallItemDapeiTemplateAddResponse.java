package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: tmall.item.dapei.template.add response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TmallItemDapeiTemplateAddResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6746828786956285423L;

	/** 
	 * 新创建搭配模板的ID
	 */
	@ApiField("id")
	private Long id;


	public void setId(Long id) {
		this.id = id;
	}
	public Long getId( ) {
		return this.id;
	}
	


}
