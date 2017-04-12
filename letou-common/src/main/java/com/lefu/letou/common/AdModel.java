package com.lefu.letou.common;

import java.util.Date;
import java.util.List;

public class AdModel {
	private int adId; // 广告ID
	private List<Integer> productIds;
	private Date startDate;
	private Date endDate; // 起止时间
	private int totalQuantity; // 投放总量
	private int dailyQuantity; // 计划日曝光量

	private List<Integer> targetRegionId;

	private boolean limitQuantity; // 是否控制曝光量,如果控制曝光量,则当天曝光数量投满后将不再投放, 否则会持续投放
	private String ldp;
	private String content; // POS的文字,app的url
	private int width;
	private int height;
	private String extName;

	// private List<PlanModel> adPlans = new ArrayList<PlanModel>();

	@Override
	public String toString() {
		return "AdModel [adId=" + adId + ", productIds=" + productIds
				+ ", startDate=" + startDate + ", endDate=" + endDate
				+ ", totalQuantity=" + totalQuantity + ", dailyQuantity="
				+ dailyQuantity + ", targetRegionId=" + targetRegionId
				+ ", limitQuantity=" + limitQuantity + ", ldp=" + ldp
				+ ", content=" + content + ", width=" + width + ", height="
				+ height + ", extName=" + extName + "]";
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getExtName() {
		return extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLdp() {
		return ldp;
	}

	public void setLdp(String ldp) {
		this.ldp = ldp;
	}

	//
	// public void addPlan(PlanModel planModel) {
	// adPlans.add(planModel);
	// }

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

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public int getDailyQuantity() {
		return dailyQuantity;
	}

	public void setDailyQuantity(int dailyQuantity) {
		this.dailyQuantity = dailyQuantity;
	}

	public boolean isLimitQuantity() {
		return limitQuantity;
	}

	public void setLimitQuantity(boolean limitQuantity) {
		this.limitQuantity = limitQuantity;
	}

	public List<Integer> getTargetRegionId() {
		return targetRegionId;
	}

	public void setTargetRegionId(List<Integer> targetRegionId) {
		this.targetRegionId = targetRegionId;
	}

	public List<Integer> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Integer> productIds) {
		this.productIds = productIds;
	}

}
