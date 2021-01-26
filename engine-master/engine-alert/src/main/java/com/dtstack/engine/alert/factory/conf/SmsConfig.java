package com.dtstack.engine.alert.factory.conf;

import java.util.List;
import java.util.Map;

public class SmsConfig {
	
	private boolean cookieStore;
	
	private List<HttpConfig> configs;
	
	private Map<String, Object> context;

	public boolean isCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(boolean cookieStore) {
		this.cookieStore = cookieStore;
	}

	public List<HttpConfig> getConfigs() {
		return configs;
	}

	public void setConfigs(List<HttpConfig> configs) {
		this.configs = configs;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

}
