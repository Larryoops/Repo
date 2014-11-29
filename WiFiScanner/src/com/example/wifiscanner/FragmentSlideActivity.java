package com.example.wifiscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class FragmentSlideActivity extends FragmentActivity {
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_slide);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When changing pages, reset the action bar actions since they
				// are dependent
				// on which page is currently active. An alternative approach is
				// to have each
				// fragment expose actions itself (rather than the activity
				// exposing actions),
				// but for simplicity, the activity provides the actions in this
				// sample.
				invalidateOptionsMenu();
			}
		});
	}
*/

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// �p�G�Τ��e���b�ݲĤ@�B�]�]�N�O�Ĥ@���^�A���N�n���t�ΨӳB�z��^���s�C
			// �o�ӬO�����]finish()�^��e���ʨüu�X�^�h�̡C
			super.onBackPressed();
		} else {
			// �_�h�A��^�e�@��
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}
/*
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position) {

            case 0: return MainActivity.newInstance("FirstFragment, Instance 1");
            case 1: return ConnectedWifiActivity.newInstance("SecondFragment, Instance 1");
            }
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
*/
}
