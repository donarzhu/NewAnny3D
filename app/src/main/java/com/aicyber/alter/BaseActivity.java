package com.aicyber.alter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class BaseActivity extends FragmentActivity {

	protected FragmentManager mFragmentManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//mFragmentManager = getSupportFragmentManager();
	}
	
	protected void addFragment(Fragment fragment) {
		addFragment(android.R.id.content, fragment);
	}

	protected void addFragment(int layoutId, Fragment fragment) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.add(layoutId, fragment);
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
	}

	protected void addFragment(int layoutId, Fragment fragment, String label) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.add(layoutId, fragment, label);
		transaction.commit();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/**
	 * activity ÇÐ»»
	 * @param cls
	 */
	protected void forwordNextActivity(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		startActivity(intent);
		overridePendingTransition(R.anim.push_in, R.anim.push_down);
	}
}
