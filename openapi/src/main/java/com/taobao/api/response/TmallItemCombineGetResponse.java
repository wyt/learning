package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: tmall.item.combine.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TmallItemCombineGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 7219348617687319419L;

	/** 
	 * results
	 */
	@ApiListField("results")
	@ApiField("json")
	private List<String> results;


	public void setResults(List<String> results) {
		this.results = results;
	}
	public List<String> getResults( ) {
		return this.results;
	}
	


}
