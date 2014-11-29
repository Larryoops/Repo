/**
 *  @auther : Larryoops, Edgejerry
 *  @version 1.5
 *  primary activity : information.class
 *  secondary activity : MainActivity.class
 *  floating view manager : MyWindowManager.class
 *  view : draw.class, FloatWindowSmallView.class, BigWindow.class
 *  service : FloatWindowService.class, WifiService.class
 *  object : WifiInformation.class, FinalInformation.class, Node.class, Signal.class
 *  layout : activity_main.xml(information.class), newlayout.xml(MainActivity.class)
 */
package com.example.wifiscanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.MathException;

import com.google.ads.*;

import will.file.FileIO;
import will.service.PedometerService;
import will.service.PedometerService.PedometerBinder;
import will.service.WifiService;
import android.R.menu;
import android.R.string;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private WifiService mWifiAdmin;
	private List<ScanResult> list; // 掃描結果列表
	private ScanResult mScanResult;
	private StringBuffer sb = new StringBuffer();
	private String info_show = "";
	private static final String TAG = "PDRActivity";
	boolean mBound = false;
	boolean sensorStoped = true;

	int stepNum = 0;
	float totalLength = 0;
	float strideLength = 0;
	double orientation = 0.0;
	float velocity = 0;

	int accuracyStepNum = 0;
	float accuracyTotalLength = 0;
	float accuracyStrideLength = 0;
	float accuracyVelocity = 0;

	private static boolean run = false;
	private Handler handler = new Handler();

	private Button start_button = null;
	private Button map_button = null;
	private Button setting_button = null;
	private Button wifi_button = null;
	private Button input_button = null;
	private Button information_button = null;
	
	//private AdView adView;

	TextView txtCount;
	String msgg = "0";
	public WifiManager WiFiManager;
	public static final int openWifi = 1, closeWifi = 2, view = 3, setting = 4;


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newlayout);

		

		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); for custom title bar
		
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlerow);
		
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		start_button = (Button) findViewById(R.id.start_button);
		start_button.setEnabled(true);
		start_button.setOnClickListener(this);
		//start_button.setOnTouchListener((OnTouchListener) this);

		
		setting_button = (Button) findViewById(R.id.setting_button);
		setting_button.setEnabled(true);
		setting_button.setOnClickListener(this);
		//setting_button.setOnTouchListener((OnTouchListener) this);

		wifi_button = (Button) findViewById(R.id.wifi_button);
		wifi_button.setEnabled(true);
		wifi_button.setOnClickListener(this);
		//wifi_button.setOnTouchListener((OnTouchListener) this);

		map_button = (Button) findViewById(R.id.map_button);
		map_button.setEnabled(true);
		map_button.setOnClickListener(this);
		//map_button.setOnTouchListener((OnTouchListener) this);

		input_button = (Button) findViewById(R.id.input_button);
		input_button.setEnabled(true);
		input_button.setOnClickListener(this);
		//input_button.setOnTouchListener((OnTouchListener) this);

		information_button = (Button) findViewById(R.id.information_button);
		information_button.setEnabled(true);
		information_button.setOnClickListener(this);
		//information_button.setOnTouchListener((OnTouchListener) this);

		mWifiAdmin = new WifiService(MainActivity.this);

		if (WiFiManager.isWifiEnabled()) {
			wifi_button.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.wifion_selector));
		}
		else
		{
			wifi_button.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.wifi_selector));
		}
		
		//adView = (AdView)findViewById(R.id.adView);
	    //AdRequest adRequest = new AdRequest();
	    //adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
	    // Start loading the ad in the background.
	   // adView.loadAd(adRequest);

	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (WiFiManager.isWifiEnabled()) {
			wifi_button.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.wifion_selector));
		}
		else{
			wifi_button.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.wifi_selector));
		}
		//adView = (AdView)findViewById(R.id.adView);
	    //AdRequest adRequest = new AdRequest();
	    //adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
	    // Start loading the ad in the background.
	    //adView.loadAd(adRequest);
	
	}

	public void onClick(View view) {
		Log.i(TAG, "onClick()ing...");
		switch (view.getId()) {
		case R.id.start_button:
			/*
			Log.i(TAG, "PDRActivity onStartClick()......");
			run = true;
			handler.postDelayed(task, 1000);
*/
			Intent q = new Intent();
			q.setClass(MainActivity.this, scan_info_view.class);
			startActivity(q);
			break;
		case R.id.setting_button:
			Intent i = new Intent(this, Setting.class);
			startActivity(i);
			break;
		case R.id.wifi_button:
			if (!WiFiManager.isWifiEnabled()) {
				Toast.makeText(this, "Wi-Fi 開啟中...", Toast.LENGTH_LONG).show();
				WiFiManager.setWifiEnabled(true);
				wifi_button.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.wifion_selector));
			} else if (WiFiManager.isWifiEnabled()) {
				Toast.makeText(this, "WiFi 關閉中...", Toast.LENGTH_SHORT).show();
				WiFiManager.setWifiEnabled(false);
				wifi_button.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.wifi_selector));
			}
			break;
		case R.id.map_button:// primary
			Intent wintent = new Intent();
			wintent.setClass(MainActivity.this, information.class);
			if (!WiFiManager.isWifiEnabled()) {
				WiFiManager.setWifiEnabled(true);
			}
			startActivity(wintent);
			break;
		case R.id.information_button:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setIcon(getResources().getDrawable(R.drawable.ic_mood_grey600_48dp));
			dialog.setTitle("關於");
			dialog.setMessage("Version：WiMap 5.8.4.1.20\n\nAuther : Larryoops, Edgejerry\n");
			dialog.setCancelable(true);

			dialog.show();
			break;
		case R.id.input_button:
			Intent iintent = new Intent();
			iintent.setClass(MainActivity.this, ShowInputActivity.class);
			
			startActivity(iintent);
			break;
		}
	}

	// Menu選單
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, openWifi, 1, "開啟 Wi-Fi");
		menu.add(0, closeWifi, 2, "關閉 Wi-Fi");
		menu.add(0,3,3,"目前連線的Wi-Fi");
		menu.add(0, setting, 4, "設定");
		return true;
	}

	// 選單動作
	public boolean onOptionsItemSelected(MenuItem item) {
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		switch (item.getItemId()) {
		case 1:
			// 若wifi狀態為關閉則將它開啟
			if (!WiFiManager.isWifiEnabled()) {
				Toast.makeText(this, "Wi-Fi 開啟中...", Toast.LENGTH_LONG).show();
				WiFiManager.setWifiEnabled(true);
				wifi_button.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.wifion_selector));
			}
			break;
		case 2:
			// 若wifi狀態為開啟則將它關閉
			if (WiFiManager.isWifiEnabled()) {
				Toast.makeText(this, "WiFi 關閉中...", Toast.LENGTH_SHORT).show();
				WiFiManager.setWifiEnabled(false);
				wifi_button.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.wifi_selector));
			}
			break;
		case 3:
			Intent ic = new Intent();
			ic.setClass(MainActivity.this, ConnectedWifiActivity.class);
			startActivity(ic);
			break;
		case 4:
			// switch to setting.class
			Intent i = new Intent(this, Setting.class);
			startActivity(i);
			break;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "PDRActivity onDestroy()......");
		super.onDestroy();
	}

	public static void stopScan() {
		run = false;
	}

	public void getAllNetWorkList() {
		if (sb != null)// 每次點擊掃描之前清空上一次的掃描結果
		{
			sb = new StringBuffer();
		}
		mWifiAdmin.startScan();// 開始掃描網絡
		list = mWifiAdmin.getwifilist();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				mScanResult = list.get(i); // 得到掃描結果
				int channel = 0;
				switch (mScanResult.frequency) {
				case 2412:
					channel = 1;
					break;
				case 2417:
					channel = 2;
					break;
				case 2422:
					channel = 3;
					break;
				case 2427:
					channel = 4;
					break;
				case 2432:
					channel = 5;
					break;
				case 2437:
					channel = 6;
					break;
				case 2442:
					channel = 7;
					break;
				case 2447:
					channel = 8;
					break;
				case 2452:
					channel = 9;
					break;
				case 2457:
					channel = 10;
					break;
				case 2462:
					channel = 11;
					break;
				case 2467:
					channel = 12;
					break;
				case 2472:
					channel = 13;
					break;
				case 2484:
					channel = 14;
					break;

				}
				sb = sb.append(
						(i + 1) + ".\n" + "SSID: " + mScanResult.SSID + "\n")
						.append("Mac address:\n" + mScanResult.BSSID + "\n")
						.append("Frequency:" + mScanResult.frequency + "\n")
						.append("訊號強度:" + mScanResult.level + "dBm\n")
						.append("Channel:" + channel + "\n\n");
			}
			info_show = "掃描到的Wi-Fi網路(共" + list.size() + "個)：\n" + sb.toString();

		} else {
			Dialog dialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle("錯誤!!")
					.setMessage("WiFi功能不正常或未開啟")
					.setPositiveButton("確定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}

							}).create();
		}
	}
}