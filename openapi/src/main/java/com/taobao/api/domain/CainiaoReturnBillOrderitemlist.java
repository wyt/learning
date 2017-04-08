package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 订单商品信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public class CainiaoReturnBillOrderitemlist extends TaobaoObject {

	private static final long serialVersionUID = 4179217359325574588L;

	/**
	 * 订单商品信息
	 */
	@ApiField("order_item")
	private CainiaoReturnBillOrderitem orderItem;


	public CainiaoReturnBillOrderitem getOrderItem() {
		return this.orderItem;
	}
	public void setOrderItem(CainiaoReturnBillOrderitem orderItem) {
		this.orderItem = orderItem;
	}

}
