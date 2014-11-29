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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.net.TrafficStats;

public class draw extends View {
	private Paint mPaint;

	private int i = 0;// f***

	private float orientation;
	private String SSID_now = "";
	private String MAC_now = "";
	private String SSID_old = "-999";
	private int SM = 30;// 比例尺

	private boolean continue_to_cal = false;
	private boolean StartMove = false;
	private boolean DrawVoronoi = false;
	private boolean OutputVoronoi = false;
	private boolean SMChanged = false;

	private double Start_RX = 0, Start_TX = 0;
	private double RX = 0.0, TX = 0.0;

	private boolean FirstOrientationRecord = true;
	private float FirstOrientation;

	int r, g, b;
	Random rand = new Random();

	/* ZOOM PARAMETER */
	private float DefaultZoomMultiply = 1.0f;
	private float totalZoom = 1.0f;
	private boolean PlusZoom = false;
	private boolean MinusZoom = false;

	/* use for file name */
	SimpleDateFormat FileDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	Date curDate = new Date(System.currentTimeMillis());
	String FileName = FileDate.format(curDate);
	SimpleDateFormat DirectoryDate = new SimpleDateFormat("yyyy_MM_dd");
	String DirectoryName = DirectoryDate.format(curDate);
	SimpleDateFormat InFileDate = new SimpleDateFormat(
			"西元yyyy年MM月dd日 HH時mm分ss秒");
	String InFileDateName = InFileDate.format(curDate);
	/* use for file name */

	private float OneTouchX;
	private float OneTouchY;
	private float TwoTouchX = -1000;
	private float TwoTouchY = -1000;
	private boolean ZOOM = false;
	private float newDist;
	private float oldDist;

	private float mScaleFocusX;
	private float mScaleFocusY;
	private float LastPosX;
	private float LastPosY;

	private Double TotalChangeProportion = 1.0;
	private int TotalChangeDisplacementX = 0;
	private int TotalChangeDisplacementY = 0;
	private boolean Changed = false;

	/* Preference parameters */
	private boolean DisplayNode = true;
	private boolean DisplayPath = true;
	private boolean NoStopPath = false;
	private int _VoronoiNode = 3;
	private int DifferenceFilter = 10;
	private int PathLoss = 2;
	private int DefaultRSSI = 50;
	private boolean VDisplayPath = false;
	private boolean VDisplayWifiCircle = true;
	private boolean VDisplayNOCALNode = true;
	private boolean VDisplayNode = true;
	private boolean VDisplayPossibleAP = false;
	private boolean DisplayRuler = true;
	/* Preference parameters */

	/* LinkedList */
	private LinkedList<Node> points = new LinkedList<Node>();
	private LinkedList<WifiInformation> wifiInformation = new LinkedList<WifiInformation>();

	/* LinkedList */

	/* bit map */
	Bitmap vBitmap;
	Bitmap WifiBitmap;
	Bitmap ConnectedWifiBitmap;
	Bitmap PossibleWifiBitmap;
	Bitmap NavigateBitmap;
	Bitmap NavigateLightBitmap;
	/* bit map */

	/* Draggable parameter */
	private static final int INVALID_POINTER_ID = -1;
	private Drawable mImage;
	private float mpPosX;
	private float mpPosY;

	private float mvPosX;
	private float mvPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private int mActivePointerId = INVALID_POINTER_ID;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	/* Draggable parameter */

	/* show message parameter */
	private float downX = -500;
	private float downY = -500;
	private float upX = -1000;
	private float upY = -1000;

	public void setPlusZoom() {
		PlusZoom = true;
		MinusZoom = false;
		DefaultZoomMultiply += 0.5f;
	}

	public void setMinusZoom() {
		MinusZoom = true;
		PlusZoom = false;
		DefaultZoomMultiply -= 0.5f;
	}

	public boolean isPausePDR() {
		return !continue_to_cal;
	}

	/* show message parameter */

	/**
	 * Display the AP's message when user click the ap image
	 */
	public void showWifiIconClickMessage() {
		for (int iAP = 0; iAP < wifiInformation.size(); iAP++) {
			if (wifiInformation.get(iAP).NoNode)// No AP image
			{
				continue;
			} else {
				if (wifiInformation.get(iAP).AlreadyCal)// one point
				{
					if (Math.abs(wifiInformation.get(iAP).x - downX) < 12
							&& Math.abs(wifiInformation.get(iAP).y - downY) < 30) {
						Toast.makeText(
								getContext(),
								"顯示" + wifiInformation.get(iAP).getSSID()
										+ "的資訊", Toast.LENGTH_SHORT).show();
						Builder MyAlertDialog = new AlertDialog.Builder(
								getContext());
						MyAlertDialog.setTitle(wifiInformation.get(iAP)
								.getSSID() + "的資訊");
						MyAlertDialog.setMessage("名稱:"
								+ wifiInformation.get(iAP).getSSID() + "\nMAC:"
								+ wifiInformation.get(iAP).getMAC()
								+ "\n預測座標:(" + wifiInformation.get(iAP).x + ","
								+ wifiInformation.get(iAP).y + ")");
						MyAlertDialog.show();
					}
				} else if (VDisplayPossibleAP
						|| wifiInformation.get(iAP).UncertainNode)// two
																	// possible
																	// point
				{
					if (Math.abs(wifiInformation.get(iAP).x - downX) < 12
							&& Math.abs(wifiInformation.get(iAP).y - downY) < 30) {
						Toast.makeText(
								getContext(),
								"顯示" + wifiInformation.get(iAP).getSSID()
										+ "的資訊", Toast.LENGTH_SHORT).show();
						Builder MyAlertDialog = new AlertDialog.Builder(
								getContext());
						MyAlertDialog.setTitle(wifiInformation.get(iAP)
								.getSSID() + "的資訊");
						MyAlertDialog.setMessage("名稱:"
								+ wifiInformation.get(iAP).getSSID() + "\nMAC:"
								+ wifiInformation.get(iAP).getMAC()
								+ "\n預測可能的座標:(" + wifiInformation.get(iAP).x
								+ "," + wifiInformation.get(iAP).y + ")");
						MyAlertDialog.show();
					}/*
					 * else if (Math.abs(dynamic_wifiInformation.get(iAP).x1 -
					 * downX) < 12 &&
					 * Math.abs(dynamic_wifiInformation.get(iAP).y1 - downY) <
					 * 30) { Toast.makeText( getContext(), "顯示" +
					 * dynamic_wifiInformation.get(iAP).getSSID() + "(1/2)的資訊",
					 * Toast.LENGTH_SHORT) .show(); Builder MyAlertDialog = new
					 * AlertDialog.Builder( getContext());
					 * MyAlertDialog.setTitle(dynamic_wifiInformation.get(iAP)
					 * .getSSID() + "的資訊"); MyAlertDialog.setMessage("名稱:" +
					 * dynamic_wifiInformation.get(iAP).getSSID() + "\nMAC:" +
					 * dynamic_wifiInformation.get(iAP).getMAC() +
					 * "\n預測可能的座標之一:(" + dynamic_wifiInformation.get(iAP).x1 +
					 * "," + dynamic_wifiInformation.get(iAP).y1 + ")");
					 * MyAlertDialog.show(); }
					 */

				}
			}
		}

	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * deal the scale and displacement
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);
		gestureDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();
			downX = ev.getX();
			downY = ev.getY();
			ZOOM = false;

			mLastTouchX = x;
			mLastTouchY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);

			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				if (DrawVoronoi) {
					mvPosX += dx;
					mvPosY += dy;
				} else {
					mpPosX += dx;
					mpPosY += dy;
				}

				invalidate();
			}

			mLastTouchX = x;
			mLastTouchY = y;

			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(ev);
			if (oldDist > 10f) {
				OneTouchX = ev.getX(0);
				OneTouchY = ev.getY(0);
				TwoTouchX = ev.getX(1);
				TwoTouchY = ev.getY(1);
				ZOOM = true;
			}

			break;

		case MotionEvent.ACTION_UP: {
			upX = ev.getX();
			upY = ev.getY();
			if (downX == upX && downY == upY && DrawVoronoi) {
				showWifiIconClickMessage();
			}
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}

		return true;
	}

	/**
	 * deal the scale and displacement
	 */
	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
			mScaleFocusX = detector.getFocusX();
			mScaleFocusY = detector.getFocusY();

			invalidate();
			return true;
		}
	}

	/**
	 * storage preference's data
	 * 
	 * @param boolean DisplayNode display node in path page
	 * @param boolean DisplayPath display path in path page
	 * @param boolean NoStopPath when pause the PDR service, still cal and
	 *        display node with different color
	 * @param int _VoronoiNode the interval of node to draw voronoi diagram
	 * @param boolean VDisplayPath display path in voronoi page
	 * @param boolean VDisplayWifiCircle display the circle of wifi's signal
	 *        range in voronoi page
	 * @param boolean VDisplayNOCALNode display the node not use to calculate
	 *        voronoi diagram in voronoi page
	 * @param boolean VDisplayNode display node in voronoi page
	 * @param int DifferenceFilter the filter of getting wifi information
	 * @param int PathLoss the exponent of path loss
	 * @param int DefaultRSSI the base of RSSI
	 */
	public void PreferenceSetting(boolean DisplayRuler,
			boolean VDisplayPossibleAP, boolean DisplayNode,
			boolean DisplayPath, boolean NoStopPath, int _VoronoiNode,
			boolean VDisplayPath, boolean VDisplayWifiCircle,
			boolean VDisplayNOCALNode, boolean VDisplayNode,
			int DifferenceFilter, int PathLoss, int DefaultRSSI) {
		this.VDisplayPossibleAP = VDisplayPossibleAP;
		this.DisplayNode = DisplayNode;
		this.DisplayPath = DisplayPath;
		this.NoStopPath = NoStopPath;
		this._VoronoiNode = _VoronoiNode;
		this.DifferenceFilter = DifferenceFilter;
		this.PathLoss = PathLoss;
		this.DefaultRSSI = DefaultRSSI;
		this.VDisplayPath = VDisplayPath;
		this.VDisplayWifiCircle = VDisplayWifiCircle;
		this.VDisplayNOCALNode = VDisplayNOCALNode;
		this.VDisplayNode = VDisplayNode;
		this.DisplayRuler = DisplayRuler;
	}

	/**
	 * constructor
	 */
	public draw(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());// for
																				// scale
																				// screen
		WifiBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.red_ap);// for draw AP's bitmap
		ConnectedWifiBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.green_ap);
		PossibleWifiBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.light_red_ap);
		NavigateBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.navigate);
		NavigateLightBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.navigate_light);
		gestureDetector = new GestureDetector(context, new GestureListener());
		init();
	}
	
	public void setdrawRuler()
	{
		if(DisplayRuler)
		{
			DisplayRuler=false;
		}
		else
		{
			DisplayRuler=true;
		}
		invalidate();
	}

	/**
	 * constructor
	 */
	public draw(Context context) {
		super(context);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());// for
																				// scale
																				// screen
		init();
	}

	/**
	 * initialize Paint
	 */
	public void init() {
		mPaint = new Paint();// ini

	}

	GestureDetector gestureDetector;

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			if (DrawVoronoi) {
				// dynamic_wifiInformation = wifiInformation;
				// dynamic_points = points;
			} else {
				mpPosX *= -1;
				mpPosY *= -1;
			}
			mScaleFactor = 1 / mScaleFactor;

			Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
			invalidate();

			if (DrawVoronoi) {
				mvPosX = 0;
				mvPosY = 0;
				LastPosX = 0;
				LastPosY = 0;
			} else {
				mpPosX = 0;
				mpPosY = 0;
			}
			mScaleFactor = 1;

			return true;
		}
	}

	/**
	 * Get PDR's message from information.class and storage them
	 * 
	 * @param float length the stride length
	 * @param int roll the angle of direction
	 */
	public void sendOrientationAndWifiMessage(int roll, String ID_now,
			String BSSID_now, int StrideMultiply) throws IOException {

		if (FirstOrientationRecord) {
			FirstOrientation = roll;
			FirstOrientationRecord = false;
		}

		orientation = roll;
		SSID_now = ID_now;
		MAC_now = BSSID_now;

		if (StrideMultiply != SM) {
			SM = StrideMultiply;
			SMChanged = true;
		}
		invalidate();
	}
	
	public void sendorientation(int roll)
	{
		if (FirstOrientationRecord) {
			FirstOrientation = roll;
			FirstOrientationRecord = false;
		}

		orientation = roll;
		invalidate();
	}

	/**
	 * Return LinkedList of WifiInformation to information.class
	 */
	public LinkedList<WifiInformation> getWifiInformation() {
		return wifiInformation;
	}

	/**
	 * Return LinkedList of Node to information.class
	 */
	public LinkedList<Node> getNode() {
		return points;
	}

	/**
	 * Get scan result and calculate : signal->distance
	 * 
	 * @param List
	 *            <ScanResult> sr the result of wifi scan
	 */
	public double CalWifiDistance(ScanResult sr) {
		double exp = ((double) DefaultRSSI - ((double) sr.level + 100.0))
				/ (10.0 * (double) PathLoss);
		DecimalFormat df = new DecimalFormat("#.##");// to the second digit
														// after the decimal
														// point
		String s = df.format(Math.pow(10.0, exp));
		return Double.parseDouble(s);
	}

	public void sendWifiInformation(LinkedList<WifiInformation> wifiinformation) {
		wifiInformation = new LinkedList<WifiInformation>(wifiinformation);
		CalPosition();
	}

	/**
	 * Calculate the position by using the circle of signal ranger : maybe no
	 * result, two possible result, only one result
	 */
	public void CalPosition() {
		for (int j = 0; j < wifiInformation.size(); j++) {
			if (wifiInformation.get(j).AlreadyCal)// already get result then
													// ignore it
			{
				continue;
			}

			/* algorithm of calculating the intersection of circles */
			if (wifiInformation.get(j).getSignal().size() >= 3)// have three
																// information
																// or more
			{
				for (int flag = 0; flag < wifiInformation.get(j).getSignal()
						.size() - 2; flag++) {
					if (wifiInformation.get(j).AlreadyCal) {
						break;
					}
					double x1 = -50.0, y1 = -50.0, x2 = -50.0, y2 = -50.0;
					Node n0 = points.get(wifiInformation.get(j).getSignal()
							.get(flag).getScanNumber());
					Node n1 = points.get(wifiInformation.get(j).getSignal()
							.get(flag + 1).getScanNumber());
					Node n2 = points.get(wifiInformation.get(j).getSignal()
							.get(flag + 2).getScanNumber());

					Signal s0 = wifiInformation.get(j).getSignal().get(flag);
					Signal s1 = wifiInformation.get(j).getSignal()
							.get(flag + 1);
					Signal s2 = wifiInformation.get(j).getSignal()
							.get(flag + 2);
					if (n0.y != n1.y) {
						double m = (n0.x - n1.x) / (n1.y - n0.y);
						double k = (Math.pow(s0.getDistance(), 2.0)
								- Math.pow(s1.getDistance(), 2.0)
								+ Math.pow(n1.x, 2.0) - Math.pow(n0.x, 2.0)
								+ Math.pow(n1.y, 2.0) - Math.pow(n0.y, 2.0))
								/ (2.0 * (n1.y - n0.y));

						double a = 1.0 + Math.pow(m, 2.0);
						double b = 2.0 * (k * m - n1.x - m * n1.y);
						double c = Math.pow(n1.x, 2.0) + Math.pow(n1.y, 2.0)
								+ Math.pow(k, 2.0) - 2.0 * k * n1.y
								- Math.pow(s1.getDistance(), 2.0);

						if (b * b - 4.0 * a * c >= 0)// 有交點時
						{
							x1 = ((-b) + Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// x=(-b+√(b^2-4ac))/2a
							y1 = m * x1 + k;// y=mx+k
							x2 = ((-b) - Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// x=(-b-√(b^2-4ac))/2a
							y2 = m * x2 + k;// y=mx+k

							if (Math.abs(Math.sqrt(Math.pow(x1 - n2.x, 2.0)
									+ Math.pow(y1 - n2.y, 2.0))
									- s2.getDistance())*SM < Math.abs(Math
									.sqrt(Math.pow(x2 - n2.x, 2.0)
											+ Math.pow(y2 - n2.y, 2.0))
									- s2.getDistance())) {
								Toast.makeText(
										getContext(),
										"Intersect node: (" + x1 + "," + y1
												+ ")", Toast.LENGTH_SHORT)
										.show();
								wifiInformation.get(j).SetPosition((int) x1,
										(int) y1);
							} else {
								Toast.makeText(
										getContext(),
										"Intersect node: (" + x2 + "," + y2
												+ ")", Toast.LENGTH_SHORT)
										.show();
								wifiInformation.get(j).SetPosition((int) x2,
										(int) y2);
							}
							wifiInformation.get(j).SetSecondPosition(-1, -1);
							wifiInformation.get(j).NoNode = false;
							wifiInformation.get(j).PossibleNode = false;
							wifiInformation.get(j).AlreadyCal = true;
							wifiInformation.get(j).UncertainNode = false;
						} else// 沒有交點時
						{
							x1 = ((-b) + Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// x=(-b+√(b^2-4ac))/2a
							y1 = m * x1 + k;// y=mx+k
							x2 = ((-b) - Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// x=(-b-√(b^2-4ac))/2a
							y2 = m * x2 + k;// y=mx+k
							wifiInformation.get(j).SetPosition(
									(int) (x1 + x2) / 2, (int) (y1 + y2) / 2);

							wifiInformation.get(j).NoNode = true;
							wifiInformation.get(j).PossibleNode = false;
							wifiInformation.get(j).AlreadyCal = false;
							wifiInformation.get(j).UncertainNode = true;

							// dynamic_wifiInformation.get(j).SetPosition(-300,
							// -300);
							// dynamic_wifiInformation.get(j).SetSecondPosition(-300,
							// -300);
						}
					} else if (n0.y == n1.y) {
						x1 = -(Math.pow(n0.x, 2.0) - Math.pow(n1.x, 2.0)
								- Math.pow(s0.getDistance(), 2.0) + Math.pow(
								s1.getDistance(), 2.0))
								/ (2.0 * n1.x - 2.0 * n0.x);
						double a = 1.0, b = -2.0 * n0.y, c = Math.pow(x1, 2.0)
								+ Math.pow(n0.x, 2.0) - 2.0 * n0.x * x1
								+ Math.pow(n0.y, 2.0)
								- Math.pow(s0.getDistance(), 2.0);
						if (b * b - 4.0 * a * c >= 0) {
							y1 = ((-b) + Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// y=(-b+√(b^2-4ac))/2a
							y2 = ((-b) - Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// y=(-b-√(b^2-4ac))/2a

							if (Math.abs(Math.sqrt(Math.pow(x1 - n2.x, 2)
									+ Math.pow(y1 - n2.y, 2))
									- s2.getDistance()) < Math.abs(Math
									.sqrt(Math.pow(x2 - n2.x, 2)
											+ Math.pow(y2 - n2.y, 2))
									- s2.getDistance())) {
								Toast.makeText(
										getContext(),
										"Intersect node: (" + x1 + "," + y1
												+ ")", Toast.LENGTH_SHORT)
										.show();
								wifiInformation.get(j).SetPosition((int) x1,
										(int) y1);
							} else {
								Toast.makeText(
										getContext(),
										"Intersect node: (" + x2 + "," + y2
												+ ")", Toast.LENGTH_SHORT)
										.show();
								wifiInformation.get(j).SetPosition((int) x2,
										(int) y2);
							}
							wifiInformation.get(j).SetSecondPosition(-1, -1);
							wifiInformation.get(j).NoNode = false;
							wifiInformation.get(j).PossibleNode = false;
							wifiInformation.get(j).AlreadyCal = true;
						} else// 沒有交點時
						{
							y1 = ((-b) + Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// y=(-b+√(b^2-4ac))/2a
							y2 = ((-b) - Math.sqrt(b * b - 4.0 * a * c))
									/ (2.0 * a);// y=(-b-√(b^2-4ac))/2a

							wifiInformation.get(j).SetPosition(
									(int) (x1 + x2) / 2, (int) (y1 + y2) / 2);

							wifiInformation.get(j).UncertainNode = true;
							wifiInformation.get(j).NoNode = true;
							wifiInformation.get(j).PossibleNode = false;
							wifiInformation.get(j).AlreadyCal = false;
						}
					}
				}
			} else if (wifiInformation.get(j).getSignal().size() == 2)// cannot
																		// recognize
																		// :
																		// less
																		// than
																		// 3
																		// signal
			{
				double x1 = -50.0, y1 = -50.0, x2 = -50.0, y2 = -50.0;
				Node n0 = points.get(wifiInformation.get(j).getSignal().get(0)
						.getScanNumber());
				Node n1 = points.get(wifiInformation.get(j).getSignal().get(1)
						.getScanNumber());

				Signal s0 = wifiInformation.get(j).getSignal().get(0);
				Signal s1 = wifiInformation.get(j).getSignal().get(1);
				if (n0.y != n1.y) {
					double m = (n0.x - n1.x) / (n1.y - n0.y);
					double k = (Math.pow(s0.getDistance(), 2.0)
							- Math.pow(s1.getDistance(), 2.0)
							+ Math.pow(n1.x, 2.0) - Math.pow(n0.x, 2.0)
							+ Math.pow(n1.y, 2.0) - Math.pow(n0.y, 2.0))
							/ (2.0 * (n1.y - n0.y));

					double a = 1 + Math.pow(m, 2.0);
					double b = 2.0 * (k * m - n1.x - m * n1.y);
					double c = Math.pow(n1.x, 2.0) + Math.pow(n1.y, 2.0)
							+ Math.pow(k, 2.0) - 2.0 * k * n1.y
							- Math.pow(s1.getDistance(), 2.0);

					if (b * b - 4 * a * c >= 0)// 有交點時
					{
						x1 = ((-b) + Math.sqrt(b * b - 4.0 * a * c))
								/ (2.0 * a);// x=(-b+√(b^2-4ac))/2a
						y1 = m * x1 + k;// y=mx+k
						x2 = ((-b) - Math.sqrt(b * b - 4.0 * a * c))
								/ (2.0 * a);// x=(-b-√(b^2-4ac))/2a
						y2 = m * x2 + k;// y=mx+k

						wifiInformation.get(j).SetPosition((int) x1, (int) y1);
						wifiInformation.get(j).SetSecondPosition((int) x2,
								(int) y2);
						wifiInformation.get(j).NoNode = false;
						wifiInformation.get(j).PossibleNode = true;
					} else// 沒有交點時
					{
						wifiInformation.get(j).SetPosition(-200, -200);
						wifiInformation.get(j).SetSecondPosition(-200, -200);
						wifiInformation.get(j).NoNode = true;
						wifiInformation.get(j).PossibleNode = false;
					}
				} else if (n0.y == n1.y) {
					x1 = -(Math.pow(n0.x, 2.0) - Math.pow(n1.x, 2.0)
							- Math.pow(s0.getDistance(), 2.0) + Math.pow(
							s1.getDistance(), 2.0))
							/ (2.0 * n1.x - 2.0 * n0.x);
					x2 = x1;
					double a = 1.0, b = -2.0 * n0.y, c = Math.pow(x1, 2.0)
							+ Math.pow(n0.x, 2.0) - 2.0 * n0.x * x1
							+ Math.pow(n0.y, 2.0)
							- Math.pow(s0.getDistance(), 2.0);
					if (b * b - 4.0 * a * c >= 0) {
						y1 = ((-b) + Math.sqrt(b * b - 4.0 * a * c))
								/ (2.0 * a);// y=(-b+√(b^2-4ac))/2a
						y2 = ((-b) - Math.sqrt(b * b - 4.0 * a * c))
								/ (2.0 * a);// y=(-b-√(b^2-4ac))/2a

						wifiInformation.get(j).SetPosition((int) x1, (int) y1);
						wifiInformation.get(j).SetSecondPosition((int) x2,
								(int) y2);
						wifiInformation.get(j).NoNode = false;
						wifiInformation.get(j).PossibleNode = true;

					} else// 沒有交點時
					{
						wifiInformation.get(j).SetPosition(-200, -200);
						wifiInformation.get(j).SetSecondPosition(-200, -200);
						wifiInformation.get(j).NoNode = true;
						wifiInformation.get(j).PossibleNode = false;
					}
				}

			} else {
				wifiInformation.get(j).SetPosition(-100, -100);
				wifiInformation.get(j).SetSecondPosition(-100, -100);
				wifiInformation.get(j).NoNode = true;
			}
		}
		/* algorithm of calculating the intersection of circles */

	}

	/**
	 * Output the voronoi diagram to SD card : execute from onDraw
	 */
	public void OutputVoronoi() {
		OutputVoronoi = true;
	}

	/**
	 * Draw the voronoi diagram : execute from the menu of information.class
	 */
	public void setVoronoiEnabled() {
		if (DrawVoronoi == true) {
			DrawVoronoi = false;
		} else {
			DrawVoronoi = true;
		}
		invalidate();
	}

	/**
	 * Get the boolean of DrawVoronoi : need to draw?
	 */
	public boolean getVoronoiEnabled() {
		return DrawVoronoi;
	}

	/**
	 * Pause the PDR service
	 */
	public void drawPause() {
		if (continue_to_cal == true) {
			Toast.makeText(getContext(), "PDR 暫停", Toast.LENGTH_SHORT).show();
			continue_to_cal = false;
		} else {
			Toast.makeText(getContext(), "PDR 繼續", Toast.LENGTH_SHORT).show();
			continue_to_cal = true;
		}
		invalidate();

	}
	
	
	public void StartPDR()
	{
		continue_to_cal=true;
		invalidate();
	}

	/**
	 * Set the new coordinate from information.class and add to LinkedList
	 * 
	 * @param int x
	 * @param int y
	 */
	public void set(int x, int y) {// now no use in information.java

		if (x == 0 && y == 0)// no move : ignore
		{

		} else if (continue_to_cal == true) {
			StartMove = true;
			points.add(new Node(points.get(points.size() - 1).x + x, points
					.get(points.size() - 1).y - y));
			i++;
		} else if (continue_to_cal == false && NoStopPath) {
			points.add(new Node(points.get(points.size() - 1).x + x, points
					.get(points.size() - 1).y - y));
			points.get(points.size() - 1).setVisible(false);
		}

		invalidate();
	}

	public void setNP(double SL, int angle) {
		float tempx, tempy;
		if (SMChanged) {
			StartMove = true;

			for (int qq = 1; qq < points.size(); qq++) {
				tempx = (float) (points.get(qq - 1).x + points.get(qq).StrideLength
						* SM
						* (Math.sin(points.get(qq).orientation / 180.0
								* Math.PI)));
				tempy = (float) (points.get(qq - 1).y - points.get(qq).StrideLength
						* SM
						* (Math.cos(points.get(qq).orientation / 180.0
								* Math.PI)));
				points.get(qq).setNode((int) tempx, (int) tempy);
			}
			tempx = (float) (SL) * SM
					* (float) (Math.sin(angle / 180.0 * Math.PI));
			tempy = (float) (SL) * SM
					* (float) (Math.cos(angle / 180.0 * Math.PI));
			points.add(new Node(points.get(points.size() - 1).x + (int) tempx,
					points.get(points.size() - 1).y - (int) tempy, SL, angle));
			SMChanged = false;

		} else {

			tempx = (float) (SL) * SM
					* (float) (Math.sin(orientation / 180.0 * Math.PI));
			tempy = (float) (SL) * SM
					* (float) (Math.cos(orientation / 180.0 * Math.PI));
			if (continue_to_cal && points.isEmpty()) {
				StartMove = true;
			}
			if ((SL == 0)) {
				// no move
			} else if (continue_to_cal == true) {
				StartMove = true;
				points.add(new Node(points.get(points.size() - 1).x
						+ (int) tempx, points.get(points.size() - 1).y
						- (int) tempy, SL, angle));
				i++;
			} else if (continue_to_cal == false && NoStopPath) {
				points.add(new Node(points.get(points.size() - 1).x
						+ (int) tempx, points.get(points.size() - 1).y
						- (int) tempy, SL, angle));
				points.get(points.size() - 1).setVisible(false);
			}
		}
		invalidate();

	}

	/**
	 * Draw bitmap of AP's icon
	 * 
	 * @param Canvas
	 *            canvas
	 * @param int x coordinate of x
	 * @param int y coordinate of y
	 */
	public void drawWifiIcon(Canvas canvas, int x, int y) {
		canvas.drawBitmap(WifiBitmap, x, y, mPaint);
	}

	public void drawNavigateIcon(Canvas canvas, int x, int y) {
		Bitmap ScaledNavigate = null;
		if (continue_to_cal) {
			long between = (System.currentTimeMillis() / 1000) % 60;

			if ((between % 2) == 1) {
				ScaledNavigate = Bitmap.createScaledBitmap(NavigateBitmap, 30,
						30, true);

			} else {
				ScaledNavigate = Bitmap.createScaledBitmap(NavigateLightBitmap,
						30, 30, true);
			}
		} else {
			ScaledNavigate = Bitmap.createScaledBitmap(NavigateBitmap, 30, 30,
					true);
		}

		float RotateAngle = orientation - FirstOrientation;
		Matrix mtx = new Matrix();

		float px = getWidth() / 2;
		float py = getHeight() / 2;
		mtx.postTranslate(-ScaledNavigate.getWidth() / 2,
				-ScaledNavigate.getHeight() / 2);
		mtx.postRotate(RotateAngle);
		// mtx.setRotate(RotateAngle, ScaledNavigate.getWidth()/2,
		// ScaledNavigate.getHeight()/2);
		mtx.postTranslate(px, py);
		// Bitmap RotateNavigate=Bitmap.createBitmap(ScaledNavigate,0,0,
		// ScaledNavigate.getWidth(), ScaledNavigate.getHeight(), mtx, true);
		// canvas.drawBitmap(RotateNavigate, x, y,mPaint);
		canvas.drawBitmap(ScaledNavigate, mtx, mPaint);
	}

	public void ChangeMapSize() {
		int maxX = -1000, maxY = -1000, minX = 1000, minY = 1000;
		System.out.println("Width:" + getWidth() + " Height:" + getHeight());
		for (int CheckP = 0; CheckP < points.size(); CheckP++) {
			if (points.get(CheckP).x > maxX) {
				maxX = points.get(CheckP).x;
			}
			if (points.get(CheckP).x < minX) {
				minX = points.get(CheckP).x;
			}

			if (points.get(CheckP).y > maxY) {
				maxY = points.get(CheckP).y;
			}
			if (points.get(CheckP).y < minY) {
				minY = points.get(CheckP).y;
			}
		}
		for (int CheckW = 0; CheckW < wifiInformation.size(); CheckW++) {
			if (wifiInformation.get(CheckW).AlreadyCal) {
				if (wifiInformation.get(CheckW).x > maxX) {
					maxX = wifiInformation.get(CheckW).x;
				}
				if (wifiInformation.get(CheckW).x < minX) {
					minX = wifiInformation.get(CheckW).x;
				}

				if (wifiInformation.get(CheckW).y > maxY) {
					maxY = wifiInformation.get(CheckW).y;
				}
				if (wifiInformation.get(CheckW).y < minY) {
					minY = wifiInformation.get(CheckW).y;
				}
			} else if (wifiInformation.get(CheckW).PossibleNode) {
				if (wifiInformation.get(CheckW).x > maxX) {
					maxX = wifiInformation.get(CheckW).x;
				}
				if (wifiInformation.get(CheckW).x < minX) {
					minX = wifiInformation.get(CheckW).x;
				}

				if (wifiInformation.get(CheckW).y > maxY) {
					maxY = wifiInformation.get(CheckW).y;
				}
				if (wifiInformation.get(CheckW).y < minY) {
					minY = wifiInformation.get(CheckW).y;
				}

				if (wifiInformation.get(CheckW).x1 > maxX) {
					maxX = wifiInformation.get(CheckW).x1;
				}
				if (wifiInformation.get(CheckW).x1 < minX) {
					minX = wifiInformation.get(CheckW).x1;
				}

				if (wifiInformation.get(CheckW).y1 > maxY) {
					maxY = wifiInformation.get(CheckW).y1;
				}
				if (wifiInformation.get(CheckW).y1 < minY) {
					minY = wifiInformation.get(CheckW).y1;
				}
			}
		}

		System.out.println("MaxX:" + maxX + " MaxY:" + maxY + " MinX:" + minX
				+ " MinY:" + minY);

		if (minX <= 0) {
			int Displacement = (-1) * minX + 3;
			System.out
					.println("Displacement(for X-Coordinate):" + Displacement);
			TotalChangeDisplacementX = Displacement;
			minX += Displacement;
			maxX += Displacement;
		}
		if (minY <= 0) {
			int Displacement = (-1) * minY + 3;
			System.out
					.println("Displacement(for Y-Coordinate):" + Displacement);
			TotalChangeDisplacementY = Displacement;
			minY += Displacement;
			maxY += Displacement;
		}

		if (maxX > this.getWidth()) {
			Double DmaxX = new Double(maxX);
			Double Proportion = (this.getWidth() - 50) / DmaxX;
			System.out.println("Proporion(for maxX):" + Proportion);
			TotalChangeProportion *= Proportion;
			maxX *= Proportion;
			maxY *= Proportion;
		}
		if (maxY > this.getHeight()) {
			Double DmaxY = new Double(maxY);
			Double Proportion = (this.getHeight() - 50) / DmaxY;
			System.out.println("ProporionY(for maxY):" + Proportion);
			TotalChangeProportion *= Proportion;
			maxX *= Proportion;
			maxY *= Proportion;
		}
		System.out.println("MaxX:" + maxX + " MaxY:" + maxY + " MinX:" + minX
				+ " MinY:" + minY);
		/* Start Change */
		for (int ChangeP = 0; ChangeP < points.size(); ChangeP++) {
			points.get(ChangeP).x += TotalChangeDisplacementX;
			points.get(ChangeP).x *= TotalChangeProportion;

			points.get(ChangeP).y += TotalChangeDisplacementY;
			points.get(ChangeP).y *= TotalChangeProportion;
		}

		for (int ChangeW = 0; ChangeW < wifiInformation.size(); ChangeW++) {
			if (wifiInformation.get(ChangeW).AlreadyCal
					|| wifiInformation.get(ChangeW).UncertainNode) {
				wifiInformation.get(ChangeW).x += TotalChangeDisplacementX;
				wifiInformation.get(ChangeW).x *= TotalChangeProportion;

				wifiInformation.get(ChangeW).y += TotalChangeDisplacementY;
				wifiInformation.get(ChangeW).y *= TotalChangeProportion;
			} else if (wifiInformation.get(ChangeW).PossibleNode) {
				wifiInformation.get(ChangeW).x += TotalChangeDisplacementX;
				wifiInformation.get(ChangeW).x *= TotalChangeProportion;

				wifiInformation.get(ChangeW).y += TotalChangeDisplacementY;
				wifiInformation.get(ChangeW).y *= TotalChangeProportion;

				wifiInformation.get(ChangeW).x1 += TotalChangeDisplacementX;
				wifiInformation.get(ChangeW).x1 *= TotalChangeProportion;

				wifiInformation.get(ChangeW).y1 += TotalChangeDisplacementY;
				wifiInformation.get(ChangeW).y1 *= TotalChangeProportion;
			}
		}
	}

	

	public void DrawRuler(Canvas canvas) {
		/* View's width and height */
		int Width = getWidth();
		int Height = getHeight();

		double WReal_length = Width / SM;// real width length
		double WLength_per = Math.floor(Width
				* (Math.ceil(WReal_length / 3) / WReal_length));// width length
																// per one field
		double WTotal_Remain = Width;// now remain width for drawing
		int Wdraw_times = 0;
		mPaint.setTextSize(20);
		while (WTotal_Remain > 0) {
			Wdraw_times++;
			if (WTotal_Remain >= WLength_per) {
				WTotal_Remain -= WLength_per;
				mPaint.setColor(Color.BLACK);
				canvas.drawLine((float) WLength_per * (Wdraw_times - 1),
						(float) (Height * 0.99) - 8, (float) WLength_per
								* Wdraw_times, (float) (Height * 0.99) - 8,
						mPaint);// -
				canvas.drawLine(0, (float) (Height * 0.99) - 16, 0,
						(float) (Height * 0.99), mPaint);// |-
				canvas.drawLine((float) WLength_per * Wdraw_times,
						(float) (Height * 0.99) - 16, (float) WLength_per
								* Wdraw_times, (float) (Height * 0.99), mPaint);// |-|
				mPaint.setColor(Color.BLACK);
				if (((float) WLength_per * Wdraw_times - 20) < Width * 0.9) {
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(2);
					canvas.drawText(
							String.valueOf((nf
									.format(((WReal_length / 3) * Wdraw_times)
											/ (mScaleFactor == 1.0f ? DefaultZoomMultiply
													: mScaleFactor
															* DefaultZoomMultiply))))
									+ "m", (float) WLength_per * Wdraw_times
									- 20, (float) (Height * 0.99) - 26, mPaint);
				}
				mPaint.setColor(Color.BLACK);
				for (int Wdrawlittle = 0; Wdrawlittle < 5; Wdrawlittle++) {
					canvas.drawLine(
							(float) ((Wdrawlittle + 1) * (WLength_per / 5) + (Wdraw_times - 1)
									* (WLength_per)),
							(float) (Height * 0.99) - 12,
							(float) ((Wdrawlittle + 1) * (WLength_per / 5) + (Wdraw_times - 1)
									* (WLength_per)),
							(float) (Height * 0.99) - 4, mPaint);// little |
				}

			} else {
				double WRemain = WLength_per - WTotal_Remain;
				WTotal_Remain = 0;
				mPaint.setColor(Color.BLACK);
				canvas.drawLine((float) WLength_per * (Wdraw_times - 1),
						(float) (Height * 0.99) - 8, Width,
						(float) (Height * 0.99) - 8, mPaint);// -
				int RemainTimes = 0;
				while (RemainTimes < 4) {
					RemainTimes++;
					if (WRemain < (WLength_per / 5)) {
						// break;
					}
					canvas.drawLine(
							(float) ((Wdraw_times - 1) * (WLength_per) + RemainTimes
									* (WLength_per / 5)),
							(float) (Height * 0.99) - 12,
							(float) ((Wdraw_times - 1) * (WLength_per) + RemainTimes
									* (WLength_per / 5)),
							(float) (Height * 0.99) - 4, mPaint);// little |

				}
			}
		}

		double HReal_length = Height / SM;
		double HLength_per = Math.floor(Height
				* (Math.ceil(HReal_length / 5) / HReal_length));
		double HTotal_Remain = Height;
		int Hdraw_times = 0;
		while (HTotal_Remain > 0) {
			Hdraw_times++;
			if (HTotal_Remain >= HLength_per) {
				HTotal_Remain -= HLength_per;
				mPaint.setColor(Color.BLACK);
				canvas.drawLine((float) (Width * 0.99) - 8, (float) HLength_per
						* (Hdraw_times - 1), (float) (Width * 0.99) - 8,
						(float) HLength_per * Hdraw_times, mPaint);// |
				canvas.drawLine((float) (Width * 0.99) - 16, 0,
						(float) (Width * 0.99), 0, mPaint);// -|
				canvas.drawLine((float) (Width * 0.99) - 16,
						(float) HLength_per * Hdraw_times,
						(float) (Width * 0.99), (float) HLength_per
								* Hdraw_times, mPaint);// -|-
				mPaint.setColor(Color.BLACK);
				if (HLength_per * Hdraw_times < Height * 0.9) {
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(2);
					canvas.drawText(
							String.valueOf((nf
									.format(((HReal_length / 5) * Hdraw_times)
											/ (mScaleFactor == 1.0f ? DefaultZoomMultiply
													: mScaleFactor
															* DefaultZoomMultiply))))
									+ "m", (float) (Width * 0.99) - 72,
							(float) HLength_per * Hdraw_times, mPaint);
				}
				mPaint.setColor(Color.BLACK);
				for (int Hdrawlittle = 0; Hdrawlittle < 5; Hdrawlittle++) {
					canvas.drawLine(
							(float) (Width * 0.99) - 12,
							(float) ((Hdrawlittle + 1) * (HLength_per / 5) + (Hdraw_times - 1)
									* (HLength_per)),
							(float) (Width * 0.99) - 4,
							(float) ((Hdrawlittle + 1) * (HLength_per / 5) + (Hdraw_times - 1)
									* (HLength_per)), mPaint);// little -
				}

			} else {
				double HRemain = HLength_per - HTotal_Remain;
				HTotal_Remain = 0;
				mPaint.setColor(Color.BLACK);
				canvas.drawLine((float) (Width * 0.99) - 8, (float) HLength_per
						* (Hdraw_times - 1), (float) (Width * 0.99) - 8,
						Height, mPaint);// |
				int RemainTimes = 0;
				while (RemainTimes < 4) {
					RemainTimes++;
					if (HRemain < (HLength_per / 5)) {
						// break;
					}
					canvas.drawLine(
							(float) (Width * 0.99) - 12,
							(float) ((Hdraw_times - 1) * (HLength_per) + RemainTimes
									* (HLength_per / 5)),
							(float) (Width * 0.99) - 4,
							(float) ((Hdraw_times - 1) * (HLength_per) + RemainTimes
									* (HLength_per / 5)), mPaint);// little -

				}
			}

		}

		/*
		 * canvas.drawLine(10, (float) (getHeight() * 0.99) - 8, 80, (float)
		 * (getHeight() * 0.99) - 8, mPaint);
		 * 
		 * canvas.drawLine(10, (float) (getHeight() * 0.99) - 15, 10, (float)
		 * (getHeight() * 0.99) - 5, mPaint); canvas.drawLine(80, (float)
		 * (getHeight() * 0.99) - 15, 80, (float) (getHeight() * 0.99) - 5,
		 * mPaint);
		 * 
		 * canvas.drawText(SM + "m", 85, (float) (getHeight() * 0.99) - 5,
		 * mPaint);
		 */
	}

	/**
	 * Drawing
	 * 
	 * @param Canvas
	 *            canvas
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);


		if (DisplayRuler) {
			DrawRuler(canvas);

		}

		if (DrawVoronoi)// in voronoi page
		{
			if (!Changed) {
				ChangeMapSize();
				Changed = true;
			} else {
				for (int translate = 0; translate < points.size(); translate++) {
					points.get(translate).x -= (LastPosX - mvPosX);
					points.get(translate).y -= (LastPosY - mvPosY);
				}
				for (int translate = 0; translate < wifiInformation.size(); translate++) {
					if (wifiInformation.get(translate).AlreadyCal
							|| wifiInformation.get(translate).UncertainNode) {
						wifiInformation.get(translate).x -= (LastPosX - mvPosX);
						wifiInformation.get(translate).y -= (LastPosY - mvPosY);
					} else if (wifiInformation.get(translate).PossibleNode) {
						wifiInformation.get(translate).x -= (LastPosX - mvPosX);
						wifiInformation.get(translate).y -= (LastPosY - mvPosY);
						wifiInformation.get(translate).x1 -= (LastPosX - mvPosX);
						wifiInformation.get(translate).y1 -= (LastPosY - mvPosY);
					}
				}

				LastPosX = mvPosX;
				LastPosY = mvPosY;
			}
			if (OutputVoronoi)// initialize bitmap for draw icon
			{
				vBitmap = Bitmap.createBitmap(this.getWidth(),
						this.getHeight(), Bitmap.Config.RGB_565);
				canvas = new Canvas(vBitmap);
			}

			/* for screen draggable */
			canvas.save();
			// canvas.translate(mvPosX, mvPosY);
			if (PlusZoom) {
				canvas.scale(DefaultZoomMultiply, DefaultZoomMultiply,
						getWidth() / 2, getHeight() / 2);
			}
			if (MinusZoom) {
				canvas.scale(DefaultZoomMultiply, DefaultZoomMultiply,
						getWidth() / 2, getHeight() / 2);
			}
			if (ZOOM) {

				canvas.scale(mScaleFactor, mScaleFactor, mScaleFocusX,
						mScaleFocusY);
			}
			/* for screen draggable */

			Voronoi v = new Voronoi(0.00001f);
			int NodeSize = points.size() / (_VoronoiNode + 1)
					+ ((points.size() % (_VoronoiNode + 1) == 0 ? 0 : 1));

			if (VDisplayPath)// display path
			{
				for (int j = 0; j < points.size() - 1; j++) {
					mPaint.setColor(Color.BLACK);
					canvas.drawLine(points.get(j).x, points.get(j).y,
							points.get(j + 1).x, points.get(j + 1).y, mPaint);
				}
			}

			double[] x = new double[NodeSize], y = new double[NodeSize];

			/* Get node to calculate voronoi edges */
			for (int k = 0, index = 0; k < points.size() && index < NodeSize; k = k
					+ _VoronoiNode + 1, index++) {
				x[index] = (double) points.get(k).x;
				y[index] = (double) points.get(k).y;
				if (VDisplayNode) {
					mPaint.setColor(Color.GREEN);
					canvas.drawCircle((float) x[index], (float) y[index], 10,
							mPaint);
				}
				if (!VDisplayNOCALNode && VDisplayNode)// No display node not
														// use in voronoi
														// diagram
				{
					if (k == points.size() - 1) {
						mPaint.setColor(Color.RED);// now position
					} else {
						mPaint.setColor(Color.BLUE);// now now position
					}
					canvas.drawCircle(points.get(k).x, points.get(k).y, 6,
							mPaint);
					mPaint.setColor(Color.BLACK);
					mPaint.setTextSize(15);
					canvas.drawText("" + (k + 1), points.get(k).x - 8,
							points.get(k).y - 8, mPaint);// number of
															// node
				}
			}
			List<GraphEdge> allEdges = v.generateVoronoi(x, y, 0.0,
					this.getWidth(), 0.0, this.getHeight());// generate voronoi
															// diagram's edge
			PathEffect effects = new DashPathEffect(new float[] { 20, 20, 20,
					20 }, 1);// dash line
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Style.STROKE);
			mPaint.setPathEffect(effects);
			for (int j = 0; j < allEdges.size(); j++) {
				/* draw dash line by using Path function */
				Path path = new Path();
				path.moveTo((float) allEdges.get(j).x1,
						(float) allEdges.get(j).y1);
				path.lineTo((float) allEdges.get(j).x2,
						(float) allEdges.get(j).y2);
				canvas.drawPath(path, mPaint);
			}

			mPaint.setPathEffect(null);
			mPaint.setStyle(Style.FILL);

			for (int j = 0; j < allEdges.size(); j++) {
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.GRAY);
				canvas.drawCircle((float) allEdges.get(j).x1,
						(float) allEdges.get(j).y1, 3, mPaint);
				Log.i("Voronoi", "No." + j + " (" + allEdges.get(j).x1 + ","
						+ (float) allEdges.get(j).y1 + ")");
				canvas.drawCircle((float) allEdges.get(j).x2,
						(float) allEdges.get(j).y2, 3, mPaint);
				Log.i("Voronoi", "    " + " (" + allEdges.get(j).x2 + ","
						+ (float) allEdges.get(j).y2 + ")");
			}

			/* draw point&number */
			if (VDisplayNode) {
				if (!VDisplayNOCALNode)// already display above
				{

				} else// display all node
				{
					for (int j = 0; j < points.size(); j++) {
						if (j == points.size() - 1) {
							mPaint.setColor(Color.RED);
						} else {
							mPaint.setColor(Color.BLUE);
						}
						canvas.drawCircle(points.get(j).x, points.get(j).y, 6,
								mPaint);
						mPaint.setColor(Color.BLACK);
						mPaint.setTextSize(15);
						canvas.drawText("" + (j + 1), points.get(j).x - 8,
								points.get(j).y - 8, mPaint);
					}
				}
			}

			/* Draw Icon if have result */
			for (int j = 0; j < wifiInformation.size(); j++) {
				if (wifiInformation.get(j).AlreadyCal)// one result
				{
					long between = (System.currentTimeMillis() / 1000) % 60;

					if (wifiInformation.get(j).getMAC()
							.equalsIgnoreCase(MAC_now)
							&& (between % 2) == 1) {
						canvas.drawBitmap(ConnectedWifiBitmap,
								wifiInformation.get(j).x - 12,
								wifiInformation.get(j).y - 30, mPaint);
						/*
						 * mPaint.setColor(Color.WHITE);
						 * mPaint.setStyle(Style.FILL); mPaint.setTextSize(15);
						 * canvas.drawText("" +
						 * wifiInformation.get(j).getSSID(),
						 * wifiInformation.get(j).x - 30,
						 * wifiInformation.get(j).y - 18, mPaint);
						 */
					}

					else {
						canvas.drawBitmap(WifiBitmap,
								wifiInformation.get(j).x - 12,
								wifiInformation.get(j).y - 30, mPaint); // wait
						// for
						// modifying:
						// need
						// center
						/*
						 * mPaint.setColor(Color.WHITE);
						 * mPaint.setStyle(Style.FILL); mPaint.setTextSize(15);
						 * canvas.drawText("" +
						 * wifiInformation.get(j).getSSID(),
						 * wifiInformation.get(j).x - 30,
						 * wifiInformation.get(j).y - 18, mPaint);
						 */
					}

					if ((wifiInformation.get(j).getMAC()
							.equalsIgnoreCase(MAC_now) == false)
							&& (wifiInformation.get(j).getSSID()
									.equals(SSID_now))) {
						canvas.drawBitmap(ConnectedWifiBitmap,
								wifiInformation.get(j).x - 12,
								wifiInformation.get(j).y - 30, mPaint);

					}

				} else if ((wifiInformation.get(j).PossibleNode || wifiInformation
						.get(j).UncertainNode) && VDisplayPossibleAP)// two
																		// possible
				// result
				{
					canvas.drawBitmap(PossibleWifiBitmap,
							wifiInformation.get(j).x - 12,
							wifiInformation.get(j).y - 30, mPaint);
					// canvas.drawBitmap(PossibleWifiBitmap,wifiInformation.get(j).x1
					// - 12,wifiInformation.get(j).y1 - 30, mPaint);
					/*
					 * mPaint.setColor(Color.WHITE);
					 * mPaint.setStyle(Style.FILL); mPaint.setTextSize(15);
					 * canvas.drawText("1." + wifiInformation.get(j).getSSID(),
					 * wifiInformation.get(j).x - 30, wifiInformation.get(j).y -
					 * 18, mPaint); canvas.drawText("2." +
					 * wifiInformation.get(j).getSSID(),
					 * wifiInformation.get(j).x1 - 30, wifiInformation.get(j).y1
					 * - 18, mPaint);
					 */
				} else if ((wifiInformation.get(j).NoNode)
						&& VDisplayWifiCircle)// no result
				{

					mPaint.setColor(wifiInformation.get(j).color.hashCode());
					mPaint.setStyle(Style.STROKE);
					mPaint.setStrokeWidth(3);
					canvas.drawCircle(
							(float) points.get(wifiInformation.get(j)
									.getSignal().get(0).getScanNumber()).x,
							(float) points.get(wifiInformation.get(j)
									.getSignal().get(0).getScanNumber()).y,
							(float) wifiInformation.get(j).getSignal().get(0)
									.getDistance(), mPaint);

					mPaint.setColor(Color.WHITE);
					mPaint.setStyle(Style.FILL);
					mPaint.setTextSize(15);
				}
			}

			/* for screen draggable */
			canvas.restore();
			/* for screen draggable */

			if (OutputVoronoi)// output voronoi diagram bitmap to SD card
			{
				canvas.drawBitmap(vBitmap, 0, 0, null);

				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy_MM_dd@HH.mm.ss");
				Date curDate = new Date(System.currentTimeMillis()); // get now
																		// time

				String str = formatter.format(curDate);
				File sd = Environment.getExternalStorageDirectory();
				String path = sd + "/WifiScanner/VoronoiDiagram";
				File dirFile = new File(path);

				if (!dirFile.exists()) {
					dirFile.mkdirs();
					Toast.makeText(getContext(), "創建目錄:" + path,
							Toast.LENGTH_SHORT).show();
				}
				try {
					// 輸出的圖檔位置
					FileOutputStream fos = new FileOutputStream(path + "/"
							+ str + ".png");
					// 將 Bitmap 儲存成 PNG / JPEG 檔案格式
					boolean success = vBitmap.compress(
							Bitmap.CompressFormat.PNG, 100, fos);
					if (success) {
						Toast.makeText(getContext(),
								"儲存路徑:" + path + "/" + str + ".png",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getContext(), "儲存失敗", Toast.LENGTH_SHORT)
								.show();
					}

					// 釋放
					fos.close();
				} catch (IOException e) {

				}
				OutputVoronoi = false;
			}

		}

		else// in path page
		{

			Changed = false;

			/* for draggable */
			canvas.save();
			canvas.translate(mpPosX, mpPosY);

			for(int rotation=0;rotation<orientation;rotation++)
			{
				canvas.rotate(-1, getWidth()/2, getHeight()/2);
			}

			if (PlusZoom) {
				canvas.scale(DefaultZoomMultiply, DefaultZoomMultiply,
						getWidth() / 2, getHeight() / 2);
			}
			if (MinusZoom) {
				canvas.scale(DefaultZoomMultiply, DefaultZoomMultiply,
						getWidth() / 2, getHeight() / 2);
			}

			if (ZOOM/*
					 * &&(totalZoom*mScaleFactor<5.0f)&&(totalZoom*mScaleFactor>0.5f
					 * )
					 */) {

				Log.i("!!!!!!ZOOM", "FACTOR :" + mScaleFactor);
				totalZoom *= mScaleFactor;
				/*
				 * for(int ZoomPoints=0;ZoomPoints<points.size();ZoomPoints++) {
				 * Log .i("ZOOM","("+points.get(ZoomPoints).x+","+points
				 * .get(ZoomPoints).y+")");
				 * points.get(ZoomPoints).x*=mScaleFactor;
				 * points.get(ZoomPoints).y*=mScaleFactor;
				 * 
				 * Log.i("ZOOM","THEN ("+points.get(ZoomPoints).x+","+
				 * points.get(ZoomPoints).y+")"); }
				 * Log.i("ZOOM","displacement X:"
				 * +mScaleFocusX+"->"+mScaleFocusX*mScaleFactor);
				 * Log.i("ZOOM","displacement Y:"
				 * +mScaleFocusY+"->"+mScaleFocusY*mScaleFactor); int
				 * ZoomDisplacementX
				 * =(int)(mScaleFocusX-mScaleFocusX*mScaleFactor); int
				 * ZoomDisplacementY
				 * =(int)(mScaleFocusY-mScaleFocusY*mScaleFactor); for(int
				 * TranslatePoints
				 * =0;TranslatePoints<points.size();TranslatePoints++) {
				 * points.get(TranslatePoints).x+=ZoomDisplacementX;
				 * points.get(TranslatePoints).y+=ZoomDisplacementY; }
				 */
				canvas.scale(mScaleFactor, mScaleFactor,
						(OneTouchX + TwoTouchX + mpPosX) / 2, (OneTouchY
								+ TwoTouchY + mpPosY) / 2);
				// ZOOM=false;
			}
			/* for draggable */

			/* Draw original point */
			mPaint.setAntiAlias(true);
			if (StartMove == false&& !continue_to_cal) {
				mPaint.setColor(Color.BLACK);
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(getWidth() / 2, getHeight() / 2, 8, mPaint);
			} else if (StartMove == false && continue_to_cal) {
				mPaint.setColor(Color.RED);
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(getWidth() / 2, getHeight() / 2, 8, mPaint);
			} else {
				// mPaint.setColor(Color.RED);
				drawNavigateIcon(canvas, getWidth() / 2 - 15,
						getHeight() / 2 - 15);
			}

			/* Draw original point */

			/* add original point */
			if (points.size() == 0) {
				points.add(new Node(this.getWidth() / 2, this.getHeight() / 2));// center,
																				// original
																				// point
				i++;// flag
			}

			Node tempc = new Node(this.getWidth() / 2, this.getHeight() / 2);
			mPaint.setStrokeWidth(3);
			if (points.size() == 2)// special case : when two point
			{
				LinkedList<Node> temp_points = new LinkedList<Node>();

				Node currentNode = points.get(1);
				Node nextNode = points.get(0);
				Node tempn = new Node();

				tempn.x = tempc.x - (currentNode.x - nextNode.x);
				tempn.y = tempc.y - (currentNode.y - nextNode.y);
				tempn.visible = points.get(1).visible;

				temp_points.add(tempc);
				temp_points.add(tempn);

				mPaint.setStyle(Style.STROKE);
				mPaint.setColor(Color.BLUE);
				if (DisplayPath) {

					if ((tempc.visible == false && NoStopPath == true)
							|| (points.get(points.size() - 1).visible == false)) {
						mPaint.setColor(Color.DKGRAY);
					}
					canvas.drawLine(tempc.x, tempc.y, tempn.x, tempn.y, mPaint);

				}

				// Toast.makeText(getContext(),
				// "("+currentNode.x+","+currentNode.y+")->"+"("+nextNode.x+","+nextNode.y+")",
				// Toast.LENGTH_SHORT).show();

				/* 點最後畫 */
				mPaint.setColor(Color.BLACK);
				mPaint.setStyle(Paint.Style.FILL);
				if (DisplayNode) {

					if (tempn.visible == false && NoStopPath == true)// NoStopPath
																		// mode
					{
						mPaint.setStyle(Paint.Style.STROKE);
					}
					canvas.drawCircle(tempn.x, tempn.y, 6, mPaint);

				}

				if (StartMove) {
					mPaint.setColor(Color.RED);
					mPaint.setStyle(Paint.Style.FILL);
					drawNavigateIcon(canvas, getWidth() / 2 - 15,
							getHeight() / 2 - 15);
				}
				if (points.get(points.size() - 1).visible == false)// NoStopPath
				// mode
				{
					mPaint.setColor(Color.DKGRAY);
					mPaint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(getWidth() / 2, getHeight() / 2, 10,
							mPaint);
				}
			} else {
				LinkedList<Node> temp_points = new LinkedList<Node>();

				for (int j = points.size() - 1; j > 0; j--) {
					Node currentNode = points.get(j);
					Node nextNode = points.get(j - 1);// 其實是前一個
					Node tempn = new Node();
					tempn.x = tempc.x - (currentNode.x - nextNode.x);
					tempn.y = tempc.y - (currentNode.y - nextNode.y);
					tempn.visible = nextNode.visible;

					mPaint.setStyle(Style.STROKE);
					mPaint.setColor(Color.BLUE);

					if (DisplayPath)// display path in path page
					{
						if ((tempc.visible == false && NoStopPath == true)
								|| (points.get(points.size() - 1).visible == false && j == points
										.size() - 1))// NoStopPath mode
						{
							mPaint.setColor(Color.DKGRAY);
						}
						canvas.drawLine(tempc.x, tempc.y, tempn.x, tempn.y,
								mPaint);
					}

					temp_points.add(tempc);
					tempc = tempn;
					if (j == 1) {
						temp_points.add(tempc);
					}
				}

				if (DisplayNode)// display node in path page
				{
					for (int j = temp_points.size() - 1; j > 0; j--) {
						if ((temp_points.size() - 1 - j + _VoronoiNode + 1)
								% (_VoronoiNode + 1) == 0)// 每隔n個灰點就有一個黑點,頭永遠是紅點,尾(起點)永遠是黑點
						{
							mPaint.setColor(Color.BLACK);
						} else {
							mPaint.setColor(Color.GRAY);
						}
						mPaint.setStyle(Paint.Style.FILL);
						if (temp_points.get(j).visible == false
								&& NoStopPath == true)// NoStopPath preference
														// mode
						{
							mPaint.setStyle(Paint.Style.STROKE);
						}

						canvas.drawCircle(temp_points.get(j).x,
								temp_points.get(j).y, 5, mPaint);
					}
				}

				if (StartMove) {
					// mPaint.setColor(Color.RED);
					// mPaint.setStyle(Paint.Style.FILL);
					drawNavigateIcon(canvas, getWidth() / 2 - 15,
							getHeight() / 2 - 15);
					// canvas.drawCircle(getWidth() / 2, getHeight() / 2,
					// 8,mPaint);
				}
				if (points.get(points.size() - 1).visible == false)// NoStopPath
																	// preference
																	// mode
				{
					mPaint.setColor(Color.DKGRAY);
					mPaint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(getWidth() / 2, getHeight() / 2, 10,
							mPaint);
				}
			}
			/* for draggable */
			canvas.restore(); // restore canvas
			/* for draggable */
		}
	}
}