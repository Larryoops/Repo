package com.example.wifiscanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import will.service.FloatWindowService;
import will.service.WifiService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ShowInputActivity extends Activity {

	private ShowInput showinput;
	private WifiInfo WiFiInfo;
	private WifiManager WiFiManager;
	private String MAC_now = "";
	private String SSID_now = "";
	private boolean run = false;
	private Handler handler = new Handler();
	private ProgressDialog progressDialog;
	LinkedList<WifiInformation> wifiInformation = new LinkedList<WifiInformation>();
	LinkedList<Node> points = new LinkedList<Node>();
	String StartTime = "ERROR";
	int FilterDifference = -100;
	
	private Button analyze_button;




	/* Preference parameter */
	private boolean DisplayNode = true;
	private boolean DisplayPath = true;
	private boolean NoStopPath = false;
	private int VoronoiNode = 3;
	private boolean VDisplayPath = false;
	private int DifferenceFilter = 10;
	private int PathLoss = 2;
	private int DefaultRSSI = 50;
	private boolean VDisplayWifiCircle = false;
	private int DelayTime = 1000;
	private int StrideMultiply = 30;
	private boolean VDisplayNOCALNode = true;
	private boolean VDisplayNode = true;
	private boolean VDisplayPossibleAP = false;
	private boolean DisplayRuler = true;
	/* Preference parameter */

	public final int setting = 1;
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// WiFiManager.enableNetwork(NETWORKID, true);//重連WiFi<<測試用
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// NETWORKID = WiFiInfo.getNetworkId();
			// WiFiManager.disconnect();//關掉WiFi<<測試用
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出頁面",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {

				if (run) {
					run = false;

				} else {

				}


				super.onDestroy();
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, setting, 1, "設定");
		menu.add(0,2,2,"分析");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent i = new Intent(this, Setting.class);
			startActivity(i);
			break;
		case 2:
			String[] ChannelAP={"0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
			ArrayList<String> SSIDList=new ArrayList<String>();
			
			for(int ChannelCount=0;ChannelCount<wifiInformation.size();ChannelCount++)
			{
				switch(wifiInformation.get(ChannelCount).getChannel())
				{
				case 1:
					ChannelAP[0]=String.valueOf(Integer.parseInt(ChannelAP[0])+1);
					break;
				case 2:
					ChannelAP[1]=String.valueOf(Integer.parseInt(ChannelAP[1])+1);
					break;
				case 3:
					ChannelAP[2]=String.valueOf(Integer.parseInt(ChannelAP[2])+1);
					break;
				case 4:
					ChannelAP[3]=String.valueOf(Integer.parseInt(ChannelAP[3])+1);
					break;
				case 5:
					ChannelAP[4]=String.valueOf(Integer.parseInt(ChannelAP[4])+1);
					break;
				case 6:
					ChannelAP[5]=String.valueOf(Integer.parseInt(ChannelAP[5])+1);
					break;
				case 7:
					ChannelAP[6]=String.valueOf(Integer.parseInt(ChannelAP[6])+1);
					break;
				case 8:
					ChannelAP[7]=String.valueOf(Integer.parseInt(ChannelAP[7])+1);
					break;
				case 9:
					ChannelAP[8]=String.valueOf(Integer.parseInt(ChannelAP[8])+1);
					break;
				case 10:
					ChannelAP[9]=String.valueOf(Integer.parseInt(ChannelAP[9])+1);
					break;
				case 11:
					ChannelAP[10]=String.valueOf(Integer.parseInt(ChannelAP[10])+1);
					break;
				case 12:
					ChannelAP[11]=String.valueOf(Integer.parseInt(ChannelAP[11])+1);
					break;
				case 13:
					ChannelAP[12]=String.valueOf(Integer.parseInt(ChannelAP[12])+1);
					break;
				case 14:
					ChannelAP[13]=String.valueOf(Integer.parseInt(ChannelAP[13])+1);
					break;
					default:
						
				}
			}
			
			for(int ApSSID=0;ApSSID<wifiInformation.size();ApSSID++)
			{
				SSIDList.add(wifiInformation.get(ApSSID).getSSID());
				
			}
			String[] APSSID=new String[SSIDList.size()];
			APSSID=SSIDList.toArray(APSSID);
			
			Intent intent=new Intent(this,AnalyzeActivity.class);
			intent.putExtra("ChannelAP", ChannelAP);
			intent.putExtra("APSSID", APSSID);
			startActivity(intent);

			break;
		}

		return true;
	}

	private Runnable task = new Runnable() { // no use

		public void run() {
			if (run) {
				/* 取得現在連線的WiFi AP 資訊 */
				WiFiInfo = WiFiManager.getConnectionInfo();
				MAC_now = WiFiInfo.getBSSID();
				SSID_now = WiFiInfo.getSSID();

				System.out.println("" + SSID_now + " " + MAC_now);

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.inputview);
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		showinput = (ShowInput) findViewById(R.id.show_input);
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // get file
		// action
		
		File picFile = new File(Environment.getExternalStorageDirectory()
				+ "/WifiScanner/DATA/");
		intent.setDataAndType(Uri.fromFile(picFile), "text/plain"); // set
		// file
		// type
		final Intent Dintent=new Intent(this,AnalyzeActivity.class);
		analyze_button=(Button)findViewById(R.id.analyze_button);
		analyze_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String[] ChannelAP={"0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
				ArrayList<String> SSIDList=new ArrayList<String>();
				
				for(int ChannelCount=0;ChannelCount<wifiInformation.size();ChannelCount++)
				{
					switch(wifiInformation.get(ChannelCount).getChannel())
					{
					case 1:
						ChannelAP[0]=String.valueOf(Integer.parseInt(ChannelAP[0])+1);
						break;
					case 2:
						ChannelAP[1]=String.valueOf(Integer.parseInt(ChannelAP[1])+1);
						break;
					case 3:
						ChannelAP[2]=String.valueOf(Integer.parseInt(ChannelAP[2])+1);
						break;
					case 4:
						ChannelAP[3]=String.valueOf(Integer.parseInt(ChannelAP[3])+1);
						break;
					case 5:
						ChannelAP[4]=String.valueOf(Integer.parseInt(ChannelAP[4])+1);
						break;
					case 6:
						ChannelAP[5]=String.valueOf(Integer.parseInt(ChannelAP[5])+1);
						break;
					case 7:
						ChannelAP[6]=String.valueOf(Integer.parseInt(ChannelAP[6])+1);
						break;
					case 8:
						ChannelAP[7]=String.valueOf(Integer.parseInt(ChannelAP[7])+1);
						break;
					case 9:
						ChannelAP[8]=String.valueOf(Integer.parseInt(ChannelAP[8])+1);
						break;
					case 10:
						ChannelAP[9]=String.valueOf(Integer.parseInt(ChannelAP[9])+1);
						break;
					case 11:
						ChannelAP[10]=String.valueOf(Integer.parseInt(ChannelAP[10])+1);
						break;
					case 12:
						ChannelAP[11]=String.valueOf(Integer.parseInt(ChannelAP[11])+1);
						break;
					case 13:
						ChannelAP[12]=String.valueOf(Integer.parseInt(ChannelAP[12])+1);
						break;
					case 14:
						ChannelAP[13]=String.valueOf(Integer.parseInt(ChannelAP[13])+1);
						break;
						default:
							
					}
				}
				
				for(int ApSSID=0;ApSSID<wifiInformation.size();ApSSID++)
				{
					SSIDList.add(wifiInformation.get(ApSSID).getSSID());
					
				}
				String[] APSSID=new String[SSIDList.size()];
				APSSID=SSIDList.toArray(APSSID);
				
				
				Dintent.putExtra("ChannelAP", ChannelAP);
				Dintent.putExtra("APSSID", APSSID);
				startActivity(Dintent);
			}
		});
		
		Intent SelectIntent = Intent.createChooser(intent, "Select Imported File");
		startActivityForResult(SelectIntent, 0);
	}

	

	protected void onResume()// set preference
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		DisplayNode = preferences.getBoolean("DisplayNode", true);
		DisplayPath = preferences.getBoolean("DisplayPath", true);
		NoStopPath = preferences.getBoolean("NoStopPath", false);
		VoronoiNode = Integer.parseInt(preferences
				.getString("VoronoiNode", "3"));
		DifferenceFilter = Integer.parseInt(preferences.getString(
				"DifferenceFilter", "10"));
		PathLoss = Integer.parseInt(preferences.getString("PathLoss", "2"));
		DefaultRSSI = Integer.parseInt(preferences.getString("DefaultRSSI",
				"50"));
		VDisplayPath = preferences.getBoolean("VDisplayPath", true);
		VDisplayWifiCircle = preferences
				.getBoolean("VDisplayWifiCircle", false);
		DelayTime = (int) (1000 * Double.parseDouble((preferences.getString(
				"Frequency", "0.1")).replace("秒", "")));
		StrideMultiply = Integer.parseInt(preferences.getString(
				"StrideMultiply", "30"));// no need send to draw.class
		VDisplayNOCALNode = preferences.getBoolean("VDisplayNOCALNode", false);
		VDisplayNode = preferences.getBoolean("VDisplayNode", true);
		VDisplayPossibleAP = preferences
				.getBoolean("VDisplayPossibleAP", false);

		showinput.PreferenceSetting(DisplayRuler, VDisplayPossibleAP,
				DisplayNode, DisplayPath, NoStopPath, VoronoiNode,
				VDisplayPath, VDisplayWifiCircle, VDisplayNOCALNode,
				VDisplayNode, DifferenceFilter, PathLoss, DefaultRSSI,
				StrideMultiply);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)// get input file
		{
			Uri uri = data.getData();// get file url
			if (uri != null) {

				File InputFile = new File(uri.getPath());
				FileReader fr;
				try {
					fr = new FileReader(InputFile);
					BufferedReader br = new BufferedReader(fr);

					String temp = br.readLine();
					if (!Character.isDigit(temp.charAt(0))) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								this);
						dialog.setIcon(getResources().getDrawable(
								R.drawable.ic_error_grey600_48dp));
						dialog.setTitle("錯誤");
						dialog.setMessage("匯入檔案格式錯誤！");
						DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								finish();

							}

						};

						dialog.setNegativeButton("關閉", CancelClick);
						dialog.setCancelable(false);
						dialog.show();
					} else {
						progressDialog = new ProgressDialog(
								ShowInputActivity.this);
						progressDialog = ProgressDialog.show(
								ShowInputActivity.this, "", "匯入資料中...");
						int NodeCount = Integer.parseInt(temp);
						int ApCount = Integer.parseInt(br.readLine());

						int PathLoss = -100;
						int DefaultRSSI = -100;
						int FilterDifference = -100;
						while ((temp = br.readLine()) != null) {
							if (temp.length() == 0) {

								continue;
							}
							if (temp.contains("開始時間")) {
								StartTime = temp.substring(
										temp.indexOf(": ") + 2)
										.replace("<", "");
							} else if (temp.contains("路徑消耗")) {
								PathLoss = Integer.parseInt(temp.substring(
										temp.indexOf(": ") + 2)
										.replace("<", ""));
							} else if (temp.contains("訊號強度參考")) {
								DefaultRSSI = Integer.parseInt(temp.substring(
										temp.indexOf(": ") + 2)
										.replace("<", ""));
							} else if (temp.contains("篩選")) {
								FilterDifference = Integer.parseInt(temp
										.substring(temp.indexOf(": ") + 2)
										.replace("<", ""));
							} else if (temp.charAt(0) == ('N')) {

								String ApName = temp.substring(
										temp.indexOf(":") + 1).replace("<", "");
								String ApTemp = br.readLine();
								String ApMac = ApTemp.substring(
										ApTemp.indexOf(":") + 1).replace("<",
										"");
								
								WifiInformation readObject = new WifiInformation(
										ApMac, ApName);

								ApTemp = br.readLine();
								
								if(ApTemp.charAt(0)=='C')
								{
									int ApChannel=Integer.parseInt(ApTemp.substring(ApTemp.indexOf(":")+1).replace("<", ""));
									readObject.setChannel(ApChannel);
									
									ApTemp = br.readLine();
								}
								
								
								if (ApTemp.charAt(2) == ('未')) {
									readObject.NoNode = true;
								} else if (ApTemp.charAt(2) == ('兩')) {
									readObject.PossibleNode = true;
									int x1, x2, y1, y2;
									String FirstDim = ApTemp.substring(
											ApTemp.indexOf(":(") + 2,
											ApTemp.indexOf(") &"));
									String SecondDim = ApTemp.substring(
											ApTemp.indexOf("& (") + 3,
											ApTemp.indexOf(")<"));

									x1 = Integer.parseInt(FirstDim.substring(0,
											FirstDim.indexOf(",")));
									y1 = Integer
											.parseInt(FirstDim
													.substring(FirstDim
															.indexOf(",") + 1));
									x2 = Integer.parseInt(SecondDim.substring(
											0, SecondDim.indexOf(",")));
									y2 = Integer
											.parseInt(SecondDim
													.substring(SecondDim
															.indexOf(",") + 1));
									readObject.SetPosition(x1, y1);
									readObject.SetSecondPosition(x2, y2);
								} else if (ApTemp.charAt(2) == ('已')) {
									readObject.AlreadyCal = true;
									String Dim = ApTemp.substring(
											ApTemp.indexOf(":(") + 2,
											ApTemp.indexOf(")<"));
									int x, y;
									x = Integer.parseInt(Dim.substring(0,
											Dim.indexOf(",")));
									y = Integer.parseInt(Dim.substring(Dim
											.indexOf(",") + 1));
									;
									readObject.SetPosition(x, y);
								}
								ApTemp = br.readLine();
								int ApRecordCount = Integer.parseInt(ApTemp
										.substring(ApTemp.indexOf("(") + 1,
												ApTemp.indexOf("個)")));
								int CountFlag = 0;

								ApTemp = br.readLine();
								while (ApRecordCount > CountFlag) {
									if (ApTemp.contains("紀錄編號")) {
										Signal Sig = new Signal();
										int RecordNumber = Integer
												.parseInt(ApTemp.substring(ApTemp
														.indexOf(":") + 1));
										ApTemp = br.readLine();
										ApTemp = br.readLine();
										int RecordRSSI = Integer
												.parseInt(ApTemp.substring(ApTemp
														.indexOf(":") + 1));
										ApTemp = br.readLine();
										double RecordDistance = Double
												.parseDouble(ApTemp.substring(
														ApTemp.indexOf(":") + 1,
														ApTemp.indexOf(" m")));
										Sig.SetAll(RecordRSSI, RecordNumber,
												RecordDistance);
										readObject.getSignal().add(Sig);

										CountFlag++;
									}
									ApTemp = br.readLine();
								}
								wifiInformation.add(readObject);

							} else if (temp.contains("移動路徑座標")) {
								int NodeRecordCount = Integer.parseInt(temp
										.substring(temp.indexOf("(") + 1,
												temp.indexOf("個)")));

								int CountFlag = 0;
								String NodeTemp = br.readLine();
								while (NodeRecordCount > CountFlag) {
									if (NodeTemp.contains(": (")) {
										Node readNodeObject = new Node(
												Integer.parseInt(NodeTemp.substring(
														NodeTemp.indexOf("(") + 1,
														NodeTemp.indexOf(","))),
												Integer.parseInt(NodeTemp.substring(
														NodeTemp.indexOf(",") + 1,
														NodeTemp.indexOf(")"))));
										points.add(readNodeObject);
										CountFlag++;
									}
									NodeTemp = br.readLine();
								}

							}

						}
						WiFiInfo = WiFiManager.getConnectionInfo();
						MAC_now = WiFiInfo.getBSSID();
						SSID_now = WiFiInfo.getSSID();
						System.out.println("" + SSID_now + " " + MAC_now);
						/* TEST */
						/*
						 * System.out.println("TEST DUMP!!");
						 * System.out.println("Node Count:" + NodeCount);
						 * System.out.println("Ap Count:" + ApCount);
						 * System.out.println("Start Time:" + StartTime);
						 * System.out.println("PathLoss:" + PathLoss);
						 * System.out.println("DefaultRSSI:" + DefaultRSSI);
						 * System.out.println("FilterDifference:" +
						 * FilterDifference); System.out.println("AP(" +
						 * wifiInformation.size() + "):"); for (int ApDump = 0;
						 * ApDump < wifiInformation.size(); ApDump++) {
						 * System.out.print("No." + (ApDump + 1) + " ");
						 * System.out
						 * .println(wifiInformation.get(ApDump).Dump()); }
						 * System.out.println("Node(" + points.size() + "):");
						 * for (int NodeDump = 0; NodeDump < points.size();
						 * NodeDump++) { System.out.print("No." + (NodeDump + 1)
						 * + " ");
						 * System.out.println(points.get(NodeDump).Dump()); }
						 */
						showinput.SendRecord(points, wifiInformation,
								StartTime, PathLoss, DefaultRSSI,
								FilterDifference, MAC_now, SSID_now);
						progressDialog.dismiss();

					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(this, "無效的路徑", Toast.LENGTH_SHORT).show();
			}
		} else {
			finish();
			Toast.makeText(this, "取消匯入檔案", Toast.LENGTH_SHORT).show();
		}
	}
}
