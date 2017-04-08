package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.CainiaoReturnBillReturnorderinfo;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.wlb.wms.return.bill.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class WlbWmsReturnBillGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 1116624738148561174L;

	/** 
	 * 回退订单信息
	 */
	@ApiField("return_order_info")
	private CainiaoReturnBillReturnorderinfo returnOrderInfo;


	public void setReturnOrderInfo(CainiaoReturnBillReturnorderinfo returnOrderInfo) {
		this.returnOrderInfo = returnOrderInfo;
	}
	public CainiaoReturnBillReturnorderinfo getReturnOrderInfo( ) {
		return this.returnOrderInfo;
	}
	


}
