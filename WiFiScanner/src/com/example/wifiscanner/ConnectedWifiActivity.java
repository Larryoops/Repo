package com.example.wifiscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectedWifiActivity extends Activity {

	private WifiInfo WiFiInfo;
	private WifiManager WiFiManager;
	private boolean run = false;
	private Handler handler = new Handler();
	TextView connected_wifi;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connected_wifi_information);
		WiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		connected_wifi=(TextView)findViewById(R.id.TEXTVIEW_connectedwifi);
		run=true;
		handler.postDelayed(task, 2000);

	}
	
	
	private Runnable task = new Runnable() {	//no use

		public void run() {
			if (run) {
				/* ���o�{�b�s�u��WiFi AP ��T */
				if(WiFiManager.isWifiEnabled())
				{
					WiFiInfo = WiFiManager.getConnectionInfo();
					int ipAddress=WiFiInfo.getIpAddress();
					
					String ip=String.format("%d.%d.%d.%d",(ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
					connected_wifi.setText("���b�s�u��AP\nSSID:"+WiFiInfo.getSSID()+"\n�O�_����SSID:"+(WiFiInfo.getHiddenSSID()?"�O":"�_")+"\nMac:"+WiFiInfo.getMacAddress()+"\nIP:"+ip+"\n�s�u�t��:"+WiFiInfo.getLinkSpeed()+"Mbps\n�T���j��:"+WiFiInfo.getRssi());
				}
				else
				{
					connected_wifi.setText("Wi-Fi�ثe�S���s�u�I");
				}
				
				

			}

		}
	};



}
