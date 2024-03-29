/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.wgx.common.utils;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.wgx.common.exception.ExceptionCode;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R<T> extends HashMap<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;

	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R error(ExceptionCode e) {
		return R.error(e.code(), e.message());
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Integer getCode() {
		return (Integer) this.get("code");
	}

	public String getMsg() {
		return (String) this.get("msg");
	}

	public R<T> setData(T data) {
		return this.put("data", data);
	}

	public T getData(TypeReference<?> typeReference) {
		String data = JSON.toJSONString(this.get("data"));
		return JSON.parseObject(data, typeReference);
	}

	public R<T> putDate(T data) {
		return this.put("data", data);
	}
}
