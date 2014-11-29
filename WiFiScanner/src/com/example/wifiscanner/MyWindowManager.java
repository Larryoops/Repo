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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyWindowManager {
	private static FloatWindowSmallView smallWindow;
	private static bigWindow bigWindow;
	private static LayoutParams smallWindowParams;
	private static LayoutParams bigWindowParams;
	private static WindowManager mWindowManager;
	private static ActivityManager mActivityManager;

	static String StartTime;
	static String PathLoss;
	static String DefaultRSSI;
	static String DifferenceFilter;
	static String NodePoint = "";

	static SimpleDateFormat FileDate = new SimpleDateFormat("yyyy_MM_dd@HH.mm");
	static Date curDate = new Date(System.currentTimeMillis());
	static String FileName = FileDate.format(curDate);
	static SimpleDateFormat DirectoryDate = new SimpleDateFormat("yyyy_MM_dd");
	static String DirectoryName = DirectoryDate.format(curDate);
	static File sd = Environment.getExternalStorageDirectory();
	static String path = sd + "/WifiScanner/DATA/" + DirectoryName + "/data_"
			+ FileName + ".txt";
	File dirFile;

	static LinkedList<FinalInformation> fi = new LinkedList<FinalInformation>();
	static String Node, Wifi, Message;
	static double mStartRX = -1, RX = 0.0;
	static double mStartTX = -1, TX = 0.0;

	public static void createSmallWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (smallWindow == null) {
			smallWindow = new FloatWindowSmallView(context);
			if (smallWindowParams == null) {
				smallWindowParams = new LayoutParams();
				smallWindowParams.type = LayoutParams.TYPE_PHONE;
				smallWindowParams.format = PixelFormat.RGBA_8888;
				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				smallWindowParams.width = FloatWindowSmallView.viewWidth;
				smallWindowParams.height = FloatWindowSmallView.viewHeight;
				smallWindowParams.x = 0 /* screenWidth */;
				smallWindowParams.y = 0/* screenHeight / 2 */;
			}
			smallWindow.setParams(smallWindowParams);
			windowManager.addView(smallWindow, smallWindowParams);
		}
	}

	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	public static void createBigWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (bigWindow == null) {
			bigWindow = new bigWindow(context);
			if (bigWindowParams == null) {
				bigWindowParams = new LayoutParams();
				bigWindowParams.x = screenWidth / 2 - bigWindow.viewWidth / 2;
				bigWindowParams.y = screenHeight / 2 - bigWindow.viewHeight / 2;
				bigWindowParams.type = LayoutParams.TYPE_PHONE;
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = bigWindow.viewWidth;
				bigWindowParams.height = bigWindow.viewHeight;
			}
			windowManager.addView(bigWindow, bigWindowParams);
		}

	}

	public static void removeBigWindow(Context context) {
		if (bigWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(bigWindow);
			bigWindow = null;
		}
	}

	/* Update the data of floating window */
	public static void updateUsedValue(final Context context)
			throws IOException {
		if (Wifi == null) {
			Wifi = "0";
		}
		if (Node == null) {
			Node = "0";
		}
		if (mStartRX == -1) {
			mStartRX = TrafficStats.getTotalRxBytes();
		}
		if (mStartTX == -1) {
			mStartTX = TrafficStats.getTotalTxBytes();
		}
		if (smallWindow != null)// update small window
		{
			TextView FloatNodeView = (TextView) smallWindow
					.findViewById(R.id.FloatNode);
			TextView FloatNodeView_pass = (TextView) smallWindow
					.findViewById(R.id.FloatNode_pass);

			// Toast.makeText(context, "GET! :"+Node+" "+Wifi,
			// Toast.LENGTH_SHORT).show();

			RX = (TrafficStats.getTotalRxBytes() - mStartRX) / 1048576;
			TX = (TrafficStats.getTotalTxBytes() - mStartTX) / 1048576;

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);

			FloatNodeView.setText("步數:" + Node + "\n" + "AP數:" + Wifi);
			FloatNodeView_pass.setTextColor(Color.GREEN);
			FloatNodeView_pass.setText("下載: " + nf.format(RX) + " MB\n"
					+ "上傳: " + nf.format(TX) + " MB");
			
		}
		if (bigWindow != null)// update big window
		{
			TextView tv = (TextView) bigWindow
					.findViewById(R.id.InformationView);
			if (StartTime == null) {
				tv.setText("尚無資料");
			} else {
				tv.setText("開始時間:" + StartTime + "\n路徑消耗係數:" + PathLoss
						+ "\n訊號強度參考值:" + DefaultRSSI + "\n訊號強度篩選差值:"
						+ DifferenceFilter + "\n目前共" + fi.size() + "個AP");
			}

			ListView InformationView = (ListView) bigWindow
					.findViewById(R.id.InformationList);
			List<String> words = new ArrayList<String>();
			for (int displaylist = 0; displaylist < fi.size(); displaylist++) {
				words.add((displaylist + 1) + ". " + fi.get(displaylist).name
						+ "\nMac:" + fi.get(displaylist).mac + "\n狀態:"
						+ fi.get(displaylist).status + ""
						+ fi.get(displaylist).WifiPoint);
			}
			if (NodePoint != null) {
				words.add("\n" + NodePoint);
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					R.layout.cuslistview, words);

			if (InformationView.getAdapter() == null) {
				InformationView.setAdapter(adapter);
			} else {
				adapter.clear();
				for (String object : words) {
					adapter.insert(object, adapter.getCount());
				}
				adapter.notifyDataSetChanged();
				
			}
			InformationView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Log.i("MyFloatViewActivity", "You Clicked Item "
									+ (arg2 + 1));
							if (arg2 >= fi.size()) {
								Toast.makeText(context, "路徑座標",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(context, "" + fi.get(arg2).name,
										Toast.LENGTH_SHORT).show();
							}

						}
					});
		}
	}

	/* detect is any window showing */
	public static boolean isWindowShowing() {
		return smallWindow != null || bigWindow != null;
	}

	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	/* get data from file in SD card */
	public static void getUsedValue(Context context) {
		File dirFile = new File(path);
		fi = new LinkedList<FinalInformation>();
		try {
			FileReader fr = new FileReader(dirFile);
			BufferedReader br = new BufferedReader(fr);
			String strtemp;

			Node = br.readLine();
			Wifi = br.readLine();
			FinalInformation temp = new FinalInformation();
			;
			while ((strtemp = br.readLine()) != null) {
				if (strtemp.length() == 0) {
					continue;
				}
				if (strtemp.charAt(0) == 'N' || strtemp.charAt(0) == 'M'
						|| strtemp.charAt(0) == 'S' || strtemp.charAt(0) == 'P') {

					if (strtemp.charAt(0) == 'N')// wifi name
					{
						String s = "";
						for (int read = 2; read < strtemp.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								temp.setName(s);
								fi.add(temp);

								break;
							}
							s += strtemp.charAt(read);
						}
					}

					if (strtemp.charAt(0) == 'M')// wifi mac
					{
						String s = "";
						temp = new FinalInformation();
						for (int read = 2; read < strtemp.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								temp.setMac(s);
								fi.get(fi.size() - 1).setMac(s);
								break;
							}
							s += strtemp.charAt(read);
						}
					}

					if (strtemp.charAt(0) == 'S')// wifi status
					{
						String s = "";
						temp = new FinalInformation();
						for (int read = 2; read < strtemp.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								temp.setStatus(s);
								fi.get(fi.size() - 1).setStatus(s);
								break;
							}
							s += strtemp.charAt(read);
						}
						/* get wifi's record */
						String w = "", t = "";
						w = br.readLine();
						while (w.charAt(0) != '<')// until meet '<'
						{
							t = t + "\n" + w;
							w = br.readLine();
						}
						fi.get(fi.size() - 1).setWifiPoint(t);
					}
					/* get path node */
					if (strtemp.charAt(0) == 'P') {
						String t = "";
						strtemp = br.readLine();
						while (strtemp.charAt(0) != '<')// until meet '<'
						{
							t = t + "\n" + strtemp;
							strtemp = br.readLine();
						}
						NodePoint = t;
					}
				} else// get normal information
				{
					if (strtemp.indexOf("開始時間 : ") >= 0) {
						StartTime = "";
						for (int read = strtemp.indexOf("開始時間 : ")
								+ "開始時間 : ".length(); read < strtemp.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								break;
							}
							StartTime += strtemp.charAt(read);
						}
					}

					if (strtemp.indexOf("路徑消耗係數 : ") >= 0) {
						PathLoss = "";
						for (int read = strtemp.indexOf("路徑消耗係數 : ")
								+ "路徑消耗係數 : ".length(); read < strtemp.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								break;
							}
							PathLoss += strtemp.charAt(read);
						}
					}

					if (strtemp.indexOf("訊號強度參考值 : ") >= 0) {
						DefaultRSSI = "";
						for (int read = strtemp.indexOf("訊號強度參考值 : ")
								+ "訊號強度參考值 : ".length(); read < strtemp
								.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								break;
							}
							DefaultRSSI += strtemp.charAt(read);
						}
					}

					if (strtemp.indexOf("篩選差值 : ") >= 0) {
						DifferenceFilter = "";
						for (int read = strtemp.indexOf("篩選差值 : ")
								+ "篩選差值 : ".length(); read < strtemp.length(); read++) {
							if (strtemp.charAt(read) == '<')// until meet '<'
							{
								break;
							}
							DifferenceFilter += strtemp.charAt(read);
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
