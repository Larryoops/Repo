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
package will.service;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class WifiService extends Service {
	private static final String TAG = "WifiService";
	private WifiManager WM;
	private WifiInfo wifiinfo;
	private List<ScanResult> wifilist; //scan result list 
	private ScanResult mScanResult; 
	private long curTime = 0;
	private WifiLock mWifiLock;
	private List<WifiConfiguration> mWifiConfigurations; //網絡連接列表
	
	
	private final IBinder mBinder = new WifiBinder();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class WifiBinder extends Binder {
    	public WifiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WifiService.this;
        }
    }

  
	
	public WifiService(Context context)
	{ 
		WM=(WifiManager)context.getSystemService(Context.WIFI_SERVICE); //取得WifiManager對象 
		wifiinfo=WM.getConnectionInfo(); //取得wifiinfo對象
	} 
	
	public void openWifi()//打開wifi
	{  
		if(!WM.isWifiEnabled())
		{ 
			WM.setWifiEnabled(true); 
		} 
	} 
	
	public void closeWifi()//關閉wifi 
	{ 
		if(WM.isWifiEnabled())
		{ 
			WM.setWifiEnabled(false); 
		} 
	} 

	 
	public int checkState()//檢查當前wifi狀態
	{ 
		return WM.getWifiState(); 
	} 
	
	public void acquireWifiLock()//鎖定wifiLock 
	{ 
		mWifiLock.acquire(); 
	} 
	
	public void releaseWifiLock()//解鎖wifiLock 
	{ 
		if(mWifiLock.isHeld()) //判斷是否鎖定
		{ 
			mWifiLock.acquire(); 
		} 
	} 
	public void CreateWifiLock()//創建一個wifiLock
	{ 
		mWifiLock=WM.createWifiLock("test");
	}
	 
	public List<WifiConfiguration>getConfiguration()//得到配置好的網絡 
	{ 
		return mWifiConfigurations; 
	} 
	
	public void connetionConfiguration(int index)//指定配置好的網絡進行連接 
	{
		if(index>mWifiConfigurations.size())
		{ 
			return; 
		} 
		WM.enableNetwork(mWifiConfigurations.get(index).networkId,true); //連接配置好指定ID的網絡 
	} 
	
	public void startScan()
	{ 
		WM.startScan(); 
		wifilist=WM.getScanResults(); //得到掃描結果 
		mWifiConfigurations=WM.getConfiguredNetworks();//得到配置好的網絡連接  
	} 
	
	public List<ScanResult>getwifilist()//得到網絡列表 
	{ 
		return wifilist; 
	} 
	
	public StringBuffer lookUpScan()//查看掃描結果 
	{ 
		StringBuffer sb=new StringBuffer(); 
		for(int i=0;i<wifilist.size();i++)
		{ 
			sb.append("Index_"+new Integer(i+1).toString()+":"); //將ScanResult信息轉換成一個字符串包 
			sb.append((wifilist.get(i)).toString()).append("\n"); //包括：BSSID、SSID、capabilities、frequency、level
		} 
		return sb; 
	} 
	
	public String getMacAddress()
	{ 
		return(wifiinfo==null)?"NULL":wifiinfo.getMacAddress(); 
	} 
	
	public String getBSSID()
	{ 
		return(wifiinfo==null)?"NULL":wifiinfo.getBSSID(); 
	} 
	
	public int getIpAddress()
	{ 
		return(wifiinfo==null)?0:wifiinfo.getIpAddress(); 
	} 
	
	public int getNetWordId()//得到連接的ID 
	{ 
		return(wifiinfo==null)?0:wifiinfo.getNetworkId(); 
	} 
	 
	public String getwifiinfo()//得到wifiInfo的所有信息
	{ 
		return(wifiinfo==null)?"NULL":wifiinfo.toString(); 
	} 
	
	
	
	
	
	
	public void addNetWork(WifiConfiguration configuration)//添加一個網絡并連接 
	{ 
		int wcgId=WM.addNetwork(configuration); 
		WM.enableNetwork(wcgId,true); 
	} 
	
	public void disConnectionWifi(int netId)//斷開指定ID的網絡 
	{ 
		WM.disableNetwork(netId); 
		WM.disconnect(); 
	} 
    
    
   
    
    
}
