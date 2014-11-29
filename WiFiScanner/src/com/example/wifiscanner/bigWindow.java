/**
 *  @auther : Larryoops, Edgejerry
 *  @version 1.5
 *  primary activity : information.class
 *  secondary activity : MainActivity.class
 *  floating view manager : MyWindowManager.class(manage FloatWindowSmallView & BigWindow.class)
 *  view : draw.class, FloatWindowSmallView.class, BigWindow.class
 *  service : FloatWindowService.class, WifiService.class
 *  object : WifiInformation.class(information.class), Node.class(information.class),, Signal.class(information.class),, FinalInformation.class(MyWindowManager.class)
 *  layout : activity_main.xml(information.class), newlayout.xml(MainActivity.class)
 */
package com.example.wifiscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class bigWindow extends LinearLayout {
	public static int viewWidth;

	public static int viewHeight;

	public bigWindow(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.bigfloating, this);

		View view = findViewById(R.id.big_window_layout);

		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		setFocusableInTouchMode(true);
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				Rect rect = new Rect();
				getGlobalVisibleRect(rect);
				if (!rect.contains(x, y)) {
					MyWindowManager.removeBigWindow(context);
					MyWindowManager.createSmallWindow(context);
				}
				return false;
			}
		});

		/*
		 * close.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * MyWindowManager.removeBigWindow(context);
		 * MyWindowManager.removeSmallWindow(context); Intent intent = new
		 * Intent(getContext(), FloatWindowService.class);
		 * context.stopService(intent); } }); back.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * MyWindowManager.removeBigWindow(context);
		 * MyWindowManager.createSmallWindow(context); } });
		 */

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			MyWindowManager.removeBigWindow(getContext());
			MyWindowManager.createSmallWindow(getContext());

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();
		Rect rect = new Rect();
		getGlobalVisibleRect(rect);
		if (!rect.contains(x, y)) {
			MyWindowManager.removeBigWindow(getContext());
			MyWindowManager.createSmallWindow(getContext());
		}
		return true;
	}
}
