package com.lefu.letou.adserver.pos.model;

public class PosMaterial {
	private String url;
	private int adId;

	@Override
	public String toString() {
		return "PosMaterial [url=" + url + ", adId=" + adId + "]";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

}
