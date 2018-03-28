package com.aicyber.loaction;

import com.aicyber.util.Logger;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.animation.IntArrayEvaluator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.StaticLayout;

public class LocationService implements BDLocationListener{

	private static LocationClient locationClient;
	private BDLocation bdLocation;          //百度地图定位信息  
	private static LocationService mInstance = null;
	private static String mCityName = "";
	private static Context mContex;
	
	public static LocationService getInstance()
	{
		if (null == mInstance) {
			mInstance = new LocationService();
		}
		return mInstance;
	}
	
	private LocationService()
	{
		
	}
	/**
	 * @author Jackie 百度地图定位初始化
	 */
	public void initLocation(Context context) 
	{
		mContex = context;
		locationClient = new LocationClient(context);
		locationClient.registerLocationListener(this); // 设置地图定位回调监听
		locationClient.requestOfflineLocation();
		reLocation();
	}
	
	public void reLocation(){
		LocationClientOption option = new LocationClientOption();
		// 设置定位模式
		// Hight_Accuracy高精度、Battery_Saving低功耗、Device_Sensors仅设备(GPS)
		option.setLocationMode(LocationMode.Hight_Accuracy);
					
		// 返回的定位结果是百度经纬度,默认值gcj02
		// 国测局经纬度坐标系gcj02、百度墨卡托坐标系bd09、百度经纬度坐标系bd09ll
		option.setCoorType("gcj02");
						
		// 返回的定位结果包含地址信息
		option.setIsNeedAddress(true);
		option.setScanSpan(10000);//设置发起定位请求的间隔时间为5000ms
		// 不设置或设置数值小于1000ms标识只定位一次
		// option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		locationClient.setLocOption(option);

		locationClient.start();
	}
	
	public String getCityName()
	{
		return mCityName;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		if (null == location) {
			if (bdLocation != null) {
				bdLocation = null;
			}
			Logger.debug("定位失败:location is null");
			return;
		}
		/**
		 * 61 ： GPS定位结果 
		 * 62 ： 扫描整合定位依据失败。此时定位结果无效。
		 * 63 ：网络异常，没有成功向服务器发起请求。此时定位结果无效。 
		 * 65 ： 定位缓存的结果。
		 * 66 ：离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
		 * 67 ：离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
		 * 68 ：网络连接失败时，查找本地离线定位时对应的返回结果 
		 * 161： 表示网络定位结果
		 * 162~167： 服务端定位失败 
		 * 502：key参数错误
		 * 505：key不存在或者非法 
		 * 601：key服务被开发者自己禁用
		 * 602：key mcode不匹配 501～700：key验证失败
		 */
		int code = location.getLocType();
		if (code == 161) {
			this.bdLocation = location;
			mCityName = location.getCity();
			double latitude = location.getLatitude();
			double lontitude = location.getLongitude();
			String address = location.getAddrStr();
			Logger.debug(mCityName + ",(latitude,lontitude) (" + latitude + "," + lontitude + "),address " + address);
			//requestWeather();
			locationClient.stop();
		} else {
			if (bdLocation != null) {
				bdLocation = null;
			}
			mCityName = "北京";
			Logger.debug("定位失败:code=" + code);
		}
	}
}
