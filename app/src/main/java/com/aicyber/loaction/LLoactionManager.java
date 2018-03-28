package com.aicyber.loaction;
/****
 * 
 * @author zhisheng
 * 获取定位的类
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
		//设置刷新监听
		mLocationManager.requestLocationUpdates(providerType, 2000, 10, locationListener);
		
		//得到最新的位置
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
			double lat = mLocation.getLatitude();   //纬度
			double lng = mLocation.getLongitude();  //精度
			Geocoder gc = new Geocoder(mContext, Locale.getDefault());
			try {
				// 取得地址相关的一些信息\经度、纬度
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
			cityName = "天津";
		}

		//mLocationManager.removeUpdates(locationListener);
		return cityName;
	}

	
	// 获取Location Provider
	private String getProvider() {
		// 构建位置查询条件
		Criteria criteria = new Criteria();
		// 查询精度：高
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//设置是否要求速度
        criteria.setSpeedRequired(false);
		// 是否查询海拨：否
		criteria.setAltitudeRequired(false);
		// 是否查询方位角 : 否
		criteria.setBearingRequired(false);
		// 是否允许付费：是
		criteria.setCostAllowed(true);
		// 电量要求：低
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
		return mLocationManager.getBestProvider(criteria, true);
	}
	
	private LocationListener locationListener = new LocationListener() {
		// 位置发生改变后调用
		public void onLocationChanged(Location location) {
			mLocation = location;
		}

		// provider 被用户关闭后调用
		public void onProviderDisabled(String provider) {
			mLocation = null;
		}

		// provider 被用户开启后调用
		public void onProviderEnabled(String provider) {
			mLocation = mLocationManager.getLastKnownLocation(provider);
			while (mLocation == null) {
				mLocation = mLocationManager.getLastKnownLocation(provider);
			}
		}

		// provider 状态变化时调用
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	};
}
