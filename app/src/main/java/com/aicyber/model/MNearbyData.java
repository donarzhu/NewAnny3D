package com.aicyber.model;

import java.util.List;

public class MNearbyData extends MDataBase {

	List<PoiData> pois; // 附近的所有POI 具体数据

	public List<PoiData> getPois() {
		return pois;
	}

	public void setPois(List<PoiData> pois) {
		this.pois = pois;
	}
}
