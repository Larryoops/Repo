package com.example.wifiscanner;

import java.util.ArrayList;
import java.util.List;

import com.example.wifiscanner.MainActivity;

import will.service.FloatWindowService;
import will.service.WifiService;
import android.app.Activity;
import android.net.wifi.ScanResult;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class scan_info_view extends Activity {
	
	public class ScanInformation{
		String SSID;
		String MAC;
		int RSSI;
		int Channel;
		String Auth;
	}
	ScanAdapter scanAdapter;
	
	
	//static TextView s;
	public WifiManager WiFiManager;
	private Handler handler = new Handler();
	private String info_show = "";
	private StringBuffer sb = new StringBuffer();
	private WifiService mWifiAdmin;
	private List<ScanResult> list; // 掃描結果列表
	private ScanResult mScanResult;
	private boolean run=false;
	
	private ProgressDialog progressDialog;

	/**
	 * @param args
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
					
		setContentView(R.layout.scan_list);
		ListView WiFiScanListView = (ListView)findViewById(R.id.ScanList);	
		scanAdapter=new ScanAdapter();
		WiFiScanListView.setAdapter(scanAdapter);
		//s = (TextView) findViewById(R.id.scan_info_show);
		
		//WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mWifiAdmin = new WifiService(scan_info_view.this);
		WiFiScanListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					ScanInformation temp = scanAdapter.getInformation(arg2);
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(scan_info_view.this);
					dialog.setIcon(getResources().getDrawable(R.drawable.ic_format_list_bulleted_grey600_48dp));
					dialog.setTitle(temp.SSID);
					dialog.setMessage("MAC Address:"+temp.MAC+"\n訊號強度:"+String.valueOf(temp.RSSI)+"dBm\n通道:"+String.valueOf(temp.Channel)+"\n加密方式:"+String.valueOf(temp.Auth));		
					DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener() {
						
						            public void onClick(DialogInterface dialog, int which) {
						
						                
						
						            }
						
						        };

					dialog.setNegativeButton("關閉",CancelClick);
					dialog.setCancelable(true);
					dialog.show();
					
					
					
					Toast.makeText(scan_info_view.this, "顯示"+temp.SSID+"的資訊",Toast.LENGTH_SHORT).show();
					
				}
			});
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if(!WiFiManager.isWifiEnabled())
		{
			this.progressDialog = new ProgressDialog(scan_info_view.this);
	        this.progressDialog.setTitle("開啟Wi-Fi中");
	        this.progressDialog.setMessage("請稍後");
	        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        this.progressDialog.show();
			
			new Thread() 
			{
			  public void run() 
			  {

			     try
			       {
						WiFiManager.setWifiEnabled(true);
						while(!WiFiManager.isWifiEnabled())
						{

						}
			  // do the background process or any work that takes time to see progreaa dialog

			      }
			    catch (Exception e)
			    {
			        Log.e("tag",e.getMessage());
			    }
			// dismiss the progressdialog   
			  progressDialog.dismiss();
			 }
			}.start();
		}

		run=true;
		handler.postDelayed(task, 1000);

		
			
			
		
		
				
	}
	
	@Override
	protected void onDestroy(){
		mWifiAdmin.onDestroy();

		super.onDestroy();
	}


	
	private Runnable task = new Runnable() {
		public void run() {
			if (run) {
				if(WiFiManager.isWifiEnabled())
				{
					scanAdapter.refresh((ArrayList)getWifiScanList());
					//setText(info_show);					
				}
				else
				{
					//Toast.makeText(getBaseContext(), "Wi-Fi沒開啟或故障!", Toast.LENGTH_LONG).show();;
					//setText("Wi-Fi沒開啟或故障！");
				}
				handler.postDelayed(this, 2000);

			}

		}
	};
	
	public List<ScanInformation> getWifiScanList() {
		if (sb != null)// 每次點擊掃描之前清空上一次的掃描結果
		{
			sb = new StringBuffer();
		}
		
		List<ScanInformation> AddScanList = new ArrayList<ScanInformation>();
		
		mWifiAdmin=new WifiService(this);
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
				String tempAuth=mScanResult.capabilities;
				String WifiAuth;
				if(tempAuth.contains("WPA2"))
				{
					WifiAuth="WPA2";
				}
				else if(tempAuth.contains("WEP"))
				{
					WifiAuth="WEP";
				}
				else if(tempAuth.contains("WPA"))
				{
					WifiAuth="WPA";
				}
				else if(tempAuth.contains("ESS"))
				{
					WifiAuth="Open";
				}
				else
				{
					WifiAuth="Unknown";
				}
		    	/*get information*/	    		
				ScanInformation NewScan = new ScanInformation();
		    	NewScan.SSID=mScanResult.SSID;
		    	NewScan.MAC=mScanResult.BSSID;
		    	NewScan.RSSI=mScanResult.level;
		    	NewScan.Channel=channel;
		    	NewScan.Auth=WifiAuth;
		    	AddScanList.add(NewScan);
		    	
			}
		    info_show = "掃描到的Wi-Fi網路(共" + list.size() + "個)：\n" + sb.toString();
			return AddScanList;	
			
		}
		return AddScanList;
		
	}
	/*
	public static void setText(String info){
		s.setText(info);
	}
*/
	
	public class ScanAdapter extends BaseAdapter {
		List<ScanInformation> ScanList=getWifiScanList();
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ScanList.size();
		}

		@Override
		public ScanInformation getItem(int position) {
			// TODO Auto-generated method stub
			return ScanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;				
		}
		
		public void refresh(ArrayList<ScanInformation> list)
		{
			ScanList=list;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) scan_info_view.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.scan_field, parent, false);
			}

			TextView SSIDView = (TextView) view.findViewById(R.id.SSIDView);
			TextView MACView = (TextView) view.findViewById(R.id.MACView);
			TextView RSSIView=(TextView) view.findViewById(R.id.RSSIView);
			//TextView LockView=(TextView) view.findViewById(R.id.LockView);
			TextView ChannelView=(TextView) view.findViewById(R.id.ChannelView);
			ImageView WifiView=(ImageView) view.findViewById(R.id.WifiView);

			ScanInformation temp = ScanList.get(position);

			SSIDView.setText(temp.SSID);
			MACView.setText(temp.MAC);
			RSSIView.setText(String.valueOf(temp.RSSI)+" dBm ");
			//LockView.setText(temp.Auth);
			ChannelView.setText(String.valueOf(temp.Channel));

			if(temp.RSSI<=-85)
			{
				WifiView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_signal_wifi_1_bar_black_48dp));
			}
			else if(temp.RSSI<=-75)
			{
				WifiView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_signal_wifi_2_bar_black_48dp));
			}
			else if(temp.RSSI<=-65)
			{
				WifiView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_signal_wifi_3_bar_black_48dp));
			}
			else
			{
				WifiView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_signal_wifi_4_bar_black_48dp));
			}

			return view;
		}

		public ScanInformation getInformation(int position) {
			return ScanList.get(position);
		}

	}
}


