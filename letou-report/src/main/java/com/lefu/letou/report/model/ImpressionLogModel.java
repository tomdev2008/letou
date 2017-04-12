package com.lefu.letou.report.model;

import java.util.Date;

public class ImpressionLogModel extends ReportModel {
	private int impressionCount;

	public int getImpressionCount() {
		return impressionCount;
	}

	public void setImpressionCount(int impressionCount) {
		this.impressionCount = impressionCount;
	}

	public ImpressionLogModel(Date date) {
		super(date);
	}

}
