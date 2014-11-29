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

import java.lang.reflect.Field;

import will.service.PedometerService;

import android.app.AlertDialog;
import android.content.Context;
import android.net.TrafficStats;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatWindowSmallView extends LinearLayout {

	PedometerService mService;

	public static int viewWidth;
	public static int viewHeight;
	private static int statusBarHeight;
	private WindowManager windowManager;
	private WindowManager.LayoutParams mParams;
	private float xInScreen;
	private float yInScreen;
	private float xDownInScreen;
	private float yDownInScreen;
	private float xInView;
	private float yInView;

	public FloatWindowSmallView(Context context) {
		super(context);
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.floating, this);
		View view = findViewById(R.id.FloatWindowLayout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		TextView FloatView = (TextView) findViewById(R.id.FloatNode);
		FloatView.setText(" «Ý©R");

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - getStatusBarHeight();
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();

			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
				openBigWindow();
			}
			break;
		default:
			break;
		}
		return true;
	}

	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}

	private void openBigWindow() {
		MyWindowManager.createBigWindow(getContext());
		MyWindowManager.removeSmallWindow(getContext());
	}

	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}

}
