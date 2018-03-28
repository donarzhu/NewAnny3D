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
	private BDLocation bdLocation;          //�ٶȵ�ͼ��λ��Ϣ  
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
	 * @author Jackie �ٶȵ�ͼ��λ��ʼ��
	 */
	public void initLocation(Context context) 
	{
		mContex = context;
		locationClient = new LocationClient(context);
		locationClient.registerLocationListener(this); // ���õ�ͼ��λ�ص�����
		locationClient.requestOfflineLocation();
		reLocation();
	}
	
	public void reLocation(){
		LocationClientOption option = new LocationClientOption();
		// ���ö�λģʽ
		// Hight_Accuracy�߾��ȡ�Battery_Saving�͹��ġ�Device_Sensors���豸(GPS)
		option.setLocationMode(LocationMode.Hight_Accuracy);
					
		// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		// ����־�γ������ϵgcj02���ٶ�ī��������ϵbd09���ٶȾ�γ������ϵbd09ll
		option.setCoorType("gcj02");
						
		// ���صĶ�λ���������ַ��Ϣ
		option.setIsNeedAddress(true);
		option.setScanSpan(10000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		// �����û�������ֵС��1000ms��ʶֻ��λһ��
		// option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
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
			Logger.debug("��λʧ��:location is null");
			return;
		}
		/**
		 * 61 �� GPS��λ��� 
		 * 62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч��
		 * 63 �������쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч�� 
		 * 65 �� ��λ����Ľ����
		 * 66 �����߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
		 * 67 �����߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
		 * 68 ����������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ�� 
		 * 161�� ��ʾ���綨λ���
		 * 162~167�� ����˶�λʧ�� 
		 * 502��key��������
		 * 505��key�����ڻ��߷Ƿ� 
		 * 601��key���񱻿������Լ�����
		 * 602��key mcode��ƥ�� 501��700��key��֤ʧ��
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
			mCityName = "����";
			Logger.debug("��λʧ��:code=" + code);
		}
	}
}
