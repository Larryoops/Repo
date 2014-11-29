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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Start extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        final ImageView start_title=(ImageView)findViewById(R.id.start_title);
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                	
                	
                	final Animation am = new AlphaAnimation(0.0f,1.0f);
                	am.setDuration(3000);
                	am.setRepeatCount(0);
                	
                	/*
                	Display display = getWindowManager().getDefaultDisplay(); 
                	int width = display.getWidth();
                	final int height = display.getHeight();
                	System.out.println("h:"+height+" w:"+width);
                	Animation am = new TranslateAnimation(0, 0, 0, height*(-0.4f));
                	am.setDuration(3000);
                	am.setRepeatCount(1);
                	am.setFillAfter(true);
                	am.setFillEnabled(true);
                	*/
                	
                	am.setAnimationListener(new TranslateAnimation.AnimationListener() {

                		@Override
                		public void onAnimationStart(Animation animation) { }

                		@Override
                		public void onAnimationRepeat(Animation animation) { }

                		@Override
                		public void onAnimationEnd(Animation animation)           		
                		{
                			am.cancel();
                		}
                		});
                	
                	start_title.setAnimation(am);
                	am.startNow();
                	Thread.sleep(3000);
                    startActivity(new Intent().setClass(Start.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }).start();
        
    }

}
