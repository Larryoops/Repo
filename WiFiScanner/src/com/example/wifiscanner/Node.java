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

public class Node {
	public int x;
	public int y;
	public double StrideLength;
	public int orientation;
	public boolean visible=true;
	public Node()
	{}
	public Node(int x, int y, double StrideLength, int orientation)
	{
		this.x=x;
		this.y=y;
		this.StrideLength = StrideLength;
		this.orientation = orientation;
	}
	public Node(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	public Node(Node n)
	{
		this.x=n.x;
		this.y=n.y;
		this.StrideLength = n.StrideLength;
		this.orientation = n.orientation;
	}
	public void setNode(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	public void setVisible(boolean v)
	{
		this.visible=v;
	}
	public String Dump()
	{
		return "("+x+","+y+")";
	}

}
