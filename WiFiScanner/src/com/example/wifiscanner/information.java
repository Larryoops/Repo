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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import will.service.FloatWindowService;
import will.service.PedometerService;
import will.service.WifiService;
import will.service.PedometerService.PedometerBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public class information extends Activity {

	/* service */
	PedometerService mService;
	WifiService mWifiAdmin;
	/* service */

	/* View */
	private draw mdraw;
	/* View */

	/* PDR */
	int stepNum = 0;
	float totalLength = 0;
	float strideLength = 0;
	String SSID_now = "";
	String MAC_now = "";
	int NETWORKID = -1;
	double orientation = 0.0;
	float velocity = 0;
	boolean mBound = false;
	boolean sensorStoped = true;
	int stepn = 0;// �U���`�B��
	int stepc = 0;// �o���`�B��
	int accuracyStepNum = 0;
	float accuracyTotalLength = 0;
	float accuracyStrideLength = 0;
	float accuracyVelocity = 0;
	private boolean run = false;
	private boolean FirstTime = true;
	private double NOW_ORIENTATION = 0.0;
	/* PDR */

	Button SlideButton;
	SlidingDrawer slidingDrawer;
	TextView PathInformation;
	private double Start_Download = 0, Start_Upload = 0;
	private double Download = 0.0, Upload = 0.0;
	private String SSID_old = "NO_SSID";
	private double LaststrideLength = 0.0;

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
	private int DelayTime = 100;
	private int StrideMultiply = 30;
	private boolean VDisplayNOCALNode = true;
	private boolean VDisplayNode = true;
	private boolean VDisplayPossibleAP = false;
	private boolean DisplayRuler = true;
	/* Preference parameter */

	/* WiFi */
	private List<ScanResult> list;
	private ScanResult mScanResult;
	private int ScanTimes = 0;
	private WifiManager WiFiManager;
	private WifiInfo WiFiInfo;// ��ܲ{�b�s�u��WiFi��T
	/* WiFi */

	/* LinkedList */
	private LinkedList<Node> points = new LinkedList<Node>();
	private LinkedList<WifiInformation> wifiInformation = new LinkedList<WifiInformation>();
	/* LinkedList */

	/* menu */
	public static final int walk = 1, pause = 2, voronoi = 3, setting = 4,
			outputvoronoi = 5, inputvoronoi = 6;
	/* menu */

	private Handler handler = new Handler();

	/* Intent */
	Intent fintent;
	Intent intent = new Intent("ACTION");
	/* Intent */

	/* use for file name */
	private StringBuffer sb = new StringBuffer();
	Timer timer = new Timer();
	SimpleDateFormat FileDate = new SimpleDateFormat("yyyy_MM_dd@HH.mm");
	Date curDate = new Date(System.currentTimeMillis()); // �����e�ɶ�
	String FileName = FileDate.format(curDate);
	SimpleDateFormat DirectoryDate = new SimpleDateFormat("yyyy_MM_dd");
	String DirectoryName = DirectoryDate.format(curDate);
	SimpleDateFormat InFileDate = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
	String InFileDateName = InFileDate.format(curDate);
	File sd = Environment.getExternalStorageDirectory();
	String path = sd + "/WifiScanner/DATA/" + DirectoryName;
	/* use for file name */

	int action = 1;
	/* double back : finish the activity */
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// WiFiManager.enableNetwork(NETWORKID, true);//���sWiFi<<���ե�
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// NETWORKID = WiFiInfo.getNetworkId();
			// WiFiManager.disconnect();//����WiFi<<���ե�
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�A���@���h�X����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				stopService(fintent);
				if (run) {
					run = false;
					if (sensorStoped == false) {
						mService.StopSensor();
						sensorStoped = true;
						mService.wakeLock.release();
					}
					if (mBound) {
						unbindService(mConnection);
						mBound = false;
					}
				} else {
					unbindService(mConnection);
				}
				mWifiAdmin.onDestroy();

				super.onDestroy();
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Runnable task = new Runnable() {

		public void run() {
			if (run) {
				if (!mdraw.isPausePDR()) {
					double[] PDRSet=mService.getStepAndStrideAndOrientation();
					orientation=(float) PDRSet[2];
					stepn=(int)PDRSet[0];
					strideLength=(float)PDRSet[1];
					
					
					
					
					
					
					
					//orientation = (float) mService.getOrientation();
					//stepn = mService.getStepNum();

					if (FirstTime)// first time use : at original point
					// record orientation
					{
						NOW_ORIENTATION = orientation;
						stepc = stepn;

						/* get WiFi information */

						getAllNetWorkList();
						if (list != null) {// ��wifi����Ƥ~����
							new BackupDataTask().execute();
						} else {
							Toast.makeText(getBaseContext(), "�۰ʶ}��Wi-Fi",
									Toast.LENGTH_SHORT).show();
							WiFiManager.setWifiEnabled(true);
						}
						ScanTimes++;// record scan times for combinating PDR
					}

					else if (stepc == stepn)// step same(no move) : stride=0
					{
						strideLength = 0;
						Log.i("PDR", "���o���� :" + orientation);
						/* correct the orientation */
						orientation = orientation - NOW_ORIENTATION;
						if (orientation < 0.0) {
							orientation += 360;
						} else if (orientation > 360) {
							orientation -= 360;
						}
						/* correct the orientation */
						mdraw.sendorientation((int) orientation);

					} else {
						stepc = stepn;
						/* ���o�{�b�s�u��WiFi AP ��T */
						WiFiInfo = WiFiManager.getConnectionInfo();
						SSID_now = WiFiInfo.getSSID();// ���b�s�u��SSID
						MAC_now = WiFiInfo.getBSSID();

						/* get PDR information */

						Log.i("PDR", "���o���� :" + orientation);

						// two case to deal : first time scan, else
						// store data in txt, then floatingwindow read it to
						// display

						/* correct the orientation */
						
						orientation = orientation - NOW_ORIENTATION;
						if (orientation < 0.0) {
							orientation += 360;
						} else if (orientation > 360) {
							orientation -= 360;
						}
						
						/* correct the orientation */
						try {
							mdraw.sendOrientationAndWifiMessage(
									(int) orientation, SSID_now, MAC_now,
									StrideMultiply);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						
						
						
						
						
						
						
						
						
						
						//strideLength = (float) mService.getStrideLength();
						Log.i("PDR", "���o�B�Z :" + strideLength);

						/* get PDR information */

						

						/* send to view(draw.class) */

						if (strideLength != 0 && !FirstTime) {
							mdraw.setNP(strideLength, (int) orientation);
							/* get WiFi information */

							getAllNetWorkList();
							if (list != null) {// ��wifi����Ƥ~����
								new BackupDataTask().execute();
							} else {
								Toast.makeText(getBaseContext(), "�䤣��Wi-Fi��T",
										Toast.LENGTH_SHORT).show();
							}
							ScanTimes++;// record scan times for combinating
										// with
										// PDR
							new BackupDataTask().execute();
						}
					}
					/* Path Information */
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(2);
					if (LaststrideLength != strideLength
							&& strideLength != 0) {
						LaststrideLength = strideLength;
					}

					if (SSID_now == "" || SSID_now == null) {
						PathInformation.setText("�����s�u��");
					}

					else {
						PathInformation.setText("\nAP ��" + SSID_now
								+ "�� �s�u��");

						if (!(SSID_old.equalsIgnoreCase(SSID_now))) {
							Start_Download = TrafficStats.getTotalRxBytes();
							Start_Upload = TrafficStats.getTotalTxBytes();
							SSID_old = SSID_now;
						}

						Download = ((TrafficStats.getTotalRxBytes() - Start_Download) / 1048576);
						Upload = ((TrafficStats.getTotalTxBytes() - Start_Upload) / 1048576);
						String strRX = nf.format(Download);
						String strTX = nf.format(Upload);
						PathInformation.append("\n�U���G" + (strRX)
								+ "��MB �W�ǡG" + (strTX) + "��MB");
					}
					if (mdraw.isPausePDR()) {
						PathInformation.append("\n���A : �Ȱ�");
					} else if (strideLength != 0.0) {
						PathInformation.append("\n���A : ���ʤ�");
					} else {
						PathInformation.append("\n���A : ����");
					}
					nf.setMaximumFractionDigits(5);
					String sl = nf.format(strideLength);
					String lsl = nf.format(LaststrideLength);
					if (strideLength == 0) {
						PathInformation.append("\n�B�Z : " + lsl + " ��");
					} else {
						PathInformation.append("\n�B�Z : " + sl + " ��");
					}
					nf.setMaximumFractionDigits(2);
					String orient = nf.format(orientation);
					PathInformation.append("\n��V�� : " + orient + "�X");
					/* Path Information */
					handler.postDelayed(this, DelayTime);
				} else {
					handler.postDelayed(this, DelayTime);
				}
			}

		}
	};

	private Button plus_button;
	private Button minus_button;
	private Button ruler_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mdraw = (draw) findViewById(R.id.draw_path);
		mWifiAdmin = new WifiService(information.this);
		plus_button = (Button) findViewById(R.id.plus_button);
		plus_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mdraw.setPlusZoom();
			}
		});
		minus_button = (Button) findViewById(R.id.minus_button);
		minus_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mdraw.setMinusZoom();
			}
		});

		ruler_button = (Button) findViewById(R.id.ruler_button);
		ruler_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mdraw.setdrawRuler();
			}
		});

		PathInformation = (TextView) findViewById(R.id.PathInformation);

		Log.i("informationCLASS", "onCreate()...");
		fintent = new Intent(information.this, FloatWindowService.class);
		startService(fintent);

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(getResources().getDrawable(
				R.drawable.ic_live_help_grey600_48dp));
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		};

		DialogInterface.OnClickListener StartClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				File dirFile = new File(path);// store data and bmp
				if (!dirFile.exists()) {
					dirFile.mkdirs();
					// Toast.makeText(getBaseContext(), "�Ыإؿ�:" +
					// path,Toast.LENGTH_SHORT).show();
				}
				if (!WiFiManager.isWifiEnabled()) {
					WiFiManager.setWifiEnabled(true);
				}
				run = true;
				mService.SensorEvent();
				mService.acquireWakeLock();

				// WiFiManager = (WifiManager)
				// this.getSystemService(Context.WIFI_SERVICE);
				WiFiManager.setWifiEnabled(true);
				Toast.makeText(getBaseContext(), "�A�Ȥw�ҰʡA�i�H�}�l�C���C",
						Toast.LENGTH_SHORT).show();
				mdraw.StartPDR();
				handler.postDelayed(task, DelayTime);// start thread
			}
		};

		dialog.setTitle("�p��ϥ�");
		dialog.setMessage("1.�бqMenu�I��\"�}�l��H������A��\"�Ϋ�\"�����}�l�a!\"�}�l�b���a���C���C(�Y��ܸ�T�����`�A�i�q�]�w�I��\"���]�Ҧ��]�w\"��A�ϥΡC)\n\n2.�C�������i�qMenu����ܤ����ζץXVoronoi�Ϥ��C\n\n3.�Ϥ�����ܪ���T�Υ\��Ѽƥi�q�]�w���վ�C\n\n4.���|�Ҧ����i�Q��Double Tap�N�Ϥ��^����I�FVoronoi�Ҧ��h�i�H��_�쥻�ϥܪ��j�p�C\n");
		dialog.setPositiveButton("�ڤF�ѤF!", OkClick);
		dialog.setNegativeButton("�����}�l�a!", StartClick);
		dialog.setCancelable(false);
		dialog.show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, walk, 1, "�}�l ��H������A��");
		menu.add(0, pause, 2, "�Ȱ�/�~�� ��H������A�� ");
		menu.add(0, voronoi, 3, "��� Voronoi��/���|��");
		menu.add(0, setting, 4, "�]�w");
		menu.add(0, outputvoronoi, 5, "�ץXVoronoi��");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			if (run) {
				Toast.makeText(getBaseContext(), "Already start",
						Toast.LENGTH_SHORT).show();
			} else {
				File dirFile = new File(path);// store data and bmp
				if (!dirFile.exists()) {
					dirFile.mkdirs();
					// Toast.makeText(getBaseContext(), "�Ыإؿ�:" +
					// path,Toast.LENGTH_SHORT).show();
				}
				if (!WiFiManager.isWifiEnabled()) {
					WiFiManager.setWifiEnabled(true);
				}
				run = true;
				mService.SensorEvent();
				mService.acquireWakeLock();

				WiFiManager = (WifiManager) this
						.getSystemService(Context.WIFI_SERVICE);
				WiFiManager.setWifiEnabled(true);
				Toast.makeText(getBaseContext(), "�A�Ȥw�ҰʡA�i�H�}�l�C���C",
						Toast.LENGTH_SHORT).show();
				mdraw.StartPDR();
				handler.postDelayed(task, DelayTime);// start thread

			}
			break;
		case 2:
			mdraw.drawPause();
			break;
		case 3:
			mdraw.setVoronoiEnabled();
			break;
		case 4:
			Intent i = new Intent(this, Setting.class);
			startActivity(i);
			break;
		case 5:
			if (mdraw.getVoronoiEnabled()) {
				if (!Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					Toast.makeText(getBaseContext(), "�д��JSD�d",
							Toast.LENGTH_SHORT).show();
				}
				mdraw.OutputVoronoi();
			} else {
				Toast.makeText(getBaseContext(), "�Х��i�JVoronoi�ϵe��",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}

		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("informationCLASS", "onStart()...");
		Intent bintent = new Intent(information.this, PedometerService.class);
		bindService(bintent, mConnection, Context.BIND_AUTO_CREATE);
		mBound = true;

		if (mConnection == null)// debug
		{
			System.out.println("mConnection is NULL!!");
		}
		if (mService == null)// debug
		{
			System.out.println("mService is NULL!!");
		}
		sensorStoped = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("informationCLASS", "onStop()...");
	}

	@Override
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
				"Frequency", "0.1")).replace("��", "")));
		StrideMultiply = Integer.parseInt(preferences.getString(
				"StrideMultiply", "30"));// no need send to draw.class
		VDisplayNOCALNode = preferences.getBoolean("VDisplayNOCALNode", false);
		VDisplayNode = preferences.getBoolean("VDisplayNode", true);
		DisplayRuler = preferences.getBoolean("DisplayRuler", true);

		VDisplayPossibleAP = preferences
				.getBoolean("VDisplayPossibleAP", false);

		mdraw.PreferenceSetting(DisplayRuler, VDisplayPossibleAP, DisplayNode,
				DisplayPath, NoStopPath, VoronoiNode, VDisplayPath,
				VDisplayWifiCircle, VDisplayNOCALNode, VDisplayNode,
				DifferenceFilter, PathLoss, DefaultRSSI);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (run) {
			run = false;
			if (sensorStoped == false) {
				mService.StopSensor();
				sensorStoped = true;
				mService.wakeLock.release();
			}
			if (mBound) {
				unbindService(mConnection);
				mBound = false;
			}

		}

		else {
			unbindService(mConnection);
		}
		super.onDestroy();
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			PedometerBinder binder = (PedometerBinder) service;
			mService = binder.getService();
			Log.i("informationCLASS", "binder.getService()...");
			mBound = true;
			if (mService != null) {
				Log.i("QQservice-bind", "Service is bonded Successfully!");
				System.out.println("bind QQ");
			}

			else {
				Log.e("QQservice-bind", "Service is Fail!");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			Log.i("QQservice-bind", "Service unbound!");
		}
	};

	/**
	 * Get scan result and calculate : signal->distance
	 * 
	 * @param List
	 *            <ScanResult> sr the result of wifi scan
	 */
	public double CalWifiDistance(ScanResult sr) {
		double exp = ((double) DefaultRSSI - ((double) sr.level + 100.0))
				/ (10.0 * (double) PathLoss);
		DecimalFormat df = new DecimalFormat("#.##");// to the second digit
														// after the decimal
														// point
		String s = df.format(Math.pow(10.0, exp));
		return Double.parseDouble(s);
	}

	public void getAllNetWorkList() {
		if (sb != null)// �C���I�����y���e�M�ŤW�@�������y���G
		{
			sb = new StringBuffer();
		}
		if (!WiFiManager.isWifiEnabled()) {
			WiFiManager.setWifiEnabled(true);
		}
		if (!mdraw.isPausePDR() || (!mdraw.isPausePDR() && NoStopPath)) {

			mWifiAdmin.startScan();// �}�l���y����
			list = mWifiAdmin.getwifilist();
			if (list != null) {
				int r, g, b;
				Random rand = new Random();
				if (wifiInformation.isEmpty())// first scan : directly store
				{
					for (int j = 0; j < list.size(); j++) {
						WifiInformation temp = new WifiInformation(
								list.get(j).BSSID, list.get(j).SSID,
								list.get(j).level + 100, ScanTimes);// add
						// information
						// : ID,
						// MAC,
						// signal
						int channel = 0;
						switch (list.get(j).frequency) {
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
						temp.setChannel(channel);

						temp.getSignal().get(temp.getSignal().size() - 1)
								.SetDistance(CalWifiDistance(list.get(j)));// cal
																			// distance&add

						/* color for each AP to draw circle of signal range */
						r = rand.nextInt(255);
						g = rand.nextInt(255);
						b = rand.nextInt(255);
						Color randomColor = new Color();
						randomColor.rgb(r, g, b); // need modifying
						temp.setColor(randomColor);

						wifiInformation.add(temp);
					}
				} else// need compare signal(filter)
				{
					for (int j = 0; j < list.size(); j++) {
						boolean NewWifi = true;// is new wifi(not add yet)?
						for (int k = 0; k < wifiInformation.size(); k++) {
							if (list.get(j).BSSID.equals(wifiInformation.get(k)
									.getMAC()))// same mac address
							{
								NewWifi = false;
								if (Math.abs(((list.get(j).level + 100) - wifiInformation
										.get(k)
										.getSignal()
										.get(wifiInformation.get(k).getSignal()
												.size() - 1).getRSSI())) > DifferenceFilter)// difference
																							// must
																							// bigger
																							// than
																							// DifferenceFilter
																							// preference
								{
									Signal s = new Signal();
									s.Set(list.get(j).level + 100, ScanTimes);
									s.SetDistance(CalWifiDistance(list.get(j)));
									wifiInformation.get(k).getSignal().add(s);
								}
							}
						}
						if (NewWifi)// is new wifi : add to LinkedList
						{
							WifiInformation temp = new WifiInformation(
									list.get(j).BSSID, list.get(j).SSID,
									list.get(j).level + 100, ScanTimes);

							int channel = 0;
							switch (list.get(j).frequency) {
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
							temp.setChannel(channel);
							temp.getSignal().get(temp.getSignal().size() - 1)
									.SetDistance(CalWifiDistance(list.get(j)));

							/*
							 * color for each AP to draw circle of signal range
							 */
							r = rand.nextInt(255);
							g = rand.nextInt(255);
							b = rand.nextInt(255);
							Color randomColor = new Color();
							randomColor.rgb(r, g, b); // need modifying
							temp.setColor(randomColor);

							wifiInformation.add(temp);
						}
					}
				}
				mdraw.sendWifiInformation(wifiInformation);
				// CalPosition();
			}
		} else// available?
		{
			Dialog dialog = new AlertDialog.Builder(information.this)
					.setTitle("���~!!")
					.setMessage("WiFi�\�ण���`�Υ��}��")
					.setPositiveButton("�T�w",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}

							}).create();
		}
	}

	public class BackupDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			if ((list != null) && FirstTime) {
				FirstTime = false;
				points = mdraw.getNode();
				wifiInformation = mdraw.getWifiInformation();

				String Result = "null";
				try {
					FileWriter fw = new FileWriter(path + "/data_" + FileName
							+ ".txt", false);
					Result = fw.toString();
					BufferedWriter bw = new BufferedWriter(fw); // �NBufferedWeiter�PFileWrite���󰵳s��
					if (points.isEmpty()) {
						bw.write("0");
					} else {
						bw.write(String.valueOf(points.size()));
					}
					bw.newLine();
					if (wifiInformation.isEmpty()) {
						bw.write("0");
					} else {
						bw.write(String.valueOf(wifiInformation.size()));
					}
					bw.newLine();
					bw.write("�}�l�ɶ� : " + InFileDateName + "<\n");
					bw.write("########################\n");
					bw.write("���|���ӫY�� : " + PathLoss + "<\n");
					bw.write("�T���j�װѦҭ� : " + DefaultRSSI + "<\n");
					bw.write("�z��t�� : " + DifferenceFilter + "<\n\n");
					bw.write("AP��T (" + wifiInformation.size() + "��):\n");
					for (int j = 0; j < wifiInformation.size(); j++) {
						bw.write((j + 1) + ".\n");
						bw.write("N:" + wifiInformation.get(j).getSSID()
								+ "<\n");
						bw.write("M:" + wifiInformation.get(j).getMAC() + "<\n");

						bw.write("C:" + wifiInformation.get(j).getChannel()
								+ "<\n");

						bw.write("S:");
						if (wifiInformation.get(j).AlreadyCal) {
							bw.write("�w���T����m :(" + wifiInformation.get(j).x
									+ "," + wifiInformation.get(j).y + ")<\n");
						} else if (wifiInformation.get(j).PossibleNode) {
							bw.write("��ӥi���m:(" + wifiInformation.get(j).x
									+ "," + wifiInformation.get(j).y + ") & ("
									+ wifiInformation.get(j).x1 + ","
									+ wifiInformation.get(j).y1 + ")<\n");
						} else if (wifiInformation.get(j).NoNode) {
							bw.write("���� �A");
							if (wifiInformation.get(j).getSignal().size() == 0) {
								bw.write("�w�]�ȡC<\n");
							} else if (wifiInformation.get(j).getSignal()
									.size() == 1) {
								bw.write("�@�ӰT�������I�C<\n");
							} else if (wifiInformation.get(j).getSignal()
									.size() == 2) {
								bw.write("�G�ӰT�������I�C<\n");
							} else if (wifiInformation.get(j).getSignal()
									.size() >= 3) {
								bw.write("�T�ӥH�W�T�������I�C<\n");
							}
						}
						bw.write("  �T�������I("
								+ wifiInformation.get(j).getSignal().size()
								+ "��):\n");
						for (int k = 0; k < wifiInformation.get(j).getSignal()
								.size(); k++) {
							bw.write("  (" + (k + 1) + ")\n");
							bw.write("    �����s��:"
									+ wifiInformation.get(j).getSignal().get(k)
											.getScanNumber() + "\n");
							bw.write("    �����y��:"
									+ "("
									+ points.get(wifiInformation.get(j)
											.getSignal().get(k).getScanNumber()).x
									+ ","
									+ points.get(wifiInformation.get(j)
											.getSignal().get(k).getScanNumber()).y
									+ ")\n");
							bw.write("    �T���j��(RSSI):"
									+ wifiInformation.get(j).getSignal().get(k)
											.getRSSI() + "\n");
							bw.write("    ����Z��:"
									+ wifiInformation.get(j).getSignal().get(k)
											.getDistance() + " m\n");
						}
						bw.write("<=====================\n");
					}
					bw.write("P-------------------------\n");
					bw.write("���ʸ��|�y�� (" + points.size() + "��):\n");
					for (int j = 0; j < points.size(); j++) {
						bw.write((j + 1) + " : " + "(" + points.get(j).x + ","
								+ points.get(j).y + ")\n");
					}
					bw.write("<=====================\n");
					bw.write("#####################\n");
					bw.newLine();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Toast.makeText(getBaseContext(),
				// "Send WiFi Message : "+ScanTimes,
				// Toast.LENGTH_SHORT).show();

				Bundle bundle = new Bundle();
				bundle.putInt("ACTION", 1);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				// Toast.makeText(getApplicationContext(), Result,
				// Toast.LENGTH_SHORT);

				return Result;
			}
			if ((strideLength != 0) && !FirstTime) {
				/* get WiFi information */
				String Result = "null";
				if (list != null) {
					points = mdraw.getNode();
					wifiInformation = mdraw.getWifiInformation();

					try {
						FileWriter fw = new FileWriter(path + "/data_"
								+ FileName + ".txt", false);

						Result = fw.getEncoding();
						BufferedWriter bw = new BufferedWriter(fw); // �NBufferedWeiter�PFileWrite���󰵳s��
						// Toast.makeText(getBaseContext(),"�g��:"+path+"/data_"+FileName+".txt",
						// Toast.LENGTH_SHORT).show();
						if (points.isEmpty()) {
							bw.write("0");
						} else {
							bw.write(String.valueOf(points.size()));
						}
						bw.newLine();
						if (wifiInformation.isEmpty()) {
							bw.write("0");
						} else {
							bw.write(String.valueOf(wifiInformation.size()));
						}
						bw.newLine();
						bw.write("�}�l�ɶ� : " + InFileDateName + "<\n");
						bw.write("##############\n");
						bw.write("���|���ӫY�� : " + PathLoss + "<\n");
						bw.write("�T���j�װѦҭ� : " + DefaultRSSI + "<\n");
						bw.write("�z��t�� : " + DifferenceFilter + "<\n\n");
						bw.write("AP��T (" + wifiInformation.size() + "��):\n");
						for (int j = 0; j < wifiInformation.size(); j++) {
							bw.write((j + 1) + ".\n");
							bw.write("N:" + wifiInformation.get(j).getSSID()
									+ "<\n");
							bw.write("M:" + wifiInformation.get(j).getMAC()
									+ "<\n");

							bw.write("C:" + wifiInformation.get(j).getChannel()
									+ "<\n");

							bw.write("S:");
							if (wifiInformation.get(j).AlreadyCal) {
								bw.write("�w���T����m :(" + wifiInformation.get(j).x
										+ "," + wifiInformation.get(j).y
										+ ")<\n");
							} else if (wifiInformation.get(j).PossibleNode) {
								bw.write("��ӥi���m:(" + wifiInformation.get(j).x
										+ "," + wifiInformation.get(j).y
										+ ") & (" + wifiInformation.get(j).x1
										+ "," + wifiInformation.get(j).y1
										+ ")<\n");
							} else if (wifiInformation.get(j).NoNode) {
								bw.write("���� �A");
								if (wifiInformation.get(j).getSignal().size() == 0) {
									bw.write("�w�]�ȡC<\n");
								} else if (wifiInformation.get(j).getSignal()
										.size() == 1) {
									bw.write("�@�ӰT�������I�C<\n");
								} else if (wifiInformation.get(j).getSignal()
										.size() == 2) {
									bw.write("�G�ӰT�������I�C<\n");
								} else if (wifiInformation.get(j).getSignal()
										.size() >= 3) {
									bw.write("�T�ӥH�W�T�������I�C<\n");
								}
							}
							bw.write("  �T�������I("
									+ wifiInformation.get(j).getSignal().size()
									+ "��):\n");
							for (int k = 0; k < wifiInformation.get(j)
									.getSignal().size(); k++) {
								bw.write("  (" + (k + 1) + ")\n");
								bw.write("    �����s��:"
										+ wifiInformation.get(j).getSignal()
												.get(k).getScanNumber() + "\n");
								bw.write("    �����y��:"
										+ "("
										+ points.get(wifiInformation.get(j)
												.getSignal().get(k)
												.getScanNumber()).x
										+ ","
										+ points.get(wifiInformation.get(j)
												.getSignal().get(k)
												.getScanNumber()).y + ")\n");
								bw.write("    �T���j��(RSSI):"
										+ wifiInformation.get(j).getSignal()
												.get(k).getRSSI() + "\n");
								bw.write("    ����Z��:"
										+ wifiInformation.get(j).getSignal()
												.get(k).getDistance() + " m\n");
							}
							bw.write("<============\n");
						}
						bw.write("P-----------------\n");
						bw.write("���ʸ��|�y�� (" + points.size() + "��):\n");
						for (int j = 0; j < points.size(); j++) {
							bw.write((j + 1) + " : " + "(" + points.get(j).x
									+ "," + points.get(j).y + ")\n");
						}
						bw.write("<============\n");
						bw.write("##############\n");
						bw.newLine();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					/* Send Broadcast to update the data */
					Intent intent = new Intent("ACTION");
					Bundle bundle = new Bundle();
					bundle.putInt("ACTION", 1);
					intent.putExtras(bundle);
					sendBroadcast(intent);
					return Result;
					/* Send Broadcast to update the data */
				} else {
					// Toast.makeText(getBaseContext(), "�䤣��Wi-Fi��T",
					// Toast.LENGTH_SHORT).show();
				}
			}

			else {
				// Toast.makeText(getApplicationContext(), "null",
				// Toast.LENGTH_SHORT);

				return "no backup data";
			}
			return "";
		}

		protected void onPostExecute(String result) {
			Log.i("BACKUP", result);
			// Toast.makeText(getApplicationContext(), result,
			// Toast.LENGTH_SHORT);
		}

	}

	public class WifiInformationTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			return "";
		}

		protected void onPostExecute(String result) {

		}

	}
}
