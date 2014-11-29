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

public class Signal {
	private int RSSI=0;
	private int ScanNumber=-1;
	private double Distance=-1.0;
	
	public int getRSSI()
	{
		return RSSI;
	}
	
	public int getScanNumber()
	{
		return ScanNumber;
	}
	
	public double getDistance()
	{
		return Distance;
	}
	
	public void Set(int RSSI,int ScanNumber)
	{
		this.RSSI=RSSI;
		this.ScanNumber=ScanNumber;
	}
	
	public void SetAll(int RSSI, int ScanNumber, double Distance)
	{
		this.RSSI=RSSI;
		this.ScanNumber=ScanNumber;
		this.Distance=Distance;
	}
	
	public void SetDistance(double Distance)
	{
		this.Distance=Distance;
	}

}
