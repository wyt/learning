package com.qimencloud.api;

import java.io.IOException;
import java.util.Date;

import com.taobao.api.ApiException;
import com.taobao.api.Constants;
import com.taobao.api.TaobaoParser;
import com.taobao.api.internal.cluster.ClusterManager;
import com.taobao.api.internal.cluster.DnsConfig;
import com.taobao.api.internal.parser.json.QimenCloudJsonParser;
import com.taobao.api.internal.parser.xml.QimenCloudXmlParser;
import com.taobao.api.internal.util.RequestParametersHolder;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.TaobaoLogger;
import com.taobao.api.internal.util.TaobaoUtils;
import com.taobao.api.internal.util.WebUtils;

public class DefaultQimenCloudClient implements QimenCloudClient {

	protected String serverUrl;
	protected String appKey;
	protected String appSecret;
	protected String format = Constants.FORMAT_JSON;
	protected String signMethod = Constants.SIGN_METHOD_HMAC_SHA256;
	protected int connectTimeout = 15000; // 默认连接超时时间为15秒
	protected int readTimeout = 30000; // 默认响应超时时间为30秒
	protected boolean needEnableParser = true; // 是否对响应结果进行解释
	protected boolean useGzipEncoding = true; // 是否启用响应GZIP压缩
	private   boolean isHttpDnsEnabled = false; //是否启用了httpdns
    private   String  originalHttpHost = null;  //原始请求Host
    
	public DefaultQimenCloudClient(String serverUrl, String appKey, String appSecret) {
		this.serverUrl = serverUrl;
		this.appKey = appKey;
		this.appSecret = appSecret;
	}
	
	public DefaultQimenCloudClient(String serverUrl, String appKey, String appSecret, String format) {
		this(serverUrl, appKey, appSecret);
		this.format = format;
	}
	
	public DefaultQimenCloudClient(String serverUrl, String appKey, String appSecret, String format, int connectTimeout, int readTimeout) {
		this(serverUrl, appKey, appSecret, format);
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}
	
	public DefaultQimenCloudClient(String serverUrl, String appKey, String appSecret, String format, int connectTimeout, int readTimeout, String signMethod) {
		this(serverUrl, appKey, appSecret, format, connectTimeout, readTimeout);
		this.signMethod = signMethod;
	}
	
	public <T extends AbstractQimenCloudResponse> T execute(IQimenCloudRequest<T> request)
			throws ApiException {
		long start = System.currentTimeMillis();
		// 构建响应解释器
		TaobaoParser<T> parser = null;
		if (this.needEnableParser) {
			if (Constants.FORMAT_XML.equals(this.format)) {
				parser = new QimenCloudXmlParser<T>(request.getResponseClass());
			} else {
				parser = new QimenCloudJsonParser<T>(request.getResponseClass());
			}
		}

		RequestParametersHolder requestHolder = new RequestParametersHolder();
		TaobaoHashMap appParams = new TaobaoHashMap(request.getTextParams());
		requestHolder.setApplicationParams(appParams);
		
		// 添加协议级请求参数
		TaobaoHashMap protocalMustParams = new TaobaoHashMap();
		protocalMustParams.put(Constants.METHOD, request.getApiMethodName());
		protocalMustParams.put(Constants.APP_KEY, appKey);
		Long timestamp = request.getTimestamp();
		if (timestamp == null) {
			timestamp = System.currentTimeMillis();
		}

		protocalMustParams.put(Constants.TIMESTAMP, new Date(timestamp));
		requestHolder.setProtocalMustParams(protocalMustParams);

		TaobaoHashMap protocalOptParams = new TaobaoHashMap();
		protocalOptParams.put(Constants.FORMAT, format);
		protocalOptParams.put(Constants.SIGN_METHOD, signMethod);
		protocalOptParams.put(Constants.TARGET_APP_KEY, request.getTargetAppKey());
		requestHolder.setProtocalOptParams(protocalOptParams);

		String fullUrl = null;
		try {
			// 添加签名参数
			protocalMustParams.put(Constants.SIGN, TaobaoUtils.signTopRequest(requestHolder, appSecret, signMethod));

			String realServerUrl = getServerUrl(this.serverUrl);
			String sysMustQuery = WebUtils.buildQuery(requestHolder.getProtocalMustParams(), Constants.CHARSET_UTF8);
			String sysOptQuery = WebUtils.buildQuery(requestHolder.getProtocalOptParams(), Constants.CHARSET_UTF8);
			String appQuery = WebUtils.buildQuery(requestHolder.getApplicationParams(), Constants.CHARSET_UTF8);
			fullUrl = WebUtils.buildRequestUrl(realServerUrl, sysMustQuery, sysOptQuery, appQuery);
			
			String rsp = null;
			// 是否需要压缩响应
			if (this.useGzipEncoding) {
				request.getHeaderMap().put(Constants.ACCEPT_ENCODING, Constants.CONTENT_ENCODING_GZIP);
			}
			if(getTopHttpDnsHost() != null){
				request.getHeaderMap().put(Constants.TOP_HTTP_DNS_HOST, getTopHttpDnsHost());
			}
			
			rsp = WebUtils.doPost(fullUrl, new TaobaoHashMap(), Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
			requestHolder.setResponseBody(rsp);
		} catch (IOException e) {
			TaobaoLogger.logApiError(appKey, request.getApiMethodName(), serverUrl, requestHolder.getAllParams(), System.currentTimeMillis() - start, e.toString());
			throw new ApiException(e);
		}
		
		T tRsp = null;
		if (this.needEnableParser) {
			tRsp = parser.parse(requestHolder.getResponseBody());
			tRsp.setRequestUrl(fullUrl);
			tRsp.setBody(requestHolder.getResponseBody());
		} else {
			try {
				tRsp = request.getResponseClass().newInstance();
				tRsp.setRequestUrl(fullUrl);
				tRsp.setBody(requestHolder.getResponseBody());
			} catch (Exception e) {
				throw new ApiException(e);
			}
		}

		if (!tRsp.isSuccess()) {
			TaobaoLogger.logApiError(appKey, request.getApiMethodName(), serverUrl, requestHolder.getAllParams(), System.currentTimeMillis() - start, tRsp.getBody());
		}
		return tRsp;
	}
	
	public QimenCloudResponse execute(QimenCloudRequest request)
			throws ApiException {
		long start = System.currentTimeMillis();
		// 构建响应解释器
		TaobaoParser<QimenCloudResponse> parser = null;
		if (this.needEnableParser) {
			if (Constants.FORMAT_XML.equals(this.format)) {
				parser = new QimenCloudXmlParser<QimenCloudResponse>(request.getResponseClass());
			} else {
				parser = new QimenCloudJsonParser<QimenCloudResponse>(request.getResponseClass());
			}
		}

		RequestParametersHolder requestHolder = new RequestParametersHolder();
		TaobaoHashMap appParams = new TaobaoHashMap(request.getQueryParams());
		requestHolder.setApplicationParams(appParams);
		
		// 添加协议级请求参数
		TaobaoHashMap protocalMustParams = new TaobaoHashMap();
		protocalMustParams.put(Constants.METHOD, request.getApiMethodName());
		protocalMustParams.put(Constants.APP_KEY, appKey);
		Long timestamp = request.getTimestamp();
		if (timestamp == null) {
			timestamp = System.currentTimeMillis();
		}

		protocalMustParams.put(Constants.TIMESTAMP, new Date(timestamp));
		requestHolder.setProtocalMustParams(protocalMustParams);

		TaobaoHashMap protocalOptParams = new TaobaoHashMap();
		protocalOptParams.put(Constants.FORMAT, format);
		protocalOptParams.put(Constants.SIGN_METHOD, signMethod);
		protocalOptParams.put(Constants.TARGET_APP_KEY, request.getTargetAppKey());
		requestHolder.setProtocalOptParams(protocalOptParams);

		String fullUrl = null;
		try {
			// 添加签名参数
			protocalMustParams.put(Constants.SIGN, TaobaoUtils.signTopRequest(requestHolder, appSecret, signMethod));

			String realServerUrl = getServerUrl(this.serverUrl);
			String sysMustQuery = WebUtils.buildQuery(requestHolder.getProtocalMustParams(), Constants.CHARSET_UTF8);
			String sysOptQuery = WebUtils.buildQuery(requestHolder.getProtocalOptParams(), Constants.CHARSET_UTF8);
			String appQuery = WebUtils.buildQuery(requestHolder.getApplicationParams(), Constants.CHARSET_UTF8);
			fullUrl = WebUtils.buildRequestUrl(realServerUrl, sysMustQuery, sysOptQuery, appQuery);
			
			String rsp = null;
			// 是否需要压缩响应
			if (this.useGzipEncoding) {
				request.getHeaderMap().put(Constants.ACCEPT_ENCODING, Constants.CONTENT_ENCODING_GZIP);
			}
			if(getTopHttpDnsHost() != null){
				request.getHeaderMap().put(Constants.TOP_HTTP_DNS_HOST, getTopHttpDnsHost());
			}
			
			rsp = WebUtils.doPost(fullUrl, new TaobaoHashMap(), Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
			requestHolder.setResponseBody(rsp);
		} catch (IOException e) {
			TaobaoLogger.logApiError(appKey, request.getApiMethodName(), serverUrl, requestHolder.getAllParams(), System.currentTimeMillis() - start, e.toString());
			throw new ApiException(e);
		}
		
		QimenCloudResponse tRsp = null;
		if (this.needEnableParser) {
			tRsp = parser.parse(requestHolder.getResponseBody());
			tRsp.setRequestUrl(fullUrl);
			tRsp.setBody(requestHolder.getResponseBody());
		} else {
			try {
				tRsp = request.getResponseClass().newInstance();
				tRsp.setRequestUrl(fullUrl);
				tRsp.setBody(requestHolder.getResponseBody());
			} catch (Exception e) {
				throw new ApiException(e);
			}
		}

		if (!tRsp.isSuccess()) {
			TaobaoLogger.logApiError(appKey, request.getApiMethodName(), serverUrl, requestHolder.getAllParams(), System.currentTimeMillis() - start, tRsp.getBody());
		}
		return tRsp;
	}
	
	public String getServerUrl(String serverUrl) {
		if(isHttpDnsEnabled){
	        DnsConfig dnsConfig = ClusterManager.GetDnsConfigFromCache();
	        if (dnsConfig == null) {
	            return serverUrl;
	        } else {
	            return dnsConfig.getVipUrl(serverUrl);
	        } 
        }
        return serverUrl;
	}
	
	public String getTopHttpDnsHost(){
	    if(isHttpDnsEnabled){
	        return originalHttpHost;
	    }
	    return null;
	}
}
