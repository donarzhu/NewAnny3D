package com.aicyber.fragment;

import com.aicyber.alter.BaseActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


public class BaseFragment extends Fragment{

	protected FragmentManager mFragmentManager;
	protected BaseActivity mBaseActvity;
	protected View mView;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mFragmentManager = getFragmentManager();
		mBaseActvity = (BaseActivity) getActivity();
		mBaseActvity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater paramLayoutInflater,
			ViewGroup paramViewGroup, Bundle paramBundle) {
		// TODO Auto-generated method stub
		if (getLayoutId() <= 0) {
			return null;
		}
		View localView = paramLayoutInflater.inflate(getLayoutId(),
				paramViewGroup, false);
		initViews(localView);
		mView = localView;
		return localView;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	protected int getLayoutId() {
		return -1;
	}
	
	protected void initViews(View localView) {

	}
}
