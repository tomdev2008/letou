package com.lefu.letou.common;

import java.util.Date;

/**
 * 广告排期
 *
 * @author bin.deng
 *
 */
public class PlanModel {
	private int planId;
	private int adId; // 所属广告ID
	private Date startDate; // 开始结束时间
	private Date endDate;
	private int impression; // 当天计划曝光量

	@Override
	public String toString() {
		return "PlanModel [planId=" + planId + ", adId=" + adId
				+ ", startDate=" + startDate + ", endDate=" + endDate
				+ ", impression=" + impression + "]";
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getImpression() {
		return impression;
	}

	public void setImpression(int impression) {
		this.impression = impression;
	}

}
