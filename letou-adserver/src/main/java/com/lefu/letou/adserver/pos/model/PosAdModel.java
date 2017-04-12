package com.lefu.letou.adserver.pos.model;

import java.util.List;

public class PosAdModel {
	private String posId;
	private List<PosMaterial> materials;

	@Override
	public String toString() {
		return "PosAdModel [posId=" + posId + ", materials=" + materials + "]";
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public List<PosMaterial> getMaterials() {
		return materials;
	}

	public void setMaterials(List<PosMaterial> materials) {
		this.materials = materials;
	}

}
