package com.example.wifiscanner;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

public class AnalyzeActivity extends Activity {

	private LinearLayout APNumberChart;
	private View vChart;
	private Spinner spinner;
	private String[] SpinnerItem = { "請選擇", "AP通道統計(柱狀圖)", "AP通道統計(圓餅圖)",
			"通道衝突(還在想...)" };
	private ArrayAdapter<String> ItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analyze);
		APNumberChart = (LinearLayout) findViewById(R.id.APNumberChart);
		Intent intent = getIntent();
		final String[] ChannelAP = intent.getStringArrayExtra("ChannelAP");
		final String[] APSSID = intent.getStringArrayExtra("APSSID");

		spinner = (Spinner) findViewById(R.id.Spinner);
		ItemList = new ArrayAdapter<String>(this, R.layout.spinnerlayout,
				SpinnerItem);
		ItemList.setDropDownViewResource(R.layout.spinnerdrop);
		spinner.setAdapter(ItemList);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView adapterview, View view,
					int position, long id) {
				switch (adapterview.getSelectedItemPosition()) {
				case 0:
					APNumberChart.removeAllViews();
					break;
				case 1:
					APNumberChart.removeAllViews();
					vChart = getBarChart("", "通道", "AP數", ChannelAP);
					APNumberChart.addView(vChart);
					break;
				case 2:
					APNumberChart.removeAllViews();
					vChart=getPieChart("",ChannelAP);
					APNumberChart.addView(vChart);
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

	}

	private View getPieChart(String chartTitle, String[] xy) {
		int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED,
				Color.YELLOW, Color.CYAN, Color.GRAY, Color.LTGRAY,
				Color.DKGRAY, Color.rgb(255, 228, 181),
				Color.rgb(180, 99, 181), Color.rgb(99, 181, 181),
				Color.rgb(180, 191, 181), Color.rgb(154, 255, 154) };
		
		int count=0;
		CategorySeries series = new CategorySeries("");	
		for(int check=0;check<xy.length;check++)
		{
			if(!(xy[check].equals("0")))
			{
				series.add("通道"+String.valueOf(check+1),Integer.parseInt(xy[check]));
				count++;
			}
		}
		
		DefaultRenderer renderer = new DefaultRenderer();

		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setDisplayValues(true);

		renderer.setLegendTextSize(20);
		renderer.setZoomEnabled(false);
		renderer.setLabelsTextSize(20);
		renderer.setPanEnabled(false);
		renderer.setClickEnabled(true);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color =0;color<count;color++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[color]);
			renderer.addSeriesRenderer(r);
		}
		
		View view=ChartFactory.getPieChartView(getBaseContext(), series, renderer);
		return view;
	}

	private View getBarChart(String chartTitle, String XTitle, String YTitle,
			String[] xy) {
		XYSeries Series = new XYSeries(YTitle);
		XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();
		Dataset.addSeries(Series);
		XYMultipleSeriesRenderer Renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer yRenderer = new XYSeriesRenderer();
		Renderer.addSeriesRenderer(yRenderer);

		Renderer.setApplyBackgroundColor(true);
		Renderer.setBackgroundColor(Color.BLACK);
		Renderer.setMarginsColor(Color.WHITE);
		Renderer.setTextTypeface(null, Typeface.NORMAL);
		Renderer.setShowGrid(true);
		Renderer.setGridColor(Color.WHITE);

		Renderer.setChartTitle(chartTitle);
		Renderer.setLabelsColor(Color.WHITE);
		Renderer.setChartTitleTextSize(20);
		Renderer.setAxesColor(Color.BLUE);
		Renderer.setBarSpacing(0.5);

		Renderer.setXTitle(XTitle);
		Renderer.setYTitle(YTitle);
		Renderer.setAxisTitleTextSize(30);
		Renderer.setAxesColor(Color.RED);

		Renderer.setXLabelsColor(Color.BLUE);
		Renderer.setYLabelsColor(0, Color.BLUE);
		Renderer.setLabelsColor(Color.BLACK);
		Renderer.setXLabelsAlign(Align.CENTER);
		Renderer.setYLabelsAlign(Align.LEFT);
		Renderer.setXLabelsAngle(0);
		Renderer.setYLabelsPadding(15);
		Renderer.setLabelsTextSize(17);

		Renderer.setZoomRate(1.1f);
		Renderer.setXLabels(0);
		Renderer.setXAxisMin(0);
		Renderer.setXAxisMax(14);
		Renderer.setYAxisMin(0);
		Renderer.setZoomInLimitX(14);

		Renderer.setFitLegend(true);

		Renderer.setLegendTextSize(20);
		yRenderer.setColor(Color.RED);
		yRenderer.setChartValuesTextSize(25);
		yRenderer.setDisplayChartValues(true);

		// Renderer.addTextLabel(0, "");
		for (int r = 0; r < xy.length; r++) {
			Renderer.addTextLabel(r + 1, String.valueOf(r + 1));
			Series.add(r + 1, Integer.parseInt(xy[r]));
		}

		Renderer.addTextLabel(xy.length + 1, "");
		View view = ChartFactory.getBarChartView(getBaseContext(), Dataset,
				Renderer, Type.STACKED);
		return view;

	}

}
