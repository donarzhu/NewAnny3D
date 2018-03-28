package com.aicyber.loaction;
/****
 * 
 * @author zhisheng
 * ��ȡ��λ����
 */

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.aicyber.util.Logger;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LLoactionManager {
	
	private static LLoactionManager mInstance = null;
	private static LocationManager mLocationManager = null;
	private Location mLocation = null;
	private Context mContext = null;
	private int count = 0;
	
	private LLoactionManager()
	{
		
	}
	
	public static LLoactionManager getInstance()
	{
		if (null == mInstance) {
			mInstance = new LLoactionManager();
		}
		return mInstance;
	}
	
	public void init(Context context)
	{	
		mContext = context;
		mLocationManager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String providerType = getProvider();
		//����ˢ�¼���
		mLocationManager.requestLocationUpdates(providerType, 2000, 10, locationListener);
		
		//�õ����µ�λ��
		mLocation = mLocationManager.getLastKnownLocation(providerType);
		while (mLocation == null) {
			Logger.debug("11111111111111111111==============================");
			mLocation = mLocationManager.getLastKnownLocation(providerType);
			count++;
			if (count > 100) {
				//break;
			}
		}
	}
	
	public String getCityName(){
		String cityName = "";
		if (mLocation != null) {
			double lat = mLocation.getLatitude();   //γ��
			double lng = mLocation.getLongitude();  //����
			Geocoder gc = new Geocoder(mContext, Locale.getDefault());
			try {
				// ȡ�õ�ַ��ص�һЩ��Ϣ\���ȡ�γ��
				List<Address> addresses = gc.getFromLocation(lat, lng, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					sb.append(address.getLocality()).append("\n");
					cityName = cityName + sb.toString();
				}
			} catch (IOException e) {
			}
		}
		else
		{
			cityName = "���";
		}

		//mLocationManager.removeUpdates(locationListener);
		return cityName;
	}

	
	// ��ȡLocation Provider
	private String getProvider() {
		// ����λ�ò�ѯ����
		Criteria criteria = new Criteria();
		// ��ѯ���ȣ���
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//�����Ƿ�Ҫ���ٶ�
        criteria.setSpeedRequired(false);
		// �Ƿ��ѯ��������
		criteria.setAltitudeRequired(false);
		// �Ƿ��ѯ��λ�� : ��
		criteria.setBearingRequired(false);
		// �Ƿ������ѣ���
		criteria.setCostAllowed(true);
		// ����Ҫ�󣺵�
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// ��������ʵķ���������provider����2������Ϊtrue˵�� , ���ֻ��һ��provider����Ч��,�򷵻ص�ǰprovider
		return mLocationManager.getBestProvider(criteria, true);
	}
	
	private LocationListener locationListener = new LocationListener() {
		// λ�÷����ı�����
		public void onLocationChanged(Location location) {
			mLocation = location;
		}

		// provider ���û��رպ����
		public void onProviderDisabled(String provider) {
			mLocation = null;
		}

		// provider ���û����������
		public void onProviderEnabled(String provider) {
			mLocation = mLocationManager.getLastKnownLocation(provider);
			while (mLocation == null) {
				mLocation = mLocationManager.getLastKnownLocation(provider);
			}
		}

		// provider ״̬�仯ʱ����
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	};
}
