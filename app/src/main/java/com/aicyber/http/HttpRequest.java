package com.aicyber.http;
/**
 * HTTP联网基础处理框架
 * 
 * @author zhangzhisheng
 * 
 */

import java.io.UnsupportedEncodingException;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class HttpRequest<T,TT> extends JsonRequest<T> {
	private final Listener<T> mListener;
	private final Map<String, String> mHeaders;
	private String charset;
	private Class<TT> clazz;

	public HttpRequest(int method, String url, Map<String, String> headers, String requestBody, Class<TT> clazz, Listener<T> listener,
			ErrorListener errorListener) {
		super(method, url, requestBody, listener, errorListener);
		this.mHeaders = headers;
		this.mListener = listener;
		this.clazz = clazz;
	}

	public HttpRequest(String url, String requestBody, Class<TT> clazz, Listener<T> listener, ErrorListener errorListener) {
		this(requestBody == null ? Method.GET : Method.POST, url, null, requestBody, clazz, listener, errorListener);
	}

	public HttpRequest(String url, String requestBody, String charset, Class<TT> clazz, Listener<T> listener,
			ErrorListener errorListener) {
		this(requestBody == null ? Method.GET : Method.POST, url, null, requestBody, clazz, listener, errorListener);
		this.charset = charset;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	public String getCharset(NetworkResponse response) {
		return charset != null ? charset : HttpHeaderParser.parseCharset(response.headers);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {

		try {
			//取得json 串
			String json = new String(response.data, getCharset(response));
			//解析json后对应的数据对象
			TT result = JSON.parseObject(json, clazz);
			if (result != null) {
				return (Response<T>) Response.success(result.toString(), HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return Response.error(new VolleyError("data error"));
			}
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}
}