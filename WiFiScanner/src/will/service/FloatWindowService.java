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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.wifiscanner.MyWindowManager;
import com.example.wifiscanner.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class FloatWindowService extends Service
{
	private Handler handler = new Handler();  
	BroadcastReceiver mReceiver;
    private Timer timer;  
  
    public class MyReceiver extends BroadcastReceiver 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            if ( MyWindowManager.isWindowShowing()) 
            {
				MyWindowManager.getUsedValue(getApplicationContext());   				
				Toast.makeText(getBaseContext(),"­pºâ¤¤", Toast.LENGTH_SHORT).show();
			}	
        }
        // constructor
        public MyReceiver()
        {

        }
    }
    
    @Override  
    public IBinder onBind(Intent intent) 
    {  
        return null;  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) 
    { 
    	IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION");
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);

        if (timer == null) 
        {  
            timer = new Timer();  
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 300);  
        }  
        return super.onStartCommand(intent, flags, startId);  
    }  
  
    @Override  
    public void onDestroy() 
    {  
        super.onDestroy();  

        unregisterReceiver(mReceiver);
        Toast.makeText(getBaseContext(), "Service  Destroy!!", Toast.LENGTH_SHORT).show();
        MyWindowManager.removeBigWindow(getApplicationContext());
		MyWindowManager.removeSmallWindow(getApplicationContext());
        timer.cancel();  
        timer = null;  
    }  
  
    class RefreshTask extends TimerTask 
    {  
  
        @Override  
        public void run() 
        {  

            if (!MyWindowManager.isWindowShowing()) 
            {  
                handler.post(new Runnable() 
                {  
                    @Override  
                    public void run() 
                    {  
                        MyWindowManager.createSmallWindow(getApplicationContext());  
                    }  
                });  
            }   
            else if ( MyWindowManager.isWindowShowing()) 
            {  
                handler.post(new Runnable() 
                {  
                    @Override  
                    public void run() 
                    {  
                        try 
                        {
							MyWindowManager.updateUsedValue(getApplicationContext());
						} catch (IOException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
                    }  
                });  
            }
        }  
  
    }  
}
