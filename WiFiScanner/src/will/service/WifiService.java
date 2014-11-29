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
	private List<WifiConfiguration> mWifiConfigurations; //�����s���C��
	
	
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
		WM=(WifiManager)context.getSystemService(Context.WIFI_SERVICE); //���oWifiManager��H 
		wifiinfo=WM.getConnectionInfo(); //���owifiinfo��H
	} 
	
	public void openWifi()//���}wifi
	{  
		if(!WM.isWifiEnabled())
		{ 
			WM.setWifiEnabled(true); 
		} 
	} 
	
	public void closeWifi()//����wifi 
	{ 
		if(WM.isWifiEnabled())
		{ 
			WM.setWifiEnabled(false); 
		} 
	} 

	 
	public int checkState()//�ˬd��ewifi���A
	{ 
		return WM.getWifiState(); 
	} 
	
	public void acquireWifiLock()//��wwifiLock 
	{ 
		mWifiLock.acquire(); 
	} 
	
	public void releaseWifiLock()//����wifiLock 
	{ 
		if(mWifiLock.isHeld()) //�P�_�O�_��w
		{ 
			mWifiLock.acquire(); 
		} 
	} 
	public void CreateWifiLock()//�Ыؤ@��wifiLock
	{ 
		mWifiLock=WM.createWifiLock("test");
	}
	 
	public List<WifiConfiguration>getConfiguration()//�o��t�m�n������ 
	{ 
		return mWifiConfigurations; 
	} 
	
	public void connetionConfiguration(int index)//���w�t�m�n�������i��s�� 
	{
		if(index>mWifiConfigurations.size())
		{ 
			return; 
		} 
		WM.enableNetwork(mWifiConfigurations.get(index).networkId,true); //�s���t�m�n���wID������ 
	} 
	
	public void startScan()
	{ 
		WM.startScan(); 
		wifilist=WM.getScanResults(); //�o�챽�y���G 
		mWifiConfigurations=WM.getConfiguredNetworks();//�o��t�m�n�������s��  
	} 
	
	public List<ScanResult>getwifilist()//�o������C�� 
	{ 
		return wifilist; 
	} 
	
	public StringBuffer lookUpScan()//�d�ݱ��y���G 
	{ 
		StringBuffer sb=new StringBuffer(); 
		for(int i=0;i<wifilist.size();i++)
		{ 
			sb.append("Index_"+new Integer(i+1).toString()+":"); //�NScanResult�H���ഫ���@�Ӧr�Ŧ�] 
			sb.append((wifilist.get(i)).toString()).append("\n"); //�]�A�GBSSID�BSSID�Bcapabilities�Bfrequency�Blevel
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
	
	public int getNetWordId()//�o��s����ID 
	{ 
		return(wifiinfo==null)?0:wifiinfo.getNetworkId(); 
	} 
	 
	public String getwifiinfo()//�o��wifiInfo���Ҧ��H��
	{ 
		return(wifiinfo==null)?"NULL":wifiinfo.toString(); 
	} 
	
	
	
	
	
	
	public void addNetWork(WifiConfiguration configuration)//�K�[�@�Ӻ����}�s�� 
	{ 
		int wcgId=WM.addNetwork(configuration); 
		WM.enableNetwork(wcgId,true); 
	} 
	
	public void disConnectionWifi(int netId)//�_�}���wID������ 
	{ 
		WM.disableNetwork(netId); 
		WM.disconnect(); 
	} 
    
    
   
    
    
}
