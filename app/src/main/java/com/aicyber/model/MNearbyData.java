package com.aicyber.model;

import java.util.List;

public class MNearbyData extends MDataBase {

	List<PoiData> pois; // ����������POI ��������

	public List<PoiData> getPois() {
		return pois;
	}

	public void setPois(List<PoiData> pois) {
		this.pois = pois;
	}
}
