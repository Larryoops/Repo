package com.example.wifiscanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

public class ShowInput extends View {

	private LinkedList<Node> points = new LinkedList<Node>();
	private LinkedList<WifiInformation> wifiInformation = new LinkedList<WifiInformation>();

	private LinkedList<WifiInformation> dynamic_wifiInformation = new LinkedList<WifiInformation>();
	private LinkedList<Node> dynamic_points = new LinkedList<Node>();

	private String StartTime;
	private int FilterDifference;
	private Paint mPaint;
	private String MAC_now = "";
	private String SSID_now = "";
	private boolean StartDraw = false;

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
	private boolean VDisplayWifiCircle = false;
	private boolean VDisplayNOCALNode = true;
	private boolean VDisplayNode = true;
	private boolean VDisplayPossibleAP = false;
	private boolean DisplayRuler = true;
	private int StrideMultiply = 30;
	/* Preference parameters */

	/* bit map */
	Bitmap vBitmap;
	Bitmap WifiBitmap;
	Bitmap ConnectedWifiBitmap;
	Bitmap PossibleWifiBitmap;
	/* bit map */

	/* Draggable parameter */
	private static final int INVALID_POINTER_ID = -1;
	private Drawable mImage;
	private float mPosX;
	private float mPosY;
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

	/* show message parameter */

	/**
	 * initialize Paint
	 */
	public void init() {
		mPaint = new Paint();// ini
	}

	void Start() {
		StartDraw = true;
	}

	/**
	 * constructor
	 */
	public ShowInput(Context context, AttributeSet attributeSet) {
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
		gestureDetector = new GestureDetector(context, new GestureListener());
		init();
	}

	/**
	 * constructor
	 */
	public ShowInput(Context context) {
		super(context);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());// for
																				// scale
																				// screen
		init();
	}

	public void PreferenceSetting(boolean DisplayRuler,
			boolean VDisplayPossibleAP, boolean DisplayNode,
			boolean DisplayPath, boolean NoStopPath, int _VoronoiNode,
			boolean VDisplayPath, boolean VDisplayWifiCircle,
			boolean VDisplayNOCALNode, boolean VDisplayNode,
			int DifferenceFilter, int PathLoss, int DefaultRSSI,
			int StrideMultiply) {
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
		this.VDisplayPossibleAP = VDisplayPossibleAP;
		this.DisplayRuler = DisplayRuler;
		this.StrideMultiply = StrideMultiply;
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

			for (int translate = 0; translate < dynamic_points.size(); translate++) {
				dynamic_points.get(translate).x = points.get(translate).x;
				dynamic_points.get(translate).y = points.get(translate).y;
			}
			for (int translate = 0; translate < dynamic_wifiInformation.size(); translate++) {
				if (dynamic_wifiInformation.get(translate).AlreadyCal) {
					dynamic_wifiInformation.get(translate).x = wifiInformation
							.get(translate).x;
					dynamic_wifiInformation.get(translate).y = wifiInformation
							.get(translate).y;
				} else if (dynamic_wifiInformation.get(translate).PossibleNode) {
					dynamic_wifiInformation.get(translate).x = wifiInformation
							.get(translate).x;
					dynamic_wifiInformation.get(translate).y = wifiInformation
							.get(translate).y;
					dynamic_wifiInformation.get(translate).x1 = wifiInformation
							.get(translate).x1;
					dynamic_wifiInformation.get(translate).y1 = wifiInformation
							.get(translate).y1;
				}
			}
			// Changed=false;

			// dynamic_wifiInformation=wifiInformation;

			// dynamic_points=points;
			// mPosX*=-1;
			// mPosY*=-1;
			// LastPosX=0.f;
			// LastPosY=0.f;
			// mScaleFactor=1/mScaleFactor;

			/*
			 * Changed=false; mPosX=0.f; mPosY=0.f; LastPosX=0.f; LastPosY=0.f;
			 * mScaleFactor=1.f; TotalChangeProportion = 1.0;
			 * TotalChangeDisplacementX = 0; TotalChangeDisplacementY = 0;
			 */
			Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
			invalidate();

			mPosX = 0.f;
			mPosY = 0.f;
			LastPosX = 0;
			LastPosY = 0;
			mScaleFactor = 1.f;

			return true;
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

				mPosX += dx;
				mPosY += dy;

				// Toast.makeText(getContext(), "MOVE",
				// Toast.LENGTH_SHORT).show();

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
			if (downX == upX && downY == upY) {
				showMessage();
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
	 * Display the AP's message when user click the ap image
	 */
	public void showMessage() {
		for (int iAP = 0; iAP < dynamic_wifiInformation.size(); iAP++) {
			if (dynamic_wifiInformation.get(iAP).NoNode)// No AP image
			{
				continue;
			} else {
				if (dynamic_wifiInformation.get(iAP).AlreadyCal)// one point
				{
					if (Math.abs(dynamic_wifiInformation.get(iAP).x - downX) < 12
							&& Math.abs(dynamic_wifiInformation.get(iAP).y
									- downY) < 30) {
						Toast.makeText(
								getContext(),
								"顯示"
										+ dynamic_wifiInformation.get(iAP)
												.getSSID() + "的資訊",
								Toast.LENGTH_SHORT).show();
						Builder MyAlertDialog = new AlertDialog.Builder(
								getContext());
						MyAlertDialog.setTitle(dynamic_wifiInformation.get(iAP)
								.getSSID() + "的資訊");
						MyAlertDialog.setMessage("名稱:"
								+ dynamic_wifiInformation.get(iAP).getSSID()
								+ "\nMAC:"
								+ dynamic_wifiInformation.get(iAP).getMAC()
								+ "\n預測座標:("
								+ dynamic_wifiInformation.get(iAP).x + ","
								+ dynamic_wifiInformation.get(iAP).y + ")");
						MyAlertDialog.show();
					}
				} else if (VDisplayPossibleAP)// two possible point
				{
					if (Math.abs(dynamic_wifiInformation.get(iAP).x - downX) < 12
							&& Math.abs(dynamic_wifiInformation.get(iAP).y
									- downY) < 30) {
						Toast.makeText(
								getContext(),
								"顯示"
										+ dynamic_wifiInformation.get(iAP)
												.getSSID() + "(1/2)的資訊",
								Toast.LENGTH_SHORT).show();
						Builder MyAlertDialog = new AlertDialog.Builder(
								getContext());
						MyAlertDialog.setTitle(dynamic_wifiInformation.get(iAP)
								.getSSID() + "的資訊");
						MyAlertDialog.setMessage("名稱:"
								+ dynamic_wifiInformation.get(iAP).getSSID()
								+ "\nMAC:"
								+ dynamic_wifiInformation.get(iAP).getMAC()
								+ "\n預測可能的座標之一:("
								+ dynamic_wifiInformation.get(iAP).x + ","
								+ dynamic_wifiInformation.get(iAP).y + ")");
						MyAlertDialog.show();
					} else if (Math.abs(dynamic_wifiInformation.get(iAP).x1
							- downX) < 12
							&& Math.abs(dynamic_wifiInformation.get(iAP).y1
									- downY) < 30) {
						Toast.makeText(
								getContext(),
								"顯示"
										+ dynamic_wifiInformation.get(iAP)
												.getSSID() + "(1/2)的資訊",
								Toast.LENGTH_SHORT).show();
						Builder MyAlertDialog = new AlertDialog.Builder(
								getContext());
						MyAlertDialog.setTitle(dynamic_wifiInformation.get(iAP)
								.getSSID() + "的資訊");
						MyAlertDialog.setMessage("名稱:"
								+ dynamic_wifiInformation.get(iAP).getSSID()
								+ "\nMAC:"
								+ dynamic_wifiInformation.get(iAP).getMAC()
								+ "\n預測可能的座標之一:("
								+ dynamic_wifiInformation.get(iAP).x1 + ","
								+ dynamic_wifiInformation.get(iAP).y1 + ")");
						MyAlertDialog.show();
					}

				}
			}
		}

	}

	public void SendRecord(LinkedList<Node> points,
			LinkedList<WifiInformation> wifiInformation, String StartTime,
			int PathLoss, int DefaultRSSI, int FilterDifference,
			String MAC_now, String SSID_now) {
		this.points = points;
		this.wifiInformation = wifiInformation;
		this.StartTime = StartTime;
		this.PathLoss = PathLoss;
		this.DefaultRSSI = DefaultRSSI;
		this.FilterDifference = FilterDifference;
		this.MAC_now = MAC_now;
		this.SSID_now = SSID_now;

		this.dynamic_points = points;
		this.dynamic_wifiInformation = wifiInformation;

		Start();
		invalidate();
	}

	public void ChangeMapSize() {
		TotalChangeProportion = 1.0;
		TotalChangeDisplacementX = 0;
		TotalChangeDisplacementY = 0;
		int maxX = -1000, maxY = -1000, minX = 1000, minY = 1000;
		System.out.println("Width:" + getWidth() + " Height:" + getHeight());
		for (int CheckP = 0; CheckP < dynamic_points.size(); CheckP++) {
			if (dynamic_points.get(CheckP).x > maxX) {
				maxX = dynamic_points.get(CheckP).x;
			}
			if (dynamic_points.get(CheckP).x < minX) {
				minX = dynamic_points.get(CheckP).x;
			}

			if (dynamic_points.get(CheckP).y > maxY) {
				maxY = dynamic_points.get(CheckP).y;
			}
			if (dynamic_points.get(CheckP).y < minY) {
				minY = dynamic_points.get(CheckP).y;
			}
		}
		for (int CheckW = 0; CheckW < dynamic_wifiInformation.size(); CheckW++) {
			if (dynamic_wifiInformation.get(CheckW).AlreadyCal) {
				if (dynamic_wifiInformation.get(CheckW).x > maxX) {
					maxX = dynamic_wifiInformation.get(CheckW).x;
				}
				if (dynamic_wifiInformation.get(CheckW).x < minX) {
					minX = dynamic_wifiInformation.get(CheckW).x;
				}

				if (dynamic_wifiInformation.get(CheckW).y > maxY) {
					maxY = dynamic_wifiInformation.get(CheckW).y;
				}
				if (dynamic_wifiInformation.get(CheckW).y < minY) {
					minY = dynamic_wifiInformation.get(CheckW).y;
				}
			}
		}

		System.out.println("MaxX:" + maxX + " MaxY:" + maxY + " MinX:" + minX
				+ " MinY:" + minY);

		if (minX <= 0) {
			int Displacement = (-1) * minX + 10;
			System.out
					.println("Displacement(for X-Coordinate):" + Displacement);
			Log.i("MAP SIZE", "Displacement(for X-Coordinate):" + Displacement);
			TotalChangeDisplacementX = Displacement;
			minX += Displacement;
			maxX += Displacement;
		}
		if (minY <= 0) {
			int Displacement = (-1) * minY + 10;
			System.out
					.println("Displacement(for Y-Coordinate):" + Displacement);
			Log.i("MAP SIZE", "Displacement(for Y-Coordinate):" + Displacement);
			TotalChangeDisplacementY = Displacement;
			minY += Displacement;
			maxY += Displacement;
		}

		if (maxX > this.getWidth()) {
			Double DmaxX = new Double(maxX);
			Double Proportion = (this.getWidth() - 50) / DmaxX;
			System.out.println("Proporion(for maxX):" + Proportion);
			Log.i("MAP SIZE", "ProporionY(for maxX):" + Proportion);
			TotalChangeProportion *= Proportion;
			maxX *= Proportion;
			maxY *= Proportion;
		}
		if (maxY > this.getHeight()) {
			Double DmaxY = new Double(maxY);
			Double Proportion = (this.getHeight() - 50) / DmaxY;
			System.out.println("ProporionY(for maxY):" + Proportion);
			TotalChangeProportion *= Proportion;
			Log.i("MAP SIZE", "ProporionY(for maxY):" + Proportion);
			maxX *= Proportion;
			maxY *= Proportion;
		}

		System.out.println("MaxX:" + maxX + " MaxY:" + maxY + " MinX:" + minX
				+ " MinY:" + minY);
		/* Start Change */
		for (int ChangeP = 0; ChangeP < dynamic_points.size(); ChangeP++) {
			dynamic_points.get(ChangeP).x += TotalChangeDisplacementX;

			dynamic_points.get(ChangeP).x *= TotalChangeProportion;
			System.out.println((ChangeP + 1) + " x :"
					+ dynamic_points.get(ChangeP).x);

			dynamic_points.get(ChangeP).y += TotalChangeDisplacementY;
			dynamic_points.get(ChangeP).y *= TotalChangeProportion;
			System.out.println(" y :" + dynamic_points.get(ChangeP).y);
		}

		/*
		 * for (int ChangeW = 0; ChangeW < dynamic_wifiInformation.size();
		 * ChangeW++) { if (dynamic_wifiInformation.get(ChangeW).AlreadyCal) {
		 * dynamic_wifiInformation.get(ChangeW).x += TotalChangeDisplacementX;
		 * dynamic_wifiInformation.get(ChangeW).x *= TotalChangeProportion;
		 * 
		 * dynamic_wifiInformation.get(ChangeW).y += TotalChangeDisplacementY;
		 * dynamic_wifiInformation.get(ChangeW).y *= TotalChangeProportion; }
		 * else if (dynamic_wifiInformation.get(ChangeW).PossibleNode) {
		 * dynamic_wifiInformation.get(ChangeW).x += TotalChangeDisplacementX;
		 * dynamic_wifiInformation.get(ChangeW).x *= TotalChangeProportion;
		 * 
		 * dynamic_wifiInformation.get(ChangeW).y += TotalChangeDisplacementY;
		 * dynamic_wifiInformation.get(ChangeW).y *= TotalChangeProportion;
		 * 
		 * dynamic_wifiInformation.get(ChangeW).x1 += TotalChangeDisplacementX;
		 * dynamic_wifiInformation.get(ChangeW).x1 *= TotalChangeProportion;
		 * 
		 * dynamic_wifiInformation.get(ChangeW).y1 += TotalChangeDisplacementY;
		 * dynamic_wifiInformation.get(ChangeW).y1 *= TotalChangeProportion; } }
		 */

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (DisplayRuler) {
			int Width = getWidth();
			int Height = getHeight();
			double WReal_length = Width / StrideMultiply;
			double WLength_per = Math.floor(Width
					* (Math.ceil(WReal_length / 3) / WReal_length));
			double WTotal_Remain = Width;
			int Wdraw_times = 0;
			mPaint.setTextSize(20);
			while (WTotal_Remain > 0) {
				Wdraw_times++;
				if (WTotal_Remain >= WLength_per) {
					WTotal_Remain -= WLength_per;
					mPaint.setColor(Color.WHITE);
					canvas.drawLine((float) WLength_per * (Wdraw_times - 1),
							(float) (Height * 0.99) - 8, (float) WLength_per
									* Wdraw_times, (float) (Height * 0.99) - 8,
							mPaint);// -
					canvas.drawLine(0, (float) (Height * 0.99) - 16, 0,
							(float) (Height * 0.99), mPaint);// |-
					canvas.drawLine((float) WLength_per * Wdraw_times,
							(float) (Height * 0.99) - 16, (float) WLength_per
									* Wdraw_times, (float) (Height * 0.99),
							mPaint);// |-|
					mPaint.setColor(Color.BLACK);
					if (((float) WLength_per * Wdraw_times - 20) < Width * 0.9) {
						NumberFormat nf = NumberFormat.getInstance();
						nf.setMaximumFractionDigits(2);
						canvas.drawText(
								String.valueOf((nf
										.format(((WReal_length / 3) * Wdraw_times)
												/ mScaleFactor)))
										+ "m", (float) WLength_per
										* Wdraw_times - 20,
								(float) (Height * 0.99) - 26, mPaint);
					}
					mPaint.setColor(Color.WHITE);
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
					mPaint.setColor(Color.WHITE);
					canvas.drawLine((float) WLength_per * (Wdraw_times - 1),
							(float) (Height * 0.99) - 8, Width,
							(float) (Height * 0.99) - 8, mPaint);// -
					int RemainTimes = 0;
					while (RemainTimes < 4) {
						RemainTimes++;
						if (WRemain < (WLength_per / 5)) {
							//break;
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

			double HReal_length = Height / StrideMultiply;
			double HLength_per = Math.floor(Height
					* (Math.ceil(HReal_length / 5) / HReal_length));
			double HTotal_Remain = Height;
			int Hdraw_times = 0;
			while (HTotal_Remain > 0) {
				Hdraw_times++;
				if (HTotal_Remain >= HLength_per) {
					HTotal_Remain -= HLength_per;
					mPaint.setColor(Color.WHITE);
					canvas.drawLine((float) (Width * 0.99) - 8,
							(float) HLength_per * (Hdraw_times - 1),
							(float) (Width * 0.99) - 8, (float) HLength_per
									* Hdraw_times, mPaint);// |
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
												/ mScaleFactor)))
										+ "m", (float) (Width * 0.99) - 72,
								(float) HLength_per * Hdraw_times, mPaint);
					}
					mPaint.setColor(Color.WHITE);
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
					mPaint.setColor(Color.WHITE);
					canvas.drawLine((float) (Width * 0.99) - 8,
							(float) HLength_per * (Hdraw_times - 1),
							(float) (Width * 0.99) - 8, Height, mPaint);// |
					int RemainTimes = 0;
					while (RemainTimes < 4) {
						RemainTimes++;
						if (HRemain < (HLength_per / 5)) {
							//break;
						}
						canvas.drawLine(
								(float) (Width * 0.99) - 12,
								(float) ((Hdraw_times - 1) * (HLength_per) + RemainTimes
										* (HLength_per / 5)),
								(float) (Width * 0.99) - 4,
								(float) ((Hdraw_times - 1) * (HLength_per) + RemainTimes
										* (HLength_per / 5)), mPaint);// little
																		// -

					}
				}

			}

			/*
			 * canvas.drawLine(10, (float) (getHeight() * 0.99) - 8, 80, (float)
			 * (getHeight() * 0.99) - 8, mPaint);
			 * 
			 * canvas.drawLine(10, (float) (getHeight() * 0.99) - 15, 10,
			 * (float) (getHeight() * 0.99) - 5, mPaint); canvas.drawLine(80,
			 * (float) (getHeight() * 0.99) - 15, 80, (float) (getHeight() *
			 * 0.99) - 5, mPaint);
			 * 
			 * canvas.drawText(SM + "m", 85, (float) (getHeight() * 0.99) - 5,
			 * mPaint);
			 */
		}

		if (StartDraw) {
			if (!Changed) {
				ChangeMapSize();
				Changed = true;
			} else {

				for (int translate = 0; translate < dynamic_points.size(); translate++) {
					dynamic_points.get(translate).x -= (LastPosX - mPosX);
					dynamic_points.get(translate).y -= (LastPosY - mPosY);
				}
				for (int translate = 0; translate < dynamic_wifiInformation
						.size(); translate++) {
					if (dynamic_wifiInformation.get(translate).AlreadyCal) {
						dynamic_wifiInformation.get(translate).x -= (LastPosX - mPosX);
						dynamic_wifiInformation.get(translate).y -= (LastPosY - mPosY);
					} else if (dynamic_wifiInformation.get(translate).PossibleNode) {
						dynamic_wifiInformation.get(translate).x -= (LastPosX - mPosX);
						dynamic_wifiInformation.get(translate).y -= (LastPosY - mPosY);
						dynamic_wifiInformation.get(translate).x1 -= (LastPosX - mPosX);
						dynamic_wifiInformation.get(translate).y1 -= (LastPosY - mPosY);
					}
				}

				LastPosX = mPosX;
				LastPosY = mPosY;
			}

			/* for screen draggable */
			canvas.save();
			// canvas.translate(mPosX, mPosY);
			if (ZOOM) {
				// canvas.scale(mScaleFactor, mScaleFactor,(OneTouchX +
				// TwoTouchX + mPosX) / 2, (OneTouchY+ TwoTouchY + mPosY) / 2);
				canvas.scale(mScaleFactor, mScaleFactor, mScaleFocusX,
						mScaleFocusY);
			}

			/* for screen draggable */

			Voronoi v = new Voronoi(0.00001f);
			int NodeSize = dynamic_points.size()
					/ (_VoronoiNode + 1)
					+ ((dynamic_points.size() % (_VoronoiNode + 1) == 0 ? 0 : 1));

			if (VDisplayPath)// display path
			{
				for (int j = 0; j < dynamic_points.size() - 1; j++) {
					mPaint.setColor(Color.WHITE);
					canvas.drawLine(dynamic_points.get(j).x,
							dynamic_points.get(j).y,
							dynamic_points.get(j + 1).x,
							dynamic_points.get(j + 1).y, mPaint);
				}
			}

			double[] x = new double[NodeSize], y = new double[NodeSize];

			/* Get node to calculate voronoi edges */
			for (int k = 0, index = 0; k < dynamic_points.size()
					&& index < NodeSize; k = k + _VoronoiNode + 1, index++) {
				x[index] = (double) dynamic_points.get(k).x;
				y[index] = (double) dynamic_points.get(k).y;
				if (VDisplayNode) {
					mPaint.setColor(Color.GREEN);
					canvas.drawCircle((float) x[index], (float) y[index], 10,
							mPaint);
				}
				if (!VDisplayNOCALNode && VDisplayNode)// No display node not
														// use in voronoi
														// diagram
				{
					if (k == dynamic_points.size() - 1) {
						mPaint.setColor(Color.RED);// now position
					} else {
						mPaint.setColor(Color.BLUE);// now now position
					}
					canvas.drawCircle(dynamic_points.get(k).x,
							dynamic_points.get(k).y, 6, mPaint);
					mPaint.setColor(Color.WHITE);
					mPaint.setTextSize(15);
					canvas.drawText("" + (k + 1), dynamic_points.get(k).x - 8,
							dynamic_points.get(k).y - 8, mPaint);// number of
																	// node
				}
			}
			List<GraphEdge> allEdges = v.generateVoronoi(x, y, 0.0,
					this.getWidth(), 0.0, this.getHeight());// generate voronoi
															// diagram's edge
			PathEffect effects = new DashPathEffect(new float[] { 20, 20, 20,
					20 }, 1);// dash line
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.GRAY);
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
					for (int j = 0; j < dynamic_points.size(); j++) {
						if (j == dynamic_points.size() - 1) {
							mPaint.setColor(Color.RED);
						} else {
							mPaint.setColor(Color.BLUE);
						}
						canvas.drawCircle(dynamic_points.get(j).x,
								dynamic_points.get(j).y, 6, mPaint);
						mPaint.setColor(Color.WHITE);
						mPaint.setTextSize(15);
						canvas.drawText("" + (j + 1),
								dynamic_points.get(j).x - 8,
								dynamic_points.get(j).y - 8, mPaint);
					}
				}
			}

			/* Draw Icon if have result */
			System.out.println("NOW SSID" + SSID_now);
			System.out.println("NOW MAC" + MAC_now);
			for (int j = 0; j < dynamic_wifiInformation.size(); j++) {
				if (dynamic_wifiInformation.get(j).AlreadyCal)// one result
				{
					long between = (System.currentTimeMillis() / 1000) % 60;
					if (dynamic_wifiInformation.get(j).getMAC()
							.equalsIgnoreCase(MAC_now)
							&& (between % 2) == 1) {
						canvas.drawBitmap(ConnectedWifiBitmap,
								dynamic_wifiInformation.get(j).x - 12,
								dynamic_wifiInformation.get(j).y - 30, mPaint);
						/*
						 * mPaint.setColor(Color.WHITE);
						 * mPaint.setStyle(Style.FILL); mPaint.setTextSize(15);
						 * canvas.drawText("" +
						 * dynamic_wifiInformation.get(j).getSSID(),
						 * dynamic_wifiInformation.get(j).x - 30,
						 * dynamic_wifiInformation.get(j).y - 18, mPaint);
						 */
					}

					else {
						canvas.drawBitmap(WifiBitmap,
								dynamic_wifiInformation.get(j).x - 12,
								dynamic_wifiInformation.get(j).y - 30, mPaint); // wait
						// for
						// modifying:
						// need
						// center
						/*
						 * mPaint.setColor(Color.WHITE);
						 * mPaint.setStyle(Style.FILL); mPaint.setTextSize(15);
						 * canvas.drawText("" +
						 * dynamic_wifiInformation.get(j).getSSID(),
						 * dynamic_wifiInformation.get(j).x - 30,
						 * dynamic_wifiInformation.get(j).y - 18, mPaint);
						 */
					}

					if ((dynamic_wifiInformation.get(j).getMAC()
							.equalsIgnoreCase(MAC_now) == false)
							&& (dynamic_wifiInformation.get(j).getSSID()
									.equals(SSID_now))) {
						canvas.drawBitmap(ConnectedWifiBitmap,
								dynamic_wifiInformation.get(j).x - 12,
								dynamic_wifiInformation.get(j).y - 30, mPaint);

					}

				} else if ((dynamic_wifiInformation.get(j).PossibleNode)
						&& VDisplayPossibleAP)// two possible
				// result
				{
					canvas.drawBitmap(PossibleWifiBitmap,
							dynamic_wifiInformation.get(j).x - 12,
							dynamic_wifiInformation.get(j).y - 30, mPaint);
					canvas.drawBitmap(PossibleWifiBitmap,
							dynamic_wifiInformation.get(j).x1 - 12,
							dynamic_wifiInformation.get(j).y1 - 30, mPaint);
					/*
					 * mPaint.setStyle(Style.STROKE); mPaint.setStrokeWidth(2);
					 * mPaint.setColor(Color.YELLOW);
					 * canvas.drawLine(dynamic_wifiInformation.get(j).x,
					 * dynamic_wifiInformation.get(j).y,
					 * dynamic_wifiInformation.get(j).x1,
					 * dynamic_wifiInformation.get(j).y1, mPaint);
					 * 
					 * mPaint.setStyle(Style.FILL);
					 * mPaint.setColor(Color.BLACK);
					 * canvas.drawCircle(dynamic_wifiInformation.get(j).x,
					 * dynamic_wifiInformation.get(j).y, 2,mPaint);
					 * canvas.drawCircle(dynamic_wifiInformation.get(j).x1,
					 * dynamic_wifiInformation.get(j).y1, 2,mPaint);
					 */
					/*
					 * mPaint.setColor(Color.WHITE);
					 * mPaint.setStyle(Style.FILL); mPaint.setTextSize(15);
					 * 
					 * canvas.drawText("1." +
					 * dynamic_wifiInformation.get(j).getSSID(),
					 * dynamic_wifiInformation.get(j).x - 30,
					 * dynamic_wifiInformation.get(j).y - 18, mPaint);
					 * canvas.drawText("2." +
					 * dynamic_wifiInformation.get(j).getSSID(),
					 * dynamic_wifiInformation.get(j).x1 - 30,
					 * dynamic_wifiInformation.get(j).y1 - 18, mPaint);
					 */
				} else if ((dynamic_wifiInformation.get(j).NoNode)
						&& VDisplayWifiCircle)// no result
				{

					// mPaint.setColor(dynamic_wifiInformation.get(j).color.hashCode());
					// now set color
					mPaint.setStyle(Style.STROKE);
					mPaint.setStrokeWidth(3);
					canvas.drawCircle(
							(float) dynamic_points.get(dynamic_wifiInformation
									.get(j).getSignal().get(0).getScanNumber()).x,
							(float) dynamic_points.get(dynamic_wifiInformation
									.get(j).getSignal().get(0).getScanNumber()).y,
							(float) dynamic_wifiInformation.get(j).getSignal()
									.get(0).getDistance(), mPaint);

					mPaint.setColor(Color.WHITE);
					mPaint.setStyle(Style.FILL);
					mPaint.setTextSize(15);
				}
			}

			/* for screen draggable */
			canvas.restore();
			/* for screen draggable */
		}

	}

}
