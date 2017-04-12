package com.lefu.letou.adserver.model;

import java.net.URISyntaxException;

public class AdException extends Exception {
	private String param;

	private static final long serialVersionUID = 1803905068379950886L;

	public AdException(String message) {
		super(message);
	}

	public AdException(String msg, String param) {
		this(msg);
		this.param = param;
	}

	public AdException(String msg, String param, Exception cause) {
		super(msg, cause);
		this.param = param;
	}

	public AdException(String string, Exception e) {
		super(string, e);
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

}
