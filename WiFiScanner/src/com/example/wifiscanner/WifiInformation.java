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

import java.util.LinkedList;

import android.graphics.Color;

public class WifiInformation {
	private String MAC="";
	private String SSID="";
	private int channel=0;
	private LinkedList <Signal> signals=new LinkedList<Signal>();
	public int x=-10,y=-10;
	public int x1=-10,y1=-10; //second possible node
	public boolean PossibleNode=false;
	public boolean NoNode=false;
	public boolean AlreadyCal=false;
	public boolean UncertainNode=false;
	public Color color;
	
	public int getChannel()
	{
		return channel;
	}
	
	public void setColor(Color c)
	{
		color=c;
	}
	public void setChannel(int channel)
	{
		this.channel=channel;
	}
	
	public WifiInformation()
	{
		MAC="NULL";
		SSID="NULL";
	}
	public WifiInformation(String MAC,String SSID)
	{
		this.MAC=MAC;
		this.SSID=SSID;
		
		
	}
	public WifiInformation(String MAC,String SSID,int RSSI,int times)
	{
		this.MAC=MAC;
		this.SSID=SSID;
		Signal s=new Signal();
		s.Set(RSSI, times);
		signals.add(s);
	}
	public String getMAC()
	{
		return MAC;
	}
	
	public String getSSID()
	{
		return SSID;
	}
	
	public LinkedList<Signal> getSignal()
	{
		return signals;
	}
	
	public void SetPosition(int x, int y)
	{
		this.x=x;
		this.y=y;
	}
	public void SetSecondPosition(int x1, int y1)
	{
		this.x1=x1;
		this.y1=y1;
	}
	
	public String Dump()
	{
		String sig="";
		for(int i=0;i<signals.size();i++)
		{
			sig+=" ("+(i+1)+")\n"+" Number:"+signals.get(i).getScanNumber()+"\n RSSI:"+signals.get(i).getRSSI()+"\n DIST:"+signals.get(i).getDistance()+"\n";			
		}
		if(AlreadyCal)
		{
			return "MAC:"+MAC+"\nSSID:"+SSID+"\nStatus:AlreadyCal"+"("+x+","+y+")\n"+sig;
		}
		else if(PossibleNode)
		{
			return "MAC:"+MAC+"\nSSID:"+SSID+"\nStatus:PossibleNode"+"("+x+","+y+")¡B("+x1+","+y1+")\n"+sig;
		}
		else if(NoNode)
		{
			return "MAC:"+MAC+"\nSSID:"+SSID+"\nStatus:NoNode\n"+sig;
		}
		else
		{
			return "MAC:"+MAC+"\nSSID:"+SSID+"\nStatus:ABNORMAL!"+sig;
		}
	}
}
