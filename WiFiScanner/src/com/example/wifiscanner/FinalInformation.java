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
public class FinalInformation {
	public String name="defaultn";
	public String mac="defaultm";
	public String status="defaults";
	public String WifiPoint="defaultw";
	public FinalInformation()
	{
		name="Q_Q";
		mac="Q__Q";
		status="Q___Q";
		WifiPoint="Q____Q";
	}
	
	public void setName(String s)
	{
		name=s;
	}
	public void setMac(String s)
	{
		mac=s;
	}
	public void setStatus(String s)
	{
		status=s;
	}
	public void setWifiPoint(String s)
	{
		WifiPoint=s;
	}

}
