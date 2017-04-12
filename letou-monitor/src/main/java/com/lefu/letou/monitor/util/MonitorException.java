package com.lefu.letou.monitor.util;

public class MonitorException extends Exception {
	private static final long serialVersionUID = -1091250420554538383L;
	private String param;

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public MonitorException(String message, Throwable cause) {
		super(message, cause);
	}

	public MonitorException(String message) {
		super(message);
	}

	public MonitorException(String message, String param) {
		super(message);
		this.param = param;
	}

	public MonitorException(String message, String param, Throwable cause) {
		this(message, cause);
		this.param = param;
	}
}
